/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.marshall.stax.impl;

import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import io.github.mmm.marshall.AbstractStructuredStringReader;
import io.github.mmm.marshall.StructuredFormat;
import io.github.mmm.marshall.StructuredReader;

/**
 * Implementation of {@link StructuredReader} for XML using {@link XMLStreamReader}.
 *
 * @since 1.0.0
 */
public class StaxReader extends AbstractStructuredStringReader {

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
      this.state = State.NAME;
      next();
    } catch (XMLStreamException e) {
      throw new IllegalStateException(e);
    }
  }

  @Override
  public State next() {

    if (this.state == State.DONE) {
      return State.DONE;
    } else if (this.state == State.NAME) {
      this.state = nextValue();
    } else {
      try {
        if (this.xml.hasNext()) {
          State e = null;
          while (e == null) {
            e = convertEvent(this.xml.next());
          }
          this.state = e;
        } else {
          this.state = State.DONE;
        }
      } catch (XMLStreamException e) {
        throw new IllegalStateException(e);
      }
    }
    return this.state;
  }

  private State nextValue() {

    String uri = this.xml.getNamespaceURI();
    if (uri == null) {
      return State.VALUE;
    } else if (uri.equals(StructuredFormat.NS_URI_ARRAY)) {
      this.arrayStack++;
      return State.START_ARRAY;
    } else if (uri.equals(StructuredFormat.NS_URI_OBJECT)) {
      return State.START_OBJECT;
    } else {
      throw error("Enexpected namespace URI " + uri);
    }
  }

  private State convertEvent(int e) {

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
        return State.NAME;
      case XMLStreamConstants.END_ELEMENT:
        this.arrayStack >>= 1;
        uri = this.xml.getNamespaceURI();
        if (uri == null) {
          return null;
        } else if (uri.equals(StructuredFormat.NS_URI_ARRAY)) {
          return State.END_ARRAY;
        } else if (uri.equals(StructuredFormat.NS_URI_OBJECT)) {
          return State.END_OBJECT;
        } else {
          error("Unexpected namespace URI " + uri);
        }
        break;
      case XMLStreamConstants.END_DOCUMENT:
        return State.DONE;
    }
    return null;
  }

  @Override
  public Object readValue() {

    expect(State.VALUE);
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

    expect(State.VALUE);
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

    if (this.state == State.VALUE) {
      String value = this.xml.getAttributeValue(null, StructuredFormat.ATR_STRING_VALUE);
      return (value != null);
    }
    return false;
  }

  @Override
  protected RuntimeException error(String message, Throwable cause) {

    Location location = this.xml.getLocation();
    if (location != null) {
      message = "XML invalid at line " + location.getLineNumber() + " and column " + location.getColumnNumber() + ": "
          + message;
    }
    return super.error(message, cause);
  }

  @Override
  public void close() {

    if (this.xml != null) {
      try {
        this.xml.close();
      } catch (XMLStreamException e) {
        throw new IllegalStateException(e);
      }
      this.xml = null;
    }
  }

}
