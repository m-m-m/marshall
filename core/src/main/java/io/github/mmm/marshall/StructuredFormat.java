/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.marshall;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;

/**
 * Interface providing a structured format such as JSON or XML.
 *
 * @since 1.0.0
 */
public interface StructuredFormat {

  /** {@link StructuredFormatProvider#getId() Name} of <a href="https://www.json.org/j">JSON format</a>. */
  String ID_JSON = "application/json";

  /** {@link StructuredFormatProvider#getId() Name} of <a href="https://www.w3.org/XML/">XML format</a>. */
  String ID_XML = "text/xml";

  /**
   * {@link StructuredFormatProvider#getId() Name} of <a href="https://en.wikipedia.org/wiki/YAML">YAML format</a>.
   */
  String ID_YAML = "text/yaml";

  /** The (XML) namespace prefix for an object. */
  String NS_PREFIX_OBJECT = "o";

  /** The (XML) namespace URI for an object. */
  String NS_URI_OBJECT = "object";

  /** The (XML) namespace prefix for an array. */
  String NS_PREFIX_ARRAY = "a";

  /** The (XML) namespace URI for an array. */
  String NS_URI_ARRAY = "array";

  /** The (XML) root tag. */
  String TAG_ROOT = "json";

  /** The (XML) tag for an array item. */
  String TAG_ITEM = "i";

  /** The (XML) attribute for a string value. */
  String ART_STRING_VALUE = "s";

  /** The (XML) attribute for a boolean value. */
  String ART_BOOLEAN_VALUE = "b";

  /** The (XML) attribute for a numeric value. */
  String ART_NUMBER_VALUE = "n";

  /**
   * @return the name of this format. E.g. {@link #ID_JSON JSON}, {@link #ID_XML XML}, or {@link #ID_YAML YAML}.
   */
  String getId();

  /**
   * @param reader the {@link Reader} pointing to the structured data to read and parse.
   * @return the {@link StructuredReader}.
   */
  StructuredReader reader(Reader reader);

  /**
   * @param in the {@link InputStream} pointing to the structured data to read (in UTF-8) and parse.
   * @return the {@link StructuredReader}.
   */
  default StructuredReader reader(InputStream in) {

    return reader(new InputStreamReader(in, StandardCharsets.UTF_8));
  }

  /**
   * @param data the structured data as {@link String}.
   * @return the {@link StructuredReader}.
   */
  default StructuredReader reader(String data) {

    return reader(new StringReader(data));
  }

  /**
   * @param data the data as a potentially proprietary implementation-specific type.
   * @return the {@link StringReader}.
   */
  default StructuredReader reader(Object data) {

    if (data instanceof CharSequence) {
      return reader(data.toString());
    }
    throw new UnsupportedOperationException();
  }

  /**
   * @param writer the {@link Writer} where to write the structured data to.
   * @return the wrapped {@link StructuredWriter}.
   */
  StructuredWriter writer(Writer writer);

  /**
   * @param out the {@link OutputStream} where to write the structured data to (in UTF-8).
   * @return the wrapped {@link StructuredWriter}.
   */
  default StructuredWriter writer(OutputStream out) {

    return writer(new OutputStreamWriter(out, StandardCharsets.UTF_8));
  }

  /**
   * @param object the {@link MarshallableObject} to serialize.
   * @return the serialized data as {@link String}.
   */
  default String write(MarshallableObject object) {

    if (object == null) {
      return null;
    }
    StringWriter writer = new StringWriter(512);
    StructuredWriter structuredWriter = writer(writer);
    object.write(structuredWriter);
    return writer.toString();
  }

}
