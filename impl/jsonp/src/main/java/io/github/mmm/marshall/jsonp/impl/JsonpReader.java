/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.marshall.jsonp.impl;

import javax.json.stream.JsonParser;
import javax.json.stream.JsonParser.Event;

import io.github.mmm.base.number.NumberType;
import io.github.mmm.marshall.AbstractStructuredReader;
import io.github.mmm.marshall.StructuredFormat;
import io.github.mmm.marshall.StructuredReader;

/**
 * Implementation of {@link StructuredReader} for JSON using {@link JsonParser}.
 *
 * @see JsonpFormat
 *
 * @since 1.0.0
 */
public class JsonpReader extends AbstractStructuredReader {

  private JsonParser json;

  private Event event;

  /**
   * The constructor.
   *
   * @param json the {@link JsonpReader}.
   * @param format the {@link #getFormat() format}.
   */
  public JsonpReader(JsonParser json, StructuredFormat format) {

    super(format);
    this.json = json;
    next();
  }

  @Override
  public State next() {

    if (this.json.hasNext()) {
      this.event = this.json.next();
      this.state = convertEvent(this.event);
      if (this.state == State.NAME) {
        this.name = this.json.getString();
      }
    } else {
      this.event = null;
      this.state = State.DONE;
    }
    return this.state;
  }

  private State convertEvent(Event e) {

    switch (e) {
      case KEY_NAME:
        return State.NAME;
      case START_ARRAY:
        return State.START_ARRAY;
      case START_OBJECT:
        return State.START_OBJECT;
      case END_ARRAY:
        return State.END_ARRAY;
      case END_OBJECT:
        return State.END_OBJECT;
      case VALUE_NULL:
      case VALUE_FALSE:
      case VALUE_TRUE:
      case VALUE_NUMBER:
      case VALUE_STRING:
        return State.VALUE;
    }
    return null;
  }

  @Override
  public boolean isStringValue() {

    return this.event == Event.VALUE_STRING;
  }

  @Override
  public Object readValue() {

    expect(State.VALUE);
    Object value;
    if (this.event == Event.VALUE_NULL) {
      value = null;
    } else if (this.event == Event.VALUE_TRUE) {
      value = Boolean.TRUE;
    } else if (this.event == Event.VALUE_FALSE) {
      value = Boolean.FALSE;
    } else if (this.event == Event.VALUE_NUMBER) {
      value = NumberType.simplify(this.json.getBigDecimal(), NumberType.INTEGER);
    } else if (this.event == Event.VALUE_STRING) {
      value = this.json.getString();
    } else {
      throw new IllegalStateException();
    }
    next();
    return value;
  }

  @SuppressWarnings("unchecked")
  @Override
  protected <N extends Number> N readValueAsNumber(NumberType<N> type) {

    N result;
    if (this.event == Event.VALUE_NULL) {
      result = null;
    } else if ((this.event == Event.VALUE_TRUE) || (this.event == Event.VALUE_FALSE)) {
      throw error("Expecting number but found boolean.");
    } else if (type == NumberType.INTEGER) {
      result = (N) Integer.valueOf(this.json.getInt());
    } else if (type == NumberType.LONG) {
      result = (N) Long.valueOf(this.json.getLong());
    } else {
      result = type.valueOf(this.json.getString());
    }
    next();
    return result;
  }

  @Override
  public void close() {

    if (this.json != null) {
      this.json.close();
      this.json = null;
    }
  }

}
