/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.marshall.snakeyaml.impl.state;

import java.util.Collection;
import java.util.Map;

import io.github.mmm.marshall.StructuredReader.State;

/**
 * State object for reading or writing JSON.
 *
 * @since 1.0.0
 */
public abstract class SnakeYamlState {

  private final String name;

  final SnakeYamlParentState parent;

  State state;

  SnakeYamlState(String name, SnakeYamlParentState parent) {

    super();
    this.name = name;
    this.parent = parent;
    if (name != null) {
      this.state = State.NAME;
    }
  }

  /**
   * Proceeds to the next {@link State}.
   *
   * @return the next {@link SnakeYamlState}. Can be this {@link SnakeYamlState} itself, a child or its parent.
   */
  public abstract SnakeYamlState next();

  /**
   * @return the current {@link State}.
   */
  public State getState() {

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
   * @return the parent {@link SnakeYamlParentState state} or {@code null} if this is the root node.
   */
  public SnakeYamlParentState getParent() {

    return this.parent;
  }

  /**
   * @return {@code true} if {@link SnakeYamlArrayState}, {@code false} otherwise.
   */
  public boolean isArray() {

    return false;
  }

  /**
   * @return {@code true} if {@link SnakeYamlObjectState}, {@code false} otherwise.
   */
  public boolean isObject() {

    return false;
  }

  /**
   * @return {@code true} if {@link SnakeYamlValueState}, {@code false} otherwise.
   */
  public boolean isValue() {

    return false;
  }

  @SuppressWarnings("unchecked")
  static SnakeYamlState of(String name, SnakeYamlParentState parent, Object value) {

    if (value instanceof Map) {
      return new SnakeYamlObjectState(name, parent, (Map<String, Object>) value);
    } else if (value instanceof Collection) {
      return new SnakeYamlArrayState(name, parent, (Collection<Object>) value);
    } else {
      return new SnakeYamlValueState(name, parent, value);
    }
  }

  /**
   * @param value the initial {@link #getValue() value}.
   * @return the initial {@link SnakeYamlState}.
   */
  public static SnakeYamlState of(Object value) {

    return of(null, null, value);
  }

}