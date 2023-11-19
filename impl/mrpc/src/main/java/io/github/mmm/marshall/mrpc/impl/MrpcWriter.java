/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.marshall.mrpc.impl;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.google.protobuf.CodedOutputStream;
import com.google.protobuf.WireFormat;

import io.github.mmm.base.exception.RuntimeIoException;
import io.github.mmm.marshall.StructuredState;
import io.github.mmm.marshall.StructuredWriter;
import io.github.mmm.marshall.id.StructuredIdMapping;
import io.github.mmm.marshall.id.StructuredIdMappingObject;
import io.github.mmm.marshall.spi.AbstractStructuredBinaryWriter;
import io.github.mmm.marshall.spi.StructuredNodeType;

/**
 * Implementation of {@link StructuredWriter} for mRPC.
 *
 * @see MrpcFormat
 *
 * @since 1.0.0
 */
public class MrpcWriter extends AbstractStructuredBinaryWriter<MrpcNode> {

  private final CodedOutputStream out;

  private final List<Object> array;

  private int arrayItemType;

  private int id;

  /**
   * The constructor.
   *
   * @param out the {@link CodedOutputStream} to write the data to.
   * @param format the {@link #getFormat()}.
   */
  public MrpcWriter(CodedOutputStream out, MrpcFormat format) {

    super(format);
    this.out = out;
    this.array = new ArrayList<>();
    this.arrayItemType = -1;
  }

  @Override
  protected MrpcNode newNode(StructuredNodeType type, StructuredIdMappingObject object) {

    StructuredIdMapping idMapping = null;
    if (type == StructuredNodeType.OBJECT) {
      idMapping = this.idMappingProvider.getMapping(object);
      Objects.requireNonNull(idMapping);
    }
    return new MrpcNode(this.node, type, idMapping);
  }

  @Override
  public void writeName(String newName) {

    super.writeName(newName);
    this.id = this.node.idMapping.id(this.name);
    assert (this.id > 0);
  }

  @Override
  protected void doWriteStart(StructuredNodeType type, StructuredIdMappingObject object) {

    try {
      writeArrayBuffer();
      if (type == StructuredNodeType.ARRAY) {
        this.out.writeTag(this.id, MrpcFormat.TYPE_START_ARRAY);
      } else if (type == StructuredNodeType.OBJECT) {
        if ((this.node.parent != null) || this.encodeRootObject) {
          this.out.writeTag(this.id, MrpcFormat.TYPE_START_OBJECT);
        }
      }
      clearProperty();
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
  }

  @Override
  protected void doWriteEnd(StructuredNodeType type) {

    try {
      writeArrayBuffer();
      if ((type != StructuredNodeType.OBJECT) || ((this.node.parent != null) && (this.node.parent.parent != null))
          || this.encodeRootObject) {
        this.out.writeTag(0, MrpcFormat.TYPE_END);
      }
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
    if (this.node.type == StructuredNodeType.ARRAY) {
      writeRawNull();
    }
    clearProperty();
  }

  private void writeArrayItem(Object item, int type) {

    try {
      if (item == null) {
        writeRawNull();
        return;
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
      setState(StructuredState.VALUE);
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
      setState(StructuredState.VALUE);
    } catch (IOException e) {
      throw new RuntimeIoException(e);
    }
  }

  private void writeRawObject(Object value) {

    if (value instanceof String) {
      writeRawString((String) value);
    } else if (value instanceof Number) {
      writeRawNumber((Number) value);
    } else if (value instanceof Boolean b) {
      writeRawBoolean(b.booleanValue());
    } else if (value instanceof Enum e) {
      writeRawInteger(e.ordinal());
    }
  }

  private void writeRawNumber(Number value) {

    if (value instanceof Integer) {
      writeRawInteger(value.intValue());
    } else if (value instanceof Long) {
      writeRawLong(value.longValue());
    } else if (value instanceof Double) {
      writeRawDouble(value.doubleValue());
    } else if (value instanceof Float) {
      writeRawFloat(value.floatValue());
    } else if (value instanceof Byte) {
      writeRawInteger(value.intValue());
    } else if (value instanceof Short) {
      writeRawInteger(value.intValue());
    }
  }

  @Override
  public void writeValueAsString(String value) {

    if (this.node.type == StructuredNodeType.ARRAY) {
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
      setState(StructuredState.VALUE);
    } catch (IOException e) {
      throw new RuntimeIoException(e);
    }
  }

  @Override
  public void writeValueAsBoolean(boolean value) {

    if (this.node.type == StructuredNodeType.ARRAY) {
      writeArrayItem(Boolean.valueOf(value), WireFormat.WIRETYPE_VARINT);
    } else {
      writeRawBoolean(value);
      clearProperty();
    }
  }

  private void writeRawBoolean(boolean value) {

    try {
      if (this.id == 0) {
        this.out.writeBoolNoTag(value);
      } else {
        this.out.writeBool(this.id, value);
      }
      setState(StructuredState.VALUE);
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
  public void writeValueAsLong(long value) {

    if (this.node.type == StructuredNodeType.ARRAY) {
      writeArrayItem(Long.valueOf(value), WireFormat.WIRETYPE_VARINT);
    } else {
      writeRawLong(value);
      clearProperty();
    }
  }

  private void writeRawLong(long value) {

    try {
      if (this.id == 0) {
        this.out.writeSInt64NoTag(value);
      } else {
        this.out.writeSInt64(this.id, value);
      }
      setState(StructuredState.VALUE);
    } catch (IOException e) {
      throw new RuntimeIoException(e);
    }
  }

  @Override
  public void writeValueAsInteger(int value) {

    if (this.node.type == StructuredNodeType.ARRAY) {
      writeArrayItem(Integer.valueOf(value), WireFormat.WIRETYPE_VARINT);
    } else {
      writeRawInteger(value);
      clearProperty();
    }
  }

  private void writeRawInteger(int value) {

    try {
      if (this.id == 0) {
        this.out.writeSInt32NoTag(value);
      } else {
        this.out.writeSInt32(this.id, value);
      }
      setState(StructuredState.VALUE);
    } catch (IOException e) {
      throw new RuntimeIoException(e);
    }
  }

  @Override
  public void writeValueAsDouble(double value) {

    if (this.node.type == StructuredNodeType.ARRAY) {
      writeArrayItem(Double.valueOf(value), WireFormat.WIRETYPE_FIXED64);
    } else {
      writeRawDouble(value);
      clearProperty();
    }
  }

  private void writeRawDouble(double value) {

    try {
      if (this.id == 0) {
        this.out.writeDoubleNoTag(value);
      } else {
        this.out.writeDouble(this.id, value);
      }
      setState(StructuredState.VALUE);
    } catch (IOException e) {
      throw new RuntimeIoException(e);
    }
  }

  @Override
  public void writeValueAsFloat(float value) {

    if (this.node.type == StructuredNodeType.ARRAY) {
      writeArrayItem(Float.valueOf(value), WireFormat.WIRETYPE_FIXED32);
    } else {
      writeRawFloat(value);
      clearProperty();
    }
  }

  private void writeRawFloat(float value) {

    try {
      if (this.id == 0) {
        this.out.writeFloatNoTag(value);
      } else {
        this.out.writeFloat(this.id, value);
      }
      setState(StructuredState.VALUE);
    } catch (IOException e) {
      throw new RuntimeIoException(e);
    }
  }

  @Override
  protected void doClose() throws IOException {

    try {
      this.out.flush();
    } catch (IOException e) {
      throw new RuntimeIoException(e);
    }
  }

}
