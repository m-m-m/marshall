/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.marshall.jsonp.impl;

import java.math.BigDecimal;
import java.math.BigInteger;

import javax.json.stream.JsonGenerator;

import io.github.mmm.marshall.AbstractStructuredWriter;
import io.github.mmm.marshall.MarshallingConfig;
import io.github.mmm.marshall.StructuredFormat;
import io.github.mmm.marshall.StructuredWriter;

/**
 * Implementation of {@link StructuredWriter} for JSON using {@link JsonGenerator}.
 *
 * @see JsonpFormat
 *
 * @since 1.0.0
 */
public class JsonpWriter extends AbstractStructuredWriter {

  private static final long JS_NUMBER_MAX = (2L << 52) - 1;

  private static final long JS_NUMBER_MIN = -JS_NUMBER_MAX;

  private final JsonGenerator json;

  /**
   * The constructor.
   *
   * @param json the underlying {@link JsonGenerator} to write to.
   * @param format the {@link #getFormat() format}.
   * @see io.github.mmm.marshall.StructuredFormatFactory#create(String, MarshallingConfig)
   */
  public JsonpWriter(JsonGenerator json, StructuredFormat format) {

    super(format);
    this.json = json;
  }

  @Override
  public void writeStartArray(int size) {

    if (this.name == null) {
      this.json.writeStartArray();
    } else {
      this.json.writeStartArray(this.name);
      this.name = null;
    }
  }

  @Override
  public void writeStartObject(int size) {

    if (this.name == null) {
      this.json.writeStartObject();
    } else {
      this.json.writeStartObject(this.name);
      this.name = null;
    }
  }

  @Override
  public void writeEnd() {

    this.json.writeEnd();
  }

  @Override
  public void writeValueAsNull() {

    if (this.name == null) {
      this.json.writeNull();
    } else {
      if (this.writeNullValues) {
        this.json.writeNull(this.name);
        this.name = null;
      }
    }
  }

  @Override
  public void writeValueAsString(String value) {

    if (value == null) {
      writeValueAsNull();
    } else if (this.name == null) {
      this.json.write(value);
    } else {
      this.json.write(this.name, value);
      this.name = null;
    }
  }

  @Override
  public void writeValueAsBoolean(Boolean value) {

    if (value == null) {
      writeValueAsNull();
    } else {
      if (this.name == null) {
        this.json.write(value.booleanValue());
      } else {
        this.json.write(this.name, value.booleanValue());
        this.name = null;
      }
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
        if (this.name == null) {
          this.json.write(l);
        } else {
          this.json.write(this.name, l);
          this.name = null;
        }
      } else {
        writeValueAsString(value.toString());
      }
    }
  }

  @Override
  public void writeValueAsInteger(Integer value) {

    if (value == null) {
      writeValueAsNull();
    } else if (this.name == null) {
      this.json.write(value.intValue());
    } else {
      this.json.write(this.name, value.intValue());
      this.name = null;
    }
  }

  @Override
  public void writeValueAsDouble(Double value) {

    if (value == null) {
      writeValueAsNull();
    } else if (this.name == null) {
      this.json.write(value.doubleValue());
    } else {
      this.json.write(this.name, value.doubleValue());
      this.name = null;
    }
  }

  @Override
  public void writeValueAsFloat(Float value) {

    writeValueAsDouble(Double.parseDouble(Float.toString(value)));
  }

  @Override
  public void writeValueAsShort(Short value) {

    if (value == null) {
      writeValueAsNull();
    } else if (this.name == null) {
      this.json.write(value.intValue());
    } else {
      this.json.write(this.name, value.intValue());
      this.name = null;
    }
  }

  @Override
  public void writeValueAsByte(Byte value) {

    if (value == null) {
      writeValueAsNull();
    } else if (this.name == null) {
      this.json.write(value.intValue());
    } else {
      this.json.write(this.name, value.intValue());
      this.name = null;
    }
  }

  @Override
  public void close() {

    this.json.close();
  }

}
