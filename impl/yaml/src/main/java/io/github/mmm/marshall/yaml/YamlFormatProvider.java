/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.marshall.yaml;

import io.github.mmm.marshall.MarshallingConfig;
import io.github.mmm.marshall.StructuredFormat;
import io.github.mmm.marshall.StructuredFormatProvider;
import io.github.mmm.marshall.StructuredTextFormat;
import io.github.mmm.marshall.StructuredTextFormatProvider;
import io.github.mmm.marshall.yaml.impl.YamlFormat;

/**
 * Implementation of {@link StructuredFormatProvider} for JSON.
 *
 * @since 1.0.0
 */
public class YamlFormatProvider implements StructuredTextFormatProvider {

  @Override
  public String getId() {

    return StructuredFormat.ID_YAML;
  }

  @Override
  public StructuredTextFormat create() {

    return YamlFormat.of();
  }

  @Override
  public StructuredTextFormat create(MarshallingConfig config) {

    return YamlFormat.of(config);
  }

}
