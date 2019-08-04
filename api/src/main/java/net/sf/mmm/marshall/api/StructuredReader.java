package net.sf.mmm.marshall.api;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * Interface for a reader to parse a {@link StructuredFormat structured format} such as JSON or XML.
 *
 * @since 1.0.0
 */
public interface StructuredReader {

  /**
   * @return the name of the current property or element.
   */
  String readName();

  /**
   * @return {@code true} if pointing to the start of an object, {@code false} otherwise.
   * @see StructuredWriter#writeStartObject()
   */
  boolean readStartObject();

  /**
   * @return {@code true} if pointing to the start of an object, {@code false} otherwise.
   * @see StructuredWriter#writeStartArray()
   */
  boolean readStartArray();

  /**
   * Generic method to read and unmarshall a value.<br>
   * <b>ATTENTION:</b><br>
   * This generic method only exists as convenience method for the {@code readValueAs*} methods. It therefore only
   * supports standard Java value types as described in {@link net.sf.mmm.marshall.api}. For other types it will throw
   * an {@link IllegalArgumentException}.
   *
   * @param <V> type of the value to read.
   * @param type {@link Class} reflecting the value to read.
   * @return the unmarsahlled value.
   */
  default <V> V readValue(Class<V> type) {

    Objects.requireNonNull(type, "type");
    Object value;
    if (type.equals(String.class)) {
      value = readValueAsString();
    } else if (Boolean.class.equals(type)) {
      value = readValueAsBoolean();
    } else if (Integer.class.equals(type)) {
      value = readValueAsInteger();
    } else if (Long.class.equals(type)) {
      value = readValueAsLong();
    } else if (Short.class.equals(type)) {
      value = readValueAsShort();
    } else if (Byte.class.equals(type)) {
      value = readValueAsByte();
    } else if (Double.class.equals(type)) {
      value = readValueAsDouble();
    } else if (Float.class.equals(type)) {
      value = readValueAsFloat();
    } else if (BigInteger.class.equals(type)) {
      value = readValueAsBigInteger();
    } else if (BigDecimal.class.equals(type)) {
      value = readValueAsBigDecimal();
    } else if (Instant.class.equals(type)) {
      value = readValueAsInstant();
    } else if (LocalDateTime.class.equals(type)) {
      value = readValueAsLocalDateTime();
    } else if (LocalDate.class.equals(type)) {
      value = readValueAsLocalDate();
    } else if (LocalTime.class.equals(type)) {
      value = readValueAsLocalTime();
    } else if (ZonedDateTime.class.equals(type)) {
      value = readValueAsZonedDateTime();
    } else if (OffsetDateTime.class.equals(type)) {
      value = readValueAsOffsetDateTime();
    } else if (OffsetTime.class.equals(type)) {
      value = readValueAsOffsetTime();
    } else {
      throw new IllegalArgumentException("Unsupported value type " + type);
    }
    return type.cast(value);
  }

  /**
   * @return reads the value as {@link String}.
   * @see #readValue(Class)
   */
  String readValueAsString();

  /**
   * @return reads the value as {@link Boolean}.
   * @see #readValue(Class)
   */
  Boolean readValueAsBoolean();

  /**
   * @return reads the value as {@link Integer}.
   * @see #readValue(Class)
   */
  Integer readValueAsInteger();

  /**
   * @return reads the value as {@link Long}.
   * @see #readValue(Class)
   */
  Long readValueAsLong();

  /**
   * @return reads the value as {@link Double}.
   * @see #readValue(Class)
   */
  Double readValueAsDouble();

  /**
   * @return reads the value as {@link Float}.
   * @see #readValue(Class)
   */
  Float readValueAsFloat();

  /**
   * @return reads the value as {@link Short}.
   * @see #readValue(Class)
   */
  Short readValueAsShort();

  /**
   * @return reads the value as {@link Byte}.
   * @see #readValue(Class)
   */
  Byte readValueAsByte();

  /**
   * @return reads the value as {@link BigInteger}.
   * @see #readValue(Class)
   */
  BigInteger readValueAsBigInteger();

  /**
   * @return reads the value as {@link BigDecimal}.
   * @see #readValue(Class)
   */
  BigDecimal readValueAsBigDecimal();

  /**
   * @return reads the value as {@link Instant}.
   * @see #readValue(Class)
   */
  Instant readValueAsInstant();

  /**
   * @return reads the value as {@link LocalDate}.
   * @see #readValue(Class)
   */
  LocalDate readValueAsLocalDate();

  /**
   * @return reads the value as {@link LocalDateTime}.
   * @see #readValue(Class)
   */
  LocalDateTime readValueAsLocalDateTime();

  /**
   * @return reads the value as {@link LocalTime}.
   * @see #readValue(Class)
   */
  LocalTime readValueAsLocalTime();

  /**
   * @return reads the value as {@link ZonedDateTime}.
   * @see #readValue(Class)
   */
  ZonedDateTime readValueAsZonedDateTime();

  /**
   * @return reads the value as {@link OffsetDateTime}.
   * @see #readValue(Class)
   */
  OffsetDateTime readValueAsOffsetDateTime();

  /**
   * @return reads the value as {@link OffsetTime}.
   * @see #readValue(Class)
   */
  OffsetTime readValueAsOffsetTime();

  /**
   * @return {@code true} if the end of an {@link #readStartArray() array} or {@link #readStartObject() object} has been
   *         reached.
   */
  boolean readEnd();

  /**
   * @return {@code true} if all data has been read and the end of the stream has been reached, {@code false} otherwise.
   */
  boolean isDone();

}
