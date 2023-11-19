package io.github.mmm.marshall.test;

import java.time.Instant;
import java.util.List;

import io.github.mmm.marshall.id.StructuredIdMapping;
import io.github.mmm.marshall.id.StructuredIdMappingMap;
import io.github.mmm.marshall.id.StructuredIdMappingObject;

/**
 * Dummy bean for testing.
 */
public class RootTestBean implements StructuredIdMappingObject {

  static final String PROPERTY_FOO = "foo";

  static final String PROPERTY_INSTANT = "instant";

  static final String PROPERTY_LIST = "list";

  static final String PROPERTY_EMPTY = "empty";

  private String foo;

  private Instant instant;

  private List<Object> list;

  private List<Object> empty;

  /**
   * @return foo
   */
  public String getFoo() {

    return this.foo;
  }

  /**
   * @param foo new value of {@link #getFoo()}.
   */
  public void setFoo(String foo) {

    this.foo = foo;
  }

  /**
   * @return instant
   */
  public Instant getInstant() {

    return this.instant;
  }

  /**
   * @param instant new value of {@link #getInstant()}.
   */
  public void setInstant(Instant instant) {

    this.instant = instant;
  }

  /**
   * @return list
   */
  public List<Object> getList() {

    return this.list;
  }

  /**
   * @param list new value of {@link #getList()}.
   */
  public void setList(List<Object> list) {

    this.list = list;
  }

  /**
   * @return empty
   */
  public List<Object> getEmpty() {

    return this.empty;
  }

  /**
   * @param empty new value of {@link #getEmpty()}.
   */
  public void setEmpty(List<Object> empty) {

    this.empty = empty;
  }

  @Override
  public StructuredIdMapping defineIdMapping() {

    StructuredIdMappingMap map = StructuredIdMappingMap.of(4);
    map.put(PROPERTY_FOO);
    map.put(PROPERTY_INSTANT);
    map.put(PROPERTY_LIST);
    map.put(PROPERTY_EMPTY);
    return map;
  }

}
