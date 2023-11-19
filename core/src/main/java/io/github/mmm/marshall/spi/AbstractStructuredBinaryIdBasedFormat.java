/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.marshall.spi;

import io.github.mmm.marshall.MarshallingConfig;
import io.github.mmm.marshall.MarshallingObject;

/**
 * Abstract base implementation of {@link MarshallingObject} for objects
 */
public abstract class AbstractStructuredBinaryIdBasedFormat extends AbstractStructuredBinaryFormat {

  /**
   * The constructor.
   *
   * @param config the {@link MarshallingConfig}.
   * @see io.github.mmm.marshall.StructuredFormatFactory#create(String, MarshallingConfig)
   */
  public AbstractStructuredBinaryIdBasedFormat(MarshallingConfig config) {

    super(config);
  }

  @Override
  public final boolean isIdBased() {

    return true;
  }

}
