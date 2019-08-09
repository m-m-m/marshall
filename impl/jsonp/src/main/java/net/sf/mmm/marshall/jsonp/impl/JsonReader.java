package net.sf.mmm.marshall.jsonp.impl;

import java.math.BigDecimal;

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

  private final JsonParser json;

  private Event event;

  /**
   * The constructor.
   *
   * @param json the {@link JsonReader}.
   */
  public JsonReader(JsonParser json) {

    super();
    this.json = json;
    this.event = json.next();
  }

  private void expect(Event expected) {

    if (this.event != expected) {
      throw new IllegalStateException("Expecting event '" + expected + "' but found '" + this.event + "'.");
    }
  }

  private void next() {

    assert (this.event != null);
    if (this.json.hasNext()) {
      this.event = this.json.next();
    } else {
      this.event = null;
    }
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
    expect(Event.VALUE_NUMBER);
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
    expect(Event.VALUE_NUMBER);
    Long value = Long.valueOf(this.json.getLong());
    next();
    return value;
  }

  @Override
  public BigDecimal readValueAsBigDecimal() {

    if (this.event == Event.VALUE_NULL) {
      next();
      return null;
    }
    BigDecimal value = this.json.getBigDecimal();
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
  public boolean isDone() {

    return (this.event == null);
  }

}
