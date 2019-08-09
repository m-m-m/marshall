/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package net.sf.mmm.marshall;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZonedDateTime;

/**
 * Abstract base implementation of {@link StructuredReader}.
 */
public abstract class AbstractStructuredReader implements StructuredReader {

  /**
   * The current name.
   *
   * @see #readName()
   */
  protected String name;

  /**
   * @see #readValueAsString()
   * @return the value as {@link String} but assuring it as number.
   */
  protected String readValueAsNumberString() {

    return readValueAsString();
  }

  private RuntimeException handleValueParseError(String value, Class<?> typeClass, Throwable e) {

    String message = "Failed to parse value '" + value + "' as " + typeClass.getSimpleName();
    if (this.name != null) {
      message = message + " for property " + this.name;
    }
    return new IllegalStateException(message, e);
  }

  @Override
  public Boolean readValueAsBoolean() {

    String value = readValueAsString();
    if (value == null) {
      return null;
    } else if ("true".equalsIgnoreCase(value)) {
      return Boolean.TRUE;
    } else if ("false".equalsIgnoreCase(value)) {
      return Boolean.FALSE;
    } else {
      throw handleValueParseError(value, Boolean.class, null);
    }
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

    String value = readValueAsString();
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

    String value = readValueAsString();
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

}
