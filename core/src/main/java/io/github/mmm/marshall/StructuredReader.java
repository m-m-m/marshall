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
import java.util.Collection;
import java.util.Map;
import java.util.Objects;

import io.github.mmm.base.exception.ObjectMismatchException;

/**
 * Interface for a reader to parse a {@link StructuredFormat structured format} such as JSON or XML.
 *
 * @since 1.0.0
 */
public interface StructuredReader extends AutoCloseable {

  /**
   * @return the name of the current property or element. Consumes to the {@link #next() next} {@link #getState()
   *         state}.
   * @see #getName()
   * @see #isName(String, int)
   */
  default String readName() {

    require(State.NAME, true);
    return getName();
  }

  /**
   * @return the name of the current property or element. Does not change the state of this reader and can be called
   *         multiple times.
   * @see #getName(boolean)
   * @see #isName(String, int)
   */
  String getName();

  /**
   * @param next - {@code true} to advance to the {@link #next() next} {@link #getState() state}, {@code false}
   *        otherwise (keep current state and only peek the name).
   * @return the name of the current property or element.
   * @see #isName(String, int)
   */
  default String getName(boolean next) {

    if (next) {
      require(State.NAME, next);
    }
    return getName();
  }

  /**
   * @return the ID of the current property. Consumes to the {@link #next() next} {@link #getState() state}.
   * @see #getId()
   * @see #isName(String, int)
   */
  default int readId() {

    require(State.NAME, true);
    return getId();
  }

  /**
   * @return the ID of the current property. Does not change the state of this reader and can be called multiple times.
   * @see #getId(boolean)
   * @see #isName(String, int)
   */
  default int getId() {

    return -1;
  }

  /**
   * @param next - {@code true} to advance to the {@link #next() next} {@link #getState() state}, {@code false}
   *        otherwise (keep current state and only peek the name).
   * @return the ID of the current property.
   * @see #isName(String, int)
   */
  default int getId(boolean next) {

    if (next) {
      require(State.NAME, true);
    }
    return getId();
  }

  /**
   * This method checks if the current property in {@link State#NAME} matches the given {@link #getName() name} or
   * {@link #getId() ID}. In case of a match, it consumes to the {@link #next() next} {@link #getState() state}.<br>
   * Use this method instead of directly accessing {@link #getName() name} or {@link #getId() ID} to be fully portable
   * to any format in case you want to support both textual formats like JSON, YAML, or XML that identify properties via
   * {@link #getName() name} as well as gRPC/ProtoBuf that uses a numeric {@link #getId() ID} instead.
   *
   * @param name the {@link #getName() name} of the expected property.
   * @param id the {@link #getId() ID} of the expected property.
   * @return {@code true} if the expected property matches accroding to {@link #getName() name} or {@link #getId() ID},
   *         {@code false} otherwise.
   */
  default boolean isName(String name, int id) {

    return isName(name, id, true);
  }

  /**
   * This method checks if the current property in {@link State#NAME} matches the given {@link #getName() name} or
   * {@link #getId() ID}.<br>
   * Use this method instead of directly accessing {@link #getName() name} or {@link #getId() ID} to be fully portable
   * to any format in case you want to support both textual formats like JSON, YAML, or XML that identify properties via
   * {@link #getName() name} as well as gRPC/ProtoBuf that uses a numeric {@link #getId() ID} instead.
   *
   * @param name the {@link #getName() name} of the expected property.
   * @param id the {@link #getId() ID} of the expected property.
   * @param next - {@code true} to consumes to the {@link #next() next} {@link #getState() state} on match,
   *        {@code false} otherwise (do not change state).
   * @return {@code true} if the expected property matches accroding to {@link #getName() name} or {@link #getId() ID},
   *         {@code false} otherwise.
   */
  default boolean isName(String name, int id, boolean next) {

    boolean match = false;
    if (getFormat().isIdBased()) {
      match = (id == getId());
    } else {
      match = Objects.equals(name, getName());
    }
    if (match && next) {
      next();
    }
    return match;
  }

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
   * Generic method to read and unmarshall a value supporting only the build in types {@link Boolean}, {@link String},
   * and {@link Number}.<br>
   *
   * @return the unmarsahlled value. May be {@code null}.
   */
  Object readValue();

  /**
   * Generic method to read and unmarshall a value supporting the build in types {@link Boolean}, {@link String}, and
   * {@link Number}. In case {@code recursive} is {@code true} also {@link java.util.List} for {@link #readStartArray()
   * arrays} and {@link java.util.Map} for {@link #readStartObject() objects} are supported.<br>
   *
   * @param recursive - {@code true} for recursive reading of {@link #readStartArray() arrays} as {@link java.util.List}
   *        and {@link #readStartObject() objects} as {@link java.util.Map}, {@code false} otherwise.
   * @return the unmarsahlled value. May be {@code null}.
   */
  Object readValue(boolean recursive);

  /**
   * This method may be called if the {@link #getState() current state} is {@link State#START_OBJECT} to read the object
   * into the given {@link Map}. After the call of this method the {@link #getState() state} will point to the
   * {@link #next() next} one after the corresponding {@link State#END_OBJECT}.
   *
   * @param map the {@link Map} where to add the properties. Unlike {@link #readValue(boolean)} this allows you to
   *        choose the {@link Map} implementation.
   */
  void readObject(Map<String, Object> map);

  /**
   * This method may be called called if the {@link #getState() current state} is {@link State#START_ARRAY} to read the
   * array into the given {@link Collection}. After the call of this method the {@link #getState() state} will point to
   * the {@link #next() next} one after the corresponding {@link State#END_ARRAY}.
   *
   * @param array the {@link Collection} where to add the {@link #readValue(boolean) values}.
   */
  void readArray(Collection<Object> array);

  /**
   * Generic method to read and unmarshall a value.<br>
   * <b>ATTENTION:</b><br>
   * This generic method only exists as convenience method for the {@code readValueAs*} methods. It therefore only
   * supports standard Java value types as described in {@link io.github.mmm.marshall}. For other types it will throw an
   * {@link IllegalArgumentException}.
   *
   * @param <V> type of the value to read.
   * @param type {@link Class} reflecting the value to read.
   * @return the unmarsahlled value. May be {@code null}.
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
      // } else if (Instant.class.equals(type)) {
      // value = readValueAsInstant();
      // } else if (LocalDateTime.class.equals(type)) {
      // value = readValueAsLocalDateTime();
      // } else if (LocalDate.class.equals(type)) {
      // value = readValueAsLocalDate();
      // } else if (LocalTime.class.equals(type)) {
      // value = readValueAsLocalTime();
      // } else if (ZonedDateTime.class.equals(type)) {
      // value = readValueAsZonedDateTime();
      // } else if (OffsetDateTime.class.equals(type)) {
      // value = readValueAsOffsetDateTime();
      // } else if (OffsetTime.class.equals(type)) {
      // value = readValueAsOffsetTime();
    } else if (Object.class.equals(type)) {
      value = readValue();
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
   * Skips the current value. Should be called after {@link #readName()}.
   * <ul>
   * <li>If currently at {@link #readStartObject() start of object}, skip that entire object.</li>
   * <li>If currently at {@link #readStartArray() start of array}, skip the entire array.</li>
   * <li>If currently at a {@link #readValue(Class) single value}, skip that value.</li>
   * </ul>
   */
  void skipValue();

  /**
   * @return {@code true} if the end of an {@link #readStartArray() array} or {@link #readStartObject() object} has been
   *         reached.
   */
  boolean readEnd();

  /**
   * @return {@code true} if all data has been read and the end of the stream has been reached, {@code false} otherwise.
   */
  boolean isDone();

  /**
   * @return {@code true} if the {@link #getState() current state} is {@link State#VALUE} and the value is encoded as
   *         {@link String}, {@code false} otherwise. This can be helpful whilst reading numbers to distinguish between
   *         values such as {@code "1"} and {@code 1}.
   */
  boolean isStringValue();

  /**
   * @return the current state of this reader.
   */
  State getState();

  /**
   * Proceeds to the next {@link State} skipping the current information.
   *
   * @return the new {@link State}.
   */
  State next();

  /**
   * @param expected the required {@link State} expect for the current {@link #getState() state}.
   * @throws ObjectMismatchException if the {@link #getState() current state} is not the same as the given
   *         {@code expected} {@link State}.
   */
  default void require(State expected) {

    require(expected, false);
  }

  /**
   * @param expected the required {@link State} expect for the current {@link #getState() state}.
   * @param next - {@code true} to advance to the {@link #next() next} {@link #getState() state}, {@code false}
   *        otherwise (keep current state and only peek the name).
   * @return the {@link #next() next} {@link State} after successfully matching the {@link #getState() current state}.
   * @throws ObjectMismatchException if the {@link #getState() current state} is not the same as the given
   *         {@code expected} {@link State}.
   */
  default State require(State expected, boolean next) {

    State currentState = getState();
    if ((currentState != expected) && (currentState != null)) {
      throw new ObjectMismatchException(currentState, expected);
    }
    if (next) {
      return next();
    }
    return currentState;
  }

  @Override
  void close();

  /**
   * @return the owning {@link StructuredFormat} that {@link StructuredFormat#reader(java.io.InputStream) created} this
   *         writer.
   */
  StructuredFormat getFormat();

  /**
   * Enum with the possible states of a {@link StructuredReader}.
   *
   * @see StructuredReader#getState()
   */
  public enum State {

    /**
     * Start of an array.
     *
     * @see StructuredReader#readStartArray()
     */
    START_ARRAY,

    /**
     * Start of an object.
     *
     * @see StructuredReader#readStartObject()
     */
    START_OBJECT,

    /**
     * A regular value.
     *
     * @see StructuredReader#readValue()
     */
    VALUE,

    /**
     * Name of a property.
     *
     * @see StructuredReader#readName()
     */
    NAME,

    /**
     * End of an array.
     *
     * @see StructuredReader#readEnd()
     * @see StructuredReader#readStartArray()
     */
    END_ARRAY,

    /**
     * End of an object.
     *
     * @see StructuredReader#readEnd()
     * @see StructuredReader#readStartObject()
     */
    END_OBJECT,

    /**
     * End of data.
     *
     * @see StructuredReader#isDone()
     */
    DONE;

    /**
     * @return {@code true} if a start {@link State} such as {@link #START_ARRAY} or {@link #START_OBJECT},
     *         {@code false} otherwise.
     */
    public boolean isStart() {

      switch (this) {
        case START_ARRAY:
        case START_OBJECT:
          return true;
        default:
          return false;
      }
    }

    /**
     * @return {@code true} if an end {@link State} such as {@link #END_ARRAY} or {@link #END_OBJECT}, {@code false}
     *         otherwise.
     */
    public boolean isEnd() {

      switch (this) {
        case END_ARRAY:
        case END_OBJECT:
        case DONE:
          return true;
        default:
          return false;
      }
    }
  }

}
