package io.github.mmm.marshall.test;

import io.github.mmm.marshall.id.StructuredIdMapping;
import io.github.mmm.marshall.id.StructuredIdMappingMap;
import io.github.mmm.marshall.id.StructuredIdMappingObject;

/**
 * Dummy bean for testing.
 */
class ChildTestBean implements StructuredIdMappingObject {

  static final String PROPERTY_KEY = "key";

  private String key;

  /**
   * @return key
   */
  public String getKey() {

    return this.key;
  }

  /**
   * @param key new value of {@link #getKey()}.
   */
  public void setKey(String key) {

    this.key = key;
  }

  @Override
  public StructuredIdMapping defineIdMapping() {

    StructuredIdMappingMap map = StructuredIdMappingMap.of(2);
    map.put(PROPERTY_KEY);
    return map;
  }

}
