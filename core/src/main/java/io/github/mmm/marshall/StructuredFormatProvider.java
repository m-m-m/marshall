/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.marshall;

import java.util.Map;

/**
 * Factory to create instances of {@link StructuredFormat}.
 *
 * @since 1.0.0
 */
public interface StructuredFormatProvider {

  /**
   * @return the name of this format. E.g. {@link StructuredFormatFactory#NAME_JSON JSON},
   *         {@link StructuredFormatFactory#NAME_XML XML}, or {@link StructuredFormatFactory#NAME_YAML YAML}.
   */
  String getName();

  /**
   * @return a new {@link StructuredFormat} for {@link #getName() this} format using default configuration.
   */
  StructuredFormat create();

  /**
   * @param configuration the {@link Map} with the configuration to customize the format.
   * @return a new {@link StructuredFormat} for {@link #getName() this} format using the given {@code config}.
   */
  StructuredFormat create(Map<String, Object> configuration);

}
