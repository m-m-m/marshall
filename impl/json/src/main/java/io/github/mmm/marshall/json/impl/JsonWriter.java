/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.marshall.json.impl;

import java.math.BigDecimal;
import java.math.BigInteger;

import io.github.mmm.marshall.AbstractStructuredStringWriter;
import io.github.mmm.marshall.MarshallingConfig;
import io.github.mmm.marshall.StructuredWriter;

/**
 * Implementation of {@link StructuredWriter} for JSON from scratch.
 *
 * @see JsonFormatImpl
 *
 * @since 1.0.0
 */
public class JsonWriter extends AbstractStructuredStringWriter {

  private static final long JS_NUMBER_MAX = (2L << 52) - 1;

  private static final long JS_NUMBER_MIN = -JS_NUMBER_MAX;

  private JsonState jsonState;

  /**
   * The constructor.
   *
   * @param out the {@link Appendable} to write the data to.
   * @param config the {@link MarshallingConfig}.
   * @see io.github.mmm.marshall.StructuredFormatFactory#create(String, MarshallingConfig)
   */
  public JsonWriter(Appendable out, MarshallingConfig config) {

    super(out, config);
    this.jsonState = new JsonState();
  }

  @Override
  public void writeStartArray() {

    writeStart(JsonNodeType.ARRAY);
  }

  @Override
  public void writeStartObject() {

    writeStart(JsonNodeType.OBJECT);
  }

  private void writeStart(JsonNodeType type) {

    if (this.jsonState.valueCount > 0) {
      write(',');
      writeIndent();
    }
    writeName();
    write(type.getOpen());
    this.jsonState = new JsonState(this.jsonState, type);
    this.indentCount++;
    // writeIndent();
  }

  private void writeName() {

    this.jsonState.valueCount++;
    if (this.name == null) {
      return;
    }
    write('"');
    write(this.name);
    if (this.indentation == null) {
      write("\":");
    } else {
      write("\": ");
    }
    this.name = null;
  }

  @Override
  public void writeEnd() {

    if ((this.jsonState.type != null) && (this.jsonState.parent != null)) {
      this.indentCount--;
      if (this.jsonState.valueCount > 0) {
        writeIndent();
      }
      write(this.jsonState.type.getClose());
      this.jsonState = this.jsonState.parent;
    } else {
      throw new IllegalStateException();
    }
  }

  @Override
  public void writeValueAsNull() {

    writeValueInternal("null");
  }

  @Override
  public void writeValueAsString(String value) {

    if (value != null) {
      // escape string value
      value = '"' + value.replace("\"", "\\\"") + '"';
    }
    writeValueInternal(value);
  }

  @Override
  public void writeValueAsBoolean(Boolean value) {

    writeValueInternal(value);
  }

  private void writeValueInternal(Object value) {

    String s;
    if (value == null) {
      if (!this.writeNullValues) {
        return;
      }
      s = "null"; // JSON null representation
    } else {
      s = value.toString();
    }
    if (this.jsonState.valueCount > 0) {
      write(',');
    }
    writeIndent();
    writeName();
    write(s);
    this.jsonState.valueCount++;
  }

  @Override
  public void writeValueAsNumber(Number value) {

    if (value == null) {
      writeValueAsNull();
    } else if (value instanceof BigDecimal) {
      writeValueAsString(value.toString());
    } else if (value instanceof BigInteger) {
      writeValueAsString(value.toString());
    } else {
      writeValueInternal(value);
    }
  }

  @Override
  public void writeValueAsBigDecimal(BigDecimal value) {

    if (value == null) {
      writeValueAsNull();
    } else {
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
      long l = value.longValue();
      if ((l >= JS_NUMBER_MIN) && (l <= JS_NUMBER_MAX)) {
        writeValueInternal(value);
      } else {
        writeValueAsString(value.toString());
      }
    }
  }

  @Override
  public void writeValueAsInteger(Integer value) {

    writeValueInternal(value);
  }

  @Override
  public void writeValueAsDouble(Double value) {

    writeValueInternal(value);
  }

  @Override
  public void writeValueAsFloat(Float value) {

    writeValueAsDouble(Double.parseDouble(Float.toString(value)));
  }

  @Override
  public void writeValueAsShort(Short value) {

    writeValueInternal(value);
  }

  @Override
  public void writeValueAsByte(Byte value) {

    writeValueInternal(value);
  }

  @Override
  public void close() {

    if (this.jsonState == null) {
      return;
    }
    assert (this.jsonState.parent == null);
    this.jsonState = null;
  }

}
