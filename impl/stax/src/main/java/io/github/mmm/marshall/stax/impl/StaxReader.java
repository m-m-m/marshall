/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.marshall.stax.impl;

import java.math.BigDecimal;
import java.math.BigInteger;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import io.github.mmm.base.number.NumberType;
import io.github.mmm.marshall.AbstractStructuredReader;
import io.github.mmm.marshall.StructuredFormat;
import io.github.mmm.marshall.StructuredReader;

/**
 * Implementation of {@link StructuredReader} for XML using {@link XMLStreamReader}.
 *
 * @since 1.0.0
 */
public class StaxReader extends AbstractStructuredReader {

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
        invalidXml();
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
      throw invalidXml();
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
            invalidXml();
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
          invalidXml();
        }
        break;
      case XMLStreamConstants.END_DOCUMENT:
        return State.DONE;
    }
    return null;
  }

  private void endValue() {

    expect(State.VALUE);
    next();
  }

  @Override
  public Object readValue() {

    Object value;
    if (this.state == State.VALUE) {
      if (this.xml.getAttributeCount() == 0) {
        value = null;
      } else {
        value = this.xml.getAttributeValue(null, StructuredFormat.ART_STRING_VALUE);
        if (value == null) {
          String string = this.xml.getAttributeValue(null, StructuredFormat.ART_BOOLEAN_VALUE);
          if (string == null) {
            string = this.xml.getAttributeValue(null, StructuredFormat.ART_NUMBER_VALUE);
            if (string == null) {
              invalidXml();
            }
            value = parseNumber(string);
          } else {
            value = parseBoolean(string);
          }
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
      BigDecimal bd = new BigDecimal(string);
      if (string.endsWith("0")) {
        return bd; // preserve leading zeros
      }
      return NumberType.simplify(bd, NumberType.FLOAT);
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
    String value = this.xml.getAttributeValue(null, attribute);
    if ((value == null) && (this.xml.getAttributeCount() > 0)) {
      if (attribute == StructuredFormat.ART_NUMBER_VALUE) {
        value = this.xml.getAttributeValue(null, StructuredFormat.ART_STRING_VALUE);
      }
      if (value == null) {
        invalidXml();
      }
    }
    endValue();
    return value;
  }

  @Override
  public boolean isStringValue() {

    if (this.state == State.VALUE) {
      String value = this.xml.getAttributeValue(null, StructuredFormat.ART_STRING_VALUE);
      return (value != null);
    }
    return false;
  }

  private RuntimeException invalidXml() {

    throw new IllegalStateException("Invalid XML!");
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
