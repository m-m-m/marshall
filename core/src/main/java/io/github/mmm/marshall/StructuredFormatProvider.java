/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.marshall;

/// Factory to create instances of [StructuredFormat].
///
/// @since 1.0.0
public interface StructuredFormatProvider {

  /// @return the [ID][StructuredFormat#getId()] of the [StructuredFormat] to [#create()]. E.g.
  ///         [JSON][StructuredFormat#ID_JSON], [XML][StructuredFormat#ID_XML], [YAML][StructuredFormat#ID_YAML], or [StructuredFormat#ID_PROTOBUF].
  /// @see StructuredFormat#getId()
  String getId();

  /// @return a new [StructuredFormat] for [this][#getId()] format using default configuration.
  StructuredFormat create();

  /// @param config the [MarshallingConfig] to customize the format.
  /// @return a new [StructuredFormat] for [this][#getId()] format using the given `config`.
  StructuredFormat create(MarshallingConfig config);

}
