/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.marshall.jsonp.impl;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;

import javax.json.stream.JsonGenerator;

import io.github.mmm.marshall.MarshallingConfig;
import io.github.mmm.marshall.StructuredFormat;
import io.github.mmm.marshall.StructuredState;
import io.github.mmm.marshall.StructuredWriter;
import io.github.mmm.marshall.id.StructuredIdMappingObject;
import io.github.mmm.marshall.spi.AbstractStructuredWriter;
import io.github.mmm.marshall.spi.StructuredNodeDefault;
import io.github.mmm.marshall.spi.StructuredNodeType;

/**
 * Implementation of {@link StructuredWriter} for JSON using {@link JsonGenerator}.
 *
 * @see JsonpFormat
 *
 * @since 1.0.0
 */
public class JsonpWriter extends AbstractStructuredWriter<StructuredNodeDefault> {

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
  protected StructuredNodeDefault newNode(StructuredNodeType type, StructuredIdMappingObject object) {

    return new StructuredNodeDefault(this.node, type);
  }

  @Override
  protected void doWriteStart(StructuredNodeType type, StructuredIdMappingObject object) {

    if (type == StructuredNodeType.ARRAY) {
      if (this.name == null) {
        this.json.writeStartArray();
      } else {
        this.json.writeStartArray(this.name);
        this.name = null;
      }
    } else {
      if (this.name == null) {
        this.json.writeStartObject();
      } else {
        this.json.writeStartObject(this.name);
        this.name = null;
      }
    }
  }

  @Override
  protected void doWriteEnd(StructuredNodeType type) {

    this.json.writeEnd();
  }

  @Override
  public void writeValueAsNull() {

    if (this.name == null) {
      this.json.writeNull();
      setState(StructuredState.VALUE);
    } else {
      if (this.writeNullValues) {
        this.json.writeNull(this.name);
        this.name = null;
        setState(StructuredState.VALUE);
      }
    }
  }

  @Override
  public void writeValueAsString(String value) {

    if (value == null) {
      writeValueAsNull();
    } else if (this.name == null) {
      this.json.write(value);
      setState(StructuredState.VALUE);
    } else {
      this.json.write(this.name, value);
      this.name = null;
      setState(StructuredState.VALUE);
    }
  }

  @Override
  public void writeValueAsBoolean(boolean value) {

    if (this.name == null) {
      this.json.write(value);
    } else {
      this.json.write(this.name, value);
      this.name = null;
    }
    setState(StructuredState.VALUE);
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
  public void writeValueAsLong(long value) {

    // TODO make this feature configurable
    if ((value >= JS_NUMBER_MIN) && (value <= JS_NUMBER_MAX)) {
      if (this.name == null) {
        this.json.write(value);
      } else {
        this.json.write(this.name, value);
        this.name = null;
      }
      setState(StructuredState.VALUE);
    } else {
      writeValueAsString(Long.toString(value));
    }
  }

  @Override
  public void writeValueAsInteger(int value) {

    if (this.name == null) {
      this.json.write(value);
      setState(StructuredState.VALUE);
    } else {
      this.json.write(this.name, value);
      this.name = null;
      setState(StructuredState.VALUE);
    }
  }

  @Override
  public void writeValueAsDouble(double value) {

    if (this.name == null) {
      this.json.write(value);
      setState(StructuredState.VALUE);
    } else {
      this.json.write(this.name, value);
      this.name = null;
      setState(StructuredState.VALUE);
    }
  }

  @Override
  public void writeValueAsFloat(float value) {

    // TODO why???
    writeValueAsDouble(Double.parseDouble(Float.toString(value)));
  }

  @Override
  protected void doClose() throws IOException {

    this.json.close();
  }

}
