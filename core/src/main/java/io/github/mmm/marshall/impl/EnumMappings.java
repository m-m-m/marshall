/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.marshall.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Container for {@link EnumMapping}s. Supports thread-safe unmarshalling of any {@link Enum}.
 */
public class EnumMappings {

  private static final EnumMappings INSTANCE = new EnumMappings();

  private Map<Class<? extends Enum<?>>, EnumMapping<?>> mappings;

  /**
   * The constructor.
   */
  public EnumMappings() {

    super();
    this.mappings = new ConcurrentHashMap<>();
  }

  /**
   * @param <E> type of the {@link Enum}.
   * @param enumType the {@link Class} reflecting the {@link Enum}.
   * @return the requested {@link EnumMapping}.
   */
  @SuppressWarnings({ "rawtypes", "unchecked" })
  public <E extends Enum<E>> EnumMapping<E> getMapping(Class<E> enumType) {

    return (EnumMapping) this.mappings.computeIfAbsent(enumType, e -> new EnumMapping<>(enumType));
  }

  /**
   * @return the singleton instance of {@link EnumMappings}.
   */
  public static EnumMappings get() {

    return INSTANCE;
  }

}
