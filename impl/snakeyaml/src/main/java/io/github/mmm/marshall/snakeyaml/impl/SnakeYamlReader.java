/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.marshall.snakeyaml.impl;

import io.github.mmm.marshall.AbstractStructuredReader;
import io.github.mmm.marshall.StructuredFormat;
import io.github.mmm.marshall.StructuredReader;
import io.github.mmm.marshall.snakeyaml.impl.state.SnakeYamlState;

/**
 * Implementation of {@link StructuredReader} for JSON from scratch.
 *
 * @see SnakeYamlFormat
 *
 * @since 1.0.0
 */
public class SnakeYamlReader extends AbstractStructuredReader {

  private SnakeYamlState yamlState;

  /**
   * The constructor.
   *
   * @param value the value parsed from from snake-yaml.
   * @param format the {@link #getFormat() format}.
   */
  public SnakeYamlReader(Object value, StructuredFormat format) {

    super(format);
    this.yamlState = SnakeYamlState.of(value);
    this.state = this.yamlState.getState();
  }

  @Override
  public State next() {

    if (this.state == State.DONE) {
      throw new IllegalStateException("Already done!");
    }
    this.state = null;
    while (this.state == null) {
      this.yamlState = this.yamlState.next();
      if (this.yamlState == null) {
        this.state = State.DONE;
        break;
      }
      this.state = this.yamlState.getState();
    }
    String nextName = null;
    if (this.yamlState != null) {
      nextName = this.yamlState.getName();
    }
    if (nextName != null) {
      this.name = nextName;
    }
    return this.state;
  }

  @Override
  public boolean isStringValue() {

    if (this.state == State.VALUE) {
      return (this.yamlState.getValue() instanceof String);
    }
    return false;
  }

  @Override
  public Object readValue() {

    expect(State.VALUE);
    Object v = this.yamlState.getValue();
    next();
    return v;
  }

  @Override
  public String readValueAsString() {

    Object v = readValue();
    if (v == null) {
      return null;
    }
    return v.toString();
  }

  @Override
  public Boolean readValueAsBoolean() {

    Object v = readValue();
    if (v == null) {
      return null;
    } else if (v instanceof Boolean) {
      return (Boolean) v;
    } else {
      throw new IllegalArgumentException("Value of type " + v.getClass().getName() + " can not be read as boolean!");
    }
  }

  @Override
  protected String readValueAsNumberString() {

    Object v = readValue();
    if (v == null) {
      return null;
    } else if (v instanceof String) {
      return (String) v;
    } else if (v instanceof Number) {
      return v.toString();
    } else {
      throw new IllegalArgumentException("Value of type " + v.getClass().getName() + " can not be read as number!");
    }
  }

  @Override
  public void close() {

    if (this.yamlState == null) {
      return;
    }
    this.yamlState = null;
    this.state = State.DONE;
  }

  @Override
  public String toString() {

    return this.yamlState.getValue().toString();
  }

}
