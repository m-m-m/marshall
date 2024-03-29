/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.marshall.stax.impl;

import java.io.Reader;
import java.util.Map.Entry;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.mmm.base.io.AppendableWriter;
import io.github.mmm.marshall.MarshallingConfig;
import io.github.mmm.marshall.StructuredFormat;
import io.github.mmm.marshall.StructuredReader;
import io.github.mmm.marshall.StructuredWriter;
import io.github.mmm.marshall.spi.AbstractStructuredTextFormat;

/**
 * Implementation of {@link StructuredFormat} for XML using StAX.
 *
 * @since 1.0.0
 */
public class StaxFormat extends AbstractStructuredTextFormat {

  private static final Logger LOG = LoggerFactory.getLogger(StaxFormat.class);

  private static final StaxFormat DEFAULT;

  private final XMLInputFactory readerFactory;

  private final XMLOutputFactory writerFactory;

  static {

    XMLInputFactory inFactory = XMLInputFactory.newDefaultFactory();
    XMLOutputFactory outFactory = XMLOutputFactory.newDefaultFactory();
    DEFAULT = new StaxFormat(inFactory, outFactory, MarshallingConfig.DEFAULTS);
  }

  /**
   * The constructor.
   *
   * @param readerFactory the {@link XMLInputFactory} to create readers.
   * @param writerFactory the {@link XMLOutputFactory} to create writers.
   * @param config the {@link MarshallingConfig}.
   */
  public StaxFormat(XMLInputFactory readerFactory, XMLOutputFactory writerFactory, MarshallingConfig config) {

    super(config);
    this.readerFactory = readerFactory;
    this.writerFactory = writerFactory;
  }

  /**
   * The constructor.
   *
   * @param config the {@link MarshallingConfig}.
   */
  public StaxFormat(MarshallingConfig config) {

    super(config);
    XMLInputFactory xmlReaderFactory = XMLInputFactory.newFactory();
    XMLOutputFactory xmlWriterFactory = XMLOutputFactory.newFactory();
    for (Entry<String, Object> entry : config.getMap().entrySet()) {
      String key = entry.getKey();
      if (MarshallingConfig.isProprietary(key)) {
        Object value = entry.getValue();
        String unsupported = null;
        if (xmlReaderFactory.isPropertySupported(key)) {
          xmlReaderFactory.setProperty(key, value);
        } else {
          unsupported = "reader";
        }
        if (xmlWriterFactory.isPropertySupported(key)) {
          xmlWriterFactory.setProperty(key, value);
        } else {
          if (unsupported == null) {
            unsupported = "writer";
          } else {
            unsupported = "reader and writer";
          }
        }
        if (unsupported != null) {
          LOG.warn("The property '{}' is not supported for StAX {}.", key, unsupported);
        }
      }
    }
    this.readerFactory = xmlReaderFactory;
    this.writerFactory = xmlWriterFactory;
  }

  @Override
  public String getId() {

    return ID_XML;
  }

  @Override
  public StructuredReader reader(Reader reader) {

    try {
      XMLStreamReader xml = this.readerFactory.createXMLStreamReader(reader);
      return new StaxReader(xml, this);
    } catch (XMLStreamException e) {
      throw new IllegalStateException(e);
    }
  }

  @Override
  public StructuredWriter writer(Appendable writer) {

    try {
      XMLStreamWriter xml = this.writerFactory.createXMLStreamWriter(AppendableWriter.asWriter(writer));
      return new StaxWriter(xml, this);
    } catch (XMLStreamException e) {
      throw new IllegalStateException(e);
    }
  }

  @Override
  public boolean isSupportingComments() {

    return true;
  }

  /**
   * @return the default instance of {@link StaxFormat}.
   */
  public static StaxFormat of() {

    return DEFAULT;
  }

}
