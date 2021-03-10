/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.marshall.grpc.impl;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;

import com.google.protobuf.CodedOutputStream;

import io.github.mmm.base.exception.RuntimeIoException;
import io.github.mmm.marshall.AbstractStructuredWriter;
import io.github.mmm.marshall.StructuredFormat;
import io.github.mmm.marshall.StructuredWriter;

/**
 * Implementation of {@link StructuredWriter} for gRPC/ProtoBuf.
 *
 * @see GrpcFormatImpl
 *
 * @since 1.0.0
 */
public class GrpcWriter extends AbstractStructuredWriter {

  private CodedOutputStream out;

  private int id;

  private int idCounter;

  private int startCount;

  /**
   * The constructor.
   *
   * @param out the {@link Appendable} to write the data to.
   * @param format the {@link #getFormat()}.
   */
  public GrpcWriter(CodedOutputStream out, StructuredFormat format) {

    super(format);
    this.out = out;
  }

  @Override
  public void writeName(String newName, int newId) {

    super.writeName(newName, newId);
    if (newId == -1) {
      this.idCounter++;
      this.id = this.idCounter;
    } else {
      this.id = newId;
      if (this.idCounter <= newId) {
        this.idCounter = newId;
      }
    }
  }

  @Override
  public void writeStartArray() {

    // writeStart(JsonNodeType.ARRAY);
    this.startCount++;
  }

  @Override
  public void writeStartObject() {

    // writeStart(JsonNodeType.OBJECT);
    this.startCount++;
  }

  @Override
  public void writeEnd() {

    if (this.startCount <= 0) {
      throw new IllegalStateException();
    }
    this.startCount--;
  }

  private void clearProperty() {

    this.name = null;
    this.id = -1;
  }

  @Override
  public void writeValueAsNull() {

    // in gRPC null values are simply omitted...
  }

  @Override
  public void writeValueAsString(String value) {

    if (value == null) {
      writeValueAsNull();
    } else {
      try {
        this.out.writeString(this.id, value);
      } catch (IOException e) {
        throw new RuntimeIoException(e);
      }
      clearProperty();
    }
  }

  @Override
  public void writeValueAsBoolean(Boolean value) {

    if (value == null) {
      writeValueAsNull();
    } else {
      try {
        this.out.writeBool(this.id, value.booleanValue());
      } catch (IOException e) {
        throw new RuntimeIoException(e);
      }
      clearProperty();
    }
  }

  @Override
  public void writeValueAsBigDecimal(BigDecimal value) {

    if (value == null) {
      writeValueAsNull();
    } else {
      // out.writeBig
      writeValueAsString(value.toString());
    }
  }

  @Override
  public void writeValueAsBigInteger(BigInteger value) {

    if (value == null) {
      writeValueAsNull();
    } else {
      writeValueAsString(value.toString());
    }
  }

  @Override
  public void writeValueAsLong(Long value) {

    if (value == null) {
      writeValueAsNull();
    } else {
      try {
        this.out.writeSInt64(this.id, value.longValue());
      } catch (IOException e) {
        throw new RuntimeIoException(e);
      }
      clearProperty();
    }
  }

  @Override
  public void writeValueAsInteger(Integer value) {

    if (value == null) {
      writeValueAsNull();
    } else {
      try {
        this.out.writeSInt32(this.id, value.intValue());
      } catch (IOException e) {
        throw new RuntimeIoException(e);
      }
      clearProperty();
    }
  }

  @Override
  public void writeValueAsDouble(Double value) {

    if (value == null) {
      writeValueAsNull();
    } else {
      try {
        this.out.writeDouble(this.id, value.doubleValue());
      } catch (IOException e) {
        throw new RuntimeIoException(e);
      }
      clearProperty();
    }
  }

  @Override
  public void writeValueAsFloat(Float value) {

    if (value == null) {
      writeValueAsNull();
    } else {
      try {
        this.out.writeFloat(this.id, value.floatValue());
      } catch (IOException e) {
        throw new RuntimeIoException(e);
      }
      clearProperty();
    }
  }

  @Override
  public void writeValueAsShort(Short value) {

    if (value == null) {
      writeValueAsNull();
    } else {
      try {
        this.out.writeSInt32(this.id, value.intValue());
      } catch (IOException e) {
        throw new RuntimeIoException(e);
      }
      clearProperty();
    }
  }

  @Override
  public void writeValueAsByte(Byte value) {

    if (value == null) {
      writeValueAsNull();
    } else {
      try {
        this.out.writeSInt32(this.id, value.intValue());
      } catch (IOException e) {
        throw new RuntimeIoException(e);
      }
      clearProperty();
    }
  }

  @Override
  public void close() {

    this.out = null;
  }

}
