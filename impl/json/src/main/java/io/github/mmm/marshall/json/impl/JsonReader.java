/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.marshall.json.impl;

import java.io.Reader;

import io.github.mmm.base.filter.CharFilter;
import io.github.mmm.marshall.MarshallingConfig;
import io.github.mmm.marshall.StructuredReader;
import io.github.mmm.marshall.StructuredState;
import io.github.mmm.marshall.id.StructuredIdMappingObject;
import io.github.mmm.marshall.spi.AbstractStructuredScannerReader;
import io.github.mmm.marshall.spi.StructuredNodeDefault;
import io.github.mmm.marshall.spi.StructuredNodeType;
import io.github.mmm.scanner.CharStreamScanner;

/**
 * Implementation of {@link StructuredReader} for JSON from scratch.
 *
 * @see JsonFormat
 *
 * @since 1.0.0
 */
public class JsonReader extends AbstractStructuredScannerReader<StructuredNodeDefault> {

  private static final CharFilter NUMBER_START_FILTER = c -> ((c >= '0') && (c <= '9')) || (c == '+') || (c == '-')
      || (c == '.');

  private static final CharFilter NUMBER_FILTER = c -> (c >= '0') && (c <= '9') || (c == '+') || (c == '-')
      || (c == '.') || (c == 'e');

  private static final CharFilter SPACE_FILTER = c -> (c == ' ') || (c == '\t') || (c == '\n') || (c == '\r');

  private boolean requireQuotedProperties;

  private Object value;

  private boolean stringValue;

  /** 0 if no comma read, 1 if comma read, else illegal state. */
  private int commaCount;

  /**
   * The constructor.
   *
   * @param scanner the {@link Reader} with the JSON to parse.
   * @param format the {@link #getFormat() format}.
   */
  public JsonReader(CharStreamScanner scanner, JsonFormat format) {

    super(scanner, format);
    Boolean unquotedProperties = format.getConfig().get(MarshallingConfig.VAR_UNQUOTED_PROPERTIES);
    this.requireQuotedProperties = Boolean.FALSE.equals(unquotedProperties);
    next();
  }

  @Override
  protected StructuredNodeDefault newNode(StructuredNodeType type, StructuredIdMappingObject object) {

    return new StructuredNodeDefault(this.node, type);
  }

  @Override
  protected StructuredState next(boolean skip) {

    int skipCount = skip ? 1 : 0;
    boolean todo;
    do {
      todo = false;
      int skipAdd = 0;
      this.stringValue = false;
      this.reader.skipWhile(SPACE_FILTER);
      if (!this.reader.hasNext()) {
        return setState(StructuredState.DONE);
      }
      int cp = this.reader.peek();
      if (cp == '{') {
        start(StructuredNodeType.OBJECT);
        skipAdd = 1;
      } else if (cp == '}') {
        end(StructuredNodeType.OBJECT);
        skipAdd = -1;
      } else if (cp == '[') {
        start(StructuredNodeType.ARRAY);
        skipAdd = 1;
      } else if (cp == ']') {
        end(StructuredNodeType.ARRAY);
        skipAdd = -1;
      } else if (cp == ',') {
        if (this.commaCount != 0) {
          throw new IllegalStateException();
        }
        require(StructuredState.VALUE, StructuredState.END_OBJECT, StructuredState.END_ARRAY);
        this.reader.next();
        this.commaCount++;
        // next();
        todo = true;
      } else if ((this.node.type == StructuredNodeType.OBJECT) && (getState() != StructuredState.NAME)) {
        String propertyName;
        if (cp == '\"') {
          this.reader.next();
          propertyName = this.reader.readUntil('"', false, '\\');
        } else {
          if (this.requireQuotedProperties) {
            error("Expected quoted property but found character " + cp + " (0x" + Integer.toHexString(cp) + ").");
          }
          propertyName = this.reader.readUntil(':', false);
          if (propertyName == null) {
            throw new IllegalStateException();
          }
          propertyName = propertyName.trim();
        }
        nextName(propertyName);
      } else if (cp == '\"') {
        nextString();
      } else if (cp == ':') {
        require(StructuredState.NAME);
        this.reader.next();
        this.reader.skipWhile(SPACE_FILTER);
        cp = this.reader.peek();
        if (cp == '\"') {
          nextString();
        } else if (cp == '{') {
          start(StructuredNodeType.OBJECT);
          skipAdd = 1;
        } else if (cp == '[') {
          start(StructuredNodeType.ARRAY);
          skipAdd = 1;
        } else {
          nextValue(cp);
        }
      } else {
        nextValue(cp);
      }
      if (skipCount > 0) {
        skipCount += skipAdd;
        if (skipCount == 0) {
          todo = true;
        }
      }
    } while ((skipCount > 0) || todo);
    return getState();
  }

  private void nextValue(int cp) {

    if (NUMBER_START_FILTER.accept(cp)) {
      nextNumber();
    } else if (this.reader.expect("null")) {
      nextValue(null);
    } else {
      Boolean b = this.reader.readBoolean();
      if (b != null) {
        nextValue(b);
      } else {
        StringBuilder sb = new StringBuilder("Unexpected JSON character '");
        sb.appendCodePoint(cp);
        sb.append('\'');
        error(sb.toString());
      }
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
      error("Invalid number: " + numberString, e);
    }
  }

  private void nextName(String string) {

    setState(StructuredState.NAME);
    this.name = string;
  }

  @Override
  protected StructuredState start(StructuredNodeType type) {

    StructuredState state = super.start(type);
    this.reader.next();
    this.commaCount = 0;
    return state;
  }

  @Override
  protected StructuredState end(StructuredNodeType type) {

    StructuredState state = super.end(type);
    this.reader.next();
    this.commaCount = 0;
    return state;
  }

  private void nextValue(Object v) {

    this.value = v;
    setState(StructuredState.VALUE);
    this.commaCount = 0;
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
