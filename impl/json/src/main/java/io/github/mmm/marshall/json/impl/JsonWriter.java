/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.marshall.json.impl;

import java.math.BigDecimal;
import java.math.BigInteger;

import io.github.mmm.marshall.AbstractStructuredStringWriter;
import io.github.mmm.marshall.MarshallingConfig;
import io.github.mmm.marshall.StructuredState;
import io.github.mmm.marshall.StructuredWriter;
import io.github.mmm.marshall.id.StructuredIdMappingObject;
import io.github.mmm.marshall.spi.StructuredNodeDefault;
import io.github.mmm.marshall.spi.StructuredNodeType;

/**
 * Implementation of {@link StructuredWriter} for JSON from scratch.
 *
 * @see JsonFormat
 *
 * @since 1.0.0
 */
public class JsonWriter extends AbstractStructuredStringWriter<StructuredNodeDefault> {

  private static final long JS_NUMBER_MAX = (2L << 52) - 1;

  private static final long JS_NUMBER_MIN = -JS_NUMBER_MAX;

  private final boolean quoteProperties;

  /**
   * The constructor.
   *
   * @param out the {@link Appendable} to write the data to.
   * @param format the {@link #getFormat() format}.
   */
  public JsonWriter(Appendable out, JsonFormat format) {

    super(out, format);
    Boolean unquotedProperties = format.getConfig().get(MarshallingConfig.VAR_UNQUOTED_PROPERTIES);
    this.quoteProperties = !Boolean.TRUE.equals(unquotedProperties);
  }

  @Override
  protected StructuredNodeDefault newNode(StructuredNodeType type, StructuredIdMappingObject object) {

    return new StructuredNodeDefault(this.node, type);
  }

  @Override
  protected void doWriteStart(StructuredNodeType type, StructuredIdMappingObject object) {

    if (this.node.elementCount > 0) {
      write(',');
    }
    writeIndent();
    writeName();
    write(type.getOpen());
  }

  private void writeName() {

    this.node.elementCount++;
    if (this.name == null) {
      return;
    }
    if (this.quoteProperties) {
      write('"');
    }
    write(this.name);
    if (this.indentation == null) {
      if (this.quoteProperties) {
        write("\":");
      } else {
        write(':');
      }
    } else {
      if (this.quoteProperties) {
        write("\": ");
      } else {
        write(": ");
      }
    }
    this.name = null;
  }

  @Override
  protected void doWriteEnd(StructuredNodeType type) {

    if (this.node.elementCount > 0) {
      writeIndent();
    }
    write(this.node.type.getClose());
  }

  @Override
  public void writeValueAsNull() {

    writeValueInternal(null);
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
  public void writeValueAsBoolean(boolean value) {

    writeValueInternal(Boolean.toString(value));
  }

  private void writeValueInternal(Object value) {

    String s;
    if (value == null) {
      if (!this.writeNullValues && (this.node.type != StructuredNodeType.ARRAY)
          && (this.node.type != null)) {
        return;
      }
      s = "null"; // JSON null representation
    } else {
      s = value.toString();
    }
    if (this.node.elementCount > 0) {
      write(',');
    }
    if (this.indentCount > 0) {
      writeIndent();
    }
    writeName();
    write(s);
    setState(StructuredState.VALUE);
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
  public void writeValueAsLong(long value) {

    String string = Long.toString(value);
    // TODO this should be a configurable feature, though default behavior due to JavaScript that sucks
    if ((value >= JS_NUMBER_MIN) && (value <= JS_NUMBER_MAX)) {
      writeValueInternal(string);
    } else {
      writeValueAsString(string);
    }
  }

  @Override
  public void writeValueAsInteger(int value) {

    writeValueInternal(Integer.toString(value));
  }

  @Override
  public void writeValueAsDouble(double value) {

    writeValueInternal(Double.toString(value));
  }

  @Override
  public void writeValueAsFloat(float value) {

    // TODO why do we do this???
    writeValueAsDouble(Double.parseDouble(Float.toString(value)));
  }

}
