/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.marshall.tvm.xml.impl;

import io.github.mmm.marshall.AbstractStructuredStringWriter;
import io.github.mmm.marshall.StructuredFormat;
import io.github.mmm.marshall.StructuredWriter;

/**
 * Implementation of {@link StructuredWriter} for XML using TeaVM.
 *
 * @since 1.0.0
 */
public class TvmXmlStringWriter extends AbstractStructuredStringWriter {

  private StackNode node;

  /**
   * The constructor.
   *
   * @param out the {@link Appendable} to write the XML to.
   * @param format the {@link #getFormat() format}.
   */
  public TvmXmlStringWriter(Appendable out, TvmXmlFormat format) {

    super(out, format);
    write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
    // write("<o:json xmlns:o=\"object\" xmlns:a=\"array\">");
  }

  @Override
  public void writeStartArray() {

    writeIndent();
    if (this.node == null) {
      write("<a:json xmlns:o=\"object\" xmlns:a=\"array\">");
      this.node = new StackNode("</a:json>");
    } else {
      write("<a:");
      write(this.name);
      write(">");
      this.node = this.node.append("</a:" + this.name + ">");
      this.name = StructuredFormat.TAG_ITEM;
    }
    this.indentCount++;
  }

  @Override
  public void writeStartObject() {

    writeIndent();
    if (this.node == null) {
      write("<o:json xmlns:o=\"object\" xmlns:a=\"array\">");
      this.node = new StackNode("</o:json>");
    } else {
      write("<o:");
      write(this.name);
      write(">");
      this.node = this.node.append("</o:" + this.name + ">");
    }
    this.indentCount++;
  }

  @Override
  public void writeEnd() {

    if (this.node == null) {
      throw new IllegalStateException();
    }
    write(this.node.tag);
    this.node = this.node.parent;
    this.indentCount--;
  }

  @Override
  public void writeValueAsNull() {

    writeValue(null, null);
  }

  @Override
  public void writeValueAsString(String value) {

    writeValue(value, StructuredFormat.ART_STRING_VALUE);
  }

  @Override
  public void writeValueAsBoolean(Boolean value) {

    if (value == null) {
      writeValueAsNull();
    } else {
      writeValue(value.toString(), StructuredFormat.ART_BOOLEAN_VALUE);
    }
  }

  @Override
  public void writeValueAsNumber(Number value) {

    if (value == null) {
      writeValueAsNull();
    } else {
      writeValue(value.toString(), StructuredFormat.ART_NUMBER_VALUE);
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
  }

  private String escapeAttributeValue(String value) {

    return value.replace("&", "&amp;").replace("<", "&lt;").replace("\"", "&quot;");
  }

}
