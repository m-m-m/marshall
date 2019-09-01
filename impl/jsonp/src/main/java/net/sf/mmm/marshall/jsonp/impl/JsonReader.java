/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package net.sf.mmm.marshall.jsonp.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.json.stream.JsonParser;
import javax.json.stream.JsonParser.Event;

import net.sf.mmm.marshall.AbstractStructuredReader;
import net.sf.mmm.marshall.StructuredReader;

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
  public boolean readStartObject() {

    if (this.event == Event.START_OBJECT) {
      next();
      return true;
    }
    return false;
  }

  @Override
  public boolean readStartArray() {

    if (this.event == Event.START_ARRAY) {
      next();
      return true;
    }
    return false;
  }

  @Override
  public Object readValue(boolean recursive) {

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
    } else if (recursive) {
      if (this.event == Event.START_ARRAY) {
        next();
        List<Object> array = new ArrayList<>();
        readArray(array);
        return array;
      } else if (this.event == Event.START_OBJECT) {
        next();
        Map<String, Object> map = new HashMap<>();
        readObject(map);
        return map;
      } else {
        throw invalidJson();
      }
    } else {
      throw invalidJson();
    }
  }

  @Override
  public void readObject(Map<String, Object> map) {

    while (this.event != Event.END_OBJECT) {
      String key = readName();
      Object value = readValue(true);
      map.put(key, value);
    }
  }

  @Override
  public void readArray(Collection<Object> array) {

    while (this.event != Event.END_ARRAY) {
      Object value = readValue(true);
      array.add(value);
    }
    next();
  }

  private RuntimeException invalidJson() {

    throw new IllegalStateException("Invalid JSON!");
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
  public boolean readEnd() {

    if ((this.event == Event.END_ARRAY) || (this.event == Event.END_OBJECT)) {
      next();
      return true;
    }
    return false;
  }

  @Override
  public void skipValue() {

    skipValue(this.event);
    next();
  }

  private void skipValue(Event e) {

    switch (e) {
      case VALUE_FALSE:
      case VALUE_NULL:
      case VALUE_NUMBER:
      case VALUE_STRING:
      case VALUE_TRUE:
        break;
      case START_ARRAY:
        this.event = this.json.next();
        while (this.event != Event.END_ARRAY) {
          skipValue();
        }
        break;
      case START_OBJECT:
        this.event = this.json.next();
        while (this.event != Event.END_OBJECT) {
          expect(Event.KEY_NAME);
          next();
          skipValue();
        }
        break;
      default:
        throw new IllegalStateException("Unhandled event: " + e);
    }
  }

  @Override
  public boolean isDone() {

    return (this.event == null);
  }

  @Override
  public void close() {

    if (this.json != null) {
      this.json.close();
      this.json = null;
    }
  }

}
