/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.marshall;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;

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

  /** {@link StructuredFormatProvider#getId() Name} of <a href="https://grpc.io/">gRPC/protobuf format</a>. */
  String ID_PROTOBUF = "application/x-protobuf";

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
  String ATR_STRING_VALUE = "s";

  /** The (XML) attribute for a boolean value. */
  String ATR_BOOLEAN_VALUE = "b";

  /** The (XML) attribute for a numeric value. */
  String ATR_NUMBER_VALUE = "n";

  /**
   * @return the name of this format. E.g. {@link #ID_JSON JSON}, {@link #ID_XML XML}, {@link #ID_YAML YAML}, or
   *         {@link #ID_PROTOBUF gRPC/ProtoBuf}.
   */
  String getId();

  /**
   * @param in the {@link InputStream} pointing to the structured data to read (in UTF-8) and parse.
   * @return the {@link StructuredReader}.
   */
  StructuredReader reader(InputStream in);

  /**
   * @param data the data as a potentially proprietary implementation-specific type.
   * @return the {@link StringReader}.
   */
  default StructuredReader reader(Object data) {

    throw new UnsupportedOperationException();
  }

  /**
   * @param out the {@link OutputStream} where to write the structured data to (in UTF-8).
   * @return the wrapped {@link StructuredWriter}.
   */
  StructuredWriter writer(OutputStream out);

  /**
   * @return the {@link MarshallingConfig} of this format.
   */
  MarshallingConfig getConfig();

  /**
   * @return {@code true} if this is a {@link StructuredBinaryFormat binary format}, {@code false} if
   *         {@link StructuredTextFormat text format}.
   */
  boolean isBinary();

  /**
   * @return {@code true} if this is a {@link StructuredTextFormat text format} , {@code false} if
   *         {@link StructuredBinaryFormat binary format}.
   */
  default boolean isText() {

    return !isBinary();
  }

  /**
   * @return {@code true} if this format uses IDs instead of names to identify properties (like {@link #ID_PROTOBUF
   *         gRPC/ProtoBuf}), {@code false} otherwise.
   * @see StructuredWriter#writeName(String, int)
   * @see StructuredReader#readId()
   */
  default boolean isIdBased() {

    return false;
  }

  /**
   * @return {@code true} if this format supports comments, {@code false} otherwise.
   * @see StructuredWriter#writeComment(String)
   * @see StructuredReader#readComment()
   */
  default boolean isSupportingComments() {

    return false;
  }

}
