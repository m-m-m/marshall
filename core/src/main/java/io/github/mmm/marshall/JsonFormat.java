/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.marshall;

/**
 * {@link #of() Simple access} for JSON format. When using {@link JsonFormat} please ensure to have one provide
 * implementation for JSON on your module-path. The recommended one is {@code io.github.mmm.marshall.json} (from
 * artifactId {@code mmm-marshall-json}).
 *
 * @since 1.0.0
 */
public interface JsonFormat {

  /**
   * @return the {@link StructuredFormat} for {@link StructuredFormat#ID_JSON JSON} with default configuration.
   */
  static StructuredFormat of() {

    return StructuredFormatFactory.get().create(StructuredFormat.ID_JSON);
  }

  /**
   * @param configuration the {@link MarshallingConfig}.
   * @return the {@link StructuredFormat} for {@link StructuredFormat#ID_JSON JSON} with the given
   *         {@code configuration}.
   */
  static StructuredFormat of(MarshallingConfig configuration) {

    return StructuredFormatFactory.get().create(StructuredFormat.ID_JSON, configuration);
  }

}
