/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.marshall.tvm.xml;

import io.github.mmm.marshall.MarshallingConfig;
import io.github.mmm.marshall.StructuredFormat;
import io.github.mmm.marshall.StructuredFormatProvider;
import io.github.mmm.marshall.tvm.xml.impl.TvmXmlFormat;

/**
 * Implementation of {@link StructuredFormatProvider} for XML using TeaVM.
 *
 * @since 1.0.0
 */
public class TvmXmlFormatProvider implements StructuredFormatProvider {

  @Override
  public String getId() {

    return StructuredFormat.ID_XML;
  }

  @Override
  public StructuredFormat create() {

    return TvmXmlFormat.of();
  }

  @Override
  public StructuredFormat create(MarshallingConfig config) {

    return new TvmXmlFormat(config);
  }
}
