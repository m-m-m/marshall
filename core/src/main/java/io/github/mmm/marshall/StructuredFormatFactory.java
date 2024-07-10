/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.marshall;

import io.github.mmm.marshall.impl.StructuredFormatFactoryImpl;

/**
 * Factory to create instances of {@link StructuredFormat}.
 */
public interface StructuredFormatFactory {

  /**
   * @param formatId the {@link StructuredFormatProvider#getId() format Id}. May also be a
   *        {@link AbstractStructuredFormatProvider#getAliases() alias}.
   * @return a new {@link StructuredFormatProvider} for the given {@code formatId} or {@code null} if no such provider
   *         is registered.
   * @throws io.github.mmm.base.exception.ObjectNotFoundException if no such provider could be found.
   */
  StructuredFormatProvider getProvider(String formatId);

  /**
   * @param formatId the {@link StructuredFormat#getId() format ID}.
   * @return a new {@link StructuredFormat} for the given {@code formatId} using default configuration.
   */
  default StructuredFormat create(String formatId) {

    return create(formatId, null);
  }

  /**
   * @param formatId the {@link StructuredFormat#getId() format ID}.
   * @param config the {@link MarshallingConfig} to customize the format.
   * @return a new {@link StructuredFormat} for the given {@code formatId} using the given {@code config}.
   * @see StructuredFormatProvider#create(MarshallingConfig)
   */
  default StructuredFormat create(String formatId, MarshallingConfig config) {

    return getProvider(formatId).create(config);
  }

  /**
   * @param formatId the {@link StructuredFormat#getId() format ID}.
   * @return a new {@link StructuredTextFormat} for the given {@code formatId} using default configuration.
   * @see StructuredTextFormatProvider#create()
   */
  default StructuredTextFormat createText(String formatId) {

    return createText(formatId, null);
  }

  /**
   * @param formatId the {@link StructuredFormatProvider#getId() format ID}. E.g. {@link StructuredFormat#ID_JSON JSON},
   *        {@link StructuredFormat#ID_XML XML}, or {@link StructuredFormat#ID_YAML YAML}.
   * @param config the {@link MarshallingConfig} to customize the format.
   * @return a new {@link StructuredTextFormat} for the given {@code formatId} using the given {@code config}.
   * @see StructuredTextFormatProvider#create(MarshallingConfig)
   */
  default StructuredTextFormat createText(String formatId, MarshallingConfig config) {

    StructuredFormat format = getProvider(formatId).create(config);
    if (!format.isText()) {
      throw new IllegalStateException("Format is not text: " + formatId);
    }
    return (StructuredTextFormat) format;
  }

  /**
   * @param formatId the {@link StructuredFormat#getId() format ID}.
   * @return a new {@link StructuredBinaryFormat} for the given {@code formatId} using default configuration.
   * @see StructuredBinaryFormatProvider#create()
   */
  default StructuredBinaryFormat createBinary(String formatId) {

    return createBinary(formatId, null);
  }

  /**
   * @param formatId the {@link StructuredFormatProvider#getId() format ID}. E.g. {@link StructuredFormat#ID_JSON JSON},
   *        {@link StructuredFormat#ID_XML XML}, or {@link StructuredFormat#ID_YAML YAML}.
   * @param config the {@link MarshallingConfig} to customize the format.
   * @return a new {@link StructuredBinaryFormat} for the given {@code formatId} using the given {@code config}.
   * @see StructuredBinaryFormatProvider#create(MarshallingConfig)
   */
  default StructuredBinaryFormat createBinary(String formatId, MarshallingConfig config) {

    StructuredFormat format = getProvider(formatId).create(config);
    if (!format.isBinary()) {
      throw new IllegalStateException("Format is not binary: " + formatId);
    }
    return (StructuredBinaryFormat) format;
  }

  /**
   * @return the instance of this {@link StructuredFormatFactory}.
   */
  static StructuredFormatFactory get() {

    return StructuredFormatFactoryImpl.INSTANCE;
  }

}
