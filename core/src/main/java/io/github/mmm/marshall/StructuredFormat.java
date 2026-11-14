/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.marshall;

import java.io.InputStream;
import java.io.OutputStream;

/// Interface providing a structured format such as JSON or XML.
///
/// @since 1.0.0
public interface StructuredFormat {

  /// [Name][StructuredFormatProvider#getId()] of <a href="https://www.json.org/j">JSON format</a>.
  String ID_JSON = "application/json";

  /// [Name][StructuredFormatProvider#getId()] of <a href="https://www.w3.org/XML/">XML format</a>.
  String ID_XML = "text/xml";

  /// [Name][StructuredFormatProvider#getId()] of <a href="https://en.wikipedia.org/wiki/YAML">YAML format</a>.
  String ID_YAML = "text/yaml";

  /// [Name][StructuredFormatProvider#getId()] of <a href="https://grpc.io/">gRPC/protobuf format</a>.
  String ID_PROTOBUF = "application/x-protobuf";

  /// [Name][StructuredFormatProvider#getId()] of
  /// <a href="https://github.com/m-m-m/marshall/blob/master/impl/mrpc/">mRPC format</a>.
  String ID_MRPC = "application/x-mrpc";

  /// The (XML) namespace prefix for an object.
  String NS_PREFIX_OBJECT = "o";

  /// The (XML) namespace URI for an object.
  String NS_URI_OBJECT = "object";

  /// The (XML) namespace prefix for an array.
  String NS_PREFIX_ARRAY = "a";

  /// The (XML) namespace URI for an array.
  String NS_URI_ARRAY = "array";

  /// The (XML) root tag.
  String TAG_ROOT = "json";

  /// The (XML) tag for an array item.
  String TAG_ITEM = "i";

  /// The (XML) attribute for a string value.
  String ATR_STRING_VALUE = "s";

  /// The (XML) attribute for a boolean value.
  String ATR_BOOLEAN_VALUE = "b";

  /// The (XML) attribute for a numeric value.
  String ATR_NUMBER_VALUE = "n";

  /// @return the name of this format. E.g. [JSON][#ID_JSON], [XML][#ID_XML], [YAML][#ID_YAML], or
  ///         [gRPC/ProtoBuf][#ID_PROTOBUF].
  String getId();

  /// @param in the [InputStream] pointing to the structured data to read (in UTF-8) and parse.
  /// @return the [StructuredReader].
  StructuredReader reader(InputStream in);

  /// @param data the data as a potentially proprietary implementation-specific type.
  /// @return the [StructuredReader].
  default StructuredReader reader(Object data) {

    throw new UnsupportedOperationException();
  }

  /// @param out the [OutputStream] where to write the structured data to (in UTF-8).
  /// @return the wrapped [StructuredWriter].
  StructuredWriter writer(OutputStream out);

  /// @return the [MarshallingConfig] of this format.
  MarshallingConfig getConfig();

  /// @return `true` if this is a [binary format][StructuredBinaryFormat], `false` if
  ///         [text format][StructuredTextFormat].
  boolean isBinary();

  /// @return `true` if this is a [text format][StructuredTextFormat] , `false` if
  ///         [binary format][StructuredBinaryFormat].
  default boolean isText() {

    return !isBinary();
  }

  /// @return `true` if this format uses IDs instead of names to identify properties (like [gRPC/ProtoBuf][#ID_PROTOBUF]), `false` otherwise.
  /// @see io.github.mmm.marshall.id.StructuredIdMapping
  /// @see io.github.mmm.marshall.id.StructuredIdMappingProvider
  default boolean isIdBased() {

    return false;
  }

  /// @return `true` if this format supports comments, `false` otherwise.
  /// @see StructuredWriter#writeComment(String)
  /// @see StructuredReader#readComment()
  default boolean isSupportingComments() {

    return false;
  }

}
