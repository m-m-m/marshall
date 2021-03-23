/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.marshall.stax.impl;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import io.github.mmm.marshall.AbstractStructuredWriter;
import io.github.mmm.marshall.StructuredFormat;
import io.github.mmm.marshall.StructuredWriter;

/**
 * Implementation of {@link StructuredWriter} for XML using {@link XMLStreamWriter}.
 *
 * @since 1.0.0
 */
public class StaxWriter extends AbstractStructuredWriter {

  private XMLStreamWriter xml;

  /**
   * The constructor.
   *
   * @param xml the {@link XMLStreamWriter} to wrap.
   * @param format the {@link #getFormat() format}.
   */
  public StaxWriter(XMLStreamWriter xml, StructuredFormat format) {

    super(format);
    if (this.indentation != null) {
      this.xml = new IndentingXmlStreamWriter(xml, this.indentation);
    } else {
      this.xml = xml;
    }
    try {
      this.xml.writeStartDocument();
      this.xml.setPrefix(StructuredFormat.NS_PREFIX_ARRAY, StructuredFormat.NS_URI_ARRAY);
      this.xml.setPrefix(StructuredFormat.NS_PREFIX_OBJECT, StructuredFormat.NS_URI_OBJECT);
      this.name = StructuredFormat.TAG_ROOT;
    } catch (XMLStreamException e) {
      throw new IllegalStateException(e);
    }
  }

  @Override
  public void writeStartArray(int size) {

    try {
      this.xml.writeStartElement(StructuredFormat.NS_PREFIX_ARRAY, requireName(), StructuredFormat.NS_URI_ARRAY);
      if (this.name == StructuredFormat.TAG_ROOT) {
        this.xml.writeNamespace(StructuredFormat.NS_PREFIX_ARRAY, StructuredFormat.NS_URI_ARRAY);
        this.xml.writeNamespace(StructuredFormat.NS_PREFIX_OBJECT, StructuredFormat.NS_URI_OBJECT);
      }
      this.name = null;
    } catch (XMLStreamException e) {
      throw new IllegalStateException(e);
    }
  }

  @Override
  public void writeStartObject(int size) {

    try {
      this.xml.writeStartElement(StructuredFormat.NS_PREFIX_OBJECT, requireName(), StructuredFormat.NS_URI_OBJECT);
      if (this.name == StructuredFormat.TAG_ROOT) {
        this.xml.writeNamespace(StructuredFormat.NS_PREFIX_ARRAY, StructuredFormat.NS_URI_ARRAY);
        this.xml.writeNamespace(StructuredFormat.NS_PREFIX_OBJECT, StructuredFormat.NS_URI_OBJECT);
      }
      this.name = null;
    } catch (XMLStreamException e) {
      throw new IllegalStateException(e);
    }
  }

  private String requireName() {

    if (this.name == null) {
      return StructuredFormat.TAG_ITEM;
    }
    return this.name;
  }

  @Override
  public void writeEnd() {

    try {
      this.xml.writeEndElement();
    } catch (XMLStreamException e) {
      throw new IllegalStateException(e);
    }
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
  public void writeValueAsBoolean(Boolean value) {

    if (value == null) {
      writeValueAsNull();
    } else {
      writeValue(value.toString(), StructuredFormat.ATR_BOOLEAN_VALUE);
    }
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
    try {
      String tag = this.name;
      if (tag == null) {
        tag = StructuredFormat.TAG_ITEM;
      }
      this.xml.writeEmptyElement(tag);
      if (value != null) {
        this.xml.writeAttribute(attribute, value);
      }
      this.name = null;
    } catch (XMLStreamException e) {
      throw new IllegalStateException(e);
    }
  }

  @Override
  public void writeComment(String comment) {

    if (comment == null) {
      return;
    }
    try {
      this.xml.writeComment(" " + escapeXmlComment(comment) + " ");
    } catch (XMLStreamException e) {
      throw new IllegalStateException(e);
    }
  }

  @Override
  public void close() {

    try {
      if (this.xml != null) {
        this.xml.writeEndDocument();
        this.xml.close();
        this.xml = null;
      }
    } catch (XMLStreamException e) {
      throw new IllegalStateException(e);
    }
  }

}
