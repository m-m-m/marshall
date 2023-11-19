package io.github.mmm.marshall.id;

import io.github.mmm.base.exception.DuplicateObjectException;
import io.github.mmm.marshall.id.impl.StructuredIdMappingDefault;

/**
 * {@link StructuredIdMapping} allowing to {@link #put(int, String) add} ID mappings.
 *
 * @since 1.0.0
 */
public interface StructuredIdMappingMap extends StructuredIdMapping {

  /**
   * Registers the {@link StructuredIdMapping ID mapping} for the given parameters.
   *
   * @param id the {@link StructuredIdMapping#id(String) ID}.
   * @param name the {@link StructuredIdMapping#name(int) name}.
   * @throws DuplicateObjectException if a mapping was already added before for the given {@code id} or {@code name}.
   */
  void put(int id, String name);

  /**
   * Registers the {@link StructuredIdMapping ID mapping} for the given {@code name} using the next free ID.
   *
   * @param name the {@link StructuredIdMapping#name(int) name}.
   * @throws DuplicateObjectException if a mapping was already added before for the given {@code name}.
   */
  void put(String name);

  /**
   * @param capacity the capacity of the underlying map.
   * @return the {@link StructuredIdMappingMap}.
   */
  static StructuredIdMappingMap of(int capacity) {

    return new StructuredIdMappingDefault(capacity);
  }
}
