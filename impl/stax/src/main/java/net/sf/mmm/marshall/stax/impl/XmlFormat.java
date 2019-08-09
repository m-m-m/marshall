package net.sf.mmm.marshall.stax.impl;

import java.io.Reader;
import java.io.Writer;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import net.sf.mmm.marshall.StructuredFormat;
import net.sf.mmm.marshall.StructuredReader;
import net.sf.mmm.marshall.StructuredWriter;

/**
 * Implementation of {@link StructuredFormat} for XML using StAX.
 *
 * @since 1.0.0
 */
public class XmlFormat implements StructuredFormat {

  private static final XmlFormat DEFAULT;

  private final XMLInputFactory readerFactory;

  private final XMLOutputFactory writerFactory;

  static {

    XMLInputFactory inFactory = XMLInputFactory.newDefaultFactory();
    XMLOutputFactory outFactory = XMLOutputFactory.newDefaultFactory();
    DEFAULT = new XmlFormat(inFactory, outFactory);
  }

  /**
   * The constructor.
   *
   * @param readerFactory the {@link XMLInputFactory} to create readers.
   * @param writerFactory the {@link XMLOutputFactory} to create writers.
   */
  public XmlFormat(XMLInputFactory readerFactory, XMLOutputFactory writerFactory) {

    super();
    this.readerFactory = readerFactory;
    this.writerFactory = writerFactory;
  }

  @Override
  public StructuredReader reader(Reader reader) {

    try {
      XMLStreamReader xml = this.readerFactory.createXMLStreamReader(reader);
      return new XmlReader(xml);
    } catch (XMLStreamException e) {
      throw new IllegalStateException(e);
    }
  }

  @Override
  public StructuredWriter writer(Writer writer) {

    try {
      XMLStreamWriter xml = this.writerFactory.createXMLStreamWriter(writer);
      return new XmlWriter(xml);
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
