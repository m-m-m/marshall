package net.sf.mmm.marshall.base;

import net.sf.mmm.marshall.api.StructuredWriter;

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
