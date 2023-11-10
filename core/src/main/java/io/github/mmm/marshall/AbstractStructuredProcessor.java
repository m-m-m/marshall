/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.marshall;

/**
 * Abstract base implementation of {@link StructuredReader}.
 */
public abstract class AbstractStructuredProcessor implements StructuredProcessor {

  private final StructuredFormat format;

  /** The {@link MarshallingConfig}. */
  protected final MarshallingConfig config;

  /** @see MarshallingConfig#OPT_ENUM_FORMAT */
  protected final EnumFormat enumFormat;

  /**
   * The constructor.
   *
   * @param format the {@link #getFormat() format}.
   */
  public AbstractStructuredProcessor(StructuredFormat format) {

    super();
    this.format = format;
    this.config = format.getConfig();
    this.enumFormat = this.config.get(MarshallingConfig.OPT_ENUM_FORMAT);
  }

  @Override
  public StructuredFormat getFormat() {

    return this.format;
  }

}
