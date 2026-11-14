/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.marshall;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;

/// [StructuredFormat] that is [binary][#isBinary()].
///
/// @since 1.0.0
public interface StructuredTextFormat extends StructuredFormat {

  /// @param reader the [Reader] pointing to the structured data to read and parse.
  /// @return the [StructuredReader].
  StructuredReader reader(Reader reader);

  /// @param data the structured data as [String].
  /// @return the [StructuredReader].
  default StructuredReader reader(String data) {

    return reader(new StringReader(data));
  }

  @Override
  default StructuredReader reader(InputStream in) {

    return reader(new InputStreamReader(in, StandardCharsets.UTF_8));
  }

  @Override
  default StructuredReader reader(Object data) {

    if (data instanceof CharSequence) {
      return reader(data.toString());
    }
    return StructuredFormat.super.reader(data);
  }

  /// @param <T> type of the [UnmarshallableObject].
  /// @param data the structured data as [String].
  /// @param object the [UnmarshallableObject] to read.
  /// @return typically this object itself (however, for polymorphic unmarshalling it may also be a sub-type instance).
  /// @see UnmarshallableObject#read(StructuredReader)
  @SuppressWarnings("unchecked")
  default <T extends UnmarshallableObject> T read(String data, T object) {

    try (StructuredReader reader = reader(data)) {
      return (T) object.read(reader);
    }
  }

  /// @param writer the [Appendable] ([java.io.Writer] or [StringBuilder]) where to write the
  ///        structured data to.
  /// @return the wrapped [StructuredWriter].
  StructuredWriter writer(Appendable writer);

  @Override
  default StructuredWriter writer(OutputStream out) {

    return writer(new OutputStreamWriter(out, StandardCharsets.UTF_8));
  }

  /// @param object the [MarshallableObject] to serialize.
  /// @return the serialized data as [String].
  default String write(MarshallableObject object) {

    if (object == null) {
      return null;
    }
    StringBuilder writer = new StringBuilder(512);
    StructuredWriter structuredWriter = writer(writer);
    object.write(structuredWriter);
    structuredWriter.close();
    return writer.toString();
  }

  @Override
  default boolean isBinary() {

    return false;
  }

}
