/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.marshall;

import io.github.mmm.scanner.CharStreamScanner;

/**
 * {@link AbstractStructuredReader} that {@link #readValue() reads values} as {@link Object}.
 */
@SuppressWarnings("exports")
public abstract class AbstractStructuredScannerReader extends AbstractStructuredValueReader {

  /** The {@link CharStreamScanner} to read from. */
  protected final CharStreamScanner reader;

  /**
   * The constructor.
   *
   * @param scanner the {@link CharStreamScanner} to read from.
   * @param format the {@link #getFormat() format}.
   */
  public AbstractStructuredScannerReader(CharStreamScanner scanner, StructuredFormat format) {

    super(format);
    this.reader = scanner;
  }

  @Override
  protected String appendContextDetails(String message) {

    StringBuilder sb = new StringBuilder(message);
    sb.append('(');
    if (this.name != null) {
      sb.append("at property '");
      sb.append(this.name);
      sb.append("' ");
    }
    sb.append("in line ");
    sb.append(this.reader.getLine());
    sb.append(" and column ");
    sb.append(this.reader.getColumn());
    sb.append(')');
    return sb.toString();
  }

  @Override
  public void close() {

    this.reader.close();
  }

}
