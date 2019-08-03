package net.sf.mmm.marshal.impl.stax;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Month;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import net.sf.mmm.marshal.api.StructuredReader;

/**
 * Implementation of {@link StructuredReader} for XML using {@link XMLStreamReader}.
 *
 * @since 1.0.0
 */
public class XmlReader implements StructuredReader {

  private final XMLStreamReader xml;

  private int event;

  private String name;

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
      this.event = xml.nextTag();
    } catch (XMLStreamException e) {
      throw new IllegalStateException(e);
    }
  }

  private void next() {

    try {
      if (this.xml.hasNext()) {
        this.event = this.xml.nextTag();
        if (this.event == XMLStreamConstants.END_ELEMENT) {
          this.event = this.xml.nextTag();
        }
      } else {
        this.event = 0;
      }
    } catch (XMLStreamException e) {
      throw new IllegalStateException(e);
    }
  }

  @Override
  public String readName() {

    if (this.event != XMLStreamConstants.START_ELEMENT) {
      throw new IllegalStateException("Invalid XML!");
    }
    this.name = this.xml.getLocalName();
    return this.name;
  }

  @Override
  public boolean readStartObject() {

    if (this.event != XMLStreamConstants.START_ELEMENT) {
      throw new IllegalStateException("Invalid XML!");
    }
    try {
      int next = 0;
      while (next != XMLStreamConstants.START_ELEMENT) {
        switch (next) {
          case XMLStreamConstants.END_ELEMENT:
          case XMLStreamConstants.END_DOCUMENT:
            throw new IllegalStateException("Invalid XML as tag " + this.name + " ");
        }
        next = this.xml.next();
      }
      this.event = next;
      return true;
    } catch (XMLStreamException e) {
      throw new IllegalStateException(e);
    }
  }

  @Override
  public boolean readStartArray() {

    QName qName = this.xml.getName();
    if (qName.getNamespaceURI().equals(XmlWriter.NS_URI_ARRAY)) {
      next();
      return true;
    }
    return false;
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  @Override
  public <V> V readValue(Class<V> type) {

    String value = this.xml.getAttributeValue(null, XmlWriter.ART_VALUE);
    next();
    if (value == null) {
      return null;
    }
    Object result = null;
    if (String.class.equals(type)) {
      result = value;
    } else if (Boolean.class.equals(type)) {
      result = Boolean.valueOf(value);
    } else if (Integer.class.equals(type)) {
      result = Integer.valueOf(value);
    } else if (Long.class.equals(type)) {
      result = Long.valueOf(value);
    } else if (Short.class.equals(type)) {
      result = Short.valueOf(value);
    } else if (Byte.class.equals(type)) {
      result = Byte.valueOf(value);
    } else if (Double.class.equals(type)) {
      result = Double.valueOf(value);
    } else if (Float.class.equals(type)) {
      result = Float.valueOf(value);
    } else if (BigInteger.class.equals(type)) {
      result = new BigInteger(value);
    } else if (BigDecimal.class.equals(type)) {
      result = new BigDecimal(value);
    } else if (Month.class.equals(type)) {
      result = Month.of(Integer.parseInt(value));
    } else if (Enum.class.isAssignableFrom(type)) {
      result = Enum.valueOf((Class) type, value);
    } else {
      throw new IllegalStateException("Unknown value type " + type);
    }
    return type.cast(result);
  }

  @Override
  public boolean readEnd() {

    try {
      if (this.event == XMLStreamConstants.END_ELEMENT) {
        this.event = this.xml.next();
        if (this.event == XMLStreamConstants.END_DOCUMENT) {
          this.done = true;
        }
        return true;
      }
      return false;
    } catch (XMLStreamException e) {
      throw new IllegalStateException(e);
    }
  }

  @Override
  public boolean isDone() {

    return this.done;
  }

}
