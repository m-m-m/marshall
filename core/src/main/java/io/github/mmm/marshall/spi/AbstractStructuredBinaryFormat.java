/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.marshall.spi;

import java.util.Objects;

import io.github.mmm.marshall.MarshallingConfig;
import io.github.mmm.marshall.StructuredBinaryFormat;

/**
 * Abstract base implementation of {@link StructuredBinaryFormat}.
 */
public abstract class AbstractStructuredBinaryFormat extends AbstractStructuredFormat
    implements StructuredBinaryFormat {

  /**
   * The constructor.
   *
   * @param config the {@link MarshallingConfig}.
   */
  public AbstractStructuredBinaryFormat(MarshallingConfig config) {

    super(config);
    Objects.requireNonNull(config);
  }

  @Override
  public final boolean isBinary() {

    return true;
  }

}
