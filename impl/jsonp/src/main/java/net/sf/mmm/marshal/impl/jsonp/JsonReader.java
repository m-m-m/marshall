package net.sf.mmm.marshal.impl.jsonp;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.MonthDay;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.Year;
import java.time.ZonedDateTime;
import java.util.Objects;

import javax.json.stream.JsonParser;
import javax.json.stream.JsonParser.Event;

import net.sf.mmm.marshal.api.StructuredReader;

/**
 * Implementation of {@link StructuredReader} for JSON using {@link JsonParser}.
 *
 * @see JsonFormat
 *
 * @since 1.0.0
 */
public class JsonReader implements StructuredReader {

  private final JsonParser json;

  private Event event;

  private String name;

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
  public <V> V readValue(Class<V> type) {

    Objects.requireNonNull(type, "type");
    Object value = readValueRaw(type);
    try {
      next();
      return type.cast(value);
    } catch (ClassCastException e) {
      throw new IllegalArgumentException(
          "Expected value to be of type " + type.getName() + " but was " + value.getClass().getName());
    }
  }

  private Object readValueRaw(Class<?> type) {

    String string = null;
    try {
      if (this.event == Event.VALUE_NULL) {
        return null;
      } else if (this.event == Event.VALUE_TRUE) {
        return Boolean.TRUE;
      } else if (this.event == Event.VALUE_FALSE) {
        return Boolean.FALSE;
      } else if (this.event == Event.VALUE_NUMBER) {
        string = this.json.getString();
        return readNumber(string, type);
      } else if (this.event == Event.VALUE_STRING) {
        string = this.json.getString();
        return readFromString(string, type);
      } else {
        throw new IllegalStateException("Unsupported value type " + type);
      }
    } catch (IllegalStateException e) {
      throw e;
    } catch (Exception e) {
      throw new IllegalStateException(
          "Failed to parse value '" + string + "' from property " + this.name + " as " + type + "!", e);
    }
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  private Object readFromString(String string, Class<?> type) {

    if (type == String.class) {
      return string;
    } else if (type == Instant.class) {
      return Instant.parse(string);
    } else if (type == LocalDate.class) {
      return LocalDate.parse(string);
    } else if (type == LocalTime.class) {
      return LocalTime.parse(string);
    } else if (type == LocalDateTime.class) {
      return LocalDateTime.parse(string);
    } else if (type == OffsetDateTime.class) {
      return OffsetDateTime.parse(string);
    } else if (type == OffsetTime.class) {
      return OffsetTime.parse(string);
    } else if (type == ZonedDateTime.class) {
      return ZonedDateTime.parse(string);
    } else if (type == Year.class) {
      return Year.parse(string);
    } else if (type == MonthDay.class) {
      return MonthDay.parse(string);
    } else if (Enum.class.isAssignableFrom(type)) {
      return Enum.valueOf((Class) type, string);
    } else {
      throw new IllegalStateException("Unsupported value type " + type);
    }
  }

  private Object readNumber(String string, Class<?> type) {

    if (type == Integer.class) {
      return Integer.valueOf(string);
    } else if (type == Long.class) {
      return Long.valueOf(string);
    } else if (type == Double.class) {
      return Double.valueOf(string);
    } else if (type == Float.class) {
      return Float.valueOf(string);
    } else if (type == BigDecimal.class) {
      return new BigDecimal(string);
    } else if (type == BigInteger.class) {
      return new BigInteger(string);
    } else if (type == Short.class) {
      return Short.valueOf(string);
    } else if (type == Byte.class) {
      return Byte.valueOf(string);
    } else if (type == Month.class) {
      return Month.of(Integer.parseInt(string));
    } else {
      throw new IllegalStateException("Unsupported numeric type " + type);
    }
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
