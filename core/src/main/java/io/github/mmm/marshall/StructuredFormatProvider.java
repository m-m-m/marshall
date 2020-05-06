/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.marshall;

/**
 * Factory to create instances of {@link StructuredFormat}.
 *
 * @since 1.0.0
 */
public interface StructuredFormatProvider {

  /**
   * @return the {@link StructuredFormat#getId() ID} of the {@link StructuredFormat} to {@link #create()}.
   */
  String getId();

  /**
   * @return a new {@link StructuredFormat} for {@link #getId() this} format using default configuration.
   */
  StructuredFormat create();

  /**
   * @param config the {@link MarshallingConfig} to customize the format.
   * @return a new {@link StructuredFormat} for {@link #getId() this} format using the given {@code config}.
   */
  StructuredFormat create(MarshallingConfig config);

}
