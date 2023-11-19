/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.marshall.tvm.xml.impl;

import java.io.IOException;

import org.teavm.jso.dom.xml.Attr;
import org.teavm.jso.dom.xml.NamedNodeMap;
import org.teavm.jso.dom.xml.Node;

import io.github.mmm.marshall.StructuredFormat;
import io.github.mmm.marshall.StructuredReader;
import io.github.mmm.marshall.StructuredState;
import io.github.mmm.marshall.id.StructuredIdMappingObject;
import io.github.mmm.marshall.spi.AbstractStructuredStringReader;
import io.github.mmm.marshall.spi.StructuredNodeDefault;
import io.github.mmm.marshall.spi.StructuredNodeType;

/**
 * Implementation of {@link StructuredReader} for XML using TeaVM.
 *
 * @since 1.0.0
 */
public class TvmXmlDocumentReader extends AbstractStructuredStringReader<StructuredNodeDefault> {

  private Node xmlNode;

  /**
   * The constructor.
   *
   * @param xmlNode the root {@link Node}.
   * @param format the {@link #getFormat() format}.
   */
  public TvmXmlDocumentReader(Node xmlNode, StructuredFormat format) {

    super(format);
    this.xmlNode = xmlNode;
    next();
  }

  @Override
  protected StructuredNodeDefault newNode(StructuredNodeType type, StructuredIdMappingObject object) {

    return null;
  }

  @Override
  protected StructuredState next(boolean skip) {

    int skipCount = skip ? 1 : 0;
    StructuredState state = getState();
    boolean todo;
    do {
      todo = false;
      int skipAdd = 0;
      if ((state == StructuredState.NAME) || (state == StructuredState.NULL)) {
        state = nextState(true);
        if (state.isStart()) {
          skipAdd = 1;
        } else if (state.isEnd()) {
          skipAdd = -1;
        }
      } else if ((state == StructuredState.VALUE) || (state == StructuredState.END_ARRAY)
          || (state == StructuredState.END_OBJECT)) {
        Node next = null;
        short nodeType = -1;
        while (nodeType != Node.ELEMENT_NODE) {
          next = this.xmlNode.getNextSibling();
          if (next == null) {
            break;
          }
          nodeType = next.getNodeType();
          if (nodeType == Node.COMMENT_NODE) {
            addComment(next.getTextContent());
          }
        }
        if (next == null) {
          this.xmlNode = next;
        } else {
          this.xmlNode = this.xmlNode.getParentNode();
        }
        if (this.xmlNode == null) {
          state = StructuredState.DONE;
        } else {
          state = nextState(false);
        }
      } else if ((state == StructuredState.START_ARRAY) || (state == StructuredState.START_OBJECT)) {
        this.xmlNode = this.xmlNode.getFirstChild();
        state = nextState(true);
      } else if (state == StructuredState.DONE) {
        return StructuredState.DONE;
      }
      if (skipCount > 0) {
        skipCount += skipAdd;
        if (skipCount == 0) {
          todo = true;
        }
      }
      setState(state);
    } while ((skipCount > 0) || todo);
    return state;
  }

  private StructuredState nextState(boolean start) {

    String uri = this.xmlNode.getNamespaceURI();
    if (uri == null) {
      if (start) {
        return StructuredState.VALUE;
      } else {
        throw error("Expected start value!");
      }
    } else if (uri.equals(StructuredFormat.NS_URI_ARRAY)) {
      if (start) {
        return StructuredState.START_ARRAY;
      } else {
        return StructuredState.END_ARRAY;
      }
    } else if (uri.equals(StructuredFormat.NS_URI_OBJECT)) {
      if (start) {
        return StructuredState.START_OBJECT;
      } else {
        return StructuredState.END_OBJECT;
      }
    } else {
      throw error("Unexpected namespace URI " + uri);
    }
  }

  @Override
  public String getName(boolean next) {

    require(StructuredState.NAME);
    if (next) {
      next();
    }
    return this.name;
  }

  @Override
  public Object readValue() {

    require(StructuredState.VALUE);
    Object value;
    Attr attribute = null;
    NamedNodeMap<Attr> attributes = this.xmlNode.getAttributes();
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

    require(StructuredState.VALUE);
    NamedNodeMap<Attr> attributes = this.xmlNode.getAttributes();
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

    if (getState() == StructuredState.VALUE) {
      Attr stringAttr = this.xmlNode.getAttributes().getNamedItem(StructuredFormat.ATR_STRING_VALUE);
      return (stringAttr != null);
    }
    return false;
  }

  @Override
  protected void doClose() throws IOException {

    this.xmlNode = null;
  }

}
