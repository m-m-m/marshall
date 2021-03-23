/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.marshall.tvm.xml.impl;

import org.teavm.jso.dom.xml.Attr;
import org.teavm.jso.dom.xml.NamedNodeMap;
import org.teavm.jso.dom.xml.Node;

import io.github.mmm.marshall.AbstractStructuredStringReader;
import io.github.mmm.marshall.StructuredFormat;
import io.github.mmm.marshall.StructuredReader;

/**
 * Implementation of {@link StructuredReader} for XML using TeaVM.
 *
 * @since 1.0.0
 */
public class TvmXmlDocumentReader extends AbstractStructuredStringReader {

  private Node node;

  /**
   * The constructor.
   *
   * @param node the root {@link Node}.
   * @param format the {@link #getFormat() format}.
   */
  public TvmXmlDocumentReader(Node node, StructuredFormat format) {

    super(format);
    this.node = node;
    this.state = State.NAME;
    next();
  }

  @Override
  public State next() {

    if (this.state == State.DONE) {
      return State.DONE;
    } else if (this.state == State.NAME) {
      this.state = nextState(true);
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
        this.state = nextState(false);
      }
    }
    return this.state;
  }

  private State nextState(boolean start) {

    String uri = this.node.getNamespaceURI();
    if (uri == null) {
      if (start) {
        return State.VALUE;
      } else {
        throw error("Expected start value!");
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
      throw error("Unexpected namespace URI " + uri);
    }
  }

  @Override
  public String getName(boolean next) {

    expect(State.NAME);
    if (next) {
      next();
    }
    return this.name;
  }

  @Override
  public Object readValue() {

    expect(State.VALUE);
    Object value;
    Attr attribute = null;
    NamedNodeMap<Attr> attributes = this.node.getAttributes();
    if (attributes.getLength() == 0) {
      value = null;
    } else {
      attribute = attributes.getNamedItem(StructuredFormat.ATR_STRING_VALUE);
      if (attribute == null) {
        attribute = attributes.getNamedItem(StructuredFormat.ATR_BOOLEAN_VALUE);
        if (attribute == null) {
          attribute = attributes.getNamedItem(StructuredFormat.ATR_NUMBER_VALUE);
          if (attribute == null) {
            throw error("Unexpected attribute " + attributes.get(0));
          }
          value = parseNumber(attribute.getValue());
        } else {
          value = parseBoolean(attribute.getValue());
        }
      } else {
        value = attribute.getValue();
      }
    }
    next();
    return value;
  }

  @Override
  protected String readValueAsNumberString() {

    expect(State.VALUE);
    NamedNodeMap<Attr> attributes = this.node.getAttributes();
    Attr attr = null;
    if (attributes.getLength() > 0) {
      attr = attributes.getNamedItem(StructuredFormat.ATR_NUMBER_VALUE);
      if (attr == null) {
        attr = attributes.getNamedItem(StructuredFormat.ATR_STRING_VALUE);
        if (attr == null) {
          // fallback, actually an error (Boolean can not be a number)
          return readValueAsString();
        }
      }
    }
    if (attr == null) {
      throw error("Missing value attribute!");
    }
    String value = attr.getValue();
    next();
    return value;
  }

  @Override
  public boolean isStringValue() {

    if (this.state == State.VALUE) {
      Attr stringAttr = this.node.getAttributes().getNamedItem(StructuredFormat.ATR_STRING_VALUE);
      return (stringAttr != null);
    }
    return false;
  }

  @Override
  public void close() {

    this.node = null;
  }

}
