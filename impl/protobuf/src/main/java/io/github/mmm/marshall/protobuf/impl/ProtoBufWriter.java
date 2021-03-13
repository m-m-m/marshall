/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.marshall.protobuf.impl;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;

import com.google.protobuf.CodedOutputStream;

import io.github.mmm.base.exception.RuntimeIoException;
import io.github.mmm.marshall.AbstractStructuredWriter;
import io.github.mmm.marshall.StructuredFormat;
import io.github.mmm.marshall.StructuredWriter;

/**
 * Implementation of {@link StructuredWriter} for ProtoBuf/gRPC.
 *
 * @see ProtoBufFormat
 *
 * @since 1.0.0
 */
public class ProtoBufWriter extends AbstractStructuredWriter {

  private static final Byte ZERO_BYTE = Byte.valueOf((byte) 0);

  private ProtoBufOut writer;

  private int id;

  /**
   * The constructor.
   *
   * @param out the {@link Appendable} to write the data to.
   * @param format the {@link #getFormat()}.
   */
  public ProtoBufWriter(CodedOutputStream out, StructuredFormat format) {

    super(format);
    this.writer = new ProtoBufOut(out);
  }

  @Override
  public void writeName(String newName, int newId) {

    super.writeName(newName, newId);
    this.id = this.writer.id(newId);
  }

  @Override
  public void writeStartArray(int size) {

    this.writer = this.writer.startArray(this.id, size);
    clearProperty();
  }

  @Override
  public void writeStartObject(int size) {

    this.writer = this.writer.startObject(this.id, size);
    clearProperty();
  }

  @Override
  public void writeEnd() {

    this.writer = this.writer.end();
  }

  private void clearProperty() {

    this.name = null;
    this.id = 0;
  }

  @Override
  public void writeValueAsNull() {

    // in gRPC null values are simply omitted...
  }

  private void writeValueAsNull(Object defaultValue) {

    if (this.id == 0) {
      assert (defaultValue != null);
      writeValue(defaultValue);
    } else {
      writeValueAsNull();
    }
  }

  @Override
  public void writeValueAsString(String value) {

    try {
      if (value == null) {
        writeValueAsNull("");
      } else {
        if (this.id == 0) {
          this.writer.out.writeStringNoTag(value);
        } else {
          this.writer.out.writeString(this.id, value);
        }
        clearProperty();
      }
    } catch (IOException e) {
      throw new RuntimeIoException(e);
    }
  }

  @Override
  public void writeValueAsBoolean(Boolean value) {

    try {
      if (value == null) {
        writeValueAsNull(Boolean.FALSE);
      } else {
        if (this.id == 0) {
          this.writer.out.writeBoolNoTag(value.booleanValue());
        } else {
          this.writer.out.writeBool(this.id, value.booleanValue());
        }
        clearProperty();
      }
    } catch (IOException e) {
      throw new RuntimeIoException(e);
    }
  }

  @Override
  public void writeValueAsBigDecimal(BigDecimal value) {

    if (value == null) {
      writeValueAsNull(BigDecimal.ZERO);
    } else {
      // could be written more efficiently, but interoperability is also important
      writeValueAsString(value.toString());
    }
  }

  @Override
  public void writeValueAsBigInteger(BigInteger value) {

    if (value == null) {
      writeValueAsNull(BigInteger.ZERO);
    } else {
      // could be written more efficiently, but interoperability is also important
      writeValueAsString(value.toString());
    }
  }

  @Override
  public void writeValueAsLong(Long value) {

    if (value == null) {
      writeValueAsNull(Long.valueOf(0));
    } else {
      try {
        if (this.id == 0) {
          this.writer.out.writeSInt64NoTag(value.longValue());
        } else {
          this.writer.out.writeSInt64(this.id, value.longValue());
        }
      } catch (IOException e) {
        throw new RuntimeIoException(e);
      }
      clearProperty();
    }
  }

  @Override
  public void writeValueAsInteger(Integer value) {

    if (value == null) {
      writeValueAsNull(Integer.valueOf(0));
    } else {
      try {
        if (this.id == 0) {
          this.writer.out.writeSInt32NoTag(value.intValue());
        } else {
          this.writer.out.writeSInt32(this.id, value.intValue());
        }
      } catch (IOException e) {
        throw new RuntimeIoException(e);
      }
      clearProperty();
    }
  }

  @Override
  public void writeValueAsDouble(Double value) {

    if (value == null) {
      writeValueAsNull(Double.valueOf(0));
    } else {
      try {
        if (this.id == 0) {
          this.writer.out.writeDoubleNoTag(value.doubleValue());
        } else {
          this.writer.out.writeDouble(this.id, value.doubleValue());
        }
      } catch (IOException e) {
        throw new RuntimeIoException(e);
      }
      clearProperty();
    }
  }

  @Override
  public void writeValueAsFloat(Float value) {

    if (value == null) {
      writeValueAsNull(Float.valueOf(0));
    } else {
      try {
        if (this.id == 0) {
          this.writer.out.writeFloatNoTag(value.floatValue());
        } else {
          this.writer.out.writeFloat(this.id, value.floatValue());
        }
      } catch (IOException e) {
        throw new RuntimeIoException(e);
      }
      clearProperty();
    }
  }

  @Override
  public void writeValueAsShort(Short value) {

    if (value == null) {
      writeValueAsNull(Short.valueOf((short) 0));
    } else {
      try {
        if (this.id == 0) {
          this.writer.out.writeSInt32NoTag(value.intValue());
        } else {
          this.writer.out.writeSInt32(this.id, value.intValue());
        }
      } catch (IOException e) {
        throw new RuntimeIoException(e);
      }
      clearProperty();
    }
  }

  @Override
  public void writeValueAsByte(Byte value) {

    if (value == null) {
      writeValueAsNull(ZERO_BYTE);
    } else {
      try {
        if (this.id == 0) {
          this.writer.out.writeSInt32NoTag(value.intValue());
        } else {
          this.writer.out.writeSInt32(this.id, value.intValue());
        }
      } catch (IOException e) {
        throw new RuntimeIoException(e);
      }
      clearProperty();
    }
  }

  @Override
  public void close() {

    if (this.writer != null) {
      try {
        this.writer.out.flush();
      } catch (IOException e) {
        throw new RuntimeIoException(e);
      }
    }
    this.writer = null;
  }

}
