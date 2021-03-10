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
import java.time.temporal.Temporal;

/**
 * Interface for a writer to produce a {@link StructuredFormat structured format} such as JSON or XML.
 *
 * @since 1.0.0
 */
public interface StructuredWriter extends AutoCloseable {

  /**
   * Writes the start of an array or collection. After the call of this method for each element of the array or
   * collection a call of {@link #writeValue(Object)} (including any of its typed variants) needs to be called followed
   * by {@link #writeEnd()}.
   */
  void writeStartArray();

  /**
   * Writes the start of an object. After the call of this method for each property of the object
   * {@link #writeName(String)} needs to be called followed by writing its value. Then {@link #writeEnd()} needs to be
   * called to terminate the object.
   */
  void writeStartObject();

  /**
   * Writes the end of an {@link #writeStartArray() array} or {@link #writeStartObject() object}.
   */
  void writeEnd();

  /**
   * Writes the name of a property. After the call of this method a subsequent call of {@link #writeStartArray()},
   * {@link #writeStartObject()} or {@link #writeValue(Object)} (including any of its typed variants) needs to be
   * called.
   *
   * @param name the name of the property.
   */
  default void writeName(String name) {

    writeName(name, -1);
  }

  /**
   * Writes the name of a property. After the call of this method a subsequent call of {@link #writeStartArray()},
   * {@link #writeStartObject()} or {@link #writeValue(Object)} (including any of its typed variants) needs to be
   * called.
   *
   * @param name the name of the property.
   * @param id the ID of the field name. Will only be required by specific {@link StructuredFormat formats} such as
   *        {@link StructuredFormat#ID_PROTOBUF ProtoBuf/gRPC}.
   */
  void writeName(String name, int id);

  /**
   * Writes a value for the current property or {@link #writeStartArray() array} element.<br>
   * <b>ATTENTION:</b><br>
   * This generic method only exists as convenience method for the {@code writeValueAs*} methods. It therefore only
   * supports standard Java value types as described in {@link io.github.mmm.marshall}. For other types it falls back to
   * {@link Object#toString()}.
   *
   * @param value the value to write. May be {@code null}.
   */
  default void writeValue(Object value) {

    if (value == null) {
      writeValueAsNull();
    } else if (value instanceof String) {
      writeValueAsString(value.toString());
    } else if (value instanceof Boolean) {
      writeValueAsBoolean((Boolean) value);
    } else if (value instanceof Number) {
      writeValueAsNumber((Number) value);
    } else if (value instanceof Temporal) {
      if (value instanceof Instant) {
        writeValueAsInstant(((Instant) value));
      } else if (value instanceof LocalDateTime) {
        writeValueAsLocalDateTime(((LocalDateTime) value));
      } else if (value instanceof LocalDate) {
        writeValueAsLocalDate(((LocalDate) value));
      } else if (value instanceof LocalTime) {
        writeValueAsLocalTime(((LocalTime) value));
      } else if (value instanceof ZonedDateTime) {
        writeValueAsZonedDateTime(((ZonedDateTime) value));
      } else if (value instanceof OffsetDateTime) {
        writeValueAsOffsetDateTime(((OffsetDateTime) value));
      } else if (value instanceof OffsetTime) {
        writeValueAsOffsetTime(((OffsetTime) value));
      } else {
        writeValueAsString(value.toString());
      }
    } else if (value instanceof MarshallableObject) {
      ((MarshallableObject) value).write(this);
    } else {
      writeValueAsString(value.toString());
    }
  }

  /**
   * Writes a {@link Number} value for the current property or {@link #writeStartArray() array} element.<br>
   *
   * @param value the value to write. May be {@code null}.
   */
  default void writeValueAsNumber(Number value) {

    if (value == null) {
      writeValueAsNull();
    } else if (value instanceof Long) {
      writeValueAsLong((Long) value);
    } else if (value instanceof Integer) {
      writeValueAsInteger((Integer) value);
    } else if (value instanceof BigDecimal) {
      writeValueAsBigDecimal((BigDecimal) value);
    } else if (value instanceof BigInteger) {
      writeValueAsBigInteger((BigInteger) value);
    } else if (value instanceof Short) {
      writeValueAsShort((Short) value);
    } else if (value instanceof Byte) {
      writeValueAsByte((Byte) value);
    } else if (value instanceof Float) {
      writeValueAsFloat((Float) value);
    } else if (value instanceof Double) {
      writeValueAsDouble((Double) value);
    } else {
      writeValueAsDouble(Double.valueOf(value.doubleValue()));
    }
  }

  /**
   * Writes the value {@code null} (undefined). Called from all other {@code writeValue*} methods in case {@code null}
   * is provided as value. Via {@link MarshallingConfig#OPT_WRITE_NULL_VALUES} one can configure if {@code null} values
   * should be written or omitted (to save bandwidth).
   *
   * @see #writeValue(Object)
   */
  void writeValueAsNull();

  /**
   * @param value the value to write.
   * @see #writeValue(Object)
   */
  default void writeValueAsInstant(Instant value) {

    if (value == null) {
      writeValueAsNull();
    } else {
      writeValueAsString(value.toString());
    }
  }

  /**
   * @param value the value to write.
   * @see #writeValue(Object)
   */
  default void writeValueAsLocalDateTime(LocalDateTime value) {

    if (value == null) {
      writeValueAsNull();
    } else {
      writeValueAsString(value.toString());
    }
  }

  /**
   * @param value the value to write.
   * @see #writeValue(Object)
   */
  default void writeValueAsLocalDate(LocalDate value) {

    if (value == null) {
      writeValueAsNull();
    } else {
      writeValueAsString(value.toString());
    }
  }

  /**
   * @param value the value to write.
   * @see #writeValue(Object)
   */
  default void writeValueAsLocalTime(LocalTime value) {

    if (value == null) {
      writeValueAsNull();
    } else {
      writeValueAsString(value.toString());
    }
  }

  /**
   * @param value the value to write.
   * @see #writeValue(Object)
   */
  default void writeValueAsZonedDateTime(ZonedDateTime value) {

    if (value == null) {
      writeValueAsNull();
    } else {
      writeValueAsString(value.toString());
    }
  }

  /**
   * @param value the value to write.
   * @see #writeValue(Object)
   */
  default void writeValueAsOffsetDateTime(OffsetDateTime value) {

    if (value == null) {
      writeValueAsNull();
    } else {
      writeValueAsString(value.toString());
    }
  }

  /**
   * @param value the value to write.
   * @see #writeValue(Object)
   */
  default void writeValueAsOffsetTime(OffsetTime value) {

    if (value == null) {
      writeValueAsNull();
    } else {
      writeValueAsString(value.toString());
    }
  }

  /**
   * @param value the value to write.
   * @see #writeValue(Object)
   */
  void writeValueAsString(String value);

  /**
   * @param value the value to write.
   * @see #writeValue(Object)
   */
  void writeValueAsBoolean(Boolean value);

  /**
   * @param value the value to write.
   * @see #writeValue(Object)
   */
  default void writeValueAsBigDecimal(BigDecimal value) {

    writeValueAsNumber(value);
  }

  /**
   * @param value the value to write.
   * @see #writeValue(Object)
   */
  default void writeValueAsBigInteger(BigInteger value) {

    writeValueAsNumber(value);
  }

  /**
   * @param value the value to write.
   * @see #writeValue(Object)
   */
  default void writeValueAsLong(Long value) {

    writeValueAsNumber(value);
  }

  /**
   * @param value the value to write.
   * @see #writeValue(Object)
   */
  default void writeValueAsInteger(Integer value) {

    writeValueAsNumber(value);
  }

  /**
   * @param value the value to write.
   * @see #writeValue(Object)
   */
  default void writeValueAsShort(Short value) {

    writeValueAsNumber(value);
  }

  /**
   * @param value the value to write.
   * @see #writeValue(Object)
   */
  default void writeValueAsByte(Byte value) {

    writeValueAsNumber(value);
  }

  /**
   * @param value the value to write.
   * @see #writeValue(Object)
   */
  default void writeValueAsDouble(Double value) {

    writeValueAsNumber(value);
  }

  /**
   * @param value the value to write.
   * @see #writeValue(Object)
   */
  default void writeValueAsFloat(Float value) {

    writeValueAsNumber(value);
  }

  @Override
  void close();

  /**
   * @return the owning {@link StructuredFormat} that {@link StructuredFormat#writer(java.io.OutputStream) created} this
   *         writer.
   */
  StructuredFormat getFormat();

}
