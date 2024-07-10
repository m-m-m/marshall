/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.marshall.stax;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;

import io.github.mmm.marshall.AbstractStructuredFormatProvider;
import io.github.mmm.marshall.MarshallingConfig;
import io.github.mmm.marshall.StructuredFormat;
import io.github.mmm.marshall.StructuredFormatProvider;
import io.github.mmm.marshall.StructuredTextFormat;
import io.github.mmm.marshall.StructuredTextFormatProvider;
import io.github.mmm.marshall.stax.impl.StaxFormat;

/**
 * {@link StructuredFormatProvider} for XML using StAX.
 *
 * @since 1.0.0
 */
public class StaxFormatProvider extends AbstractStructuredFormatProvider implements StructuredTextFormatProvider {

  @Override
  public String getId() {

    return StructuredFormat.ID_XML;
  }

  @Override
  public String[] getAliases() {

    return new String[] { "xml" };
  }

  @Override
  public StructuredTextFormat create() {

    return StaxFormat.of();
  }

  @Override
  public StructuredTextFormat create(MarshallingConfig config) {

    if (config == null) {
      return StaxFormat.of();
    }
    return new StaxFormat(config);
  }

  /**
   * @param readerFactory the {@link XMLInputFactory}.
   * @param writerFactory the {@link XMLOutputFactory}.
   * @return a new instance of {@link StructuredFormat} for "JSON-style" XML based on StAX.
   */
  public static StructuredTextFormat of(XMLInputFactory readerFactory, XMLOutputFactory writerFactory) {

    return new StaxFormat(readerFactory, writerFactory, MarshallingConfig.DEFAULTS);
  }

}
