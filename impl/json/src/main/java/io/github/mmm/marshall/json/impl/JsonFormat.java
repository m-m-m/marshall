/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.marshall.json.impl;

import java.io.Reader;

import io.github.mmm.marshall.MarshallingConfig;
import io.github.mmm.marshall.StructuredFormat;
import io.github.mmm.marshall.StructuredReader;
import io.github.mmm.marshall.StructuredWriter;
import io.github.mmm.marshall.spi.AbstractStructuredTextFormat;
import io.github.mmm.scanner.CharReaderScanner;
import io.github.mmm.scanner.CharSequenceScanner;

/**
 * Implementation of {@link StructuredFormat} for JSON (JavaScript Object Notation).
 *
 * @since 1.0.0
 */
public class JsonFormat extends AbstractStructuredTextFormat {

  private static final JsonFormat DEFAULT = of(MarshallingConfig.DEFAULTS);

  /**
   * The constructor.
   *
   * @param config the {@link MarshallingConfig}.
   * @see io.github.mmm.marshall.StructuredFormatFactory#create(String, MarshallingConfig)
   */
  public JsonFormat(MarshallingConfig config) {

    super(config);
  }

  @Override
  public String getId() {

    return ID_JSON;
  }

  @Override
  public StructuredReader reader(Reader reader) {

    return new JsonReader(new CharReaderScanner(reader), this);
  }

  @Override
  public StructuredReader reader(String data) {

    return new JsonReader(new CharSequenceScanner(data), this);
  }

  @Override
  public StructuredWriter writer(Appendable writer) {

    return new JsonWriter(writer, this);
  }

  /**
   * @return the default instance of {@link JsonFormat}.
   */
  public static JsonFormat of() {

    return DEFAULT;
  }

  /**
   * @param config the {@link MarshallingConfig} for the JSON vendor implementation.
   * @return the new instance of {@link JsonFormat} with the given {@code config}.
   */
  public static JsonFormat of(MarshallingConfig config) {

    if (config == null) {
      return DEFAULT;
    }
    return new JsonFormat(config);
  }

}
