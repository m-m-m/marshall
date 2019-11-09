/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.marshall.jsonp.impl;

import java.math.BigDecimal;

import javax.json.stream.JsonParser;
import javax.json.stream.JsonParser.Event;

import io.github.mmm.marshall.AbstractStructuredReader;
import io.github.mmm.marshall.StructuredReader;

/**
 * Implementation of {@link StructuredReader} for JSON using {@link JsonParser}.
 *
 * @see JsonFormat
 *
 * @since 1.0.0
 */
public class JsonReader extends AbstractStructuredReader {

  private JsonParser json;

  private Event event;

  /**
   * The constructor.
   *
   * @param json the {@link JsonReader}.
   */
  public JsonReader(JsonParser json) {

    super();
    this.json = json;
    next();
  }

  private void expect(Event expected) {

    if (this.event != expected) {
      throw new IllegalStateException("Expecting event '" + expected + "' but found '" + this.event + "'.");
    }
  }

  @Override
  public State next() {

    if (this.json.hasNext()) {
      this.event = this.json.next();
      this.state = convertEvent(this.event);
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
  public String readName() {

    expect(Event.KEY_NAME);
    this.name = this.json.getString();
    next();
    return this.name;
  }

  @Override
  public Object readValue() {

    if (this.event == Event.VALUE_NULL) {
      next();
      return null;
    } else if (this.event == Event.VALUE_TRUE) {
      next();
      return Boolean.TRUE;
    } else if (this.event == Event.VALUE_FALSE) {
      next();
      return Boolean.FALSE;
    } else if (this.event == Event.VALUE_NUMBER) {
      BigDecimal value = this.json.getBigDecimal();
      next();
      return value;
    } else if (this.event == Event.VALUE_STRING) {
      String value = this.json.getString();
      next();
      return value;
    } else {
      expect(State.VALUE);
      throw new IllegalStateException();
    }
  }

  @Override
  public String readValueAsString() {

    if (this.event == Event.VALUE_NULL) {
      return null;
    }
    String string = this.json.getString();
    next();
    return string;
  }

  @Override
  public Boolean readValueAsBoolean() {

    Event e = this.event;
    next();
    if (e == Event.VALUE_NULL) {
      return null;
    } else if (e == Event.VALUE_TRUE) {
      return Boolean.TRUE;
    } else if (e == Event.VALUE_FALSE) {
      return Boolean.FALSE;
    }
    throw new IllegalStateException("Property " + this.name + " is no boolean value: " + this.json.getString());
  }

  @Override
  protected String readValueAsNumberString() {

    if (this.event == Event.VALUE_NULL) {
      next();
      return null;
    }
    if ((this.event == Event.VALUE_TRUE) || (this.event == Event.VALUE_FALSE)) {
      throw new IllegalStateException("Expecting number but found boolean.");
    }
    String string = this.json.getString();
    next();
    return string;
  }

  @Override
  public Integer readValueAsInteger() {

    if (this.event == Event.VALUE_NULL) {
      next();
      return null;
    }
    expect(Event.VALUE_NUMBER);
    Integer value = Integer.valueOf(this.json.getInt());
    next();
    return value;
  }

  @Override
  public Long readValueAsLong() {

    if (this.event == Event.VALUE_NULL) {
      next();
      return null;
    }
    Long value = Long.valueOf(this.json.getString());
    next();
    return value;
  }

  @Override
  public BigDecimal readValueAsBigDecimal() {

    if (this.event == Event.VALUE_NULL) {
      next();
      return null;
    }
    BigDecimal value = new BigDecimal(this.json.getString());
    next();
    return value;
  }

  @Override
  public void close() {

    if (this.json != null) {
      this.json.close();
      this.json = null;
    }
  }

}
