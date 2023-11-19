/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.marshall.jsonp.impl;

import java.io.Reader;
import java.util.HashMap;
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
import io.github.mmm.marshall.spi.AbstractStructuredTextFormat;

/**
 * Implementation of {@link StructuredFormat} for JSON (JavaScript Object Notation).
 *
 * @since 1.0.0
 */
public class JsonpFormat extends AbstractStructuredTextFormat {

  private static final JsonpFormat DEFAULT = of(MarshallingConfig.DEFAULTS);

  private final JsonGeneratorFactory writerFactory;

  private final JsonParserFactory readerFactory;

  /**
   * The constructor.
   *
   * @param readerFactory the {@link JsonParserFactory}.
   * @param writerFactory the {@link JsonGeneratorFactory}.
   * @param config the {@link MarshallingConfig}.
   * @see io.github.mmm.marshall.StructuredFormatFactory#create(String, MarshallingConfig)
   */
  public JsonpFormat(JsonParserFactory readerFactory, JsonGeneratorFactory writerFactory, MarshallingConfig config) {

    super(config);
    this.writerFactory = writerFactory;
    this.readerFactory = readerFactory;
  }

  @Override
  public String getId() {

    return ID_JSON;
  }

  @Override
  public StructuredReader reader(Reader reader) {

    JsonParser json = this.readerFactory.createParser(reader);
    return new JsonpReader(json, this);
  }

  @Override
  public StructuredWriter writer(Appendable writer) {

    JsonGenerator json = this.writerFactory.createGenerator(AppendableWriter.asWriter(writer));
    return new JsonpWriter(json, this);
  }

  /**
   * @return the default instance of {@link JsonpFormat}.
   */
  public static JsonpFormat of() {

    return DEFAULT;
  }

  /**
   * @param config the {@link MarshallingConfig} for the JSON vendor implementation.
   * @return the new instance of {@link JsonpFormat} with the given {@code config}.
   */
  public static JsonpFormat of(MarshallingConfig config) {

    if (config == null) {
      return DEFAULT;
    }
    Map<String, Object> map = new HashMap<>();
    String indentation = config.get(MarshallingConfig.VAR_INDENTATION);
    if (indentation != null) {
      map.put(JsonGenerator.PRETTY_PRINTING, Boolean.TRUE);
    }
    return new JsonpFormat(Json.createParserFactory(map), Json.createGeneratorFactory(map), config);
  }

}
