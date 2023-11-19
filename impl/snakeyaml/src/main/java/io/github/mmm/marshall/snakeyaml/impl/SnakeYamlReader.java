/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.marshall.snakeyaml.impl;

import java.io.IOException;

import io.github.mmm.marshall.StructuredFormat;
import io.github.mmm.marshall.StructuredReader;
import io.github.mmm.marshall.StructuredState;
import io.github.mmm.marshall.id.StructuredIdMappingObject;
import io.github.mmm.marshall.snakeyaml.impl.state.SnakeYamlNode;
import io.github.mmm.marshall.snakeyaml.impl.state.SnakeYamlRootNode;
import io.github.mmm.marshall.spi.AbstractStructuredValueReader;
import io.github.mmm.marshall.spi.StructuredNodeType;

/**
 * Implementation of {@link StructuredReader} for JSON from scratch.
 *
 * @see SnakeYamlFormat
 *
 * @since 1.0.0
 */
public class SnakeYamlReader extends AbstractStructuredValueReader<SnakeYamlNode> {

  /**
   * The constructor.
   *
   * @param value the value parsed from from snake-yaml.
   * @param format the {@link #getFormat() format}.
   */
  public SnakeYamlReader(Object value, StructuredFormat format) {

    super(format);
    this.node = SnakeYamlNode.of(value);
    setState(this.node.state);
  }

  @Override
  protected SnakeYamlNode newNode(StructuredNodeType type, StructuredIdMappingObject object) {

    return new SnakeYamlRootNode();
  }

  @Override
  protected StructuredState next(boolean skip) {

    if (skip) {
      setState(this.node.type.getEnd());
      this.node = this.node.parent;
    }
    if (isDone()) {
      throw new IllegalStateException("Already done!");
    }
    StructuredState state = null;
    SnakeYamlNode nextNode = this.node;
    while (state == null) {
      nextNode = nextNode.next();
      if (nextNode == null) {
        state = StructuredState.DONE;
        this.node = null;
        break;
      }
      state = nextNode.state;
    }
    this.node = nextNode;
    setState(state);
    String nextName = null;
    if (this.node != null) {
      nextName = this.node.getName();
    }
    if (nextName != null) {
      this.name = nextName;
    }
    return state;
  }

  @Override
  public boolean isStringValue() {

    if (getState() == StructuredState.VALUE) {
      return (this.node.getValue() instanceof String);
    }
    return false;
  }

  @Override
  public Object readValue() {

    require(StructuredState.VALUE);
    Object v = this.node.getValue();
    next();
    return v;
  }

  @Override
  protected void doClose() throws IOException {

    // nothing to do...
  }

  @Override
  public String toString() {

    return this.node.getValue().toString();
  }

}
