/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.marshall.snakeyaml.impl;

import java.io.Writer;
import java.math.BigDecimal;
import java.math.BigInteger;

import org.yaml.snakeyaml.Yaml;

import io.github.mmm.marshall.AbstractStructuredWriter;
import io.github.mmm.marshall.StructuredFormat;
import io.github.mmm.marshall.StructuredWriter;
import io.github.mmm.marshall.snakeyaml.impl.state.SnakeYamlParentState;
import io.github.mmm.marshall.snakeyaml.impl.state.SnakeYamlRootState;

/**
 * Implementation of {@link StructuredWriter} for JSON from scratch.
 *
 * @see SnakeYamlFormat
 *
 * @since 1.0.0
 */
public class SnakeYamlWriter extends AbstractStructuredWriter {

  private static final long JS_NUMBER_MAX = (2L << 52) - 1;

  private static final long JS_NUMBER_MIN = -JS_NUMBER_MAX;

  private final Yaml yaml;

  private SnakeYamlParentState jsonState;

  private Writer out;

  /**
   * The constructor.
   *
   * @param out the {@link Writer} to write the data to.
   * @param format the {@link #getFormat() format}.
   */
  public SnakeYamlWriter(Writer out, StructuredFormat format, Yaml yaml) {

    super(format);
    this.yaml = yaml;
    this.out = out;
    this.jsonState = new SnakeYamlRootState();
  }

  @Override
  public void writeStartArray(int size) {

    this.jsonState = this.jsonState.startArray();
  }

  @Override
  public void writeStartObject(int size) {

    this.jsonState = this.jsonState.startObject();
  }

  @Override
  public void writeEnd() {

    SnakeYamlParentState parent = this.jsonState.end();
    if (parent == null) {
      close();
    }
    this.jsonState = parent;
  }

  @Override
  public void writeName(String newName, int newId) {

    super.writeName(newName, newId);
    this.jsonState.setChildName(newName);
  }

  private void writeValueInternal(Object value) {

    this.name = null;
    if ((value == null) && !this.writeNullValues && !this.jsonState.isArray()) {
      return;
    }
    this.jsonState.addValue(value);
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
  public void writeValueAsBoolean(Boolean value) {

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
  public void writeValueAsLong(Long value) {

    writeValueInternal(value);
  }

  @Override
  public void writeValueAsInteger(Integer value) {

    writeValueInternal(value);
  }

  @Override
  public void writeValueAsDouble(Double value) {

    writeValueInternal(value);
  }

  @Override
  public void writeValueAsFloat(Float value) {

    writeValueInternal(value);
  }

  @Override
  public void writeValueAsShort(Short value) {

    writeValueInternal(value);
  }

  @Override
  public void writeValueAsByte(Byte value) {

    writeValueInternal(value);
  }

  @Override
  public void close() {

    if (this.jsonState == null) {
      return;
    }
    assert (this.jsonState.getParent() == null);
    Object value = this.jsonState.getValue();
    this.yaml.dump(value, this.out);
    this.jsonState = null;
  }

}
