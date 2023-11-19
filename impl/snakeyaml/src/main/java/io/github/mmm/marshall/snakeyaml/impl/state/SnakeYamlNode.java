/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.marshall.snakeyaml.impl.state;

import java.util.Collection;
import java.util.Map;

import io.github.mmm.marshall.StructuredState;
import io.github.mmm.marshall.spi.StructuredNode;
import io.github.mmm.marshall.spi.StructuredNodeType;

/**
 * State object for reading or writing JSON.
 *
 * @since 1.0.0
 */
public abstract class SnakeYamlNode extends StructuredNode<SnakeYamlNode> {

  /** The name of this state as property. */
  protected final String name;

  /** @see #getState() */
  public StructuredState state;

  SnakeYamlNode(SnakeYamlNode parent, StructuredNodeType type, String name) {

    super(parent, type);
    this.name = name;
    if (name != null) {
      this.state = StructuredState.NAME;
    }
  }

  /**
   * Proceeds to the next {@link StructuredState}.
   *
   * @return the next {@link SnakeYamlNode}. Can be this {@link SnakeYamlNode} itself, a child or its parent.
   */
  public abstract SnakeYamlNode next();

  @Override
  public StructuredNodeType getType() {

    if (this.state == StructuredState.NAME) {
      // required to validate state transitions as NAME is already in the new child node
      return this.parent.getType();
    }
    return super.getType();
  }

  /**
   * @return the current {@link StructuredState}.
   */
  public StructuredState getState() {

    return this.state;
  }

  /**
   * @return name the name of this property or {@code null} if no name.
   */
  public String getName() {

    return this.name;
  }

  /**
   * @return the value contained in this state.
   */
  public abstract Object getValue();

  /**
   * @return {@code true} if {@link SnakeYamlArrayNode}, {@code false} otherwise.
   */
  public boolean isArray() {

    return false;
  }

  /**
   * @return {@code true} if {@link SnakeYamlObjectNode}, {@code false} otherwise.
   */
  public boolean isObject() {

    return false;
  }

  /**
   * @param childName the name of the child value.
   * @param childValue the value to add to this object or array.
   */
  public abstract void addValue(String childName, Object childValue);

  /**
   * @return {@code true} if {@link SnakeYamlValueNode}, {@code false} otherwise.
   */
  public boolean isValue() {

    return false;
  }

  @SuppressWarnings("unchecked")
  static SnakeYamlNode of(String name, SnakeYamlParentNode parent, Object value) {

    if (value instanceof Map) {
      return new SnakeYamlObjectNode(parent, name, (Map<String, Object>) value);
    } else if (value instanceof Collection) {
      return new SnakeYamlArrayNode(parent, name, (Collection<Object>) value);
    } else {
      return new SnakeYamlValueNode(parent, name, value);
    }
  }

  /**
   * @param value the initial {@link #getValue() value}.
   * @return the initial {@link SnakeYamlNode}.
   */
  public static SnakeYamlNode of(Object value) {

    return of(null, SnakeYamlRootNode.get(), value);
  }

}