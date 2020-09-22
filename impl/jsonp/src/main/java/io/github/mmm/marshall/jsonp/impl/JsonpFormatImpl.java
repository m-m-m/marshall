/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.marshall.jsonp.impl;

import java.io.Reader;
import java.util.Map;

import javax.json.Json;
import javax.json.stream.JsonGenerator;
import javax.json.stream.JsonGeneratorFactory;
import javax.json.stream.JsonParser;
import javax.json.stream.JsonParserFactory;

import io.github.mmm.base.io.AppendableWriter;
import io.github.mmm.marshall.MarshallingConfig;
import io.github.mmm.marshall.StructuredFormat;
import io.github.mmm.marshall.StructuredReader;
import io.github.mmm.marshall.StructuredWriter;

/**
 * Implementation of {@link StructuredFormat} for JSON (JavaScript Object Notation).
 *
 * @since 1.0.0
 */
public class JsonpFormatImpl implements StructuredFormat {

  private static final JsonpFormatImpl DEFAULT = of(MarshallingConfig.DEFAULTS);

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
  public JsonpFormatImpl(JsonParserFactory readerFactory, JsonGeneratorFactory writerFactory, MarshallingConfig config) {

    super();
    this.writerFactory = writerFactory;
    this.readerFactory = readerFactory;
    this.config = config;
  }

  @Override
  public String getId() {

    return ID_JSON;
  }

  @Override
  public StructuredReader reader(Reader reader) {

    JsonParser json = this.readerFactory.createParser(reader);
    return new JsonpReader(json, this.config);
  }

  @Override
  public StructuredWriter writer(Appendable writer) {

    JsonGenerator json = this.writerFactory.createGenerator(AppendableWriter.asWriter(writer));
    return new JsonpWriter(json, this.config);
  }

  /**
   * @return the default instance of {@link JsonpFormatImpl}.
   */
  public static JsonpFormatImpl of() {

    return DEFAULT;
  }

  /**
   * @param config the {@link MarshallingConfig} for the JSON vendor implementation.
   * @return the new instance of {@link JsonpFormatImpl} with the given {@code config}.
   */
  public static JsonpFormatImpl of(MarshallingConfig config) {

    Map<String, Object> map = config.getMap();
    return new JsonpFormatImpl(Json.createParserFactory(map), Json.createGeneratorFactory(map), config);
  }

}
