/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.marshall.yaml.impl;

import java.math.BigDecimal;
import java.math.BigInteger;

import io.github.mmm.marshall.AbstractStructuredStringWriter;
import io.github.mmm.marshall.MarshallingConfig;
import io.github.mmm.marshall.StructuredFormat;
import io.github.mmm.marshall.StructuredState;
import io.github.mmm.marshall.StructuredWriter;
import io.github.mmm.marshall.id.StructuredIdMappingObject;
import io.github.mmm.marshall.spi.StructuredNodeType;

/**
 * Implementation of {@link StructuredWriter} for JSON from scratch.
 *
 * @see YamlFormat
 *
 * @since 1.0.0
 */
public class YamlWriter extends AbstractStructuredStringWriter<YamlNode> {

  private static final long JS_NUMBER_MAX = (2L << 52) - 1;

  private static final long JS_NUMBER_MIN = -JS_NUMBER_MAX;

  /**
   * The constructor.
   *
   * @param out the {@link Appendable} to write the data to.
   * @param format the {@link #getFormat() format}.
   */
  public YamlWriter(Appendable out, StructuredFormat format) {

    super(out, format);
  }

  @Override
  protected YamlNode newNode(StructuredNodeType type, StructuredIdMappingObject object) {

    boolean json = (type == StructuredNodeType.OBJECT) && (this.node.type == StructuredNodeType.ARRAY);
    if (type != null) {
      writeName(json);
      if ((type == StructuredNodeType.ARRAY) || ((this.node.parent == null) && !json)) {
        // in YAML we do not indent array items: "- item" on the same indent
        // as the indentCount is auto-incremented by parent class we decrement it here to neutralize
        this.indentCount--;
      }
    }
    if (json) {
      write(type.getOpen());
    }
    return new YamlNode(this.node, type, json);
  }

  @Override
  protected void doWriteStart(StructuredNodeType type, StructuredIdMappingObject object) {

  }

  @Override
  protected void doWriteEnd(StructuredNodeType type) {

    this.indentCount--;
    boolean json = this.node.json;
    if (this.node.elementCount == 0) {
      if (!json) {
        write(' ');
        write(this.node.type.getOpen());
      }
      json = true;
    }
    if (json) {
      if (this.node.elementCount > 0) {
        writeIndent();
      }
      write(this.node.type.getClose());
    }
  }

  @Override
  protected String normalizeIndentation(String indent) {

    if ((indent == null) || (indent.isEmpty())) {
      return MarshallingConfig.VAR_INDENTATION.getDefaultValue();
    }
    return super.normalizeIndentation(indent);
  }

  private void writeName(boolean spaced) {

    boolean listArray = this.node.isYamlArray();
    if (!listArray || !this.node.parent.isYamlArray()) {
      writeIndent();
    }
    this.node.elementCount++;
    if (this.name == null) {
      if (listArray) {
        write("- ");
      }
      return;
    }
    write(this.name);
    if (spaced) {
      write(": ");
    } else {
      write(":");
    }
    this.name = null;
  }

  @Override
  public void writeValueAsNull() {

    writeValueInternal("null");
  }

  @Override
  public void writeValueAsString(String value) {

    if ((value != null) && !value.isEmpty()) {
      if (this.node.json) {
        value = '"' + value.replace("\"", "\\\"") + '"';
      } else {
        // https://yaml.org/spec/current.html#id2534365
        value = '\'' + value.replace("'", "''") + '\'';
      }
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
      if (!this.writeNullValues) {
        return;
      }
      s = "null"; // JSON null representation
    } else {
      s = value.toString();
    }
    writeName(true);
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

  @Override
  protected void doWriteComment(String currentComment) {

    write("# ");
    if (currentComment.indexOf('\n') >= 0) {
      currentComment = currentComment.replace("\r\n", "\n"); // normalize CRLF (Windows EOL)
      StringBuilder sb = new StringBuilder(3 + (this.indentCount * this.indentation.length()));
      sb.append('\n');
      for (int i = this.indentCount; i > 0; i--) {
        sb.append(this.indentation);
      }
      sb.append("# ");
      String replacement = sb.toString();
      currentComment = currentComment.replace("\n", replacement);
    }
    write(currentComment);
  }

  @Override
  public void writeComment(String newComment) {

    if (this.node.parent == null) {
      doWriteComment(newComment);
    } else {
      super.writeComment(newComment);
    }
  }

}
