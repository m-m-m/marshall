/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.marshall;

/**
 * Abstract base implementation of {@link StructuredWriter}.
 */
public abstract class AbstractStructuredWriter implements StructuredWriter {

  private final StructuredFormat format;

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
   * @param format the {@link #getFormat() format}.
   */
  public AbstractStructuredWriter(StructuredFormat format) {

    super();
    this.format = format;
    this.config = format.getConfig();
    this.writeNullValues = this.config.get(MarshallingConfig.OPT_WRITE_NULL_VALUES).booleanValue();
    this.indentation = this.config.get(MarshallingConfig.OPT_INDENTATION);
  }

  @Override
  public StructuredFormat getFormat() {

    return this.format;
  }

  @Override
  public void writeName(String newName, int newId) {

    if (this.name != null) {
      throw new IllegalStateException("Cannot write name " + newName + " while previous name " + this.name
          + " has not been processed! Forgot to call writeStartObject()?");
    }
    this.name = newName;
  }

}
