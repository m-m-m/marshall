/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.marshall.json;

import io.github.mmm.marshall.MarshallingConfig;
import io.github.mmm.marshall.StructuredFormat;
import io.github.mmm.marshall.StructuredFormatProvider;

/**
 * Implementation of {@link StructuredFormatProvider} for JSON-P.
 *
 * @since 1.0.0
 */
public class JsonFormatProvider implements StructuredFormatProvider {

  @Override
  public String getId() {

    return StructuredFormat.ID_JSON;
  }

  @Override
  public StructuredFormat create() {

    return JsonMarshalling.of();
  }

  @Override
  public StructuredFormat create(MarshallingConfig config) {

    return JsonMarshalling.of(config);
  }

}
