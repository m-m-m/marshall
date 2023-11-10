/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.marshall.size;

import java.time.temporal.Temporal;

/**
 * Implementation of {@link StructuredFormatSizeComputor} if no pre-calculation of the size is required.
 *
 * @since 1.0.0
 */
public class StructuredFormatSizeComputorNone implements StructuredFormatSizeComputor {

  private static final StructuredFormatSizeComputorNone INSTANCE = new StructuredFormatSizeComputorNone();

  @Override
  public int sizeOfObject(Object value) {

    return -1;
  }

  @Override
  public int sizeOfNumber(Number value) {

    return -1;
  }

  @Override
  public int sizeOfTemporal(Temporal value) {

    return -1;
  }

  /**
   * @return the singleton instance of this class.
   */
  public static StructuredFormatSizeComputorNone get() {

    return INSTANCE;
  }

}
