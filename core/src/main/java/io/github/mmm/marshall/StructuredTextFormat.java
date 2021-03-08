package io.github.mmm.marshall;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;

/**
 * {@link StructuredFormat} that is {@link #isBinary() binary}.
 *
 * @since 1.0.0
 */
public interface StructuredTextFormat extends StructuredFormat {

  /**
   * @param reader the {@link Reader} pointing to the structured data to read and parse.
   * @return the {@link StructuredReader}.
   */
  StructuredReader reader(Reader reader);

  /**
   * @param data the structured data as {@link String}.
   * @return the {@link StructuredReader}.
   */
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

  /**
   * @param data the structured data as {@link String}.
   * @param object the {@link UnmarshallableObject} to read.
   * @see UnmarshallableObject#read(StructuredReader)
   */
  default void read(String data, UnmarshallableObject object) {

    StructuredReader reader = reader(data);
    object.read(reader);
    reader.close();
  }

  /**
   * @param writer the {@link Appendable} ({@link java.io.Writer} or {@link StringBuilder}) where to write the
   *        structured data to.
   * @return the wrapped {@link StructuredWriter}.
   */
  StructuredWriter writer(Appendable writer);

  @Override
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
