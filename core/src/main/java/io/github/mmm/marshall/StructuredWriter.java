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

import io.github.mmm.marshall.StructuredReader.State;

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
  default void writeStartArray() {

    writeStartArray(-1);
  }

  /**
   * Writes the start of an array or collection. After the call of this method for each element of the array or
   * collection a call of {@link #writeValue(Object)} (including any of its typed variants) needs to be called followed
   * by {@link #writeEnd()}.
   *
   * @param size the size of the array as payload in bytes or {@code -1} if unknown. Should be provided for efficient
   *        writing of gRPC/ProtoBuf.
   */
  void writeStartArray(int size);

  /**
   * Writes the start of an object. After the call of this method for each property of the object
   * {@link #writeName(String)} needs to be called followed by writing its value. Then {@link #writeEnd()} needs to be
   * called to terminate the object.
   */
  default void writeStartObject() {

    writeStartObject(-1);
  }

  /**
   * Writes the start of an object. After the call of this method for each property of the object
   * {@link #writeName(String)} needs to be called followed by writing its value. Then {@link #writeEnd()} needs to be
   * called to terminate the object.
   *
   * @param size the size of the object as payload in bytes or {@code -1} if unknown. Should be provided for efficient
   *        writing of gRPC/ProtoBuf.
   */
  void writeStartObject(int size);

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

  /**
   * Writes a comment at the current position. Implementations that do not support comments (e.g. JSON format does not
   * allow comments at all) will simply do nothing when this method is called. Otherwise implementations have to take
   * care that the comment is written at a very next position and in a way that results valid output. Typically when you
   * have just written a {@link #writeValue(Object) value}, start, or end the call of this method will result in a
   * comment being written after that and before the next {@link #writeName(String, int) property} or start/end.
   *
   * @param comment the comment to write.
   * @see StructuredFormat#isSupportingComments()
   */
  default void writeComment(String comment) {

    // nothing by default
  }

  /**
   * Copies the data from the given {@link StructuredReader} to this {@link StructuredWriter}. This also allows to
   * convert from one {@link StructuredFormat} to another (e.g. YAML to JSON).
   *
   * @param reader the {@link StructuredReader} to read the data from and write it to this {@link StructuredWriter}. The
   *        entire {@link StructuredReader} will be consumed until the {@link State#DONE} is reached. Then this
   *        {@link StructuredWriter} will be {@link #close() closed}.
   */
  default void write(StructuredReader reader) {

    StructuredFormat readerFormat = reader.getFormat();
    boolean readerIdBased = readerFormat.isIdBased();
    StructuredFormat writerFormat = getFormat();
    boolean writerIdBased = writerFormat.isIdBased();
    int nextId = 0;
    boolean todo = true;
    while (todo) {
      String comment = reader.readComment();
      if (comment != null) {
        writeComment(comment);
      }
      State state = reader.getState();
      if (state == null) {
        // protobuf is limited so we need to guess...
        state = State.START_OBJECT;
      }
      if (state == State.START_OBJECT) {
        reader.readStartObject();
        writeStartObject();
        nextId = 0;
      } else if (state == State.END_OBJECT) {
        reader.readEndObject();
        writeEnd();
      } else if (state == State.START_ARRAY) {
        reader.readStartArray();
        writeStartArray();
      } else if (state == State.END_ARRAY) {
        reader.readEndArray();
        writeEnd();
      } else if (state == State.NAME) {
        int id = -1;
        String name = null;
        if (readerIdBased) {
          id = reader.readId();
          if (!writerIdBased) {
            name = Integer.toString(id);
          }
        } else {
          name = reader.readName();
          if (writerIdBased) {
            nextId++;
            id = nextId;
          }
        }
        writeName(name, id);
      } else if (state == State.VALUE) {
        Object value = reader.readValue();
        writeValue(value);
      } else if (state == State.DONE) {
        close();
        todo = false;
      }
    }
  }

  @Override
  void close();

  /**
   * @return the owning {@link StructuredFormat} that {@link StructuredFormat#writer(java.io.OutputStream) created} this
   *         writer.
   */
  StructuredFormat getFormat();

}
