/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.marshall;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;

/**
 * Interface providing a structured format such as JSON or XML.
 *
 * @since 1.0.0
 */
public interface StructuredFormat {

  /**
   * @param reader the {@link Reader} pointing to the structured data to read and parse.
   * @return the wrapped {@link StructuredReader}.
   */
  StructuredReader reader(Reader reader);

  /**
   * @param in the {@link InputStream} pointing to the structured data to read (in UTF-8) and parse.
   * @return the wrapped {@link StructuredReader}.
   */
  default StructuredReader reader(InputStream in) {

    return reader(new InputStreamReader(in, StandardCharsets.UTF_8));
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
