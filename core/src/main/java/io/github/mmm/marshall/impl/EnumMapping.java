/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.marshall.impl;

import java.util.HashMap;
import java.util.Map;

import io.github.mmm.base.text.CaseHelper;
import io.github.mmm.base.text.CaseSyntax;

/**
 * Container with the mapping for an individual {@link Enum} optimized for performance.
 *
 * @param <E> type of the {@link Enum}.
 */
public class EnumMapping<E extends Enum<E>> {

  private final Map<String, E> string2enumMap;

  private final Map<Integer, E> ordinal2enumMap;

  /**
   * The constructor.
   *
   * @param enumType the {@link Class} reflecting the {@link Enum}.
   */
  public EnumMapping(Class<E> enumType) {

    super();
    this.ordinal2enumMap = new HashMap<>();
    this.string2enumMap = new HashMap<>();
    for (E e : enumType.getEnumConstants()) {
      this.ordinal2enumMap.put(Integer.valueOf(e.ordinal()), e);
      this.string2enumMap.put(e.name(), e);
      String string = e.toString();
      this.string2enumMap.putIfAbsent(string, e);
      this.string2enumMap.putIfAbsent(CaseHelper.toLowerCase(string), e);
      this.string2enumMap.putIfAbsent(CaseSyntax.TRAIN_CASE.convert(string), e);
    }
  }

  /**
   * @param value the {@link Enum} as {@link String}.
   * @return the according {@link Enum} value.
   */
  public E fromString(String value) {

    return this.string2enumMap.get(value);
  }

  /**
   * @param ordinal the {@link Enum} as {@link Enum#ordinal() ordinal}.
   * @return the according {@link Enum} value.
   */
  public E fromOrdinal(Integer ordinal) {

    return this.ordinal2enumMap.get(ordinal);
  }

}
