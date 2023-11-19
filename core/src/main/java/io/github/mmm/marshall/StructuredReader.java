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
import io.github.mmm.marshall.id.StructuredIdMapping;
import io.github.mmm.marshall.id.StructuredIdMappingObject;
import io.github.mmm.marshall.id.impl.StructuredIdMappingIdentity;

/**
 * Interface for a reader to parse a {@link StructuredFormat structured format} such as JSON or XML.
 *
 * @since 1.0.0
 */
public interface StructuredReader extends StructuredProcessor, StructuredIdMappingObject {

  /**
   * @return the name of the current property or element. Consumes to the {@link #next() next} {@link #getState()
   *         state}.
   * @see #getName()
   * @see #isName(String)
   * @see #isNameMatching(String, String)
   */
  default String readName() {

    require(StructuredState.NAME, true);
    return getName();
  }

  /**
   * Retrieves the name of the current property (see {@link StructuredState#NAME}).<br>
   * <b>ATTENTION:</b><br>
   * For full portability and flexibility you need to match names via {@link #isNameMatching(String, String)}. For
   * convenience it is strongly recommended to prefer {@link #isName(String, boolean)} instead of {@link #getName()}.
   *
   * @see #getName(boolean)
   * @see #isName(String)
   * @see #isNameMatching(String, String)
   */
  @Override
  String getName();

  /**
   * Retrieves the name of the current property (see {@link StructuredState#NAME}).<br>
   * <b>ATTENTION:</b><br>
   * For portability and flexibility it required to match names via {@link #isNameMatching(String, String)}. For
   * convenience is strongly recommended to prefer {@link #isName(String, boolean)} instead of {@link #getName()}.
   *
   * @param next - {@code true} to advance to the {@link #next() next} {@link #getState() state}, {@code false}
   *        otherwise (keep current state and only peek the name).
   * @return the name of the current property or element.
   * @see #isName(String)
   * @see #isNameMatching(String, String)
   */
  default String getName(boolean next) {

    if (next) {
      require(StructuredState.NAME, next);
    }
    return getName();
  }

  /**
   * This method checks if the given {@code name} {@link #isNameMatching(String, String) matches} the {@link #getName()
   * current property name}. In case of a match, it consumes to the {@link #next() next} {@link #getState() state}.
   *
   * @param name the {@link #getName() name} of the expected property.
   * @return {@code true} if the expected property matches according to {@link #getName() name}, {@code false}
   *         otherwise.
   */
  default boolean isName(String name) {

    return isName(name, true);
  }

  /**
   * This method checks if the given {@code name} {@link #isNameMatching(String, String) matches} the {@link #getName()
   * current property name}.
   *
   * @param name the {@link #getName() name} of the expected property.
   * @param next - {@code true} to consumes to the {@link #next() next} {@link #getState() state} on match,
   *        {@code false} otherwise (do not change state).
   * @return {@code true} if the expected property matches according to {@link #getName() name}, {@code false}
   *         otherwise.
   */
  default boolean isName(String name, boolean next) {

    boolean match = isNameMatching(getName(), name);
    if (match && next) {
      next();
    }
    return match;
  }

  /**
   * Checks if the given names match each other. Using this method instead of manually checking using
   * {@link Objects#equals(Object) equals check} is required for portability and flexibility. Some formats (e.g.
   * gRPC/protobuf) write names technically as {@link StructuredFormat#isIdBased() IDs}. Therefore you may received a
   * name back in a normalized format that is different from the exact string you have written (e.g. you may receive
   * lower case names back). In such case this method can be overridden by such formats to consider these
   * normalizations.<br>
   * This also implies that you should never use property names in the same object that only differ by syntax and not by
   * semantic (e.g. "customerNumber", "CustomerNumber", "customer-number", and "customernumber" should all mean the same
   * thing and not different things). We do not consider this as a limitation but rather as a feature avoiding
   * anti-patterns and ill-designed data formats or APIs.
   *
   * @param name the {@link #getName() actual name}.
   * @param expectedName the expected name to match.
   * @return {@code true} if both names match, {@code false} otherwise.
   */
  default boolean isNameMatching(String name, String expectedName) {

    return Objects.equals(name, expectedName);
  }

  /**
   * @param object the {@link Object} instance expected to be read (e.g. the empty bean to populate or a
   *        template/prototype instance). Should be an instance of {@link StructuredIdMappingObject}.
   * @return {@code true} if pointing to the start of an object, {@code false} otherwise.
   * @see StructuredWriter#writeStartObject(StructuredIdMappingObject)
   */
  boolean readStartObject(StructuredIdMappingObject object);

  /**
   * May be called after {@link #readStartObject(StructuredIdMappingObject)} has returned {@code true} and the
   * {@link #TYPE polymorphic type} information has been resolved.
   *
   * @param object the {@link Object} instance to read (e.g. the empty bean to populate or a template/prototype
   *        instance). Should be an instance of {@link StructuredIdMappingObject}.
   */
  default void specializeObject(StructuredIdMappingObject object) {

    // nothing to do by default...
  }

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
   * arrays} and {@link java.util.Map} for {@link #readStartObject(StructuredIdMappingObject) objects} are
   * supported.<br>
   *
   * @param recursive - {@code true} for recursive reading of {@link #readStartArray() arrays} as {@link java.util.List}
   *        and {@link #readStartObject(StructuredIdMappingObject) objects} as {@link java.util.Map}, {@code false}
   *        otherwise.
   * @return the unmarsahlled value. May be {@code null}.
   */
  Object readValue(boolean recursive);

  /**
   * This method may be called if the {@link #getState() current state} is {@link StructuredState#START_OBJECT} to read
   * the object into the given {@link Map}. After the call of this method the {@link #getState() state} will point to
   * the {@link #next() next} one after the corresponding {@link StructuredState#END_OBJECT}.
   *
   * @param map the {@link Map} where to add the properties. Unlike {@link #readValue(boolean)} this allows you to
   *        choose the {@link Map} implementation.
   */
  void readObject(Map<String, Object> map);

  /**
   * This method may be called called if the {@link #getState() current state} is {@link StructuredState#START_ARRAY} to
   * read the array into the given {@link Collection}. After the call of this method the {@link #getState() state} will
   * point to the {@link #next() next} one after the corresponding {@link StructuredState#END_ARRAY}.
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
  @SuppressWarnings({ "unchecked", "rawtypes" })
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
    } else if (type.isEnum()) {
      value = readValueAsEnum((Class<Enum>) type);
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
   * @param <E> type of the {@link Enum}.
   * @param enumType the {@link Class} reflecting the {@link Enum}.
   * @return {@link #readValue(Class)}
   */
  <E extends Enum<E>> E readValueAsEnum(Class<E> enumType);

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
   * <li>If currently at {@link #readStartObject(StructuredIdMappingObject) start of object}, skip that entire
   * object.</li>
   * <li>If currently at {@link #readStartArray() start of array}, skip the entire array.</li>
   * <li>If currently at a {@link #readValue(Class) single value}, skip that value.</li>
   * </ul>
   */
  void skipValue();

  /**
   * @return {@code true} if the end of an {@link #readStartArray() array} or
   *         {@link #readStartObject(StructuredIdMappingObject) object} has been reached.
   */
  boolean readEnd();

  /**
   * @return {@code true} if the end of an {@link #readStartObject(StructuredIdMappingObject) object} has been reached.
   */
  default boolean readEndObject() {

    if (getState() == StructuredState.END_OBJECT) {
      next();
      return true;
    }
    return false;
  }

  /**
   * @return {@code true} if the end of an {@link #readStartObject(StructuredIdMappingObject) object} has been reached.
   */
  default boolean readEndArray() {

    if (getState() == StructuredState.END_ARRAY) {
      next();
      return true;
    }
    return false;
  }

  /**
   * @return {@code true} if all data has been read and the end of the stream has been reached, {@code false} otherwise.
   */
  boolean isDone();

  /**
   * @return {@code true} if the {@link #getState() current state} is {@link StructuredState#VALUE} and the value is
   *         encoded as {@link String}, {@code false} otherwise. This can be helpful whilst reading numbers to
   *         distinguish between values such as {@code "1"} and {@code 1}.
   */
  boolean isStringValue();

  /**
   * Proceeds to the next {@link StructuredState} skipping the current information.
   *
   * @return the new {@link StructuredState}.
   */
  StructuredState next();

  /**
   * @param expected the required {@link StructuredState} expect for the current {@link #getState() state}.
   * @throws ObjectMismatchException if the {@link #getState() current state} is not the same as the given
   *         {@code expected} {@link StructuredState}.
   */
  default void require(StructuredState expected) {

    require(expected, false);
  }

  /**
   * @param expected the required {@link StructuredState} expect for the current {@link #getState() state}.
   * @param next - {@code true} to advance to the {@link #next() next} {@link #getState() state}, {@code false}
   *        otherwise (keep current state and only peek the name).
   * @return the {@link #next() next} {@link StructuredState} after successfully matching the {@link #getState() current
   *         state} if {@code next} was {@code true}. Otherwise the unmodified {@link #getState() current state}.
   * @throws ObjectMismatchException if the {@link #getState() current state} is not the same as the given
   *         {@code expected} {@link StructuredState}.
   */
  default StructuredState require(StructuredState expected, boolean next) {

    StructuredState currentState = getState();
    if ((currentState != expected) && (currentState != StructuredState.NULL)) {
      throw new ObjectMismatchException(currentState, expected);
    }
    if (next) {
      return next();
    }
    return currentState;
  }

  /**
   * Reads the comment at the current position. Comments are intentionally not part of the {@link #getState() state} and
   * can be read at any time. Implementations that support comments shall allow to access a comment from
   * {@link StructuredState#NAME} still in the {@link #next() following} {@link StructuredState}. After a comment is
   * read using this method it will be set to {@code null} until the next comment has been parsed.
   *
   * @return the comment at the current position or {@code null} if no comment is present. Will always be {@code null}
   *         for formats that do not support comments such as JSON or protoBuf.
   * @see StructuredFormat#isSupportingComments()
   */
  default String readComment() {

    return null;
  }

  @Override
  default StructuredIdMapping defineIdMapping() {

    return StructuredIdMappingIdentity.get();
  }

  @Override
  default Object asTypeKey() {

    return StructuredReader.class;
  }

}
