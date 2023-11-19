/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.marshall.spi;

import java.util.Objects;

import io.github.mmm.marshall.MarshallingConfig;
import io.github.mmm.marshall.StructuredTextFormat;

/**
 * Abstract base implementation of {@link StructuredTextFormat}.
 */
public abstract class AbstractStructuredTextFormat extends AbstractStructuredFormat implements StructuredTextFormat {

  /**
   * The constructor.
   *
   * @param config the {@link MarshallingConfig}.
   */
  public AbstractStructuredTextFormat(MarshallingConfig config) {

    super(config);
    Objects.requireNonNull(config);
  }

  @Override
  public final boolean isBinary() {

    return false;
  }

}
