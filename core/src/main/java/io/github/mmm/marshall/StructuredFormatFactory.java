/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.marshall;

import io.github.mmm.marshall.impl.StructuredFormatFactoryImpl;

/// Factory to create instances of [StructuredFormat].
public interface StructuredFormatFactory {

  /// @param formatId the [format Id][StructuredFormatProvider#getId()]. May also be a
  ///        [alias][AbstractStructuredFormatProvider#getAliases()].
  /// @return a new [StructuredFormatProvider] for the given `formatId` or `null` if no such provider
  ///         is registered.
  /// @throws io.github.mmm.base.exception.ObjectNotFoundException if no such provider could be found.
  StructuredFormatProvider getProvider(String formatId);

  /// @param formatId the [format ID][StructuredFormat#getId()].
  /// @return a new [StructuredFormat] for the given `formatId` using default configuration.
  default StructuredFormat create(String formatId) {

    return create(formatId, null);
  }

  /// @param formatId the [format ID][StructuredFormat#getId()].
  /// @param config the [MarshallingConfig] to customize the format.
  /// @return a new [StructuredFormat] for the given `formatId` using the given `config`.
  /// @see StructuredFormatProvider#create(MarshallingConfig)
  default StructuredFormat create(String formatId, MarshallingConfig config) {

    return getProvider(formatId).create(config);
  }

  /// @param formatId the [format ID][StructuredFormat#getId()].
  /// @return a new [StructuredTextFormat] for the given `formatId` using default configuration.
  /// @see StructuredTextFormatProvider#create()
  default StructuredTextFormat createText(String formatId) {

    return createText(formatId, null);
  }

  /// @param formatId the [format ID][StructuredFormatProvider#getId()]. E.g. [JSON][StructuredFormat#ID_JSON],
  ///        [XML][StructuredFormat#ID_XML], or [YAML][StructuredFormat#ID_YAML].
  /// @param config the [MarshallingConfig] to customize the format.
  /// @return a new [StructuredTextFormat] for the given `formatId` using the given `config`.
  /// @see StructuredTextFormatProvider#create(MarshallingConfig)
  default StructuredTextFormat createText(String formatId, MarshallingConfig config) {

    StructuredFormat format = getProvider(formatId).create(config);
    if (!format.isText()) {
      throw new IllegalStateException("Format is not text: " + formatId);
    }
    return (StructuredTextFormat) format;
  }

  /// @param formatId the [format ID][StructuredFormat#getId()].
  /// @return a new [StructuredBinaryFormat] for the given `formatId` using default configuration.
  /// @see StructuredBinaryFormatProvider#create()
  default StructuredBinaryFormat createBinary(String formatId) {

    return createBinary(formatId, null);
  }

  /// @param formatId the [format ID][StructuredFormatProvider#getId()]. E.g. [JSON][StructuredFormat#ID_JSON],
  ///        [XML][StructuredFormat#ID_XML], or [YAML][StructuredFormat#ID_YAML].
  /// @param config the [MarshallingConfig] to customize the format.
  /// @return a new [StructuredBinaryFormat] for the given `formatId` using the given `config`.
  /// @see StructuredBinaryFormatProvider#create(MarshallingConfig)
  default StructuredBinaryFormat createBinary(String formatId, MarshallingConfig config) {

    StructuredFormat format = getProvider(formatId).create(config);
    if (!format.isBinary()) {
      throw new IllegalStateException("Format is not binary: " + formatId);
    }
    return (StructuredBinaryFormat) format;
  }

  /// @return the instance of this [StructuredFormatFactory].
  static StructuredFormatFactory get() {

    return StructuredFormatFactoryImpl.INSTANCE;
  }

}
