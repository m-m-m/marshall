/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.marshall.snakeyaml.impl;

import io.github.mmm.marshall.StructuredState;

/**
 *
 */
public enum SnakeYamlNodeType {

  /** JSON Object: {} */
  OBJECT('{', '}', StructuredState.START_OBJECT, StructuredState.END_OBJECT),

  /** JSON Array: [] */
  ARRAY('[', ']', StructuredState.START_ARRAY, StructuredState.END_ARRAY);

  private final char open;

  private final char close;

  private final StructuredState start;

  private final StructuredState end;

  private SnakeYamlNodeType(char open, char close, StructuredState start, StructuredState end) {

    this.open = open;
    this.close = close;
    this.start = start;
    this.end = end;
  }

  /**
   * @return open
   */
  public char getOpen() {

    return this.open;
  }

  /**
   * @return close
   */
  public char getClose() {

    return this.close;
  }

  /**
   * @return start
   */
  public StructuredState getStart() {

    return this.start;
  }

  /**
   * @return end
   */
  public StructuredState getEnd() {

    return this.end;
  }

}
