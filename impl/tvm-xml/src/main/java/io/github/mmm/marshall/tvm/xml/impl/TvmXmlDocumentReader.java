/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.marshall.tvm.xml.impl;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.teavm.jso.dom.xml.Attr;
import org.teavm.jso.dom.xml.NamedNodeMap;
import org.teavm.jso.dom.xml.Node;

import io.github.mmm.marshall.AbstractStructuredReader;
import io.github.mmm.marshall.MarshallingConfig;
import io.github.mmm.marshall.StructuredFormat;
import io.github.mmm.marshall.StructuredReader;

/**
 * Implementation of {@link StructuredReader} for XML using TeaVM.
 *
 * @since 1.0.0
 */
public class TvmXmlDocumentReader extends AbstractStructuredReader {

  private Node node;

  /**
   * The constructor.
   *
   * @param node the root {@link Node}.
   * @param config the {@link MarshallingConfig}.
   */
  public TvmXmlDocumentReader(Node node, MarshallingConfig config) {

    super(config);
    this.node = node;
    this.state = State.NAME;
    next();
  }

  @Override
  public State next() {

    if (this.state == State.DONE) {
      return State.DONE;
    } else if (this.state == State.NAME) {
      this.state = nextValue(true);
    } else if ((this.state == State.VALUE) || (this.state == State.END_ARRAY) || (this.state == State.END_OBJECT)) {
      Node next;
      do {
        next = this.node.getNextSibling();
      } while ((next != null) && (next.getNodeType() != Node.ELEMENT_NODE));
      if (next == null) {
        this.node = next;
      } else {
        this.node = this.node.getParentNode();
      }
      if (this.node == null) {
        this.state = State.DONE;
      } else {
        this.state = nextValue(false);
      }
    }
    return this.state;
  }

  private State nextValue(boolean start) {

    String uri = this.node.getNamespaceURI();
    if (uri == null) {
      if (start) {
        return State.VALUE;
      } else {
        throw invalidXml();
      }
    } else if (uri.equals(StructuredFormat.NS_URI_ARRAY)) {
      if (start) {
        return State.START_ARRAY;
      } else {
        return State.END_ARRAY;
      }
    } else if (uri.equals(StructuredFormat.NS_URI_OBJECT)) {
      if (start) {
        return State.START_OBJECT;
      } else {
        return State.END_OBJECT;
      }
    } else {
      throw invalidXml();
    }
  }

  private void endValue() {

    expect(State.VALUE);
    next();
  }

  @Override
  public String readName() {

    expect(State.NAME);
    next();
    return this.name;
  }

  @Override
  public Object readValue() {

    Object value;
    Attr attribute = null;
    if (this.state == State.VALUE) {
      NamedNodeMap<Attr> attributes = this.node.getAttributes();
      if (attributes.getLength() == 0) {
        value = null;
      } else {
        attribute = attributes.getNamedItem(StructuredFormat.ART_STRING_VALUE);
        if (attribute == null) {
          attribute = attributes.getNamedItem(StructuredFormat.ART_BOOLEAN_VALUE);
          if (attribute == null) {
            attribute = attributes.getNamedItem(StructuredFormat.ART_NUMBER_VALUE);
            if (attribute == null) {
              throw invalidXml();
            }
            value = parseNumber(attribute.getValue());
          } else {
            value = parseBoolean(attribute.getValue());
          }
        } else {
          value = attribute.getValue();
        }
      }
      endValue();
    } else {
      expect(State.VALUE);
      throw invalidXml();
    }
    return value;
  }

  @Override
  protected String readValueAsNumberString() {

    return readValue(StructuredFormat.ART_NUMBER_VALUE);
  }

  @Override
  public String readValueAsString() {

    return readValue(StructuredFormat.ART_STRING_VALUE);
  }

  @Override
  public Boolean readValueAsBoolean() {

    String value = readValue(StructuredFormat.ART_BOOLEAN_VALUE);
    return parseBoolean(value);
  }

  private Boolean parseBoolean(String value) {

    if (value == null) {
      return null;
    } else if ("true".equalsIgnoreCase(value)) {
      return Boolean.TRUE;
    } else if ("false".equalsIgnoreCase(value)) {
      return Boolean.FALSE;
    } else {
      throw handleValueParseError(value, Boolean.class, null);
    }
  }

  private Number parseNumber(String string) {

    boolean decimal = (string.indexOf('.') >= 0);
    if (decimal) {
      return new BigDecimal(string);
    } else {
      BigInteger integer = new BigInteger(string);
      int bitLength = integer.bitLength();
      if (bitLength > 63) {
        return integer;
      } else if (bitLength < 31) {
        return Integer.valueOf(integer.intValue());
      } else {
        return Long.valueOf(integer.longValue());
      }
    }
  }

  private String readValue(String attribute) {

    expect(State.VALUE);
    NamedNodeMap<Attr> attributes = this.node.getAttributes();
    String value = null;
    if (attributes.getLength() > 0) {
      Attr attr = attributes.getNamedItem(attribute);
      if (attr == null) {
        if (attribute == StructuredFormat.ART_NUMBER_VALUE) {
          attr = attributes.getNamedItem(StructuredFormat.ART_STRING_VALUE);
        }
        if (attr == null) {
          throw invalidXml();
        }
      }
      value = attr.getValue();
    }
    endValue();
    return value;
  }

  private RuntimeException invalidXml() {

    throw new IllegalStateException("Invalid XML!");
  }

  @Override
  public void close() {

    this.node = null;
  }

}
