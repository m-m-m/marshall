/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.marshall.stax.impl;

import java.io.IOException;

import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import io.github.mmm.marshall.StructuredFormat;
import io.github.mmm.marshall.StructuredReader;
import io.github.mmm.marshall.StructuredState;
import io.github.mmm.marshall.id.StructuredIdMappingObject;
import io.github.mmm.marshall.spi.AbstractStructuredStringReader;
import io.github.mmm.marshall.spi.StructuredNodeDefault;
import io.github.mmm.marshall.spi.StructuredNodeType;

/**
 * Implementation of {@link StructuredReader} for XML using {@link XMLStreamReader}.
 *
 * @since 1.0.0
 */
public class StaxReader extends AbstractStructuredStringReader<StructuredNodeDefault> {

  private XMLStreamReader xml;

  private long arrayStack;

  /**
   * The constructor.
   *
   * @param xml the {@link XMLStreamReader}.
   * @param format the {@link #getFormat() format}.
   */
  public StaxReader(XMLStreamReader xml, StructuredFormat format) {

    super(format);
    this.xml = xml;
    try {
      int e = xml.nextTag();
      if (e != XMLStreamConstants.START_ELEMENT) {
        error("Expected StAX event " + XMLStreamConstants.START_ELEMENT + " (START_ELEMENT) but found event" + e);
      }
      next();
    } catch (XMLStreamException e) {
      throw new IllegalStateException(e);
    }
  }

  @Override
  protected StructuredNodeDefault newNode(StructuredNodeType type, StructuredIdMappingObject object) {

    return null;
  }

  @Override
  protected StructuredState next(boolean skip) {

    int skipCount = skip ? 1 : 0;
    StructuredState state = getState();
    if (state == StructuredState.DONE) {
      return StructuredState.DONE;
    }
    try {
      boolean todo;
      do {
        todo = false;
        if ((state == StructuredState.NAME) || (state == StructuredState.NULL)) {
          state = nextValue();
        } else {
          if (this.xml.hasNext()) {
            StructuredState e = null;
            while (e == null) {
              e = convertEvent(this.xml.next());
            }
            state = e;
          } else {
            state = StructuredState.DONE;
            break;
          }
        }
        if (skipCount > 0) {
          if (state.isStart()) {
            skipCount++;
          } else if (state.isEnd()) {
            skipCount--;
            if (skipCount == 0) {
              todo = true;
            }
          }
        }
        setState(state);
      } while ((skipCount > 0) || todo);
    } catch (XMLStreamException e) {
      throw new IllegalStateException(e);
    }
    return state;
  }

  private StructuredState nextValue() {

    String uri = this.xml.getNamespaceURI();
    if (uri == null) {
      return StructuredState.VALUE;
    } else if (uri.equals(StructuredFormat.NS_URI_ARRAY)) {
      this.arrayStack++;
      return StructuredState.START_ARRAY;
    } else if (uri.equals(StructuredFormat.NS_URI_OBJECT)) {
      return StructuredState.START_OBJECT;
    } else {
      throw error("Enexpected namespace URI " + uri);
    }
  }

  private StructuredState convertEvent(int e) {

    String uri;
    switch (e) {
      case XMLStreamConstants.START_ELEMENT:
        String tagName = this.xml.getLocalName();
        boolean isArray = (this.arrayStack % 2) == 1;
        this.arrayStack <<= 1;
        if (isArray) {
          if (!tagName.equals(StructuredFormat.TAG_ITEM)) {
            error("Unexpected tag " + tagName);
          }
          return nextValue();
        }
        this.name = tagName;
        return StructuredState.NAME;
      case XMLStreamConstants.END_ELEMENT:
        this.arrayStack >>= 1;
        uri = this.xml.getNamespaceURI();
        if (uri == null) {
          return null;
        } else if (uri.equals(StructuredFormat.NS_URI_ARRAY)) {
          return StructuredState.END_ARRAY;
        } else if (uri.equals(StructuredFormat.NS_URI_OBJECT)) {
          return StructuredState.END_OBJECT;
        } else {
          throw error("Unexpected namespace URI " + uri);
        }
      case XMLStreamConstants.COMMENT:
        addComment(unescapeXmlComment(this.xml.getText()));
        return null;
      case XMLStreamConstants.END_DOCUMENT:
        return StructuredState.DONE;
    }
    return null;
  }

  @Override
  public Object readValue() {

    require(StructuredState.VALUE);
    Object value;
    if (this.xml.getAttributeCount() == 0) {
      value = null;
    } else {
      value = this.xml.getAttributeValue(null, StructuredFormat.ATR_STRING_VALUE);
      if (value == null) {
        String string = this.xml.getAttributeValue(null, StructuredFormat.ATR_BOOLEAN_VALUE);
        if (string == null) {
          string = this.xml.getAttributeValue(null, StructuredFormat.ATR_NUMBER_VALUE);
          if (string == null) {
            error("Missing value attribute!");
          }
          value = parseNumber(string);
        } else {
          value = parseBoolean(string);
        }
      }
    }
    next();
    return value;
  }

  @Override
  protected String readValueAsNumberString() {

    require(StructuredState.VALUE);
    String value = this.xml.getAttributeValue(null, StructuredFormat.ATR_NUMBER_VALUE);
    if (value == null) {
      value = this.xml.getAttributeValue(null, StructuredFormat.ATR_STRING_VALUE);
      if (value == null) {
        // fallback, actually an error (Boolean can not be a number)
        return readValueAsString();
      }
    }
    next();
    return value;
  }

  @Override
  public boolean isStringValue() {

    if (getState() == StructuredState.VALUE) {
      String value = this.xml.getAttributeValue(null, StructuredFormat.ATR_STRING_VALUE);
      return (value != null);
    }
    return false;
  }

  @Override
  protected String appendContextDetails(String message) {

    message = super.appendContextDetails(message);
    Location location = this.xml.getLocation();
    if (location != null) {
      message = "XML invalid at line " + location.getLineNumber() + " and column " + location.getColumnNumber() + ": "
          + message;
    }
    return message;
  }

  @Override
  protected void doClose() throws IOException {

    try {
      this.xml.close();
    } catch (XMLStreamException e) {
      throw new IllegalStateException(e);
    }
    this.xml = null;
  }

}
