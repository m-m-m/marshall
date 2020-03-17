/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.marshall.jsonp.impl;

import java.io.Reader;
import java.io.Writer;
import java.util.Map;

import javax.json.Json;
import javax.json.stream.JsonGenerator;
import javax.json.stream.JsonGeneratorFactory;
import javax.json.stream.JsonParser;
import javax.json.stream.JsonParserFactory;

import io.github.mmm.marshall.MarshallingConfig;
import io.github.mmm.marshall.StructuredFormat;
import io.github.mmm.marshall.StructuredReader;
import io.github.mmm.marshall.StructuredWriter;

/**
 * Implementation of {@link StructuredFormat} for JSON (JavaScript Object Notation).
 *
 * @since 1.0.0
 */
public class JsonFormat implements StructuredFormat {

  private static final JsonFormat DEFAULT = of(MarshallingConfig.DEFAULTS);

  private final JsonGeneratorFactory writerFactory;

  private final JsonParserFactory readerFactory;

  private final MarshallingConfig config;

  /**
   * The constructor.
   *
   * @param readerFactory the {@link JsonParserFactory}.
   * @param writerFactory the {@link JsonGeneratorFactory}.
   * @param config the {@link MarshallingConfig}.
   * @see io.github.mmm.marshall.StructuredFormatFactory#create(String, MarshallingConfig)
   */
  public JsonFormat(JsonParserFactory readerFactory, JsonGeneratorFactory writerFactory, MarshallingConfig config) {

    super();
    this.writerFactory = writerFactory;
    this.readerFactory = readerFactory;
    this.config = config;
  }

  @Override
  public StructuredReader reader(Reader reader) {

    JsonParser json = this.readerFactory.createParser(reader);
    return new JsonReader(json, this.config);
  }

  @Override
  public StructuredWriter writer(Writer writer) {

    JsonGenerator json = this.writerFactory.createGenerator(writer);
    return new JsonWriter(json, this.config);
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

    Map<String, Object> map = config.getMap();
    return new JsonFormat(Json.createParserFactory(map), Json.createGeneratorFactory(map), config);
  }

}
