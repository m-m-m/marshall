/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.marshall.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/// Container for [EnumMapping]s. Supports thread-safe unmarshalling of any [Enum].
public class EnumMappings {

  private static final EnumMappings INSTANCE = new EnumMappings();

  private Map<Class<? extends Enum<?>>, EnumMapping<?>> mappings;

  /// The constructor.
  public EnumMappings() {

    super();
    this.mappings = new ConcurrentHashMap<>();
  }

  /// @param <E> type of the [Enum].
  /// @param enumType the [Class] reflecting the [Enum].
  /// @return the requested [EnumMapping].
  @SuppressWarnings({ "rawtypes", "unchecked" })
  public <E extends Enum<E>> EnumMapping<E> getMapping(Class<E> enumType) {

    return (EnumMapping) this.mappings.computeIfAbsent(enumType, _ -> new EnumMapping<>(enumType));
  }

  /// @return the singleton instance of [EnumMappings].
  public static EnumMappings get() {

    return INSTANCE;
  }

}
