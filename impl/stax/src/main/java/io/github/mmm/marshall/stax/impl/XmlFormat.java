/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.marshall.stax.impl;

import java.io.Reader;
import java.io.Writer;
import java.util.Map.Entry;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import io.github.mmm.marshall.MarshallingConfig;
import io.github.mmm.marshall.StructuredFormat;
import io.github.mmm.marshall.StructuredReader;
import io.github.mmm.marshall.StructuredWriter;

/**
 * Implementation of {@link StructuredFormat} for XML using StAX.
 *
 * @since 1.0.0
 */
public class XmlFormat implements StructuredFormat {

  private static final XmlFormat DEFAULT;

  private final XMLInputFactory readerFactory;

  private final XMLOutputFactory writerFactory;

  private final MarshallingConfig config;

  static {

    XMLInputFactory inFactory = XMLInputFactory.newDefaultFactory();
    XMLOutputFactory outFactory = XMLOutputFactory.newDefaultFactory();
    DEFAULT = new XmlFormat(inFactory, outFactory, MarshallingConfig.DEFAULTS);
  }

  /**
   * The constructor.
   *
   * @param readerFactory the {@link XMLInputFactory} to create readers.
   * @param writerFactory the {@link XMLOutputFactory} to create writers.
   * @param config the {@link MarshallingConfig}.
   */
  public XmlFormat(XMLInputFactory readerFactory, XMLOutputFactory writerFactory, MarshallingConfig config) {

    super();
    this.readerFactory = readerFactory;
    this.writerFactory = writerFactory;
    this.config = config;
  }

  /**
   * The constructor.
   *
   * @param config the {@link MarshallingConfig}.
   */
  public XmlFormat(MarshallingConfig config) {

    super();
    XMLInputFactory xmlReaderFactory = XMLInputFactory.newFactory();
    XMLOutputFactory xmlWriterFactory = XMLOutputFactory.newFactory();
    for (Entry<String, Object> entry : config.getMap().entrySet()) {
      // TODO: normalize properties and only apply to reader/writer where suitable...
      // TODO: maybe catch exception and ignore unsupported properties...
      String name = entry.getKey();
      Object value = entry.getValue();
      xmlReaderFactory.setProperty(name, value);
      xmlWriterFactory.setProperty(name, value);
    }
    this.readerFactory = xmlReaderFactory;
    this.writerFactory = xmlWriterFactory;
    this.config = config;
  }

  @Override
  public StructuredReader reader(Reader reader) {

    try {
      XMLStreamReader xml = this.readerFactory.createXMLStreamReader(reader);
      return new XmlReader(xml, this.config);
    } catch (XMLStreamException e) {
      throw new IllegalStateException(e);
    }
  }

  @Override
  public StructuredWriter writer(Writer writer) {

    try {
      XMLStreamWriter xml = this.writerFactory.createXMLStreamWriter(writer);
      return new XmlWriter(xml, this.config);
    } catch (XMLStreamException e) {
      throw new IllegalStateException(e);
    }
  }

  /**
   * @return the default instance of {@link XmlFormat}.
   */
  public static XmlFormat of() {

    return DEFAULT;
  }

}
