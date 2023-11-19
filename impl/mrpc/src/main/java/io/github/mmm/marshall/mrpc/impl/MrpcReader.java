/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.marshall.mrpc.impl;

import java.io.IOException;
import java.io.InputStream;

import com.google.protobuf.CodedInputStream;
import com.google.protobuf.WireFormat;

import io.github.mmm.base.exception.RuntimeIoException;
import io.github.mmm.base.number.NumberType;
import io.github.mmm.marshall.StructuredReader;
import io.github.mmm.marshall.StructuredState;
import io.github.mmm.marshall.id.StructuredIdMappingObject;
import io.github.mmm.marshall.spi.AbstractStructuredBinaryReader;
import io.github.mmm.marshall.spi.StructuredNodeType;

/**
 * Implementation of {@link StructuredReader} for mRPC.
 *
 * @see MrpcFormat
 *
 * @since 1.0.0
 */
public class MrpcReader extends AbstractStructuredBinaryReader<MrpcNode> {

  private static final int TYPE_NONE = -1;

  private static final int TYPE_NULL = -2;

  private CodedInputStream in;

  private InputStream is;

  private int tag;

  private int wireType;

  private int arrayItemCount;

  /**
   * The constructor.
   *
   * @param is the {@link InputStream} with the ProtoBuf content to parse.
   * @param format the {@link #getFormat() format}.
   */
  public MrpcReader(InputStream is, MrpcFormat format) {

    super(format);
    this.is = is;
    // CodedInputStream does not let us peek a single lookahead byte without consuming it
    this.in = CodedInputStream.newInstance(is);
    this.wireType = TYPE_NONE;
  }

  @Override
  protected MrpcNode newNode(StructuredNodeType type, StructuredIdMappingObject object) {

    return new MrpcNode(this.node, type, null);
  }

  @Override
  protected StructuredState next(boolean skip) {

    int skipCount = skip ? 1 : 0;
    StructuredState state = getState();
    boolean todo;
    do {
      todo = false;
      int skipAdd = 0;
      if (state == StructuredState.NAME) {
        switch (this.wireType) {
          case MrpcFormat.TYPE_START_OBJECT:
            state = start(StructuredNodeType.OBJECT);
            break;
          case MrpcFormat.TYPE_START_ARRAY:
            state = start(StructuredNodeType.ARRAY);
            break;
          case WireFormat.WIRETYPE_LENGTH_DELIMITED:
            state = setState(StructuredState.VALUE);
            break;
          case MrpcFormat.TYPE_END:
            end(null);
            // this.name = null;
            // this.id = 0;
            break;
          default:
            state = setState(StructuredState.VALUE);
        }
      } else if ((state == StructuredState.END_OBJECT) && (this.node.parent == null)) {
        state = setState(StructuredState.DONE);
      } else if (state == StructuredState.VALUE) {
        readValue(); // could be optimized but doing KISS
        state = getState();
      } else if (this.arrayItemCount == 0) {
        skipAdd = readTag(skipCount);
        state = getState();
      }
      if (skipCount > 0) {
        skipCount += skipAdd;
        if (skipCount == 0) {
          todo = true;
        }
      }
    } while ((skipCount > 0) || todo);
    return state;
  }

  private int readTag(int skipCount) {

    try {
      if (this.in.isAtEnd()) {
        if (!this.encodeRootObject && this.node.isTopObject()) {
          end(StructuredNodeType.OBJECT);
          return -1;
        }
        setState(StructuredState.DONE);
        return -1;
      }
      this.tag = this.in.readRawVarint32();
      if (this.tag == 0) {
        // this.name = null;
        if (this.node.type == StructuredNodeType.ARRAY) {
          this.wireType = TYPE_NULL;
          setState(StructuredState.VALUE);
          this.arrayItemCount = 1;
        } else {
          this.wireType = TYPE_NONE;
          setState(StructuredState.DONE);
        }
      } else {
        this.wireType = WireFormat.getTagWireType(this.tag);
        int fieldNumber = WireFormat.getTagFieldNumber(this.tag);
        if (this.node.type == StructuredNodeType.ARRAY) {
          this.arrayItemCount = fieldNumber;
        } else if (fieldNumber > 0) {
          if (skipCount == 0) {
            this.name = this.node.idMapping.name(fieldNumber);
            assert (this.name != null);
          }
        }
        if (fieldNumber == 0) {
          if (this.wireType == MrpcFormat.TYPE_START_OBJECT) {
            start(StructuredNodeType.OBJECT);
            return 1;
          } else if (this.wireType == MrpcFormat.TYPE_START_ARRAY) {
            start(StructuredNodeType.ARRAY);
            return 1;
          }
        } else {
          if (this.node.type == StructuredNodeType.ARRAY) {
            setState(StructuredState.VALUE);
          } else {
            setState(StructuredState.NAME);
          }
        }
        if (this.wireType == MrpcFormat.TYPE_END) {
          assert (this.arrayItemCount == 0);
          if (this.node.type == null) {
            setState(StructuredState.DONE);
          } else {
            setState(this.node.type.getEnd());
          }
          this.node = this.node.parent;
          return -1;
        }
      }
    } catch (IOException e) {
      throw new RuntimeIoException(e);
    }
    return 0;
  }

  @Override
  public boolean readStartObject(StructuredIdMappingObject object) {

    StructuredState state = getState();
    if (state == StructuredState.NULL) {
      if (this.node.isRoot() && !this.encodeRootObject) {
        state = start(StructuredNodeType.OBJECT); // trust caller
      } else {
        readTag(0);
        state = getState();
      }
    }
    if (state == StructuredState.START_OBJECT) {
      assert (this.node.idMapping == null);
      this.node.idMapping = this.idMappingProvider.getMapping(object);
      assert (this.node.idMapping != null);
      next();
      return true;
    }
    return false;
  }

  @Override
  public void specializeObject(StructuredIdMappingObject object) {

    super.specializeObject(object);
    this.node.idMapping = this.idMappingProvider.getMapping(object);
    assert (this.node.idMapping != null);
  }

  @Override
  public boolean readStartArray() {

    if (getState() == StructuredState.NULL) {
      readTag(0);
    }
    return super.readStartArray();
  }

  private <V> V valueCompleted(V value) {

    boolean hasArrayValue = false;
    if (this.arrayItemCount > 0) {
      this.arrayItemCount--;
      hasArrayValue = (this.arrayItemCount > 0);
    } else {
      this.tag = 0;
      // this.id = 0;
      this.wireType = TYPE_NONE;
    }
    if (!hasArrayValue) {
      setState(StructuredState.NULL);
      next();
    }
    return value;
  }

  @Override
  public boolean isStringValue() {

    return this.wireType == WireFormat.WIRETYPE_LENGTH_DELIMITED;
  }

  @Override
  public Object readValue() {

    require(StructuredState.VALUE);
    switch (this.wireType) {
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
        throw error("Unknown wire type: " + this.wireType);
    }
  }

  private void expectType(int type) {

    if ((this.wireType != type) && (this.wireType != -1)) {
      error("Expected wire type is " + type + " but actual type is " + this.wireType);
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
      if (this.wireType == WireFormat.WIRETYPE_LENGTH_DELIMITED) {
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

    if (getState() == StructuredState.NULL) {
      setState(StructuredState.DONE);
    } else {
      super.skipValue();
    }
  }

  @Override
  protected void doClose() throws IOException {

    this.is.close();
    this.is = null;
    this.in = null;
  }

}
