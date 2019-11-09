/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.marshall;

/**
 * Abstract base implementation of {@link StructuredWriter}.
 */
public abstract class AbstractStructuredWriter implements StructuredWriter {

  /**
   * The current name.
   *
   * @see #writeName(String)
   */
  protected String name;

  @Override
  public void writeName(String newName) {

    if (this.name != null) {
      throw new IllegalStateException("Cannot write name " + newName + " while previous name " + this.name
          + " has not been processed! Forgot to call writeStartObject()?");
    }
    this.name = newName;
  }

}
