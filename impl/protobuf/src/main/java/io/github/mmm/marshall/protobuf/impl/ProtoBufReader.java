/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.marshall.protobuf.impl;

import java.io.IOException;

import com.google.protobuf.CodedInputStream;
import com.google.protobuf.WireFormat;

import io.github.mmm.base.exception.RuntimeIoException;
import io.github.mmm.marshall.AbstractStructuredReader;
import io.github.mmm.marshall.StructuredFormat;
import io.github.mmm.marshall.StructuredReader;

/**
 * Implementation of {@link StructuredReader} for gRPC/ProtoBuf.
 *
 * @see ProtoBufFormat
 *
 * @since 1.0.0
 */
public class ProtoBufReader extends AbstractStructuredReader {

  private CodedInputStream in;

  private ProtoBufState grpcState;

  private int tag;

  private int type;

  private int id;

  /**
   * The constructor.
   *
   * @param in the {@link CodedInputStream} with the ProtoBuf content to parse.
   * @param format the {@link #getFormat() format}.
   */
  public ProtoBufReader(CodedInputStream in, StructuredFormat format) {

    super(format);
    this.in = in;
    this.type = -1;
    this.state = null;
    this.grpcState = new ProtoBufState();
  }

  @Override
  public State next() {

    try {
      if (this.state == State.NAME) {
        if (this.type == WireFormat.WIRETYPE_LENGTH_DELIMITED) {
          // TODO: how can we differentiate between string (value), start_array, start_object ???
          // ProtoBuf sucks and is flawed!
          this.state = State.START_OBJECT;
        } else {
          this.state = State.VALUE;
        }
      } else {
        clearTag();
        int position = this.in.getTotalBytesRead();
        if (position >= this.grpcState.end) {
          assert (position == this.grpcState.end);
          this.state = this.grpcState.getEnd();
          this.grpcState = this.grpcState.parent;
        } else if (this.grpcState.state == State.START_ARRAY) {
          this.state = State.VALUE;
        } else {
          this.tag = this.in.readTag();
          if (this.tag == 0) {
            this.state = State.DONE;
          } else {
            this.type = WireFormat.getTagWireType(this.tag);
            this.id = WireFormat.getTagFieldNumber(this.tag);
            this.state = State.NAME;
          }
        }
      }
      return this.state;
    } catch (IOException e) {
      throw new RuntimeIoException(e);
    }
  }

  @Override
  public boolean readStartObject() {

    if ((this.state == State.START_OBJECT) || (this.state == null)
        || ((this.state == State.VALUE) && (this.grpcState.state == State.START_ARRAY))) {
      try {
        int len = this.in.readRawVarint32();
        this.grpcState = this.grpcState.startObject(this.in.getTotalBytesRead() + len);
        next();
        return true;
      } catch (IOException e) {
        throw new RuntimeIoException(e);
      }
    }
    return false;
  }

  @Override
  public boolean readStartArray() {

    if ((this.type == WireFormat.WIRETYPE_LENGTH_DELIMITED) || (this.type == -1)) {
      try {
        int len = this.in.readRawVarint32();
        this.grpcState = this.grpcState.startArray(this.in.getTotalBytesRead() + len);
        next();
        return true;
      } catch (IOException e) {
        throw new RuntimeIoException(e);
      }
    }
    return false;
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

    clearTag();
    this.state = State.VALUE;
    next();
    return value;
  }

  private void clearTag() {

    this.tag = 0;
    this.id = 0;
    this.type = -1;
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
      default:
        throw new IllegalStateException("Unknown wire type: " + this.type);
    }
  }

  private void expect(int wireType) {

    if ((this.type != wireType) && (this.type != -1)) {
      throw new IllegalStateException("Expected wire type is " + wireType + " but actual type is " + this.type);
    }
  }

  @Override
  public String readValueAsString() {

    expect(WireFormat.WIRETYPE_LENGTH_DELIMITED);
    try {
      return valueCompleted(this.in.readString());
    } catch (IOException e) {
      throw new RuntimeIoException(e);
    }
  }

  @Override
  public Boolean readValueAsBoolean() {

    expect(WireFormat.WIRETYPE_VARINT);
    try {
      return valueCompleted(Boolean.valueOf(this.in.readBool()));
    } catch (IOException e) {
      throw new RuntimeIoException(e);
    }
  }

  @Override
  public Byte readValueAsByte() {

    expect(WireFormat.WIRETYPE_VARINT);
    try {
      return valueCompleted(Byte.valueOf((byte) this.in.readSInt32()));
    } catch (IOException e) {
      throw new RuntimeIoException(e);
    }
  }

  @Override
  public Short readValueAsShort() {

    expect(WireFormat.WIRETYPE_VARINT);
    try {
      return valueCompleted(Short.valueOf((short) this.in.readSInt32()));
    } catch (IOException e) {
      throw new RuntimeIoException(e);
    }
  }

  @Override
  public Integer readValueAsInteger() {

    expect(WireFormat.WIRETYPE_VARINT);
    try {
      return valueCompleted(Integer.valueOf(this.in.readSInt32()));
    } catch (IOException e) {
      throw new RuntimeIoException(e);
    }
  }

  @Override
  public Long readValueAsLong() {

    expect(WireFormat.WIRETYPE_VARINT);
    try {
      return valueCompleted(Long.valueOf(this.in.readSInt64()));
    } catch (IOException e) {
      throw new RuntimeIoException(e);
    }
  }

  @Override
  public Float readValueAsFloat() {

    expect(WireFormat.WIRETYPE_FIXED32);
    try {
      return valueCompleted(Float.valueOf(this.in.readFloat()));
    } catch (IOException e) {
      throw new RuntimeIoException(e);
    }
  }

  @Override
  public Double readValueAsDouble() {

    expect(WireFormat.WIRETYPE_FIXED64);
    try {
      return valueCompleted(Double.valueOf(this.in.readDouble()));
    } catch (IOException e) {
      throw new RuntimeIoException(e);
    }
  }

  @Override
  public void skipValue() {

    try {
      if (this.state == null) {
        this.in.skipMessage();
        this.state = State.DONE;
      } else if ((this.grpcState.state != State.START_ARRAY) && (this.tag != 0)) {
        this.in.skipField(this.tag);
        this.state = State.VALUE;
        next();
      } else {
        this.in.skipRawBytes(this.grpcState.end);
      }
    } catch (IOException e) {
      throw new RuntimeIoException(e);
    }
  }

  @Override
  public void close() {

    this.in = null;
  }

}
