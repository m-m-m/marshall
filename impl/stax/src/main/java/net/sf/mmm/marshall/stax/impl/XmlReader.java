/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package net.sf.mmm.marshall.stax.impl;

import javax.xml.namespace.QName;
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

  private final XMLStreamReader xml;

  private int event;

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

  @Override
  public String readValueAsString() {

    String value = this.xml.getAttributeValue(null, XmlWriter.ART_VALUE);
    next();
    return value;
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
