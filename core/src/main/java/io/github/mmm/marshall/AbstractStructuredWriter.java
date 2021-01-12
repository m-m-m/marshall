/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.marshall;

/**
 * Abstract base implementation of {@link StructuredWriter}.
 */
public abstract class AbstractStructuredWriter implements StructuredWriter {

  /** The {@link MarshallingConfig}. */
  protected final MarshallingConfig config;

  /** @see #writeValueAsNull() */
  protected final boolean writeNullValues;

  /** @see MarshallingConfig#OPT_INDENTATION */
  protected final String indentation;

  /**
   * The current name.
   *
   * @see #writeName(String)
   */
  protected String name;

  /**
   * The constructor.
   *
   * @param config the {@link MarshallingConfig}.
   */
  public AbstractStructuredWriter(MarshallingConfig config) {

    super();
    this.config = config;
    this.writeNullValues = config.get(MarshallingConfig.OPT_WRITE_NULL_VALUES).booleanValue();
    this.indentation = config.get(MarshallingConfig.OPT_INDENTATION);
  }

  @Override
  public void writeName(String newName) {

    if (this.name != null) {
      throw new IllegalStateException("Cannot write name " + newName + " while previous name " + this.name
          + " has not been processed! Forgot to call writeStartObject()?");
    }
    this.name = newName;
  }

}
