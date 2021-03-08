/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.marshall.jsonp;

import java.util.Map;

import javax.json.stream.JsonGeneratorFactory;
import javax.json.stream.JsonParserFactory;

import io.github.mmm.marshall.MarshallingConfig;
import io.github.mmm.marshall.StructuredFormat;
import io.github.mmm.marshall.StructuredTextFormat;
import io.github.mmm.marshall.jsonp.impl.JsonpFormatImpl;

/**
 * Provides {@link StructuredFormat} for JSON based on JSON-P.
 *
 * @since 1.0.0
 */
public final class JsonpMarshalling {

  private JsonpMarshalling() {

    super();
  }

  /**
   * @return the default instance of {@link StructuredFormat} for JSON based on JSON-P.
   */
  public static StructuredTextFormat of() {

    return JsonpFormatImpl.of();
  }

  /**
   * @param config the {@link Map} with the configuration properties for the JSON vendor implementation.
   * @return a new instance of {@link StructuredFormat} for JSON based on JSON-P.
   */
  public static StructuredTextFormat of(MarshallingConfig config) {

    return JsonpFormatImpl.of(config);
  }

  /**
   * @param readerFactory the {@link JsonParserFactory}.
   * @param writerFactory the {@link JsonGeneratorFactory}.
   * @param config the {@link MarshallingConfig} for the JSON vendor implementation.
   * @return a new instance of {@link StructuredFormat} for JSON based on JSON-P.
   */
  public static StructuredTextFormat of(JsonParserFactory readerFactory, JsonGeneratorFactory writerFactory,
      MarshallingConfig config) {

    return new JsonpFormatImpl(readerFactory, writerFactory, config);
  }

}
