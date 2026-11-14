package io.github.mmm.marshall.id;

import io.github.mmm.base.exception.DuplicateObjectException;
import io.github.mmm.marshall.id.impl.StructuredIdMappingDefault;

/// [StructuredIdMapping] allowing to [add][#put(int, String)] ID mappings.
///
/// @since 1.0.0
public interface StructuredIdMappingMap extends StructuredIdMapping {

  /// Registers the [ID mapping][StructuredIdMapping] for the given parameters.
  ///
  /// @param id the [ID][StructuredIdMapping#id(String)].
  /// @param name the [name][StructuredIdMapping#name(int)].
  /// @throws DuplicateObjectException if a mapping was already added before for the given `id` or `name`.
  void put(int id, String name);

  /// Registers the [ID mapping][StructuredIdMapping] for the given `name` using the next free ID.
  ///
  /// @param name the [name][StructuredIdMapping#name(int)].
  /// @throws DuplicateObjectException if a mapping was already added before for the given `name`.
  void put(String name);

  /// @param capacity the capacity of the underlying map.
  /// @return the [StructuredIdMappingMap].
  static StructuredIdMappingMap of(int capacity) {

    return new StructuredIdMappingDefault(capacity);
  }

  /// @param names the fixed names in the specified order. Always add new values at the end to prevent breaking the
  ///        contract.
  /// @return the [StructuredIdMappingMap].
  static StructuredIdMappingMap of(String... names) {

    StructuredIdMappingMap mapping = new StructuredIdMappingDefault(names.length);
    for (int i = 0; i < names.length; i++) {
      mapping.put(i + 1, names[i]);
    }
    return mapping;
  }
}
