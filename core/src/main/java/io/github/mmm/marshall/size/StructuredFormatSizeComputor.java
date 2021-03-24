package io.github.mmm.marshall.size;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.time.temporal.Temporal;

import io.github.mmm.marshall.StructuredWriter;

/**
 * Interface to compute the {@link #sizeOfProperty(String, int) size of a property} or an atomic
 * {@link #sizeOfObject(Object) value}.
 *
 * @since 1.0.0
 * @see io.github.mmm.marshall.StructuredBinaryFormat#getSizeComputor()
 * @see io.github.mmm.marshall.StructuredWriter#writeStartObject(int)
 * @see io.github.mmm.marshall.StructuredWriter#writeStartArray(int)
 */
public interface StructuredFormatSizeComputor {

  /**
   * @param name the {@link StructuredWriter#writeName(String, int) name to write}.
   * @param id the {@link StructuredWriter#writeName(String, int) ID to write}.
   * @return the computed size of the property specified by {@code name} and {@code id}.
   */
  default int sizeOfProperty(String name, int id) {

    return -1;
  }

  /**
   * @param value the {@link StructuredWriter#writeValue(Object) value to write}.
   * @return the computed size of the given {@code value}.
   */
  default int sizeOfObject(Object value) {

    if (value == null) {
      return 0;
    } else if (value instanceof String) {
      return sizeOfString((String) value);
    } else if (value instanceof Boolean) {
      return sizeOfBoolean((Boolean) value);
    } else if (value instanceof Number) {
      return sizeOfNumber((Number) value);
    } else if (value instanceof Temporal) {
      return sizeOfTemporal((Temporal) value);
    } else {
      return -1;
    }
  }

  /**
   * @param value the {@link StructuredWriter#writeValueAsString(String) string value to write}.
   * @return the computed size of the given {@code value}.
   */
  default int sizeOfString(String value) {

    return -1;
  }

  /**
   * @param value the {@link StructuredWriter#writeValue(Object) temporal value to write}.
   * @return the computed size of the given {@code value}.
   */
  default int sizeOfBoolean(Boolean value) {

    return -1;
  }

  /**
   * @param value the {@link StructuredWriter#writeValueAsNumber(Number) number value to write}.
   * @return the computed size of the given {@code value}.
   */
  default int sizeOfNumber(Number value) {

    if (value == null) {
      return 0;
    } else if (value instanceof BigDecimal) {
      return sizeOfBigDecimal((BigDecimal) value);
    } else if (value instanceof BigInteger) {
      return sizeOfBigInteger((BigInteger) value);
    } else if (value instanceof Double) {
      return sizeOfDouble((Double) value);
    } else if (value instanceof Long) {
      return sizeOfLong((Long) value);
    } else if (value instanceof Integer) {
      return sizeOfInteger((Integer) value);
    } else if (value instanceof Float) {
      return sizeOfFloat((Float) value);
    } else if (value instanceof Short) {
      return sizeOfShort((Short) value);
    } else if (value instanceof Byte) {
      return sizeOfByte((Byte) value);
    } else {
      return -1;
    }
  }

  /**
   * @param value the {@link StructuredWriter#writeValueAsBigDecimal(BigDecimal) big decimal value to write}.
   * @return the computed size of the given {@code value}.
   */
  default int sizeOfBigDecimal(BigDecimal value) {

    return -1;
  }

  /**
   * @param value the {@link StructuredWriter#writeValueAsBigInteger(BigInteger) big integer value to write}.
   * @return the computed size of the given {@code value}.
   */
  default int sizeOfBigInteger(BigInteger value) {

    return -1;
  }

  /**
   * @param value the {@link StructuredWriter#writeValueAsDouble(Double) double value to write}.
   * @return the computed size of the given {@code value}.
   */
  default int sizeOfDouble(Double value) {

    return -1;
  }

  /**
   * @param value the {@link StructuredWriter#writeValueAsFloat(Float) float value to write}.
   * @return the computed size of the given {@code value}.
   */
  default int sizeOfFloat(Float value) {

    return -1;
  }

  /**
   * @param value the {@link StructuredWriter#writeValueAsLong(Long) long value to write}.
   * @return the computed size of the given {@code value}.
   */
  default int sizeOfLong(Long value) {

    return -1;
  }

  /**
   * @param value the {@link StructuredWriter#writeValueAsInteger(Integer) integer value to write}.
   * @return the computed size of the given {@code value}.
   */
  default int sizeOfInteger(Integer value) {

    return -1;
  }

  /**
   * @param value the {@link StructuredWriter#writeValueAsShort(Short) short value to write}.
   * @return the computed size of the given {@code value}.
   */
  default int sizeOfShort(Short value) {

    return -1;
  }

  /**
   * @param value the {@link StructuredWriter#writeValueAsByte(Byte) byte value to write}.
   * @return the computed size of the given {@code value}.
   */
  default int sizeOfByte(Byte value) {

    return -1;
  }

  /**
   * @param value the {@link StructuredWriter#writeValue(Object) temporal value to write}.
   * @return the computed size of the given {@code value}.
   */
  default int sizeOfTemporal(Temporal value) {

    return -1;
  }

  /**
   * @param value the {@link StructuredWriter#writeValueAsInstant(Instant) instant value to write}.
   * @return the computed size of the given {@code value}.
   */
  default int sizeOfInstant(Instant value) {

    return -1;
  }

  /**
   * @param value the {@link StructuredWriter#writeValueAsLocalDate(LocalDate) local date value to write}.
   * @return the computed size of the given {@code value}.
   */
  default int sizeOfLocalDate(LocalDate value) {

    return -1;
  }

  /**
   * @param value the {@link StructuredWriter#writeValueAsLocalDateTime(LocalDateTime) local date time value to write}.
   * @return the computed size of the given {@code value}.
   */
  default int sizeOfLocalDateTime(LocalDateTime value) {

    return -1;
  }

  /**
   * @param value the {@link StructuredWriter#writeValueAsLocalTime(LocalTime) local time value to write}.
   * @return the computed size of the given {@code value}.
   */
  default int sizeOfLocalTime(LocalTime value) {

    return -1;
  }

  /**
   * @param value the {@link StructuredWriter#writeValueAsOffsetDateTime(OffsetDateTime) offset date time value to
   *        write}.
   * @return the computed size of the given {@code value}.
   */
  default int sizeOfOffsetDateTime(OffsetDateTime value) {

    return -1;
  }

  /**
   * @param value the {@link StructuredWriter#writeValueAsZonedDateTime(ZonedDateTime) zoned date time value to write}.
   * @return the computed size of the given {@code value}.
   */
  default int sizeOfZonedDateTime(ZonedDateTime value) {

    return -1;
  }

}
