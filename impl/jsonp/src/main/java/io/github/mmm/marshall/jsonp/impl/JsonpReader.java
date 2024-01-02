/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.marshall.jsonp.impl;

import java.io.IOException;

import javax.json.stream.JsonParser;
import javax.json.stream.JsonParser.Event;

import io.github.mmm.base.number.NumberType;
import io.github.mmm.marshall.StructuredFormat;
import io.github.mmm.marshall.StructuredReader;
import io.github.mmm.marshall.StructuredState;
import io.github.mmm.marshall.id.StructuredIdMappingObject;
import io.github.mmm.marshall.spi.AbstractStructuredReader;
import io.github.mmm.marshall.spi.StructuredNodeDefault;
import io.github.mmm.marshall.spi.StructuredNodeType;

/**
 * Implementation of {@link StructuredReader} for JSON using {@link JsonParser}.
 *
 * @see JsonpFormat
 *
 * @since 1.0.0
 */
public class JsonpReader extends AbstractStructuredReader<StructuredNodeDefault> {

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
  protected StructuredNodeDefault newNode(StructuredNodeType type, StructuredIdMappingObject object) {

    return null;
  }

  @Override
  protected StructuredState next(boolean skip) {

    int skipCount = skip ? 1 : 0;
    StructuredState state;
    boolean todo;
    do {
      todo = false;
      int skipAdd = 0;
      if (this.json.hasNext()) {
        this.event = this.json.next();
        state = convertEvent(this.event);
        if (state == StructuredState.NAME) {
          this.name = this.json.getString();
        } else if (state.isStart()) {
          skipAdd = 1;
        } else if (state.isEnd()) {
          skipAdd = -1;
        }
      } else {
        this.event = null;
        state = StructuredState.DONE;
        skipAdd = -1;
      }
      if (skipCount > 0) {
        skipCount += skipAdd;
        if (skipCount == 0) {
          todo = true;
        }
      }
    } while ((skipCount > 0) || todo);
    setState(state);
    return state;
  }

  private StructuredState convertEvent(Event e) {

    switch (e) {
      case KEY_NAME:
        return StructuredState.NAME;
      case START_ARRAY:
        return StructuredState.START_ARRAY;
      case START_OBJECT:
        return StructuredState.START_OBJECT;
      case END_ARRAY:
        return StructuredState.END_ARRAY;
      case END_OBJECT:
        return StructuredState.END_OBJECT;
      case VALUE_NULL:
      case VALUE_FALSE:
      case VALUE_TRUE:
      case VALUE_NUMBER:
      case VALUE_STRING:
        return StructuredState.VALUE;
    }
    return null;
  }

  @Override
  public boolean isStringValue() {

    return this.event == Event.VALUE_STRING;
  }

  @Override
  public Object readValue() {

    require(StructuredState.VALUE);
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
      result = type.parse(this.json.getString());
    }
    next();
    return result;
  }

  @Override
  protected void doClose() throws IOException {

    this.json.close();
    this.json = null;
  }

}
