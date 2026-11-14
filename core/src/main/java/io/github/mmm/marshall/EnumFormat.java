/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.marshall;

import io.github.mmm.base.text.CaseHelper;
import io.github.mmm.base.text.CaseSyntax;

/// [Enum] with the available formats how to serialize an [Enum].
public enum EnumFormat {

  /// Write [Enum] as [ordinal][Enum#ordinal()].
  ORDINAL,

  /// Write [Enum] as [name][Enum#name()].
  NAME,

  /// Write [Enum] as [toString representation][Enum#toString()].
  TO_STRING,

  /// Like [#TO_STRING] but in [lowercase][CaseHelper#toLowerCase(String)].
  TO_STRING_LOWER_CASE,

  /// Like [#TO_STRING] but in [train-case][CaseSyntax#TRAIN_CASE].
  TO_STRING_TRAIN_CASE;

  /// @param e the [Enum] value.
  /// @return the corresponding [String] representation according to this format.
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
