/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.marshall.stax;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;

import io.github.mmm.marshall.MarshallingConfig;
import io.github.mmm.marshall.StructuredFormat;
import io.github.mmm.marshall.stax.impl.StaxFormat;

/**
 * Provides {@link StructuredFormat} for XML based on StAX (Streaming API for XML).
 *
 * @since 1.0.0
 */
public final class StaxMarshalling {

  private StaxMarshalling() {

    super();
  }

  /**
   * @return the default instance of {@link StructuredFormat} for "JSON-style" XML based on StAX.
   */
  public static StructuredFormat of() {

    return StaxFormat.of();
  }

  /**
   * @param readerFactory the {@link XMLInputFactory}.
   * @param writerFactory the {@link XMLOutputFactory}.
   * @return a new instance of {@link StructuredFormat} for "JSON-style" XML based on StAX.
   */
  public static StructuredFormat of(XMLInputFactory readerFactory, XMLOutputFactory writerFactory) {

    return new StaxFormat(readerFactory, writerFactory, MarshallingConfig.DEFAULTS);
  }

  /**
   * @param config the {@link MarshallingConfig}.
   * @return a new instance of {@link StructuredFormat} for "JSON-style" XML based on StAX.
   */
  public static StructuredFormat of(MarshallingConfig config) {

    return new StaxFormat(config);
  }

}
