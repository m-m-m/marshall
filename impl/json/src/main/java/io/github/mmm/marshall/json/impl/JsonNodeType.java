/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.marshall.json.impl;

import io.github.mmm.marshall.StructuredReader.State;

/**
 *
 */
public enum JsonNodeType {

  /** JSON Object: {} */
  OBJECT('{', '}', State.START_OBJECT, State.END_OBJECT),

  /** JSON Array: [] */
  ARRAY('[', ']', State.START_ARRAY, State.END_ARRAY);

  private final char open;

  private final char close;

  private final State start;

  private final State end;

  private JsonNodeType(char open, char close, State start, State end) {

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
  public State getStart() {

    return this.start;
  }

  /**
   * @return end
   */
  public State getEnd() {

    return this.end;
  }

}
