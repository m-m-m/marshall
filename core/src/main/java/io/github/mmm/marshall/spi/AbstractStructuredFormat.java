/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.marshall.spi;

import java.util.Objects;

import io.github.mmm.marshall.MarshallingConfig;
import io.github.mmm.marshall.StructuredFormat;

/**
 * Abstract base implementation of {@link StructuredFormat}.
 */
public abstract class AbstractStructuredFormat implements StructuredFormat {

  private final MarshallingConfig config;

  /**
   * The constructor.
   *
   * @param config the {@link MarshallingConfig}.
   * @see io.github.mmm.marshall.StructuredFormatFactory#create(String, MarshallingConfig)
   */
  public AbstractStructuredFormat(MarshallingConfig config) {

    super();
    Objects.requireNonNull(config);
    this.config = config;
  }

  @Override
  public MarshallingConfig getConfig() {

    return this.config;
  }

}
