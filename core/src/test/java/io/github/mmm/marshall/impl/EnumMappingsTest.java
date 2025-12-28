/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.marshall.impl;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import io.github.mmm.marshall.EnumFormat;

/**
 * Test of {@link EnumMappings}.
 */
class EnumMappingsTest extends Assertions {

  /** Test of {@link EnumMappings} and {@link EnumMapping}. */
  @Test
  void testMapping() {

    // arrange
    EnumMappings mappings = EnumMappings.get();
    // act
    EnumMapping<EnumFormat> mapping = mappings.getMapping(EnumFormat.class);
    // assert
    assertThat(mappings.getMapping(EnumFormat.class)).isSameAs(mapping);
    assertThat(mapping.fromString("ORDINAL")).isSameAs(EnumFormat.ORDINAL);
    assertThat(mapping.fromString("ordinal")).isSameAs(EnumFormat.ORDINAL);
    assertThat(mapping.fromString("NAME")).isSameAs(EnumFormat.NAME);
    assertThat(mapping.fromString("name")).isSameAs(EnumFormat.NAME);
    assertThat(mapping.fromString("TO_STRING")).isSameAs(EnumFormat.TO_STRING);
    assertThat(mapping.fromString("to_string")).isSameAs(EnumFormat.TO_STRING);
    assertThat(mapping.fromString("to-string")).isSameAs(EnumFormat.TO_STRING);
    assertThat(mapping.fromString("NON_EXISTING_OPTION")).isNull();
    assertThat(mapping.fromOrdinal(Integer.valueOf(-1))).isNull();
    // generic test of all combinations
    for (EnumFormat format : EnumFormat.values()) {
      for (EnumFormat value : EnumFormat.values()) {
        if (format == EnumFormat.ORDINAL) {
          assertThat(mapping.fromOrdinal(Integer.valueOf(value.ordinal()))).isSameAs(value);
        } else {
          String string = format.toString(value);
          assertThat(mapping.fromString(string)).isSameAs(value);
        }
      }
    }
  }

}
