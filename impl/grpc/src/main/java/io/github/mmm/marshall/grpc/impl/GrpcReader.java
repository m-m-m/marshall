/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.marshall.grpc.impl;

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
 * @see GrpcFormatImpl
 *
 * @since 1.0.0
 */
public class GrpcReader extends AbstractStructuredReader {

  private CodedInputStream in;

  private Object value;

  private int type;

  private int id;

  private boolean stringValue;

  /**
   * The constructor.
   *
   * @param in the {@link CodedInputStream} with the ProtoBuf content to parse.
   * @param format the {@link #getFormat() format}.
   */
  public GrpcReader(CodedInputStream in, StructuredFormat format) {

    super(format);
    this.in = in;
    this.type = -1;
    this.state = State.START_OBJECT;
  }

  @Override
  public State next() {

    try {
      this.stringValue = false;
      int tag = this.in.readTag();
      if (tag == 0) {
        this.state = State.DONE;
      } else {
        if (this.state == State.NAME) {
          // TODO: how can we differentiate between string (value), start_array, start_object ???
          // ProtoBuf sucks and is flawed!
          this.state = State.VALUE;
        }
        if ((this.state == State.START_OBJECT) || (this.state == State.VALUE)) {
          this.state = State.NAME;
          this.type = WireFormat.getTagWireType(tag);
          this.id = WireFormat.getTagFieldNumber(tag);
        } else {
          // TODO
        }
      }
      return this.state;
    } catch (IOException e) {
      throw new RuntimeIoException(e);
    }
  }

  @Override
  public String getName() {

    throw new UnsupportedOperationException();
  }

  @Override
  public int getId() {

    return this.id;
  }

  @Override
  public boolean isStringValue() {

    return this.stringValue;
  }

  @Override
  public Object readValue() {

    expect(State.VALUE);
    Object v = this.value;
    this.value = null;
    next();
    return v;
  }

  @Override
  public String readValueAsString() {

    Object v = readValue();
    if (v == null) {
      return null;
    }
    return v.toString();
  }

  @Override
  public Boolean readValueAsBoolean() {

    Object v = readValue();
    if (v == null) {
      return null;
    } else if (v instanceof Boolean) {
      return (Boolean) v;
    } else {
      throw new IllegalArgumentException("Value of type " + v.getClass().getName() + " can not be read as boolean!");
    }
  }

  @Override
  protected String readValueAsNumberString() {

    Object v = readValue();
    if (v == null) {
      return null;
    } else if (v instanceof String) {
      return (String) v;
    } else if (v instanceof Number) {
      return v.toString();
    } else {
      throw new IllegalArgumentException("Value of type " + v.getClass().getName() + " can not be read as number!");
    }
  }

  @Override
  public void close() {

    this.in = null;
  }

}
