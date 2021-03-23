/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.marshall;

import io.github.mmm.base.number.NumberType;

/**
 * {@link AbstractStructuredReader} that {@link #readValue() reads values} as {@link Object}.
 */
public abstract class AbstractStructuredStringReader extends AbstractStructuredReader {

  /**
   * The constructor.
   *
   * @param format the {@link #getFormat() format}.
   */
  public AbstractStructuredStringReader(StructuredFormat format) {

    super(format);
  }

  /**
   * @see #readValueAsString()
   * @return the value as {@link String} but assuring it as number.
   */
  protected String readValueAsNumberString() {

    return readValueAsString();
  }

  @Override
  protected <N extends Number> N readValueAsNumber(NumberType<N> type) {

    String value = readValueAsNumberString();
    if (value == null) {
      return null;
    }
    try {
      return type.valueOf(value);
    } catch (RuntimeException e) {
      throw error(value, type.getType(), e);
    }
  }

}
