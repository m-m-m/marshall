/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.marshall.snakeyaml.impl;

import java.io.IOException;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import org.yaml.snakeyaml.Yaml;

import io.github.mmm.marshall.StructuredFormat;
import io.github.mmm.marshall.StructuredState;
import io.github.mmm.marshall.StructuredWriter;
import io.github.mmm.marshall.id.StructuredIdMappingObject;
import io.github.mmm.marshall.snakeyaml.impl.state.SnakeYamlArrayNode;
import io.github.mmm.marshall.snakeyaml.impl.state.SnakeYamlNode;
import io.github.mmm.marshall.snakeyaml.impl.state.SnakeYamlObjectNode;
import io.github.mmm.marshall.snakeyaml.impl.state.SnakeYamlRootNode;
import io.github.mmm.marshall.spi.AbstractStructuredWriter;
import io.github.mmm.marshall.spi.StructuredNodeType;

/**
 * Implementation of {@link StructuredWriter} for JSON from scratch.
 *
 * @see SnakeYamlFormat
 *
 * @since 1.0.0
 */
public class SnakeYamlWriter extends AbstractStructuredWriter<SnakeYamlNode> {

  private final Yaml yaml;

  private Writer out;

  /**
   * The constructor.
   *
   * @param out the {@link Writer} to write the data to.
   * @param format the {@link #getFormat() format}.
   * @param yaml the {@link Yaml} instance.
   */
  public SnakeYamlWriter(Writer out, StructuredFormat format, Yaml yaml) {

    super(format);
    this.yaml = yaml;
    this.out = out;
  }

  @Override
  protected SnakeYamlNode newNode(StructuredNodeType type, StructuredIdMappingObject object) {

    SnakeYamlNode result;
    if (type == null) {
      result = new SnakeYamlRootNode();
    } else if (type == StructuredNodeType.ARRAY) {
      result = new SnakeYamlArrayNode(this.node, this.name, new ArrayList<>());
    } else {
      result = new SnakeYamlObjectNode(this.node, this.name, new LinkedHashMap<>());
    }
    result.state = null;
    this.name = null;
    return result;
  }

  @Override
  protected void doWriteStart(StructuredNodeType type, StructuredIdMappingObject object) {

    // nothing to do as all happens in newWriteState
  }

  @Override
  protected void doWriteEnd(StructuredNodeType type) {

    if (this.node.isRoot()) {
      close();
      return;
    }
  }

  private void writeValueInternal(Object value) {

    if ((value == null) && !this.writeNullValues && !this.node.isArray()) {
      return;
    }
    this.node.addValue(this.name, value);
    this.name = null;
    setState(StructuredState.VALUE);
  }

  @Override
  public void writeValueAsNull() {

    writeValueInternal(null);
  }

  @Override
  public void writeValue(Object value) {

    writeValueInternal(value);
  }

  @Override
  public void writeValueAsString(String value) {

    writeValueInternal(value);
  }

  @Override
  public void writeValueAsBoolean(boolean value) {

    writeValueInternal(value);
  }

  @Override
  public void writeValueAsNumber(Number value) {

    writeValueInternal(value);
  }

  @Override
  public void writeValueAsBigDecimal(BigDecimal value) {

    writeValueInternal(value);
  }

  @Override
  public void writeValueAsBigInteger(BigInteger value) {

    writeValueInternal(value);
  }

  @Override
  public void writeValueAsLong(long value) {

    writeValueInternal(value);
  }

  @Override
  public void writeValueAsInteger(int value) {

    writeValueInternal(value);
  }

  @Override
  public void writeValueAsDouble(double value) {

    writeValueInternal(value);
  }

  @Override
  public void writeValueAsFloat(float value) {

    writeValueInternal(value);
  }

  @Override
  protected void doClose() throws IOException {

    assert (this.node.parent == null);
    Object value = this.node.getValue();
    this.yaml.dump(value, this.out);
  }

}
