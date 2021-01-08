/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.marshall;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Abstract base implementation of {@link StructuredReader}.
 */
public abstract class AbstractStructuredReader implements StructuredReader {

  /** The {@link MarshallingConfig}. */
  protected final MarshallingConfig config;

  /**
   * The current name.
   *
   * @see #readName()
   */
  protected String name;

  /**
   * The constructor.
   *
   * @param config the {@link MarshallingConfig}.
   */
  public AbstractStructuredReader(MarshallingConfig config) {

    super();
    this.config = config;
  }

  /**
   * @see #getState()
   */
  protected State state;

  private boolean done;

  @Override
  public State getState() {

    return this.state;
  }

  @Override
  public boolean readStartObject() {

    if (this.state == State.START_OBJECT) {
      next();
      return true;
    }
    return false;
  }

  @Override
  public boolean readStartArray() {

    if (this.state == State.START_ARRAY) {
      next();
      return true;
    }
    return false;
  }

  @Override
  public boolean readEnd() {

    if (this.state == State.DONE) {
      boolean result = !this.done;
      this.done = true;
      return result;
    }
    if ((this.state == State.END_ARRAY) || (this.state == State.END_OBJECT)) {
      next();
      return true;
    }
    return false;
  }

  @Override
  public boolean isDone() {

    return (this.state == State.DONE);
  }

  /**
   * @see #readValueAsString()
   * @return the value as {@link String} but assuring it as number.
   */
  protected String readValueAsNumberString() {

    return readValueAsString();
  }

  /**
   * Verifies that the {@link #getState() current state} is the same as the given states.
   *
   * @param expected first accepted state.
   */
  protected void expect(State expected) {

    if (this.state != expected) {
      throw new IllegalStateException("Expecting event " + expected + " but found " + this.state + ".");
    }
  }

  /**
   * Verifies that the {@link #getState() current state} is one of the given states.
   *
   * @param expected first accepted state.
   * @param expected2 second accepted state.
   */
  protected void expect(State expected, State expected2) {

    if ((this.state != expected) && (this.state != expected2)) {
      throw new IllegalStateException(
          "Expecting event " + expected + " or " + expected2 + " but found " + this.state + ".");
    }
  }

  /**
   * Verifies that the {@link #getState() current state} is one of the given states.
   *
   * @param expected first accepted state.
   * @param expected2 second accepted state.
   * @param expected3 third accepted state.
   */
  protected void expect(State expected, State expected2, State expected3) {

    if ((this.state != expected) && (this.state != expected2) && (this.state != expected3)) {
      throw new IllegalStateException(
          "Expecting event " + expected + ", " + expected2 + ",  or " + expected3 + " but found " + this.state + ".");
    }
  }

  /**
   * Verifies that the {@link #getState() current state} is not the same as the given state.
   *
   * @param unexpected the unexpected state.
   */
  protected void expectNot(State unexpected) {

    if (this.state == unexpected) {
      throw new IllegalStateException("Unexpected event " + unexpected + ".");
    }
  }

  /**
   * @param value the value that was read.
   * @param typeClass the expected type.
   * @param e a potential error that occurred or {@code null} if no cause.
   * @return nothing. Will always throw {@link IllegalStateException}. However to signal the compiler that the program
   *         flow will exit with the call of this function you may prefix it with "{@code throw}".
   */
  protected RuntimeException handleValueParseError(String value, Class<?> typeClass, Throwable e) {

    String message = "Failed to parse value '" + value + "' as " + typeClass.getSimpleName();
    if (this.name != null) {
      message = message + " for property " + this.name;
    }
    throw new IllegalStateException(message, e);
  }

  @Override
  public Integer readValueAsInteger() {

    String value = readValueAsNumberString();
    if (value == null) {
      return null;
    }
    try {
      return Integer.valueOf(value);
    } catch (NumberFormatException e) {
      throw handleValueParseError(value, Integer.class, e);
    }
  }

  @Override
  public Long readValueAsLong() {

    String value = readValueAsNumberString();
    if (value == null) {
      return null;
    }
    try {
      return Long.valueOf(value);
    } catch (NumberFormatException e) {
      throw handleValueParseError(value, Long.class, e);
    }
  }

  @Override
  public Double readValueAsDouble() {

    String value = readValueAsNumberString();
    if (value == null) {
      return null;
    }
    try {
      return Double.valueOf(value);
    } catch (NumberFormatException e) {
      throw handleValueParseError(value, Double.class, e);
    }
  }

  @Override
  public Float readValueAsFloat() {

    String value = readValueAsNumberString();
    if (value == null) {
      return null;
    }
    try {
      return Float.valueOf(value);
    } catch (NumberFormatException e) {
      throw handleValueParseError(value, Float.class, e);
    }
  }

  @Override
  public Short readValueAsShort() {

    String value = readValueAsNumberString();
    if (value == null) {
      return null;
    }
    try {
      return Short.valueOf(value);
    } catch (NumberFormatException e) {
      throw handleValueParseError(value, Short.class, e);
    }
  }

  @Override
  public Byte readValueAsByte() {

    String value = readValueAsNumberString();
    if (value == null) {
      return null;
    }
    try {
      return Byte.valueOf(value);
    } catch (NumberFormatException e) {
      throw handleValueParseError(value, Byte.class, e);
    }
  }

  @Override
  public BigInteger readValueAsBigInteger() {

    String value = readValueAsNumberString();
    if (value == null) {
      return null;
    }
    try {
      return new BigInteger(value);
    } catch (NumberFormatException e) {
      throw handleValueParseError(value, BigInteger.class, e);
    }
  }

  @Override
  public BigDecimal readValueAsBigDecimal() {

    String value = readValueAsNumberString();
    if (value == null) {
      return null;
    }
    try {
      return new BigDecimal(value);
    } catch (NumberFormatException e) {
      throw handleValueParseError(value, BigDecimal.class, e);
    }
  }

  @Override
  public Instant readValueAsInstant() {

    String value = readValueAsString();
    if (value == null) {
      return null;
    }
    try {
      return Instant.parse(value);
    } catch (RuntimeException e) {
      throw handleValueParseError(value, Instant.class, e);
    }
  }

  @Override
  public LocalDate readValueAsLocalDate() {

    String value = readValueAsString();
    if (value == null) {
      return null;
    }
    try {
      return LocalDate.parse(value);
    } catch (RuntimeException e) {
      throw handleValueParseError(value, LocalDate.class, e);
    }
  }

  @Override
  public LocalDateTime readValueAsLocalDateTime() {

    String value = readValueAsString();
    if (value == null) {
      return null;
    }
    try {
      return LocalDateTime.parse(value);
    } catch (RuntimeException e) {
      throw handleValueParseError(value, LocalDateTime.class, e);
    }
  }

  @Override
  public LocalTime readValueAsLocalTime() {

    String value = readValueAsString();
    if (value == null) {
      return null;
    }
    try {
      return LocalTime.parse(value);
    } catch (RuntimeException e) {
      throw handleValueParseError(value, LocalTime.class, e);
    }
  }

  @Override
  public ZonedDateTime readValueAsZonedDateTime() {

    String value = readValueAsString();
    if (value == null) {
      return null;
    }
    try {
      return ZonedDateTime.parse(value);
    } catch (RuntimeException e) {
      throw handleValueParseError(value, ZonedDateTime.class, e);
    }
  }

  @Override
  public OffsetDateTime readValueAsOffsetDateTime() {

    String value = readValueAsString();
    if (value == null) {
      return null;
    }
    try {
      return OffsetDateTime.parse(value);
    } catch (RuntimeException e) {
      throw handleValueParseError(value, OffsetDateTime.class, e);
    }
  }

  @Override
  public OffsetTime readValueAsOffsetTime() {

    String value = readValueAsString();
    if (value == null) {
      return null;
    }
    try {
      return OffsetTime.parse(value);
    } catch (RuntimeException e) {
      throw handleValueParseError(value, OffsetDateTime.class, e);
    }
  }

  @Override
  public Object readValue(boolean recursive) {

    Object value;
    if ((this.state == State.VALUE) || !recursive) {
      return readValue();
    } else {
      if (this.state == State.START_ARRAY) {
        Collection<Object> array = new ArrayList<>();
        readArray(array);
        value = array;
      } else if (this.state == State.START_OBJECT) {
        Map<String, Object> map = new HashMap<>();
        readObject(map);
        value = map;
      } else {
        expect(State.VALUE, State.START_ARRAY, State.START_OBJECT);
        throw new IllegalStateException();
      }
    }
    return value;
  }

  @Override
  public void readObject(Map<String, Object> map) {

    expect(State.START_OBJECT);
    next();
    while (this.state != State.END_OBJECT) {
      String key = readName();
      Object value = readValue(true);
      map.put(key, value);
    }
    next();
  }

  @Override
  public void readArray(Collection<Object> array) {

    expect(State.START_ARRAY);
    next();
    while (this.state != State.END_ARRAY) {
      Object value = readValue(true);
      array.add(value);
    }
    next();
  }

  @Override
  public void skipValue() {

    expect(State.VALUE, State.START_ARRAY, State.START_OBJECT);
    if (this.state == State.VALUE) {
      next();
    } else {
      int count = 1;
      next();
      while (count > 0) {
        switch (this.state) {
          case START_ARRAY:
          case START_OBJECT:
            count++;
            break;
          case END_ARRAY:
          case END_OBJECT:
            count--;
            break;
          case VALUE:
          case NAME:
            break;
          case DONE:
            throw new IllegalStateException();
        }
        next();
      }
    }
  }

}
