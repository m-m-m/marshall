/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.marshall;

import java.io.IOException;

/**
 * {@link AbstractStructuredWriter} for writing data as {@link String} to {@link Appendable}.
 *
 * @since 1.0.0
 */
public abstract class AbstractStructuredStringWriter extends AbstractStructuredWriter {

  /** The {@link Appendable} where to {@link Appendable#append(CharSequence) write} the data to. */
  protected Appendable out;

  /** The current indentation count. */
  protected int indentCount;

  /**
   * The constructor.
   *
   * @param out the {@link Appendable} to write the data to.
   * @param format the {@link #getFormat() format}.
   */
  public AbstractStructuredStringWriter(Appendable out, StructuredFormat format) {

    super(format);
    this.out = out;
  }

  /**
   * Writes a new indentation.
   */
  protected void writeIndent() {

    writeIndent(this.indentCount);
  }

  /**
   * Writes a new indentation.
   *
   * @param count the number of indentations to write.
   */
  protected void writeIndent(int count) {

    if (this.indentation == null) {
      return;
    }
    try {
      this.out.append('\n');
      for (int i = count; i > 0; i--) {
        this.out.append(this.indentation);
      }
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
  }

  /**
   * @param data the {@code char} to write.
   */
  protected void write(char data) {

    try {
      this.out.append(data);
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
  }

  /**
   * @param data the {@link String} to write.
   */
  protected void write(String data) {

    try {
      this.out.append(data);
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
  }

  @Override
  public void close() {

    if (this.out == null) {
      return;
    }
    if (this.out instanceof AutoCloseable) {
      try {
        ((AutoCloseable) this.out).close();
      } catch (Exception e) {
        throw new IllegalStateException(e);
      }
    }
    this.out = null;
  }

}
