/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.marshall.json.impl;

import java.io.Reader;

import io.github.mmm.base.filter.CharFilter;
import io.github.mmm.marshall.AbstractStructuredReader;
import io.github.mmm.marshall.StructuredFormat;
import io.github.mmm.marshall.StructuredReader;
import io.github.mmm.scanner.CharStreamScanner;

/**
 * Implementation of {@link StructuredReader} for JSON from scratch.
 *
 * @see JsonFormatImpl
 *
 * @since 1.0.0
 */
public class JsonReader extends AbstractStructuredReader {

  private static final CharFilter NUMBER_START_FILTER = c -> ((c >= '0') && (c <= '9')) || (c == '+') || (c == '-')
      || (c == '.');

  private static final CharFilter NUMBER_FILTER = c -> (c >= '0') && (c <= '9') || (c == '+') || (c == '-')
      || (c == '.') || (c == 'e');

  private static final CharFilter SPACE_FILTER = c -> (c == ' ') || (c == '\t') || (c == '\n') || (c == '\r');

  private CharStreamScanner reader;

  private JsonState jsonState;

  private Object value;

  private boolean stringValue;

  /** 0 if no comma read, 1 if comma read, else illegal state. */
  private int commaCount;

  /**
   * The constructor.
   *
   * @param reader the {@link Reader} with the JSON to parse.
   * @param format the {@link #getFormat() format}.
   */
  public JsonReader(CharStreamScanner reader, StructuredFormat format) {

    super(format);
    this.reader = reader;
    this.jsonState = new JsonState();
    next();
  }

  @Override
  public State next() {

    this.stringValue = false;
    this.reader.skipWhile(SPACE_FILTER);
    if (!this.reader.hasNext()) {
      this.state = State.DONE;
      return this.state;
    }
    char c = this.reader.peek();
    if (c == '{') {
      nextStart(JsonNodeType.OBJECT);
    } else if (c == '}') {
      nextEnd(JsonNodeType.OBJECT);
    } else if (c == '[') {
      nextStart(JsonNodeType.ARRAY);
    } else if (c == ']') {
      nextEnd(JsonNodeType.ARRAY);
    } else if (c == '\"') {
      nextString(false);
    } else if (c == ',') {
      if (this.commaCount != 0) {
        throw new IllegalStateException();
      }
      expect(State.VALUE, State.END_OBJECT, State.END_ARRAY);
      this.reader.next();
      this.commaCount++;
      next();
    } else if (c == ':') {
      expect(State.NAME);
      this.reader.next();
      this.reader.skipWhile(SPACE_FILTER);
      c = this.reader.forcePeek();
      if (c == '\"') {
        nextString(true);
      } else if (c == '{') {
        nextStart(JsonNodeType.OBJECT);
      } else if (c == '[') {
        nextStart(JsonNodeType.ARRAY);
      } else {
        nextValue(c);
      }
    } else {
      nextValue(c);
    }
    return this.state;
  }

  private void nextValue(char c) {

    if (NUMBER_START_FILTER.accept(c)) {
      nextNumber();
    } else if (this.reader.expect("null")) {
      nextValue(null);
    } else if (this.reader.expect("true")) {
      nextValue(Boolean.TRUE);
    } else if (this.reader.expect("false")) {
      nextValue(Boolean.FALSE);
    } else {
      throw new IllegalStateException("Unexpected JSON character '" + c + "'");
    }
  }

  private void nextString(boolean mustBeValue) {

    this.reader.next();
    String string = this.reader.readUntil('"', false, '\\');
    if (!mustBeValue && (this.jsonState.type == JsonNodeType.OBJECT)) {
      nextName(string);
    } else {
      this.stringValue = true;
      nextValue(string);
    }
    this.commaCount = 0;
  }

  @Override
  public boolean isStringValue() {

    return this.stringValue;
  }

  private void nextNumber() {

    String numberString = this.reader.readWhile(NUMBER_FILTER);
    try {
      Number number;
      if ((numberString.indexOf('.') >= 0) || (numberString.indexOf('e') >= 0)) {
        double d = Double.parseDouble(numberString);
        float f = (float) d;
        if (f == d) {
          number = Float.valueOf(f);
        } else {
          number = Double.valueOf(numberString);
        }
      } else {
        long l = Long.parseLong(numberString);
        int i = (int) l;
        if (i == l) {
          number = Integer.valueOf(i);
        } else {
          number = Long.valueOf(numberString);
        }
      }
      nextValue(number);
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException("Invalid number: " + numberString);
    }
  }

  private void nextName(String string) {

    expectNot(State.NAME);
    this.name = string;
    this.state = State.NAME;
  }

  private void nextStart(JsonNodeType type) {

    if (this.jsonState.type == JsonNodeType.OBJECT) {
      expect(State.NAME);
    }
    this.jsonState = new JsonState(this.jsonState, type);
    this.state = type.getStart();
    this.reader.next();
    this.commaCount = 0;
  }

  private void nextEnd(JsonNodeType type) {

    if (this.jsonState.type != type) {
      throw new IllegalStateException();
    }
    this.jsonState = this.jsonState.parent;
    this.state = type.getEnd();
    this.reader.next();
    this.commaCount = 0;
  }

  private void nextValue(Object v) {

    this.value = v;
    this.state = State.VALUE;
    this.commaCount = 0;
  }

  @Override
  public Object readValue() {

    expect(State.VALUE);
    Object v = this.value;
    this.value = null;
    next();
    return v;
  }

  @Override
  public String readValueAsString() {

    Object v = readValue();
    if (v == null) {
      return null;
    }
    return v.toString();
  }

  @Override
  public Boolean readValueAsBoolean() {

    Object v = readValue();
    if (v == null) {
      return null;
    } else if (v instanceof Boolean) {
      return (Boolean) v;
    } else {
      throw new IllegalArgumentException("Value of type " + v.getClass().getName() + " can not be read as boolean!");
    }
  }

  @Override
  protected String readValueAsNumberString() {

    Object v = readValue();
    if (v == null) {
      return null;
    } else if (v instanceof String) {
      return (String) v;
    } else if (v instanceof Number) {
      return v.toString();
    } else {
      throw new IllegalArgumentException("Value of type " + v.getClass().getName() + " can not be read as number!");
    }
  }

  @Override
  public void close() {

    if (this.jsonState == null) {
      return;
    }
    // if (this.readerState.parent != null) {
    // throw new IllegalStateException("Not at end!");
    // }
    this.jsonState = null;
    this.reader = null;
  }

  @Override
  public String toString() {

    return this.reader.toString();
  }

}
