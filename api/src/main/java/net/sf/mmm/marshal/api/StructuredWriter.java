package net.sf.mmm.marshal.api;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Month;
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
   * @param name
   */
  void writeName(String name);

  /**
   * Writes the value of the current property or {@link #writeStartArray() array} element.
   *
   * @param value the value to write. May be {@code null}.
   */
  default void writeValue(Object value) {

    if (value == null) {
      writeValueAsNull();
    } else if (value instanceof Boolean) {
      writeValueAsBoolean(((Boolean) value).booleanValue());
    } else if (value instanceof Number) {
      writeValueAsNumber((Number) value);
    } else if (value instanceof Month) {
      writeValueAsInteger(((Month) value).getValue());
    } else if (value instanceof Temporal) {
      writeValueAsTemporal(((Temporal) value));
    } else if (value instanceof Enum) {
      writeValueAsEnum(((Enum<?>) value));
    } else {
      writeValueAsString(value.toString());
    }
  }

  /**
   * Writes the value {@code null} (undefined).
   *
   * @see #writeValue(Object)
   */
  void writeValueAsNull();

  /**
   * @param value the value to write.
   * @see #writeValue(Object)
   */
  default void writeValueAsTemporal(Temporal value) {

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
  void writeValueAsBoolean(boolean value);

  /**
   * @param value the value to write.
   * @see #writeValue(Object)
   */
  default void writeValueAsEnum(Enum<?> value) {

    if (value == null) {
      writeValueAsNull();
    } else {
      writeValueAsString(value.name());
    }
  }

  /**
   * @param value the value to write.
   * @see #writeValue(Object)
   */
  default void writeValueAsNumber(Number value) {

    if (value == null) {
      writeValueAsNull();
    } else if (value instanceof Long) {
      writeValueAsLong(value.longValue());
    } else if (value instanceof BigDecimal) {
      writeValueAsBigDecimal((BigDecimal) value);
    } else if (value instanceof BigInteger) {
      writeValueAsBigInteger((BigInteger) value);
    } else if (value instanceof Integer) {
      writeValueAsInteger(value.intValue());
    } else if (value instanceof Short) {
      writeValueAsShort(value.shortValue());
    } else if (value instanceof Byte) {
      writeValueAsByte(value.byteValue());
    } else if (value instanceof Float) {
      writeValueAsFloat(value.floatValue());
    } else {
      writeValueAsDouble(value.doubleValue());
    }
  }

  /**
   * @param value the value to write.
   * @see #writeValue(Object)
   */
  void writeValueAsBigDecimal(BigDecimal value);

  /**
   * @param value the value to write.
   * @see #writeValue(Object)
   */
  void writeValueAsBigInteger(BigInteger value);

  /**
   * @param value the value to write.
   * @see #writeValue(Object)
   */
  void writeValueAsLong(long value);

  /**
   * @param value the value to write.
   * @see #writeValue(Object)
   */
  void writeValueAsInteger(int value);

  /**
   * @param value the value to write.
   * @see #writeValue(Object)
   */
  default void writeValueAsShort(short value) {

    writeValueAsInteger(value);
  }

  /**
   * @param value the value to write.
   * @see #writeValue(Object)
   */
  default void writeValueAsByte(byte value) {

    writeValueAsInteger(value);
  }

  /**
   * @param value the value to write.
   * @see #writeValue(Object)
   */
  void writeValueAsDouble(double value);

  /**
   * @param value the value to write.
   * @see #writeValue(Object)
   */
  default void writeValueAsFloat(float value) {

    writeValueAsDouble(Double.parseDouble(Float.toString(value)));
  }

}
