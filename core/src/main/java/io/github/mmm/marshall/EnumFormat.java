/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.marshall;

import io.github.mmm.base.text.CaseHelper;
import io.github.mmm.base.text.CaseSyntax;

/**
 * {@link Enum} with the available formats how to serialize an {@link Enum}.
 */
public enum EnumFormat {

  /** Write {@link Enum} as {@link Enum#ordinal() ordinal}. */
  ORDINAL,

  /** Write {@link Enum} as {@link Enum#name() name}. */
  NAME,

  /** Write {@link Enum} as {@link Enum#toString() toString representation}. */
  TO_STRING,

  /** Like {@link #TO_STRING} but in {@link CaseHelper#toLowerCase(String) lowercase}. */
  TO_STRING_LOWER_CASE,

  /** Like {@link #TO_STRING} but in {@link CaseSyntax#TRAIN_CASE train-case}. */
  TO_STRING_TRAIN_CASE;

  /**
   * @param e the {@link Enum} value.
   * @return the corresponding {@link String} representation according to this format.
   */
  public String toString(Enum<?> e) {

    if (e == null) {
      return null;
    }
    switch (this) {
      case ORDINAL:
        return null;
      case NAME:
        return e.name();
      case TO_STRING:
        return e.toString();
      case TO_STRING_LOWER_CASE:
        return CaseHelper.toLowerCase(e.toString());
      case TO_STRING_TRAIN_CASE:
        return CaseSyntax.TRAIN_CASE.convert(e.toString());
    }
    return null;
  }

}
