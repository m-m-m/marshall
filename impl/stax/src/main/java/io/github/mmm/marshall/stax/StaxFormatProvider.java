/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.marshall.stax;

import io.github.mmm.marshall.MarshallingConfig;
import io.github.mmm.marshall.StructuredFormat;
import io.github.mmm.marshall.StructuredFormatProvider;

/**
 * {@link StructuredFormatProvider} for XML using StAX.
 *
 * @since 1.0.0
 */
public class StaxFormatProvider implements StructuredFormatProvider {

  @Override
  public String getId() {

    return StructuredFormat.ID_XML;
  }

  @Override
  public StructuredFormat create() {

    return StaxMarshalling.of();
  }

  @Override
  public StructuredFormat create(MarshallingConfig config) {

    return StaxMarshalling.of(config);
  }
}
