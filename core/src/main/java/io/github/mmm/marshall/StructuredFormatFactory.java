/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.marshall;

import io.github.mmm.marshall.impl.StructuredFormatFactoryImpl;

/**
 * Factory to create instances of {@link StructuredFormat}.
 */
public interface StructuredFormatFactory {

  /**
   * @param format the {@link StructuredFormatProvider#getId() format name}. E.g. {@link StructuredFormat#ID_JSON JSON},
   *        {@link StructuredFormat#ID_XML XML}, or {@link StructuredFormat#ID_YAML YAML}.
   * @return a new {@link StructuredFormatProvider} for the given {@code format} or {@code null} if no such provider is
   *         registered.
   * @throws io.github.mmm.base.exception.ObjectNotFoundException if no such provider could be found.
   */
  StructuredFormatProvider getProvider(String format);

  /**
   * @param format the {@link StructuredFormatProvider#getId() format name}. E.g. {@link StructuredFormat#ID_JSON JSON},
   *        {@link StructuredFormat#ID_XML XML}, or {@link StructuredFormat#ID_YAML YAML}.
   * @return a new {@link StructuredFormat} for the given {@code format} using default configuration.
   */
  default StructuredFormat create(String format) {

    return getProvider(format).create();
  }

  /**
   * @param format the {@link StructuredFormatProvider#getId() format name}. E.g. {@link StructuredFormat#ID_JSON JSON},
   *        {@link StructuredFormat#ID_XML XML}, or {@link StructuredFormat#ID_YAML YAML}.
   * @param config the {@link MarshallingConfig} to customize the format.
   * @return a new {@link StructuredFormat} for the given {@code format} using the given {@code config}.
   * @see StructuredFormatProvider#create(MarshallingConfig)
   */
  default StructuredFormat create(String format, MarshallingConfig config) {

    return getProvider(format).create(config);
  }

  /**
   * @return the instance of this {@link StructuredFormatFactory}.
   */
  static StructuredFormatFactory get() {

    return StructuredFormatFactoryImpl.INSTANCE;
  }

}
