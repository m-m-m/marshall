/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.marshall;

/// Simple access for common [marshalling formats][StructuredFormat]. When using [StandardFormat] please
/// ensure to have the according provider implementation available. You will find details about this in the JavaDoc of
/// the non-arg method for each format - see e.g. [#json()].
///
/// @since 1.0.0
public interface StandardFormat {

  /// @return the [StructuredTextFormat] for [JSON][StructuredFormat#ID_JSON] with default configuration. The
  ///         recommended JSON provider is `io.github.mmm.marshall.json` (from artifactId
  ///         `mmm-marshall-json`).
  static StructuredTextFormat json() {

    return StructuredFormatFactory.get().createText(StructuredFormat.ID_JSON);
  }

  /// @param configuration the [MarshallingConfig].
  /// @return the [StructuredTextFormat] for [JSON][StructuredFormat#ID_JSON] with the given
  ///         `configuration`.
  /// @see #json()
  static StructuredTextFormat json(MarshallingConfig configuration) {

    return StructuredFormatFactory.get().createText(StructuredFormat.ID_JSON, configuration);
  }

  /// @return the [StructuredTextFormat] for [XML][StructuredFormat#ID_XML] with default configuration. The
  ///         required XML provider is `io.github.mmm.marshall.stax` (from artifactId `mmm-marshall-stax`).
  static StructuredTextFormat xml() {

    return StructuredFormatFactory.get().createText(StructuredFormat.ID_XML);
  }

  /// @param configuration the [MarshallingConfig].
  /// @return the [StructuredTextFormat] for [XML][StructuredFormat#ID_XML] with the given
  ///         `configuration`.
  /// @see #xml()
  static StructuredTextFormat xml(MarshallingConfig configuration) {

    return StructuredFormatFactory.get().createText(StructuredFormat.ID_XML, configuration);
  }

}
