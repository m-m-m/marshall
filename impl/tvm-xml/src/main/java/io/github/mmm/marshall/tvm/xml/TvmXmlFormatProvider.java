/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.marshall.tvm.xml;

import io.github.mmm.marshall.AbstractStructuredFormatProvider;
import io.github.mmm.marshall.MarshallingConfig;
import io.github.mmm.marshall.StructuredFormat;
import io.github.mmm.marshall.StructuredFormatProvider;
import io.github.mmm.marshall.StructuredTextFormat;
import io.github.mmm.marshall.StructuredTextFormatProvider;
import io.github.mmm.marshall.tvm.xml.impl.TvmXmlFormat;

/**
 * Implementation of {@link StructuredFormatProvider} for XML using TeaVM.
 *
 * @since 1.0.0
 */
public class TvmXmlFormatProvider extends AbstractStructuredFormatProvider implements StructuredTextFormatProvider {

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

    return TvmXmlFormat.of();
  }

  @Override
  public StructuredTextFormat create(MarshallingConfig config) {

    if (config == null) {
      return TvmXmlFormat.of();
    }
    return new TvmXmlFormat(config);
  }
}
