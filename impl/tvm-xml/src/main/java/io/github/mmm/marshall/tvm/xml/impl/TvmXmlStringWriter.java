/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.marshall.tvm.xml.impl;

import io.github.mmm.marshall.AbstractStructuredStringWriter;
import io.github.mmm.marshall.StructuredFormat;
import io.github.mmm.marshall.StructuredState;
import io.github.mmm.marshall.StructuredWriter;
import io.github.mmm.marshall.id.StructuredIdMappingObject;
import io.github.mmm.marshall.spi.StructuredNodeType;

/**
 * Implementation of {@link StructuredWriter} for XML using TeaVM.
 *
 * @since 1.0.0
 */
public class TvmXmlStringWriter extends AbstractStructuredStringWriter<TvmXmlState> {

  private String closingTag;

  /**
   * The constructor.
   *
   * @param out the {@link Appendable} to write the XML to.
   * @param format the {@link #getFormat() format}.
   */
  public TvmXmlStringWriter(Appendable out, TvmXmlFormat format) {

    super(out, format);
    write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
  }

  @Override
  protected TvmXmlState newNode(StructuredNodeType type, StructuredIdMappingObject object) {

    TvmXmlState result = new TvmXmlState(this.node, type, this.closingTag);
    this.closingTag = null;
    return result;
  }

  @Override
  protected void doWriteStart(StructuredNodeType type, StructuredIdMappingObject object) {

    writeIndent();
    if (this.node.isRoot()) {
      if (type == StructuredNodeType.ARRAY) {
        write("<a:json xmlns:o=\"object\" xmlns:a=\"array\">");
        this.closingTag = "</a:json>";
      } else {
        write("<o:json xmlns:o=\"object\" xmlns:a=\"array\">");
        this.closingTag = "</o:json>";
      }
    } else {
      if (type == StructuredNodeType.ARRAY) {
        write("<a:");
        write(this.name);
        write(">");
        this.closingTag = "</a:" + this.name + ">";
        this.name = StructuredFormat.TAG_ITEM;
      } else {
        write("<o:");
        write(this.name);
        write(">");
        this.closingTag = "</o:" + this.name + ">";
      }
    }
  }

  @Override
  protected void doWriteEnd(StructuredNodeType type) {

    write(this.node.tag);
  }

  @Override
  public void writeValueAsNull() {

    writeValue(null, null);
  }

  @Override
  public void writeValueAsString(String value) {

    writeValue(value, StructuredFormat.ATR_STRING_VALUE);
  }

  @Override
  public void writeValueAsBoolean(boolean value) {

    writeValue(Boolean.toString(value), StructuredFormat.ATR_BOOLEAN_VALUE);
  }

  @Override
  public void writeValueAsNumber(Number value) {

    if (value == null) {
      writeValueAsNull();
    } else {
      writeValue(value.toString(), StructuredFormat.ATR_NUMBER_VALUE);
    }
  }

  private void writeValue(String value, String attribute) {

    if ((value == null) && !this.writeNullValues) {
      return;
    }
    writeIndent();
    write("<");
    write(this.name);
    if (value != null) {
      write(" ");
      write(attribute);
      write("=\"");
      write(escapeAttributeValue(value));
      write("\"");
    }
    write("/>");
    if (this.name != StructuredFormat.TAG_ITEM) {
      this.name = null;
    }
    setState(StructuredState.VALUE);
  }

  @Override
  protected void doWriteComment(String currentComment) {

    write("<!-- ");
    write(escapeXmlComment(currentComment));
    write(" -->");
  }

  private String escapeAttributeValue(String value) {

    return value.replace("&", "&amp;").replace("<", "&lt;").replace("\"", "&quot;");
  }

}
