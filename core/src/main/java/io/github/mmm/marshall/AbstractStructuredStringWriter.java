/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.marshall;

import java.io.IOException;

import io.github.mmm.marshall.spi.AbstractStructuredWriter;
import io.github.mmm.marshall.spi.StructuredNode;

/// [AbstractStructuredWriter] for writing data as [String] to [Appendable].
///
/// @param <S> type of the [StructuredNode].
/// @since 1.0.0
public abstract class AbstractStructuredStringWriter<S extends StructuredNode<S>> extends AbstractStructuredWriter<S> {

  /// The [Appendable] where to [write][Appendable#append(CharSequence)] the data to.
  protected Appendable out;

  /// @see #writeComment(String)
  private String comment;

  private boolean newlineWritten;

  /// The constructor.
  ///
  /// @param out the [Appendable] to write the data to.
  /// @param format the [format][#getFormat()].
  public AbstractStructuredStringWriter(Appendable out, StructuredFormat format) {

    super(format);
    this.out = out;
  }

  /// Writes a new indentation.
  protected void writeIndent() {

    if ((this.comment != null) && getFormat().isSupportingComments()) {
      writeIndent(this.indentCount);
      doWriteComment(this.comment);
      this.comment = null;
    }
    writeIndent(this.indentCount);
  }

  /// Writes a new indentation.
  ///
  /// @param count the number of indentations to write.
  private void writeIndent(int count) {

    if (this.indentation == null) {
      return;
    }
    try {
      if (this.newlineWritten) {
        this.out.append('\n');
      } else {
        this.newlineWritten = true;
      }
      for (int i = count; i > 0; i--) {
        this.out.append(this.indentation);
      }
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
  }

  /// @param data the `char` to write.
  protected void write(char data) {

    try {
      this.out.append(data);
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
  }

  /// @param data the [String] to write.
  protected void write(String data) {

    try {
      this.out.append(data);
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
  }

  @Override
  public void writeComment(String newComment) {

    if (newComment == null) {
      return;
    }
    if (this.comment == null) {
      this.comment = newComment;
    } else {
      this.comment = this.comment + "\n" + newComment;
    }
  }

  /// @param currentComment the comment to write physically at the current position.
  protected void doWriteComment(String currentComment) {

  }

  @Override
  protected void doClose() {

    if (this.out instanceof AutoCloseable) {
      try {
        ((AutoCloseable) this.out).close();
      } catch (Exception e) {
        throw new IllegalStateException(e);
      }
    }
    this.out = null;
  }

  @Override
  public String toString() {

    return this.out.toString();
  }

}
