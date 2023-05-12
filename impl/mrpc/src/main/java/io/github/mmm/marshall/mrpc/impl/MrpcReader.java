/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.marshall.mrpc.impl;

import java.io.IOException;

import com.google.protobuf.CodedInputStream;
import com.google.protobuf.WireFormat;

import io.github.mmm.base.exception.RuntimeIoException;
import io.github.mmm.base.number.NumberType;
import io.github.mmm.marshall.AbstractStructuredReader;
import io.github.mmm.marshall.StructuredFormat;
import io.github.mmm.marshall.StructuredReader;
import io.github.mmm.marshall.spi.StructuredNodeType;

/**
 * Implementation of {@link StructuredReader} for mRPC.
 *
 * @see MrpcFormat
 *
 * @since 1.0.0
 */
public class MrpcReader extends AbstractStructuredReader {

  private static final int TYPE_NONE = -1;

  private static final int TYPE_NULL = -2;

  private CodedInputStream in;

  private MrpcReadState mrpcState;

  private int tag;

  private int type;

  private int id;

  private int arrayItemCount;

  /**
   * The constructor.
   *
   * @param in the {@link CodedInputStream} with the ProtoBuf content to parse.
   * @param format the {@link #getFormat() format}.
   */
  public MrpcReader(CodedInputStream in, StructuredFormat format) {

    super(format);
    this.in = in;
    this.type = TYPE_NONE;
    this.state = null;
    this.mrpcState = new MrpcReadState();
  }

  @Override
  public State next() {

    if (this.state == State.NAME) {
      switch (this.type) {
        case MrpcFormat.TYPE_START_OBJECT:
          start(StructuredNodeType.OBJECT);
          break;
        case MrpcFormat.TYPE_START_ARRAY:
          start(StructuredNodeType.ARRAY);
          break;
        case WireFormat.WIRETYPE_LENGTH_DELIMITED:
          this.state = State.VALUE;
          break;
        case MrpcFormat.TYPE_END:
          this.state = this.mrpcState.type.getEnd();
          this.id = 0;
          break;
        default:
          this.state = State.VALUE;
      }
    } else if ((this.state == State.END_OBJECT) && (this.mrpcState.parent == null)) {
      this.state = State.DONE;
    } else if (this.state == State.VALUE) {
      readValue(); // could be optimized but doing KISS
    } else if (this.arrayItemCount == 0) {
      readTag();
    }
    return this.state;
  }

  private void readTag() {

    try {
      if ((this.mrpcState.parent == null) && this.in.isAtEnd()) {
        this.state = State.DONE;
        return;
      }
      this.tag = this.in.readRawVarint32();
      if (this.tag == 0) {
        this.id = 0;
        if (this.mrpcState.type == StructuredNodeType.ARRAY) {
          this.type = TYPE_NULL;
          this.state = State.VALUE;
          this.arrayItemCount = 1;
        } else {
          this.type = TYPE_NONE;
          this.state = State.DONE;
        }
      } else {
        this.type = WireFormat.getTagWireType(this.tag);
        int fieldNumber = WireFormat.getTagFieldNumber(this.tag);
        if (this.mrpcState.type == StructuredNodeType.ARRAY) {
          this.arrayItemCount = fieldNumber;
        } else {
          this.id = fieldNumber;
        }
        assert (this.id != -1);
        if (fieldNumber == 0) {
          if (this.type == MrpcFormat.TYPE_START_OBJECT) {
            start(StructuredNodeType.OBJECT);
          } else if (this.type == MrpcFormat.TYPE_START_ARRAY) {
            start(StructuredNodeType.ARRAY);
          }
        } else {
          if (this.mrpcState.type == StructuredNodeType.ARRAY) {
            this.state = State.VALUE;
          } else {
            this.state = State.NAME;
          }
        }
        if (this.type == MrpcFormat.TYPE_END) {
          assert (this.arrayItemCount == 0);
          if (this.mrpcState.type == null) {
            this.state = State.DONE;
          } else {
            this.state = this.mrpcState.type.getEnd();
          }
          this.mrpcState = this.mrpcState.parent;
        }
      }
    } catch (IOException e) {
      throw new RuntimeIoException(e);
    }
  }

  private void start(StructuredNodeType nodeType) {

    assert (this.arrayItemCount == 0);
    this.state = nodeType.getStart();
    this.mrpcState = new MrpcReadState(this.mrpcState, nodeType);
  }

  @Override
  public boolean readStartObject() {

    if (this.state == null) {
      readTag();
    }
    return super.readStartObject();
  }

  @Override
  public boolean readStartArray() {

    if (this.state == null) {
      readTag();
    }
    return super.readStartArray();
  }

  @Override
  public String getName() {

    throw new UnsupportedOperationException();
  }

  @Override
  public int getId() {

    return this.id;
  }

  private <V> V valueCompleted(V value) {

    boolean hasArrayValue = false;
    if (this.arrayItemCount > 0) {
      this.arrayItemCount--;
      hasArrayValue = (this.arrayItemCount > 0);
    } else {
      this.tag = 0;
      this.id = 0;
      this.type = TYPE_NONE;
    }
    if (!hasArrayValue) {
      this.state = null;
      next();
    }
    return value;
  }

  @Override
  public boolean isStringValue() {

    return this.type == WireFormat.WIRETYPE_LENGTH_DELIMITED;
  }

  @Override
  public Object readValue() {

    expect(State.VALUE);
    switch (this.type) {
      case WireFormat.WIRETYPE_LENGTH_DELIMITED:
        return readValueAsString();
      case WireFormat.WIRETYPE_FIXED32:
        return readValueAsFloat();
      case WireFormat.WIRETYPE_FIXED64:
        return readValueAsDouble();
      case WireFormat.WIRETYPE_VARINT:
        return readValueAsLong();
      case TYPE_NULL:
        assert (this.arrayItemCount == 1);
        this.arrayItemCount--;
        return valueCompleted(null);
      default:
        throw error("Unknown wire type: " + this.type);
    }
  }

  private void expectType(int wireType) {

    if ((this.type != wireType) && (this.type != -1)) {
      error("Expected wire type is " + wireType + " but actual type is " + this.type);
    }
  }

  @Override
  public String readValueAsString() {

    expectType(WireFormat.WIRETYPE_LENGTH_DELIMITED);
    try {
      return valueCompleted(this.in.readString());
    } catch (IOException e) {
      throw new RuntimeIoException(e);
    }
  }

  @Override
  public Boolean readValueAsBoolean() {

    try {
      if (this.type == WireFormat.WIRETYPE_LENGTH_DELIMITED) {
        return valueCompleted(parseBoolean(this.in.readString()));
      }
      expectType(WireFormat.WIRETYPE_VARINT);
      return valueCompleted(Boolean.valueOf(this.in.readBool()));
    } catch (IOException e) {
      throw new RuntimeIoException(e);
    }
  }

  @Override
  public Byte readValueAsByte() {

    expectType(WireFormat.WIRETYPE_VARINT);
    try {
      return valueCompleted(Byte.valueOf((byte) this.in.readSInt32()));
    } catch (IOException e) {
      throw new RuntimeIoException(e);
    }
  }

  @Override
  public Short readValueAsShort() {

    expectType(WireFormat.WIRETYPE_VARINT);
    try {
      return valueCompleted(Short.valueOf((short) this.in.readSInt32()));
    } catch (IOException e) {
      throw new RuntimeIoException(e);
    }
  }

  @Override
  public Integer readValueAsInteger() {

    expectType(WireFormat.WIRETYPE_VARINT);
    try {
      return valueCompleted(Integer.valueOf(this.in.readSInt32()));
    } catch (IOException e) {
      throw new RuntimeIoException(e);
    }
  }

  @Override
  public Long readValueAsLong() {

    expectType(WireFormat.WIRETYPE_VARINT);
    try {
      return valueCompleted(Long.valueOf(this.in.readSInt64()));
    } catch (IOException e) {
      throw new RuntimeIoException(e);
    }
  }

  @Override
  public Float readValueAsFloat() {

    expectType(WireFormat.WIRETYPE_FIXED32);
    try {
      return valueCompleted(Float.valueOf(this.in.readFloat()));
    } catch (IOException e) {
      throw new RuntimeIoException(e);
    }
  }

  @Override
  public Double readValueAsDouble() {

    expectType(WireFormat.WIRETYPE_FIXED64);
    try {
      return valueCompleted(Double.valueOf(this.in.readDouble()));
    } catch (IOException e) {
      throw new RuntimeIoException(e);
    }
  }

  @Override
  protected <N extends Number> N readValueAsNumber(NumberType<N> numberType) {

    // for BigInteger or BigDecimal
    String value = readValueAsString();
    if (value == null) {
      return null;
    }
    try {
      return numberType.valueOf(value);
    } catch (RuntimeException e) {
      throw error(value, numberType.getType(), e);
    }
  }

  @Override
  public void skipValue() {

    if (this.state == null) {
      this.state = State.DONE;
    } else {
      super.skipValue();
    }
  }

  @Override
  public void close() {

    this.in = null;
  }

}
