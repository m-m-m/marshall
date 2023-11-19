/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.marshall.id;

import io.github.mmm.marshall.StructuredProcessor;
import io.github.mmm.marshall.id.impl.StructuredIdMappingDefault;
import io.github.mmm.marshall.id.impl.StructuredIdMappingEmpty;

/**
 * Interface for the bidirectional mapping from {@link #name(int) name} to numeric ID and {@link #id(String) vice
 * versa}.
 *
 * @since 1.0.0
 */
public interface StructuredIdMapping extends StructuredIdMappingObject {

  /**
   * Reserved {@link #id(String) ID} for {@link StructuredProcessor#TYPE}. The value {@value} is the highest ID that
   * fits into a tag encoded in two bytes. We assume that no reasonable object has more than 2000 properties and will
   * ever hit this reserved ID. Otherwise such object must be configured such that its regular properties do not use
   * this reserved ID.
   */
  int TYPE = 2047;

  /**
   * @param id the ID of the property.
   * @return the name of the property with the given ID or {@code null} if no mapping is defined for the given ID.
   */
  String name(int id);

  /**
   * @param name the name to map.
   * @return the ID of the property with the given {@code name}.
   */
  int id(String name);

  @Override
  default StructuredIdMapping defineIdMapping() {

    return this;
  }

  @Override
  default Object asTypeKey() {

    return null;
  }

  /**
   * @return an empty implementation of {@link StructuredIdMapping}.
   */
  static StructuredIdMapping empty() {

    return StructuredIdMappingEmpty.get();
  }

  /**
   * @param names the property names to map. The first name will get ID {@code 1}, second name will get ID {@code 2},
   *        etc.
   * @return the {@link StructuredIdMappingMap}.
   */
  static StructuredIdMapping of(String... names) {

    if (names.length == 0) {
      return StructuredIdMappingEmpty.get();
    }
    // TODO create more efficient implementation
    StructuredIdMappingMap map = new StructuredIdMappingDefault(names.length);
    for (String name : names) {
      map.put(name);
    }
    return map;
  }

}