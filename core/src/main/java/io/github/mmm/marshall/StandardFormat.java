/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.marshall;

/**
 * Simple access for common {@link StructuredFormat marshalling formats}. When using {@link StandardFormat} please
 * ensure to have the according provider implementation available. You will find details about this in the JavaDoc of
 * the non-arg method for each format - see e.g. {@link #json()}.
 *
 * @since 1.0.0
 */
public interface StandardFormat {

  /**
   * @return the {@link StructuredTextFormat} for {@link StructuredFormat#ID_JSON JSON} with default configuration. The
   *         recommended JSON provider is {@code io.github.mmm.marshall.json} (from artifactId
   *         {@code mmm-marshall-json}).
   */
  static StructuredTextFormat json() {

    return StructuredFormatFactory.get().createText(StructuredFormat.ID_JSON);
  }

  /**
   * @param configuration the {@link MarshallingConfig}.
   * @return the {@link StructuredTextFormat} for {@link StructuredFormat#ID_JSON JSON} with the given
   *         {@code configuration}.
   * @see #json()
   */
  static StructuredTextFormat json(MarshallingConfig configuration) {

    return StructuredFormatFactory.get().createText(StructuredFormat.ID_JSON, configuration);
  }

  /**
   * @return the {@link StructuredTextFormat} for {@link StructuredFormat#ID_XML XML} with default configuration. The
   *         required XML provider is {@code io.github.mmm.marshall.stax} (from artifactId {@code mmm-marshall-stax}).
   */
  static StructuredTextFormat xml() {

    return StructuredFormatFactory.get().createText(StructuredFormat.ID_XML);
  }

  /**
   * @param configuration the {@link MarshallingConfig}.
   * @return the {@link StructuredTextFormat} for {@link StructuredFormat#ID_XML XML} with the given
   *         {@code configuration}.
   * @see #xml()
   */
  static StructuredTextFormat xml(MarshallingConfig configuration) {

    return StructuredFormatFactory.get().createText(StructuredFormat.ID_XML, configuration);
  }

}
