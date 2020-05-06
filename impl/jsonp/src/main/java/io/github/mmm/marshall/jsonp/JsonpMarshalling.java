/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.marshall.jsonp;

import java.util.Map;

import javax.json.stream.JsonGeneratorFactory;
import javax.json.stream.JsonParserFactory;

import io.github.mmm.marshall.MarshallingConfig;
import io.github.mmm.marshall.StructuredFormat;
import io.github.mmm.marshall.jsonp.impl.JsonpFormat;

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
  public static StructuredFormat of() {

    return JsonpFormat.of();
  }

  /**
   * @param config the {@link Map} with the configuration properties for the JSON vendor implementation.
   * @return a new instance of {@link StructuredFormat} for JSON based on JSON-P.
   */
  public static StructuredFormat of(MarshallingConfig config) {

    return JsonpFormat.of(config);
  }

  /**
   * @param readerFactory the {@link JsonParserFactory}.
   * @param writerFactory the {@link JsonGeneratorFactory}.
   * @param config the {@link MarshallingConfig} for the JSON vendor implementation.
   * @return a new instance of {@link StructuredFormat} for JSON based on JSON-P.
   */
  public static StructuredFormat of(JsonParserFactory readerFactory, JsonGeneratorFactory writerFactory,
      MarshallingConfig config) {

    return new JsonpFormat(readerFactory, writerFactory, config);
  }

}
