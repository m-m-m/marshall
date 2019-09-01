/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package net.sf.mmm.marshall.stax.impl;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import net.sf.mmm.marshall.AbstractStructuredReader;
import net.sf.mmm.marshall.StructuredReader;

/**
 * Implementation of {@link StructuredReader} for XML using {@link XMLStreamReader}.
 *
 * @since 1.0.0
 */
public class XmlReader extends AbstractStructuredReader {

  private XMLStreamReader xml;

  private State previousState;

  private long arrayStack;

  private boolean done;

  /**
   * The constructor.
   *
   * @param xml the {@link XMLStreamReader}.
   */
  public XmlReader(XMLStreamReader xml) {

    super();
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

  private void expect(State expected) {

    if (this.state != expected) {
      throw new IllegalStateException("Expecting event " + expected + " but found " + this.state + ".");
    }
  }

  private void expect(State expected, State expected2) {

    if ((this.state != expected) && (this.state != expected2)) {
      throw new IllegalStateException(
          "Expecting event " + expected + " or " + expected2 + " but found " + this.state + ".");
    }
  }

  private void expect(State expected, State expected2, State expected3) {

    if ((this.state != expected) && (this.state != expected2) && (this.state != expected3)) {
      throw new IllegalStateException(
          "Expecting event " + expected + ", " + expected2 + ",  or " + expected3 + " but found " + this.state + ".");
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
        this.previousState = this.state;
        if (this.xml.hasNext()) {
          State e = null;
          while (e == null) {
            e = convertEvent(this.xml.next());
          }
          this.state = e;
        } else {
          this.state = State.DONE;
          this.done = true;
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
    } else if (uri.equals(XmlWriter.NS_URI_ARRAY)) {
      this.arrayStack++;
      return State.START_ARRAY;
    } else if (uri.equals(XmlWriter.NS_URI_OBJECT)) {
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
          if (!tagName.equals(XmlWriter.TAG_ITEM)) {
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
        } else if (uri.equals(XmlWriter.NS_URI_ARRAY)) {
          return State.END_ARRAY;
        } else if (uri.equals(XmlWriter.NS_URI_OBJECT)) {
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
  public String readName() {

    expect(State.NAME);
    next();
    return this.name;
  }

  @Override
  public boolean readStartObject() {

    if (this.state == State.START_OBJECT) {
      next();
      return true;
    }
    return false;
  }

  @Override
  public boolean readStartArray() {

    if (this.state == State.START_ARRAY) {
      next();
      return true;
    }
    return false;
  }

  @Override
  public Object readValue(boolean recursive) {

    Object value;
    if (this.state == State.VALUE) {
      if (this.xml.getAttributeCount() == 0) {
        value = null;
      } else {
        value = this.xml.getAttributeValue(null, XmlWriter.ART_STRING_VALUE);
        if (value == null) {
          String string = this.xml.getAttributeValue(null, XmlWriter.ART_BOOLEAN_VALUE);
          if (string == null) {
            string = this.xml.getAttributeValue(null, XmlWriter.ART_NUMBER_VALUE);
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
    } else if (recursive) {
      if (this.state == State.START_ARRAY) {
        next();
        Collection<Object> array = new ArrayList<>();
        readArray(array);
        value = array;
      } else if (this.state == State.START_OBJECT) {
        next();
        Map<String, Object> map = new HashMap<>();
        readObject(map);
        value = map;
      } else {
        expect(State.VALUE, State.START_ARRAY, State.START_OBJECT);
        throw invalidXml();
      }
    } else {
      expect(State.VALUE);
      throw invalidXml();
    }
    return value;
  }

  @Override
  public void readObject(Map<String, Object> map) {

    if (this.previousState != State.START_OBJECT) {
      invalidXml();
    }
    while (this.state != State.END_OBJECT) {
      String key = readName();
      Object value = readValue(true);
      map.put(key, value);
    }
    next();
  }

  @Override
  public void readArray(Collection<Object> array) {

    if (this.previousState != State.START_ARRAY) {
      invalidXml();
    }
    while (this.state != State.END_ARRAY) {
      Object value = readValue(true);
      array.add(value);
    }
    next();
  }

  @Override
  protected String readValueAsNumberString() {

    return readValue(XmlWriter.ART_NUMBER_VALUE);
  }

  @Override
  public String readValueAsString() {

    return readValue(XmlWriter.ART_STRING_VALUE);
  }

  @Override
  public Boolean readValueAsBoolean() {

    String value = readValue(XmlWriter.ART_BOOLEAN_VALUE);
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
    String value = this.xml.getAttributeValue(null, attribute);
    if ((value == null) && (this.xml.getAttributeCount() > 0)) {
      if (attribute == XmlWriter.ART_NUMBER_VALUE) {
        value = this.xml.getAttributeValue(null, XmlWriter.ART_STRING_VALUE);
      }
      if (value == null) {
        invalidXml();
      }
    }
    endValue();
    return value;
  }

  @Override
  public boolean readEnd() {

    if (this.state == State.DONE) {
      boolean result = !this.done;
      this.done = true;
      return result;
    }
    if ((this.state == State.END_ARRAY) || (this.state == State.END_OBJECT)) {
      next();
      return true;
    }
    return false;
  }

  @Override
  public void skipValue() {

    expect(State.VALUE, State.START_ARRAY, State.START_OBJECT);
    if (this.state == State.VALUE) {
      next();
    } else {
      int count = 1;
      next();
      while (count > 0) {
        switch (this.state) {
          case START_ARRAY:
          case START_OBJECT:
            count++;
            break;
          case END_ARRAY:
          case END_OBJECT:
            count--;
            break;
          case VALUE:
          case NAME:
            break;
          case DONE:
            invalidXml();
        }
        next();
      }
    }
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
