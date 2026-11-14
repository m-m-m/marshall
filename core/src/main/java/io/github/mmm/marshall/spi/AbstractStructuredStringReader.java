/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.marshall.spi;

import io.github.mmm.base.number.NumberType;
import io.github.mmm.marshall.StructuredFormat;

/// [AbstractStructuredReader] that [reads values][#readValue()] from a textual representation.
///
/// @param <S> type of the [StructuredNode].
/// @since 1.0.0
public abstract class AbstractStructuredStringReader<S extends StructuredNode<S>> extends AbstractStructuredReader<S> {

  /// The constructor.
  ///
  /// @param format the [format][#getFormat()].
  public AbstractStructuredStringReader(StructuredFormat format) {

    super(format);
  }

  /// @see #readValueAsString()
  /// @return the value as [String] but assuring it as number.
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
      return type.parse(value);
    } catch (RuntimeException e) {
      throw error(value, type.getType(), e);
    }
  }

}
