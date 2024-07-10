/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.marshall.jsonp;

import javax.json.stream.JsonGeneratorFactory;
import javax.json.stream.JsonParserFactory;

import io.github.mmm.marshall.AbstractStructuredFormatProvider;
import io.github.mmm.marshall.MarshallingConfig;
import io.github.mmm.marshall.StructuredFormat;
import io.github.mmm.marshall.StructuredFormatProvider;
import io.github.mmm.marshall.StructuredTextFormat;
import io.github.mmm.marshall.StructuredTextFormatProvider;
import io.github.mmm.marshall.jsonp.impl.JsonpFormat;

/**
 * Implementation of {@link StructuredFormatProvider} for JSON-P.
 *
 * @since 1.0.0
 */
public class JsonpFormatProvider extends AbstractStructuredFormatProvider implements StructuredTextFormatProvider {

  @Override
  public String getId() {

    return StructuredFormat.ID_JSON;
  }

  @Override
  public String[] getAliases() {

    return new String[] { "json" };
  }

  @Override
  public StructuredTextFormat create() {

    return JsonpFormat.of();
  }

  @Override
  public StructuredTextFormat create(MarshallingConfig config) {

    return JsonpFormat.of(config);
  }

  /**
   * @param readerFactory the {@link JsonParserFactory}.
   * @param writerFactory the {@link JsonGeneratorFactory}.
   * @param config the {@link MarshallingConfig} for the JSON vendor implementation.
   * @return a new instance of {@link StructuredFormat} for JSON based on JSON-P.
   */
  public static StructuredTextFormat of(JsonParserFactory readerFactory, JsonGeneratorFactory writerFactory,
      MarshallingConfig config) {

    return new JsonpFormat(readerFactory, writerFactory, config);
  }

}
