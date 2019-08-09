package net.sf.mmm.marshall.stax;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;

import net.sf.mmm.marshall.StructuredFormat;
import net.sf.mmm.marshall.stax.impl.XmlFormat;

/**
 * @author hohwille
 *
 */
public final class StaxMarshalling {

  private StaxMarshalling() {

    super();
  }

  /**
   * @return the default instance of {@link StructuredFormat} for "JSON-style" XML based on StAX.
   */
  public static StructuredFormat of() {

    return XmlFormat.of();
  }

  /**
   * @param readerFactory the {@link XMLInputFactory}.
   * @param writerFactory the {@link XMLOutputFactory}.
   * @return a new instance of {@link StructuredFormat} for "JSON-style" XML based on StAX.
   */
  public static StructuredFormat of(XMLInputFactory readerFactory, XMLOutputFactory writerFactory) {

    return new XmlFormat(readerFactory, writerFactory);
  }

}
