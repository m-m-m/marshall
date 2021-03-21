package io.github.mmm.marshall.snakeyaml.impl.state;

import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * {@link SnakeYamlState} that contains children as either {@link SnakeYamlObjectState} or {@link SnakeYamlArrayState}.
 *
 * @since 1.0.0
 */
public abstract class SnakeYamlParentState extends SnakeYamlState {

  private String childName;

  SnakeYamlParentState(String name, SnakeYamlParentState parent) {

    super(name, parent);
  }

  /**
   * @param name the {@link #getName() name}. May be {@code null}.
   * @param value the {@link #getValue() value}. May be {@code null}.
   * @return the new child {@link SnakeYamlState}.
   */
  public SnakeYamlState of(String name, Object value) {

    return of(name, this, value);
  }

  /**
   * @return the name for the next {@link #addValue(Object) value}, {@link #startObject() object}, or
   *         {@link #startArray()}.
   */
  public String getChildName() {

    return this.childName;
  }

  /**
   * @param childName the name for the next {@link #addValue(Object) value}, {@link #startObject() object}, or
   *        {@link #startArray()}. Only supported by {@link SnakeYamlObjectState}.
   */
  public void setChildName(String childName) {

    this.childName = childName;
  }

  /**
   * @return the new {@link SnakeYamlObjectState}.
   * @see io.github.mmm.marshall.StructuredWriter#writeStartObject()
   */
  public SnakeYamlObjectState startObject() {

    return new SnakeYamlObjectState(this.childName, this, new LinkedHashMap<>());
  }

  /**
   * @return the new {@link SnakeYamlArrayState}.
   * @see io.github.mmm.marshall.StructuredWriter#writeStartArray()
   */
  public SnakeYamlArrayState startArray() {

    return new SnakeYamlArrayState(this.childName, this, new ArrayList<>());
  }

  /**
   * @param value the value to add to this object or array.
   */
  public abstract void addValue(Object value);

  /**
   * @return the parent state.
   */
  public SnakeYamlParentState end() {

    this.parent.addValue(getValue());
    return this.parent;
  }

}
