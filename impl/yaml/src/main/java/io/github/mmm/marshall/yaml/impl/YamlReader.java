/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.marshall.yaml.impl;

import java.io.Reader;

import io.github.mmm.base.filter.CharFilter;
import io.github.mmm.base.filter.ListCharFilter;
import io.github.mmm.marshall.AbstractStructuredReader;
import io.github.mmm.marshall.StructuredFormat;
import io.github.mmm.marshall.StructuredReader;
import io.github.mmm.marshall.spi.StructuredNodeType;
import io.github.mmm.scanner.CharStreamScanner;

/**
 * Implementation of {@link StructuredReader} for JSON from scratch.
 *
 * @see YamlFormat
 *
 * @since 1.0.0
 */
public class YamlReader extends AbstractStructuredReader {

  private static final CharFilter NOT_NEWLINE_FILTER = CharFilter.NEWLINE_FILTER.negate();

  private static final CharFilter SPACE_FILTER = c -> (c == ' ') || (c == '\t');

  private static final CharFilter VALUE_FILTER = new ListCharFilter(':', ',', '{', '[', '&', '\n', '\r');

  private CharStreamScanner reader;

  private YamlState yamlState;

  private YamlExpectedType expectedState;

  private String nextName;

  private Object value;

  private int line;

  private int lineStartPosition;

  private int nextColumn;

  private boolean yamlArrayValue;

  private boolean end;

  private boolean stringValue;

  /** char like ',' or ':' that may not be repeated. */
  private char singleChar;

  /**
   * The constructor.
   *
   * @param reader the {@link Reader} with the JSON to parse.
   * @param format the {@link #getFormat() format}.
   */
  public YamlReader(CharStreamScanner reader, StructuredFormat format) {

    super(format);
    this.reader = reader;
    this.yamlState = new YamlState();
    this.expectedState = YamlExpectedType.ANY_VALUE;
    this.nextColumn = -1;
    next();
  }

  @Override
  public State next() {

    this.singleChar = 0;
    if (this.nextColumn != -1) {
      if (this.nextColumn > this.yamlState.column) {
        StructuredNodeType type;
        if (this.yamlArrayValue) {
          type = StructuredNodeType.ARRAY;
        } else {
          type = StructuredNodeType.OBJECT;
        }
        nextStart(type, false, this.nextColumn);
        this.nextColumn = -1;
      } else {
        nextEnd(this.yamlState.type);
        int delta = this.yamlState.column - this.nextColumn;
        if (delta == 0) {
          if (this.yamlArrayValue) {
            if (!this.yamlState.isYamlArray()) {
              error("Not in YAML array.");
            }
          }
          this.nextColumn = -1;
        } else if (delta < 0) {
          error("Invalid indentation.");
        }
      }
    } else if (this.nextName != null) {
      this.state = State.NAME;
      this.name = this.nextName;
      this.nextName = null;
    } else if (this.end) {
      this.yamlState = this.yamlState.parent;
      this.state = this.yamlState.type.getEnd();
    } else {
      nextToken();
    }
    this.expectedState.verify(this.state);
    this.expectedState = YamlExpectedType.of(this.yamlState, this.state);
    return this.state;
  }

  private void nextToken() {

    this.stringValue = false;
    this.reader.skipWhile(SPACE_FILTER);
    char c = this.reader.forcePeek();
    switch (c) {
      case '\n':
      case '\r':
        skipNewlines();
        nextToken();
        return;
      case ':':
        expect(State.NAME);
        this.reader.next();
        nextToken();
        return;
      case '{':
        nextStart(StructuredNodeType.OBJECT, true, -1);
        this.reader.next();
        return;
      case '}':
        requireJson(c);
        nextEnd(StructuredNodeType.OBJECT);
        this.reader.next();
        return;
      case '[':
        nextStart(StructuredNodeType.ARRAY, true, -1);
        this.reader.next();
        return;
      case ']':
        requireJson(c);
        nextEnd(StructuredNodeType.ARRAY);
        this.reader.next();
        return;
      case ',':
        requireJson(c);
        if (this.singleChar != 0) {
          error("Found ',' after '" + this.singleChar + "'.");
        }
        this.singleChar = ',';
        expect(State.VALUE, State.END_OBJECT, State.END_ARRAY);
        this.reader.next();
        this.reader.skipWhile(SPACE_FILTER);
        nextToken();
        return;
      case '#': // comment
        this.reader.next();
        skipLine();
        nextToken();
        return;
      case '-':
        if (this.yamlState.json) {
          break;
        }
        if (this.reader.expectStrict("- ")) { // inline-array
          this.yamlArrayValue = true;
          int column = getColumn();
          int columnDelta = column - this.yamlState.column;
          if (columnDelta == 0) {
            if (this.yamlState.type != StructuredNodeType.ARRAY) {
              error("Not in YAML array.");
            }
            nextToken();
          } else if (columnDelta > 0) {
            if (columnDelta != 2) {
              error("Invalid indentation for YAML array value.");
            }
            nextStart(StructuredNodeType.ARRAY, false, column);
          } else if (columnDelta < 0) {
            this.nextColumn = column;
          }
          return;
        } else if (this.reader.expectStrict("---")) { // block
          skipLine();
          if (this.line > 1) {
            this.end = true;
          }
          nextToken();
          return;
        }
        break;
      case 0:
        if (!this.reader.hasNext()) {
          if (this.yamlState.type == null) {
            this.state = State.DONE;
          } else {
            this.state = this.yamlState.type.getEnd();
            this.yamlState = this.yamlState.parent;
          }
        }
        return;
    }
    nextStringOrValue(c);
  }

  private void nextStringOrValue(char c) {

    String string;
    int column = getColumn();
    if (c == '"') {
      this.stringValue = true;
      this.reader.next();
      string = this.reader.readUntil('"', false, '\\');
    } else if (c == '\'') {
      requireYaml(c);
      this.stringValue = true;
      this.reader.next();
      string = this.reader.readUntil('\'', false, '\'');
    } else {
      string = this.reader.readUntil(VALUE_FILTER, true);
    }
    char next = this.reader.forcePeek();
    if (next == ':') {
      this.reader.next();
      if (this.yamlState.type == null) {
        nextStart(StructuredNodeType.OBJECT, false, column);
        this.nextName = string;
      } else {
        expectNot(State.NAME);
        if (!this.yamlState.json) {
          int columnDelta = column - this.yamlState.column;
          if (columnDelta != 0) {
            this.nextName = string;
            this.nextColumn = column;
            next();
            return;
          }
        }
        this.name = string;
        this.state = State.NAME;
      }
    } else {
      if (this.stringValue) {
        nextValue(string);
      } else {
        nextValueFromString(string);
      }
    }
  }

  private void skipLine() {

    this.reader.skipWhile(NOT_NEWLINE_FILTER);
    skipNewlines();
  }

  private void skipNewlines() {

    while (true) {
      char c = this.reader.forcePeek();
      if (!CharFilter.NEWLINE_FILTER.accept(c)) {
        return;
      }
      this.reader.next();
      this.line++;
      this.lineStartPosition = this.reader.getPosition();
      char n = this.reader.forcePeek();
      if (!CharFilter.NEWLINE_FILTER.accept(n)) {
        return;
      }
      if (c != n) {
        this.reader.next();
        this.lineStartPosition++;
      }
    }
  }

  @Override
  public boolean isStringValue() {

    return this.stringValue;
  }

  private void nextNumber(String numberString) {

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
      throw error("Invalid number: " + numberString, e);
    }
  }

  private void nextStart(StructuredNodeType type, boolean json, int column) {

    if (this.yamlState.type == StructuredNodeType.OBJECT) {
      expect(State.NAME);
    }
    if (this.yamlArrayValue) {
      //
    } else if (this.yamlState.isYamlArray()) {
      error("YAML array item must start with hyphen.");
    }
    this.yamlState = new YamlState(this.yamlState, type, json, column);
    this.state = type.getStart();
  }

  private int getColumn() {

    return this.reader.getPosition() - this.lineStartPosition;
  }

  private void requireJson(char c) {

    if (!this.yamlState.json) {
      error("Invalid character " + c);
    }
  }

  private void requireYaml(char c) {

    if (this.yamlState.json) {
      error("Invalid character " + c);
    }
  }

  private RuntimeException error(String message) {

    return error(message, null);
  }

  private RuntimeException error(String message, Throwable t) {

    throw new IllegalStateException("YAML invalid at line " + this.line + " and column " + getColumn() + ": " + message,
        t);
  }

  private void nextEnd(StructuredNodeType type) {

    if (this.yamlState.type != type) {
      throw new IllegalStateException();
    }
    this.yamlState = this.yamlState.parent;
    this.state = type.getEnd();
  }

  private void nextValueFromString(String v) {

    if (v.equals("null")) {
      nextValue(null);
    } else if (v.equalsIgnoreCase("true")) {
      nextValue(Boolean.TRUE);
    } else if (v.equalsIgnoreCase("false")) {
      nextValue(Boolean.FALSE);
    } else if (isNumber(v)) {
      nextNumber(v);
    } else {
      nextValue(v);
    }
  }

  private boolean isNumber(String number) {

    int len = number.length();
    if (len == 0) {
      return false;
    }
    int dotIndex = -1;
    int eIndex = -1;
    boolean digits = false;
    for (int i = 0; i < len; i++) {
      char c = number.charAt(i);
      if ((c >= '0') && (c <= '9')) {
        digits = true;
      } else if (c == '.') {
        if (dotIndex != -1) {
          return false; // only one dot allowed
        }
        dotIndex = i;
        digits = false; // require digits after dot
      } else if ((c == 'e') || (c == 'E')) {
        if (!digits) {
          return false; // dot cannot be followed by e/E
        }
        digits = false; // require digits after e/E
        eIndex = i;
      } else if ((c == '+') || (c == '-')) {
        if ((i > 0) && (eIndex != (i - 1))) {
          return false; // sign only allowed as first char or after e/E
        }
      } else {
        return false;
      }
    }
    return true;
  }

  private void nextValue(Object v) {

    this.value = v;
    this.state = State.VALUE;
    this.yamlArrayValue = false;
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

    if (this.yamlState == null) {
      return;
    }
    // if (this.readerState.parent != null) {
    // throw new IllegalStateException("Not at end!");
    // }
    this.yamlState = null;
    this.reader = null;
  }

  @Override
  public String toString() {

    return this.reader.toString();
  }

}
