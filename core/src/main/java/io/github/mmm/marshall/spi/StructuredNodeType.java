/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.marshall.spi;

import io.github.mmm.marshall.StructuredReader.State;

/**
 * Type of a node for JSON or YAML.
 *
 * @see io.github.mmm.marshall.StructuredFormat
 * @since 1.0.0
 */
public enum StructuredNodeType {

  /** JSON Object: {} */
  OBJECT('{', '}', State.START_OBJECT, State.END_OBJECT),

  /** JSON Array: [] */
  ARRAY('[', ']', State.START_ARRAY, State.END_ARRAY);

  private final char open;

  private final char close;

  private final State start;

  private final State end;

  private StructuredNodeType(char open, char close, State start, State end) {

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
   * @return the {@link State} to {@link State#isStart() start} this type.
   */
  public State getStart() {

    return this.start;
  }

  /**
   * @return the {@link State} to {@link State#isEnd end} this type.
   */
  public State getEnd() {

    return this.end;
  }

}
