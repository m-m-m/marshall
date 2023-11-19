package io.github.mmm.marshall.id.impl;

import io.github.mmm.marshall.id.StructuredIdMapping;

/**
 * Default implementation of {@link StructuredIdMapping}.
 */
public final class StructuredIdMappingEmpty implements StructuredIdMapping {

  private static final StructuredIdMappingEmpty INSTANCE = new StructuredIdMappingEmpty();

  private StructuredIdMappingEmpty() {

    super();
  }

  @Override
  public String name(int id) {

    return null;
  }

  @Override
  public int id(String name) {

    return 0;
  }

  /**
   * @return the singleton instance.
   */
  public static StructuredIdMappingEmpty get() {

    return INSTANCE;
  }

}
