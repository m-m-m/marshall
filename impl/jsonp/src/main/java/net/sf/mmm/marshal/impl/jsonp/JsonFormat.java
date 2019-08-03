package net.sf.mmm.marshal.impl.jsonp;

import java.io.Reader;
import java.io.Writer;
import java.util.Map;

import javax.json.Json;
import javax.json.stream.JsonGenerator;
import javax.json.stream.JsonGeneratorFactory;
import javax.json.stream.JsonParser;
import javax.json.stream.JsonParserFactory;

import net.sf.mmm.marshal.api.StructuredFormat;
import net.sf.mmm.marshal.api.StructuredReader;
import net.sf.mmm.marshal.api.StructuredWriter;

/**
 * Implementation of {@link StructuredFormat} for JSON (JavaScript Object Notation).
 *
 * @since 1.0.0
 */
public class JsonFormat implements StructuredFormat {

  private static final JsonFormat DEFAULT = of(null);

  private final JsonGeneratorFactory writerFactory;

  private final JsonParserFactory readerFactory;

  private JsonFormat(JsonGeneratorFactory writerFactory, JsonParserFactory readerFactory) {

    super();
    this.writerFactory = writerFactory;
    this.readerFactory = readerFactory;
  }

  @Override
  public StructuredReader reader(Reader reader) {

    JsonParser json = this.readerFactory.createParser(reader);
    return new JsonReader(json);
  }

  @Override
  public StructuredWriter writer(Writer writer) {

    JsonGenerator json = this.writerFactory.createGenerator(writer);
    return new JsonWriter(json);
  }

  /**
   * @return the default instance of {@link JsonFormat}.
   */
  public static JsonFormat of() {

    return DEFAULT;
  }

  /**
   * @param config the {@link Map} with the configuration properties for the JSON vendor implementation.
   * @return the new instance of {@link JsonFormat} with the given {@code config}.
   */
  public static JsonFormat of(Map<String, ?> config) {

    return new JsonFormat(Json.createGeneratorFactory(config), Json.createParserFactory(config));
  }

}
