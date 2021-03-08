/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.marshall;

/**
 * {@link #of() Simple access} for {@link StructuredFormat#ID_XML XML} format. When using {@link XmlFormat} please
 * ensure to have one provide implementation for XML on your module-path. The recommended one is
 * {@code io.github.mmm.marshall.xml} (from artifactId {@code mmm-marshall-xml}).
 *
 * @since 1.0.0
 */
public interface XmlFormat {

  /**
   * @return the {@link StructuredTextFormat} for {@link StructuredFormat#ID_XML XML} with default configuration.
   */
  static StructuredTextFormat of() {

    return StructuredFormatFactory.get().createText(StructuredFormat.ID_XML);
  }

  /**
   * @param configuration the {@link MarshallingConfig}.
   * @return the {@link StructuredTextFormat} for {@link StructuredFormat#ID_XML XML} with the given
   *         {@code configuration}.
   */
  static StructuredTextFormat of(MarshallingConfig configuration) {

    return StructuredFormatFactory.get().createText(StructuredFormat.ID_XML, configuration);
  }

}
