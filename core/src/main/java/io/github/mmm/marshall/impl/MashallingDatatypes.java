/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.marshall.impl;

import java.util.Collections;
import java.util.Set;

/**
 * Defines the supported Java standard datatypes as specified in {@link io.github.mmm.marshall}.
 *
 * @since 1.0.0
 * @see io.github.mmm.marshall.StructuredReader#readValue(Class)
 * @see io.github.mmm.marshall.StructuredWriter#writeValue(Object)
 */
public class MashallingDatatypes {

  private static final Set<String> TYPES = Collections
      .unmodifiableSet(Set.of("java.lang.String", "java.lang.Boolean", "java.lang.Integer", "java.lang.Long",
          "java.lang.Short", "java.lang.Byte", "java.lang.Double", "java.lang.Float", "java.math.BigInteger",
          "java.math.BigDecimal", "java.time.Instant", "java.time.LocalDateTime", "java.time.LocalDate",
          "java.time.LocalTime", "java.time.ZonedDateTime", "java.time.OffsetDateTime", "java.time.OffsetTime"));

  /**
   * @param type thee {@link Class} to test.
   * @return {@code true} if a supported standard datatype, {@code false} otherwise.
   */
  public static boolean isSupported(Class<?> type) {

    if (type == null) {
      return false;
    }
    return TYPES.contains(type.getName());
  }

}
