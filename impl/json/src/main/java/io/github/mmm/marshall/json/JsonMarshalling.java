/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.marshall.json;

import java.util.Map;

import io.github.mmm.marshall.MarshallingConfig;
import io.github.mmm.marshall.StructuredFormat;
import io.github.mmm.marshall.json.impl.JsonFormat;

/**
 * Provides {@link StructuredFormat} for JSON based on TeaVM.
 *
 * @since 1.0.0
 */
public final class JsonMarshalling {

  private JsonMarshalling() {

    super();
  }

  /**
   * @return the default instance of {@link StructuredFormat} for JSON based on JSON-P.
   */
  public static StructuredFormat of() {

    return JsonFormat.of();
  }

  /**
   * @param config the {@link Map} with the configuration properties for the JSON vendor implementation.
   * @return a new instance of {@link StructuredFormat} for JSON based on JSON-P.
   */
  public static StructuredFormat of(MarshallingConfig config) {

    return JsonFormat.of(config);
  }

}
