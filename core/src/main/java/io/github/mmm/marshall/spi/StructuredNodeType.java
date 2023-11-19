/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.marshall.spi;

import io.github.mmm.marshall.StructuredState;

/**
 * Type of a node for JSON or YAML.
 *
 * @see io.github.mmm.marshall.StructuredFormat
 * @since 1.0.0
 */
public enum StructuredNodeType {

  /** JSON Object: {} */
  OBJECT('{', '}', StructuredState.START_OBJECT, StructuredState.END_OBJECT),

  /** JSON Array: [] */
  ARRAY('[', ']', StructuredState.START_ARRAY, StructuredState.END_ARRAY);

  private final char open;

  private final char close;

  private final StructuredState start;

  private final StructuredState end;

  private StructuredNodeType(char open, char close, StructuredState start, StructuredState end) {

    this.open = open;
    this.close = close;
    this.start = start;
    this.end = end;
  }

  /**
   * @return the JSON character to open this type.
   */
  public char getOpen() {

    return this.open;
  }

  /**
   * @return the JSON character to close this type.
   */
  public char getClose() {

    return this.close;
  }

  /**
   * @return the {@link StructuredState} to {@link StructuredState#isStart() start} this type.
   */
  public StructuredState getStart() {

    return this.start;
  }

  /**
   * @return the {@link StructuredState} to {@link StructuredState#isEnd end} this type.
   */
  public StructuredState getEnd() {

    return this.end;
  }

}
