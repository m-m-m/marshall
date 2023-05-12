/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.marshall.mrpc.impl;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import com.google.protobuf.CodedOutputStream;
import com.google.protobuf.WireFormat;

import io.github.mmm.base.exception.RuntimeIoException;
import io.github.mmm.marshall.AbstractStructuredWriter;
import io.github.mmm.marshall.StructuredFormat;
import io.github.mmm.marshall.StructuredWriter;
import io.github.mmm.marshall.spi.StructuredNodeType;

/**
 * Implementation of {@link StructuredWriter} for mRPC.
 *
 * @see MrpcFormat
 *
 * @since 1.0.0
 */
public class MrpcWriter extends AbstractStructuredWriter {

  private final CodedOutputStream out;

  private final List<Object> array;

  private int arrayItemType;

  private MrpcWriteState state;

  private int id;

  /**
   * The constructor.
   *
   * @param out the {@link Appendable} to write the data to.
   * @param format the {@link #getFormat()}.
   */
  public MrpcWriter(CodedOutputStream out, StructuredFormat format) {

    super(format);
    this.out = out;
    this.state = new MrpcWriteState();
    this.array = new ArrayList<>();
    this.arrayItemType = -1;
  }

  @Override
  public void writeName(String newName, int newId) {

    super.writeName(newName, newId);
    this.id = this.state.id(newId);
  }

  @Override
  public void writeStartArray(int size) {

    writeStart(MrpcFormat.TYPE_START_ARRAY, StructuredNodeType.ARRAY);
  }

  @Override
  public void writeStartObject(int size) {

    writeStart(MrpcFormat.TYPE_START_OBJECT, StructuredNodeType.OBJECT);
  }

  private void writeStart(int type, StructuredNodeType nodeType) {

    try {
      writeArrayBuffer();
      this.out.writeTag(this.id, type);
      this.state = new MrpcWriteState(nodeType, this.state);
      clearProperty();
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
  }

  @Override
  public void writeEnd() {

    try {
      writeArrayBuffer();
      this.out.writeTag(0, MrpcFormat.TYPE_END);
      this.state = this.state.parent;
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
  }

  private void clearProperty() {

    this.name = null;
    this.id = 0;
  }

  @Override
  public void writeValueAsNull() {

    // in mRPC null values are omitted except in arrays...
    if (this.state.type == StructuredNodeType.ARRAY) {
      writeRawNull();
    }
    clearProperty();
  }

  private void writeArrayItem(Object item, int type) {

    try {
      if (item == null) {
        writeRawNull();
      } else if (this.arrayItemType == -1) {
        assert (this.array.isEmpty());
        this.arrayItemType = type;
        this.array.add(item);
      } else if (this.arrayItemType == type) {
        assert (!this.array.isEmpty());
        this.array.add(item);
      } else {
        writeArrayBuffer();
        this.arrayItemType = type;
        this.array.add(item);
      }
    } catch (IOException e) {
      throw new RuntimeIoException(e);
    }
  }

  private void writeArrayBuffer() throws IOException {

    int size = this.array.size();
    if (size == 0) {
      return;
    }
    this.out.writeTag(size, this.arrayItemType);
    for (Object arrayItem : this.array) {
      writeRawObject(arrayItem);
    }
    this.array.clear();
    this.arrayItemType = -1;
  }

  private void writeRawNull() {

    try {
      this.out.write((byte) 0);
    } catch (IOException e) {
      throw new RuntimeIoException(e);
    }
  }

  private void writeRawObject(Object value) {

    if (value instanceof String) {
      writeRawString((String) value);
    } else if (value instanceof Number) {
      writeRawNumber((Number) value);
    } else if (value instanceof Boolean) {
      writeRawBoolean((Boolean) value);
    } else if (value instanceof Enum) {

    }
  }

  private void writeRawNumber(Number value) {

    if (value instanceof Integer) {
      writeRawInteger((Integer) value);
    } else if (value instanceof Long) {
      writeRawLong((Long) value);
    } else if (value instanceof Double) {
      writeRawDouble((Double) value);
    } else if (value instanceof Float) {
      writeRawFloat((Float) value);
    } else if (value instanceof Byte) {
      writeRawByte((Byte) value);
    } else if (value instanceof Short) {
      writeRawShort((Short) value);
    }
  }

  @Override
  public void writeValueAsString(String value) {

    if (this.state.type == StructuredNodeType.ARRAY) {
      writeArrayItem(value, WireFormat.WIRETYPE_LENGTH_DELIMITED);
    } else if (value == null) {
      writeValueAsNull();
    } else {
      writeRawString(value);
      clearProperty();
    }
  }

  private void writeRawString(String value) {

    try {
      if (this.id == 0) {
        this.out.writeStringNoTag(value);
      } else {
        this.out.writeString(this.id, value);
      }
    } catch (IOException e) {
      throw new RuntimeIoException(e);
    }
  }

  @Override
  public void writeValueAsBoolean(Boolean value) {

    if (this.state.type == StructuredNodeType.ARRAY) {
      writeArrayItem(value, WireFormat.WIRETYPE_VARINT);
    } else if (value == null) {
      writeValueAsNull();
    } else {
      writeRawBoolean(value);
      clearProperty();
    }
  }

  private void writeRawBoolean(Boolean value) {

    try {
      if (this.id == 0) {
        this.out.writeBoolNoTag(value.booleanValue());
      } else {
        this.out.writeBool(this.id, value.booleanValue());
      }
    } catch (IOException e) {
      throw new RuntimeIoException(e);
    }
  }

  @Override
  public void writeValueAsBigDecimal(BigDecimal value) {

    if (value == null) {
      writeValueAsNull();
    } else {
      // TODO represent more efficiently
      writeValueAsString(value.toString());
    }
  }

  @Override
  public void writeValueAsBigInteger(BigInteger value) {

    if (value == null) {
      writeValueAsNull();
    } else {
      // TODO represent more efficiently
      writeValueAsString(value.toString());
    }
  }

  @Override
  public void writeValueAsLong(Long value) {

    if (this.state.type == StructuredNodeType.ARRAY) {
      writeArrayItem(value, WireFormat.WIRETYPE_VARINT);
    } else if (value == null) {
      writeValueAsNull();
    } else {
      writeRawLong(value);
      clearProperty();
    }
  }

  private void writeRawLong(Long value) {

    try {
      if (this.id == 0) {
        this.out.writeSInt64NoTag(value.longValue());
      } else {
        this.out.writeSInt64(this.id, value.longValue());
      }
    } catch (IOException e) {
      throw new RuntimeIoException(e);
    }
  }

  @Override
  public void writeValueAsInteger(Integer value) {

    if (this.state.type == StructuredNodeType.ARRAY) {
      writeArrayItem(value, WireFormat.WIRETYPE_VARINT);
    } else if (value == null) {
      writeValueAsNull();
    } else {
      writeRawInteger(value);
      clearProperty();
    }
  }

  private void writeRawInteger(Integer value) {

    try {
      if (this.id == 0) {
        this.out.writeSInt32NoTag(value.intValue());
      } else {
        this.out.writeSInt32(this.id, value.intValue());
      }
    } catch (IOException e) {
      throw new RuntimeIoException(e);
    }
  }

  @Override
  public void writeValueAsDouble(Double value) {

    if (this.state.type == StructuredNodeType.ARRAY) {
      writeArrayItem(value, WireFormat.WIRETYPE_FIXED64);
    } else if (value == null) {
      writeValueAsNull();
    } else {
      writeRawDouble(value);
      clearProperty();
    }
  }

  private void writeRawDouble(Double value) {

    try {
      if (this.id == 0) {
        this.out.writeDoubleNoTag(value.doubleValue());
      } else {
        this.out.writeDouble(this.id, value.doubleValue());
      }
    } catch (IOException e) {
      throw new RuntimeIoException(e);
    }
  }

  @Override
  public void writeValueAsFloat(Float value) {

    if (this.state.type == StructuredNodeType.ARRAY) {
      writeArrayItem(value, WireFormat.WIRETYPE_FIXED32);
    } else if (value == null) {
      writeValueAsNull();
    } else {
      writeRawFloat(value);
      clearProperty();
    }
  }

  private void writeRawFloat(Float value) {

    try {
      if (this.id == 0) {
        this.out.writeFloatNoTag(value.floatValue());
      } else {
        this.out.writeFloat(this.id, value.floatValue());
      }
    } catch (IOException e) {
      throw new RuntimeIoException(e);
    }
  }

  @Override
  public void writeValueAsShort(Short value) {

    if (this.state.type == StructuredNodeType.ARRAY) {
      writeArrayItem(value, WireFormat.WIRETYPE_VARINT);
    } else if (value == null) {
      writeValueAsNull();
    } else {
      writeRawShort(value);
      clearProperty();
    }
  }

  private void writeRawShort(Short value) {

    try {
      if (this.id == 0) {
        this.out.writeSInt32NoTag(value.intValue());
      } else {
        this.out.writeSInt32(this.id, value.intValue());
      }
    } catch (IOException e) {
      throw new RuntimeIoException(e);
    }
  }

  @Override
  public void writeValueAsByte(Byte value) {

    if (this.state.type == StructuredNodeType.ARRAY) {
      writeArrayItem(value, WireFormat.WIRETYPE_VARINT);
    } else if (value == null) {
      writeValueAsNull();
    } else {
      writeRawByte(value);
      clearProperty();
    }
  }

  private void writeRawByte(Byte value) {

    try {
      if (this.id == 0) {
        this.out.writeSInt32NoTag(value.intValue());
      } else {
        this.out.writeSInt32(this.id, value.intValue());
      }
    } catch (IOException e) {
      throw new RuntimeIoException(e);
    }
  }

  @Override
  public void close() {

    if (this.state != null) {
      try {
        this.out.flush();
      } catch (IOException e) {
        throw new RuntimeIoException(e);
      }
    }
    this.state = null;
  }

}
