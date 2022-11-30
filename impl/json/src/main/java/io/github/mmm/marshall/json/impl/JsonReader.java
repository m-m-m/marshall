/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.marshall.json.impl;

import java.io.Reader;

import io.github.mmm.base.filter.CharFilter;
import io.github.mmm.marshall.AbstractStructuredValueReader;
import io.github.mmm.marshall.MarshallingConfig;
import io.github.mmm.marshall.StructuredFormat;
import io.github.mmm.marshall.StructuredReader;
import io.github.mmm.marshall.spi.StructuredNodeType;
import io.github.mmm.scanner.CharStreamScanner;

/**
 * Implementation of {@link StructuredReader} for JSON from scratch.
 *
 * @see JsonFormat
 *
 * @since 1.0.0
 */
public class JsonReader extends AbstractStructuredValueReader {

  private static final CharFilter NUMBER_START_FILTER = c -> ((c >= '0') && (c <= '9')) || (c == '+') || (c == '-')
      || (c == '.');

  private static final CharFilter NUMBER_FILTER = c -> (c >= '0') && (c <= '9') || (c == '+') || (c == '-')
      || (c == '.') || (c == 'e');

  private static final CharFilter SPACE_FILTER = c -> (c == ' ') || (c == '\t') || (c == '\n') || (c == '\r');

  private CharStreamScanner reader;

  private boolean requireQuotedProperties;

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
    Boolean unquotedProperties = format.getConfig().get(MarshallingConfig.OPT_UNQUOTED_PROPERTIES);
    this.requireQuotedProperties = Boolean.FALSE.equals(unquotedProperties);
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
      nextStart(StructuredNodeType.OBJECT);
    } else if (c == '}') {
      nextEnd(StructuredNodeType.OBJECT);
    } else if (c == '[') {
      nextStart(StructuredNodeType.ARRAY);
    } else if (c == ']') {
      nextEnd(StructuredNodeType.ARRAY);
    } else if (c == ',') {
      if (this.commaCount != 0) {
        throw new IllegalStateException();
      }
      expect(State.VALUE, State.END_OBJECT, State.END_ARRAY);
      this.reader.next();
      this.commaCount++;
      next();
    } else if ((this.jsonState.type == StructuredNodeType.OBJECT) && (this.state != State.NAME)) {
      String propertyName;
      if (c == '\"') {
        this.reader.next();
        propertyName = this.reader.readUntil('"', false, '\\');
      } else {
        if (this.requireQuotedProperties) {
          throw new IllegalStateException(
              "Expected quoted property but found character " + c + " (0x" + Integer.toHexString(c) + ").");
        }
        propertyName = this.reader.readUntil(':', false);
        if (propertyName == null) {
          throw new IllegalStateException();
        }
        propertyName = propertyName.trim();
      }
      nextName(propertyName);
    } else if (c == '\"') {
      nextString();
    } else if (c == ':') {
      expect(State.NAME);
      this.reader.next();
      this.reader.skipWhile(SPACE_FILTER);
      c = this.reader.peek();
      if (c == '\"') {
        nextString();
      } else if (c == '{') {
        nextStart(StructuredNodeType.OBJECT);
      } else if (c == '[') {
        nextStart(StructuredNodeType.ARRAY);
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
    } else if (this.reader.expectStrict("null")) {
      nextValue(null);
    } else if (this.reader.expectStrict("true")) {
      nextValue(Boolean.TRUE);
    } else if (this.reader.expectStrict("false")) {
      nextValue(Boolean.FALSE);
    } else {
      throw new IllegalStateException("Unexpected JSON character '" + c + "'");
    }
  }

  private void nextString() {

    this.reader.next();
    this.stringValue = true;
    String string = this.reader.readUntil('"', false, '\\');
    nextValue(string);
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

  private void nextStart(StructuredNodeType type) {

    if (this.jsonState.type == StructuredNodeType.OBJECT) {
      expect(State.NAME);
    }
    this.jsonState = new JsonState(this.jsonState, type);
    this.state = type.getStart();
    this.reader.next();
    this.commaCount = 0;
  }

  private void nextEnd(StructuredNodeType type) {

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
