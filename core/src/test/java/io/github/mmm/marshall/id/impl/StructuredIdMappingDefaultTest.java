package io.github.mmm.marshall.id.impl;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import io.github.mmm.marshall.StructuredProcessor;
import io.github.mmm.marshall.id.StructuredIdMapping;

/**
 * Test of {@link StructuredIdMappingDefault}.
 */
class StructuredIdMappingDefaultTest extends Assertions {

  /** Test of {@link StructuredIdMappingDefault#computeSize(int)}. */
  @SuppressWarnings("javadoc")
  @Test
  void testComputeSize() {

    assertThat(StructuredIdMappingDefault.computeSize(0)).isEqualTo(16);
    assertThat(StructuredIdMappingDefault.computeSize(1)).isEqualTo(16);
    assertThat(StructuredIdMappingDefault.computeSize(3)).isEqualTo(16);
    assertThat(StructuredIdMappingDefault.computeSize(15)).isEqualTo(16);
    assertThat(StructuredIdMappingDefault.computeSize(16)).isEqualTo(16);
    // minimum exceeded
    assertThat(StructuredIdMappingDefault.computeSize(17)).isEqualTo(32);
    assertThat(StructuredIdMappingDefault.computeSize(31)).isEqualTo(32);
    assertThat(StructuredIdMappingDefault.computeSize(32)).isEqualTo(32);
    assertThat(StructuredIdMappingDefault.computeSize(33)).isEqualTo(64);
    assertThat(StructuredIdMappingDefault.computeSize(64)).isEqualTo(64);
    assertThat(StructuredIdMappingDefault.computeSize(65)).isEqualTo(128);
    assertThat(StructuredIdMappingDefault.computeSize(128)).isEqualTo(128);
    assertThat(StructuredIdMappingDefault.computeSize(129)).isEqualTo(256);
    assertThat(StructuredIdMappingDefault.computeSize(256)).isEqualTo(256);
    assertThat(StructuredIdMappingDefault.computeSize(257)).isEqualTo(512);
    assertThat(StructuredIdMappingDefault.computeSize(512)).isEqualTo(512);
    assertThat(StructuredIdMappingDefault.computeSize(513)).isEqualTo(1024);
    assertThat(StructuredIdMappingDefault.computeSize(1024)).isEqualTo(1024);
    assertThat(StructuredIdMappingDefault.computeSize(1025)).isEqualTo(2048);
    assertThat(StructuredIdMappingDefault.computeSize(2048)).isEqualTo(2048);
    // maximum reached
    assertThat(StructuredIdMappingDefault.computeSize(2049)).isEqualTo(2048);
    assertThat(StructuredIdMappingDefault.computeSize(4096)).isEqualTo(2048);
  }

  /** Test of {@link StructuredIdMappingDefault} (put mappings and read them). */
  @Test
  void testMapping() {

    StructuredIdMappingDefault mapping = new StructuredIdMappingDefault(32);
    for (int id = 1; id <= 128; id++) {
      mapping.put(id, "name" + id);
    }
    for (int id = 1; id <= 128; id++) {
      String name = "name" + id;
      assertThat(mapping.name(id)).isEqualTo(name);
      assertThat(mapping.id(name)).isEqualTo(id);
    }
    assertThat(mapping.id(StructuredProcessor.TYPE)).isEqualTo(StructuredIdMapping.TYPE);
    assertThat(mapping.name(StructuredIdMapping.TYPE)).isEqualTo(StructuredProcessor.TYPE);
  }

  /** Test of {@link StructuredIdMappingDefault#put(int, String)} with illegal mapping. */
  @Test
  void testIllegalMapping() {

    StructuredIdMappingDefault mapping = new StructuredIdMappingDefault(32);
    try {
      mapping.put(1, StructuredProcessor.TYPE);
      failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
    } catch (IllegalArgumentException e) {
      assertThat(e).hasMessage("Name '@type' cannot be mapped to ID 1 as it is implicitly mapped to 2047.");
    }
    try {
      mapping.put(StructuredIdMapping.TYPE, "test");
      failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
    } catch (IllegalArgumentException e) {
      assertThat(e).hasMessage("ID 2047 cannot be used for name 'test' as it is reserved for '@type'.");
    }
  }

  /** Test of {@link StructuredIdMappingDefault} (put mappings and read them). */
  @Test
  void testMappingAutoId() {

    StructuredIdMappingDefault mapping = new StructuredIdMappingDefault(32);
    for (int id = 1; id <= 32; id++) {
      mapping.put("name" + id);
    }
    mapping.put(64, "name64");
    for (int id = 65; id <= 70; id++) {
      mapping.put("name" + id);
    }

    for (int id = 1; id <= 70; id++) {
      if ((id <= 32) || (id >= 64)) {
        String name = "name" + id;
        assertThat(mapping.name(id)).isEqualTo(name);
        assertThat(mapping.id(name)).isEqualTo(id);
      }
    }
    assertThat(mapping.id(StructuredProcessor.TYPE)).isEqualTo(StructuredIdMapping.TYPE);
    assertThat(mapping.name(StructuredIdMapping.TYPE)).isEqualTo(StructuredProcessor.TYPE);
  }

}
