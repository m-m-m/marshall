/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.marshall;

/**
 * Base interface for {@link StructuredReader} and {@link StructuredWriter}.
 *
 * @since 1.0.0
 */
public abstract interface StructuredProcessor extends AutoCloseable {

  @Override
  void close();

  /**
   * @return the owning {@link StructuredFormat} that {@link StructuredFormat#reader(java.io.InputStream) created} this
   *         writer.
   */
  StructuredFormat getFormat();

}
