/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.marshall.yaml.impl;

import java.io.Reader;

import io.github.mmm.marshall.MarshallingConfig;
import io.github.mmm.marshall.StructuredFormat;
import io.github.mmm.marshall.StructuredReader;
import io.github.mmm.marshall.StructuredWriter;
import io.github.mmm.marshall.spi.AbstractStructuredTextFormat;
import io.github.mmm.scanner.CharReaderScanner;
import io.github.mmm.scanner.CharSequenceScanner;

/**
 * Implementation of {@link StructuredFormat} for YAML (YAML Ain't Markup Language) that extends JSON with nice and
 * advanced features.
 *
 * @since 1.0.0
 */
public class YamlFormat extends AbstractStructuredTextFormat {

  private static final YamlFormat DEFAULT = of(MarshallingConfig.DEFAULTS);

  /**
   * The constructor.
   *
   * @param config the {@link MarshallingConfig}.
   * @see io.github.mmm.marshall.StructuredFormatFactory#create(String, MarshallingConfig)
   */
  public YamlFormat(MarshallingConfig config) {

    super(config);
  }

  @Override
  public String getId() {

    return ID_YAML;
  }

  @Override
  public StructuredReader reader(Reader reader) {

    return new YamlReader(new CharReaderScanner(reader), this);
  }

  @Override
  public StructuredReader reader(String data) {

    return new YamlReader(new CharSequenceScanner(data), this);
  }

  @Override
  public StructuredWriter writer(Appendable writer) {

    return new YamlWriter(writer, this);
  }

  @Override
  public boolean isSupportingComments() {

    return true;
  }

  /**
   * @return the default instance of {@link YamlFormat}.
   */
  public static YamlFormat of() {

    return DEFAULT;
  }

  /**
   * @param config the {@link MarshallingConfig} for the JSON vendor implementation.
   * @return the new instance of {@link YamlFormat} with the given {@code config}.
   */
  public static YamlFormat of(MarshallingConfig config) {

    if (config == null) {
      return DEFAULT;
    }
    return new YamlFormat(config);
  }

}
