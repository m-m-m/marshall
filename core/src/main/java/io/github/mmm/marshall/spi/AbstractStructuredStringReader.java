/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.marshall.spi;

import io.github.mmm.base.number.NumberType;
import io.github.mmm.marshall.StructuredFormat;

/**
 * {@link AbstractStructuredReader} that {@link #readValue() reads values} from a textual representation.
 *
 * @param <S> type of the {@link StructuredNode}.
 * @since 1.0.0
 */
public abstract class AbstractStructuredStringReader<S extends StructuredNode<S>> extends AbstractStructuredReader<S> {

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
