/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.marshall.stax.impl;

import java.io.IOException;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import io.github.mmm.marshall.StructuredFormat;
import io.github.mmm.marshall.StructuredState;
import io.github.mmm.marshall.StructuredWriter;
import io.github.mmm.marshall.id.StructuredIdMappingObject;
import io.github.mmm.marshall.spi.AbstractStructuredWriter;
import io.github.mmm.marshall.spi.StructuredNodeDefault;
import io.github.mmm.marshall.spi.StructuredNodeType;

/**
 * Implementation of {@link StructuredWriter} for XML using {@link XMLStreamWriter}.
 *
 * @since 1.0.0
 */
public class StaxWriter extends AbstractStructuredWriter<StructuredNodeDefault> {

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
  protected StructuredNodeDefault newNode(StructuredNodeType type, StructuredIdMappingObject object) {

    return new StructuredNodeDefault(this.node, type);
  }

  @Override
  protected void doWriteStart(StructuredNodeType type, StructuredIdMappingObject object) {

    try {
      String ns;
      String uri;
      if (type == StructuredNodeType.ARRAY) {
        ns = StructuredFormat.NS_PREFIX_ARRAY;
        uri = StructuredFormat.NS_URI_ARRAY;
      } else {
        ns = StructuredFormat.NS_PREFIX_OBJECT;
        uri = StructuredFormat.NS_URI_OBJECT;
      }
      this.xml.writeStartElement(ns, requireName(), uri);
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
  protected void doWriteEnd(StructuredNodeType type) {

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
      setState(StructuredState.VALUE);
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
  protected void doClose() throws IOException {

    try {
      this.xml.writeEndDocument();
      this.xml.close();
      this.xml = null;
    } catch (XMLStreamException e) {
      throw new IllegalStateException(e);
    }
  }

}
