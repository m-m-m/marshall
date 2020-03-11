/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.marshall.stax;

import java.util.Map;

import io.github.mmm.marshall.StructuredFormat;
import io.github.mmm.marshall.StructuredFormatFactory;
import io.github.mmm.marshall.StructuredFormatProvider;

/**
 *
 */
public class XmlFormatProvider implements StructuredFormatProvider {

  @Override
  public String getName() {

    return StructuredFormatFactory.NAME_XML;
  }

  @Override
  public StructuredFormat create() {

    return StaxMarshalling.of();
  }

  @Override
  public StructuredFormat create(Map<String, Object> configuration) {

    return StaxMarshalling.of(configuration);
  }
}
