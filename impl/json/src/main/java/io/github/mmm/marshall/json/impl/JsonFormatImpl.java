/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.marshall.json.impl;

import java.io.Reader;

import io.github.mmm.marshall.MarshallingConfig;
import io.github.mmm.marshall.StructuredFormat;
import io.github.mmm.marshall.StructuredReader;
import io.github.mmm.marshall.StructuredWriter;
import io.github.mmm.scanner.CharReaderScanner;
import io.github.mmm.scanner.CharSequenceScanner;

/**
 * Implementation of {@link StructuredFormat} for JSON (JavaScript Object Notation).
 *
 * @since 1.0.0
 */
public class JsonFormatImpl implements StructuredFormat {

  private static final JsonFormatImpl DEFAULT = of(MarshallingConfig.DEFAULTS);

  private final MarshallingConfig config;

  /**
   * The constructor.
   *
   * @param config the {@link MarshallingConfig}.
   * @see io.github.mmm.marshall.StructuredFormatFactory#create(String, MarshallingConfig)
   */
  public JsonFormatImpl(MarshallingConfig config) {

    super();
    this.config = config;
  }

  @Override
  public String getId() {

    return ID_JSON;
  }

  @Override
  public StructuredReader reader(Reader reader) {

    return new JsonReader(new CharReaderScanner(reader), this.config);
  }

  @Override
  public StructuredReader reader(String data) {

    return new JsonReader(new CharSequenceScanner(data), this.config);
  }

  @Override
  public StructuredWriter writer(Appendable writer) {

    return new JsonWriter(writer, this.config);
  }

  /**
   * @return the default instance of {@link JsonFormatImpl}.
   */
  public static JsonFormatImpl of() {

    return DEFAULT;
  }

  /**
   * @param config the {@link MarshallingConfig} for the JSON vendor implementation.
   * @return the new instance of {@link JsonFormatImpl} with the given {@code config}.
   */
  public static JsonFormatImpl of(MarshallingConfig config) {

    return new JsonFormatImpl(config);
  }

}
