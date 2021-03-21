/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.marshall.yaml.impl;

import java.math.BigDecimal;
import java.math.BigInteger;

import io.github.mmm.marshall.AbstractStructuredStringWriter;
import io.github.mmm.marshall.MarshallingConfig;
import io.github.mmm.marshall.StructuredFormat;
import io.github.mmm.marshall.StructuredWriter;
import io.github.mmm.marshall.spi.StructuredNodeType;

/**
 * Implementation of {@link StructuredWriter} for JSON from scratch.
 *
 * @see YamlFormat
 *
 * @since 1.0.0
 */
public class YamlWriter extends AbstractStructuredStringWriter {

  private static final long JS_NUMBER_MAX = (2L << 52) - 1;

  private static final long JS_NUMBER_MIN = -JS_NUMBER_MAX;

  private YamlState yamlState;

  private boolean empty;

  /**
   * The constructor.
   *
   * @param out the {@link Appendable} to write the data to.
   * @param format the {@link #getFormat() format}.
   */
  public YamlWriter(Appendable out, StructuredFormat format) {

    super(out, format);
    this.yamlState = new YamlState();
    this.empty = true;
  }

  @Override
  protected String normalizeIndentation(String indent) {

    if ((indent == null) || (indent.isEmpty())) {
      return MarshallingConfig.OPT_INDENTATION.getDefaultValue();
    }
    return super.normalizeIndentation(indent);
  }

  @Override
  public void writeStartArray(int size) {

    writeStart(StructuredNodeType.ARRAY);
  }

  @Override
  public void writeStartObject(int size) {

    writeStart(StructuredNodeType.OBJECT);
  }

  private void writeStart(StructuredNodeType type) {

    boolean json = (type == StructuredNodeType.OBJECT) && (this.yamlState.type == StructuredNodeType.ARRAY);
    writeName(json);
    if ((type == StructuredNodeType.OBJECT) && (this.yamlState.parent != null)) {
      this.indentCount++;
    }
    this.yamlState = new YamlState(this.yamlState, type, json);
    if (json) {
      write(type.getOpen());
    }
  }

  private void writeName(boolean spaced) {

    this.yamlState.valueCount++;
    boolean listArray = this.yamlState.isYamlArray();
    if (!this.empty) {
      if (!listArray || !this.yamlState.parent.isYamlArray()) {
        writeIndent();
      }
    }
    if (this.name == null) {
      if (listArray) {
        write("- ");
      }
      return;
    }
    this.empty = false;
    write(this.name);
    if (spaced) {
      write(": ");
    } else {
      write(":");
    }
    this.name = null;
  }

  @Override
  public void writeEnd() {

    if ((this.yamlState.type != null) && (this.yamlState.parent != null)) {
      this.indentCount--;
      boolean json = this.yamlState.json;
      if (this.yamlState.valueCount == 0) {
        if (!json) {
          write(' ');
          write(this.yamlState.type.getOpen());
        }
        json = true;
      }
      if (json) {
        if (this.yamlState.valueCount > 0) {
          writeIndent();
        }
        write(this.yamlState.type.getClose());
      }
      this.yamlState = this.yamlState.parent;
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

    if ((value != null) && !value.isEmpty()) {
      if (this.yamlState.json) {
        value = '"' + value.replace("\"", "\\\"") + '"';
      } else {
        // https://yaml.org/spec/current.html#id2534365
        value = '\'' + value.replace("'", "''") + '\'';
      }
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
    writeName(true);
    write(s);
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

    if (this.yamlState == null) {
      return;
    }
    assert (this.yamlState.parent == null);
    this.yamlState = null;
  }

}
