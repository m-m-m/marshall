/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.marshall.yaml.impl;

import java.io.Reader;

import io.github.mmm.base.filter.CharFilter;
import io.github.mmm.base.filter.ListCharFilter;
import io.github.mmm.marshall.StructuredFormat;
import io.github.mmm.marshall.StructuredReader;
import io.github.mmm.marshall.StructuredState;
import io.github.mmm.marshall.id.StructuredIdMappingObject;
import io.github.mmm.marshall.spi.AbstractStructuredScannerReader;
import io.github.mmm.marshall.spi.StructuredNodeType;
import io.github.mmm.scanner.CharStreamScanner;

/**
 * Implementation of {@link StructuredReader} for JSON from scratch.
 *
 * @see YamlFormat
 *
 * @since 1.0.0
 */
public class YamlReader extends AbstractStructuredScannerReader<YamlNode> {

  private static final CharFilter NOT_NEWLINE_FILTER = CharFilter.NEWLINE.negate();

  private static final CharFilter SPACE_FILTER = c -> (c == ' ') || (c == '\t');

  private static final CharFilter VALUE_FILTER = new ListCharFilter(':', ',', '{', '[', '&', '\n', '\r');

  private String nextName;

  private Object value;

  private int column;

  private int nextColumn;

  private boolean json;

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

    super(reader, format);
    this.nextColumn = -1;
    next();
  }

  @Override
  protected YamlNode newNode(StructuredNodeType type, StructuredIdMappingObject object) {

    return new YamlNode(this.node, type, this.json, this.column);
  }

  @Override
  protected StructuredState next(boolean skip) {

    int skipCount = skip ? 1 : 0;
    StructuredState state;
    boolean todo;
    do {
      todo = false;
      int skipAdd = 0;
      state = getState();
      if (state != StructuredState.NAME) {
        readComment(); // clear comment to avoid appending unrelated comments
      }
      this.singleChar = 0;
      if (this.nextColumn != -1) {
        if (this.nextColumn > this.node.column) {
          StructuredNodeType type;
          if (this.yamlArrayValue) {
            type = StructuredNodeType.ARRAY;
          } else {
            type = StructuredNodeType.OBJECT;
          }
          start(type, false, this.nextColumn);
          skipAdd = 1;
          this.nextColumn = -1;
        } else {
          end(null);
          skipAdd = -1;
          int delta = this.node.column - this.nextColumn;
          if (delta == 0) {
            if (this.yamlArrayValue) {
              if (!this.node.isYamlArray()) {
                error("Not in YAML array.");
              }
            }
            this.nextColumn = -1;
          } else if (delta < 0) {
            error("Invalid indentation.");
          }
        }
      } else if (this.nextName != null) {
        state = setState(StructuredState.NAME);
        this.name = this.nextName;
        this.nextName = null;
      } else if (this.end) {
        end(null);
      } else {
        nextToken();
        state = getState();
        if (state.isStart()) {
          skipAdd = 1;
        } else if (state.isEnd()) {
          skipAdd = -1;
        }
      }
      if (skipCount > 0) {
        skipCount += skipAdd;
        if (skipCount == 0) {
          todo = true;
        }
      }
    } while ((skipCount > 0) || todo);
    return state;
  }

  private void nextToken() {

    this.stringValue = false;
    this.reader.skipWhile(SPACE_FILTER);
    char c = this.reader.peek();
    switch (c) {
      case '\n':
      case '\r':
        skipNewlines();
        nextToken();
        return;
      case ':':
        require(StructuredState.NAME);
        this.reader.next();
        nextToken();
        return;
      case '{':
        start(StructuredNodeType.OBJECT, true, -1);
        this.reader.next();
        return;
      case '}':
        requireJson(c);
        end(StructuredNodeType.OBJECT);
        this.reader.next();
        return;
      case '[':
        start(StructuredNodeType.ARRAY, true, -1);
        this.reader.next();
        return;
      case ']':
        requireJson(c);
        end(StructuredNodeType.ARRAY);
        this.reader.next();
        return;
      case ',':
        requireJson(c);
        if (this.singleChar != 0) {
          error("Found ',' after '" + this.singleChar + "'.");
        }
        this.singleChar = ',';
        require(StructuredState.VALUE, StructuredState.END_OBJECT, StructuredState.END_ARRAY);
        this.reader.next();
        this.reader.skipWhile(SPACE_FILTER);
        nextToken();
        return;
      case '#': // comment
        this.reader.next();
        addComment(this.reader.readLine(true));
        nextToken();
        return;
      case '-':
        if (this.node.json) {
          break;
        }
        if (this.reader.expect("- ")) { // inline-array
          this.yamlArrayValue = true;
          int col = this.reader.getColumn();
          int columnDelta = col - this.node.column;
          if (columnDelta == 0) {
            if (this.node.type != StructuredNodeType.ARRAY) {
              error("Not in YAML array.");
            }
            nextToken();
          } else if (columnDelta > 0) {
            if (columnDelta != 2) {
              error("Invalid indentation for YAML array value.");
            }
            start(StructuredNodeType.ARRAY, false, col);
          } else if (columnDelta < 0) {
            this.nextColumn = col;
          }
          return;
        } else if (this.reader.expect("---")) { // block
          skipLine();
          if (this.reader.getLine() > 1) {
            this.end = true;
          }
          nextToken();
          return;
        }
        break;
      case 0:
        if (!this.reader.hasNext()) {
          if (this.node.type == null) {
            setState(StructuredState.DONE);
          } else {
            end(null);
          }
        }
        return;
    }
    nextStringOrValue(c);
  }

  private void nextStringOrValue(char c) {

    String string;
    final int col = this.reader.getColumn();
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
    char next = this.reader.peek();
    if (next == ':') {
      this.reader.next();
      if (this.node.type == null) {
        start(StructuredNodeType.OBJECT, false, col);
        this.nextName = string;
      } else {
        requireNot(StructuredState.NAME);
        if (!this.node.json) {
          int columnDelta = col - this.node.column;
          if (columnDelta != 0) {
            this.nextName = string;
            this.nextColumn = col;
            next();
            return;
          }
        }
        this.name = string;
        setState(StructuredState.NAME);
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
      char c = this.reader.peek();
      if (!CharFilter.NEWLINE.accept(c)) {
        return;
      }
      this.reader.next();
      char n = this.reader.peek();
      if (!CharFilter.NEWLINE.accept(n)) {
        return;
      }
      if (c != n) {
        this.reader.next();
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

  @Override
  protected StructuredState start(StructuredNodeType type) {

    if (this.yamlArrayValue) {
      //
    } else if (this.node.isYamlArray()) {
      error("YAML array item must start with hyphen.");
    }
    return super.start(type);
  }

  private StructuredState start(StructuredNodeType type, boolean isJson, int col) {

    this.json = isJson;
    this.column = col;
    return start(type);
  }

  private void requireJson(char c) {

    if (!this.node.json) {
      error("Invalid character " + c);
    }
  }

  private void requireYaml(char c) {

    if (this.node.json) {
      error("Invalid character " + c);
    }
  }

  @Override
  protected RuntimeException error(String message, Throwable cause) {

    message = "YAML invalid at line " + this.reader.getLine() + " and column " + this.reader.getColumn() + ": "
        + message;
    return super.error(message, cause);
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
    setState(StructuredState.VALUE);
    this.yamlArrayValue = false;
  }

  @Override
  public Object readValue() {

    require(StructuredState.VALUE);
    Object v = this.value;
    this.value = null;
    next();
    return v;
  }

}
