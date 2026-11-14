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

/// Interface for a reader to parse a [structured format][StructuredFormat] such as JSON or XML.
///
/// @since 1.0.0
public interface StructuredReader extends StructuredProcessor, StructuredIdMappingObject {

  /// @return the name of the current property or element. Consumes to the [next][#next()] [state][#getState()].
  /// @see #getName()
  /// @see #isName(String)
  /// @see #isNameMatching(String, String)
  default String readName() {

    require(StructuredState.NAME, true);
    return getName();
  }

  /// Retrieves the name of the current property (see [StructuredState#NAME]). **ATTENTION:** For full portability and
  /// flexibility you need to match names via [#isNameMatching(String, String)]. For convenience it is strongly
  /// recommended to prefer [#isName(String, boolean)] instead of [#getName()].
  ///
  /// @see #getName(boolean)
  /// @see #isName(String)
  /// @see #isNameMatching(String, String)
  @Override
  String getName();

  /// Retrieves the name of the current property (see [StructuredState#NAME]). **ATTENTION:** For portability and
  /// flexibility it required to match names via [#isNameMatching(String, String)]. For convenience is strongly
  /// recommended to prefer [#isName(String, boolean)] instead of [#getName()].
  ///
  /// @param next - `true` to advance to the [next][#next()] [state][#getState()], `false` otherwise (keep current state
  ///        and only peek the name).
  /// @return the name of the current property or element.
  /// @see #isName(String)
  /// @see #isNameMatching(String, String)
  default String getName(boolean next) {

    if (next) {
      require(StructuredState.NAME, next);
    }
    return getName();
  }

  /// This method checks if the given `name` [matches][#isNameMatching(String, String)] the [current property
  /// name][#getName()]. In case of a match, it consumes to the [next][#next()] [state][#getState()].
  ///
  /// @param name the [name][#getName()] of the expected property.
  /// @return `true` if the expected property matches according to [name][#getName()], `false` otherwise.
  default boolean isName(String name) {

    return isName(name, true);
  }

  /// This method checks if the given `name` [matches][#isNameMatching(String, String)] the [current property
  /// name][#getName()].
  ///
  /// @param name the [name][#getName()] of the expected property.
  /// @param next - `true` to consumes to the [next][#next()] [state][#getState()] on match, `false` otherwise (do not
  ///        change state).
  /// @return `true` if the expected property matches according to [name][#getName()], `false` otherwise.
  default boolean isName(String name, boolean next) {

    boolean match = isNameMatching(getName(), name);
    if (match && next) {
      next();
    }
    return match;
  }

  /// Checks if the given names match each other. Using this method instead of manually checking using
  /// [equals check][Objects#equals(Object)] is required for portability and flexibility. Some formats (e.g.
  /// gRPC/protobuf) write names technically as [IDs][StructuredFormat#isIdBased()]. Therefore you may received a name
  /// back in a normalized format that is different from the exact string you have written (e.g. you may receive lower
  /// case names back). In such case this method can be overridden by such formats to consider these normalizations. This
  /// also implies that you should never use property names in the same object that only differ by syntax and not by
  /// semantic (e.g. "customerNumber", "CustomerNumber", "customer-number", and "customernumber" should all mean the same
  /// thing and not different things). We do not consider this as a limitation but rather as a feature avoiding
  /// anti-patterns and ill-designed data formats or APIs.
  ///
  /// @param name the [actual name][#getName()].
  /// @param expectedName the expected name to match.
  /// @return `true` if both names match, `false` otherwise.
  default boolean isNameMatching(String name, String expectedName) {

    return Objects.equals(name, expectedName);
  }

  /// @param object the [Object] instance expected to be read (e.g. the empty bean to populate or a template/prototype
  ///        instance). Should be an instance of [StructuredIdMappingObject].
  /// @return `true` if pointing to the start of an object, `false` otherwise.
  /// @see StructuredWriter#writeStartObject(StructuredIdMappingObject)
  boolean readStartObject(StructuredIdMappingObject object);

  /// May be called after [#readStartObject(StructuredIdMappingObject)] has returned `true` and the
  /// [polymorphic type][#TYPE] information has been resolved.
  ///
  /// @param object the [Object] instance to read (e.g. the empty bean to populate or a template/prototype instance).
  ///        Should be an instance of [StructuredIdMappingObject].
  default void specializeObject(StructuredIdMappingObject object) {

    // nothing to do by default...
  }

  /// @return `true` if pointing to the start of an object, `false` otherwise.
  /// @see StructuredWriter#writeStartArray()
  boolean readStartArray();

  /// Generic method to read and unmarshall a value supporting only the build in types [Boolean], [String], and
  /// [Number].
  ///
  /// @return the unmarsahlled value. May be `null`.
  Object readValue();

  /// Generic method to read and unmarshall a value supporting the build in types [Boolean], [String], and [Number]. In
  /// case `recursive` is `true` also [java.util.List] for [arrays][#readStartArray()] and [java.util.Map] for
  /// [objects][#readStartObject(StructuredIdMappingObject)] are supported.
  ///
  /// @param recursive - `true` for recursive reading of [arrays][#readStartArray()] as [java.util.List] and
  ///        [objects][#readStartObject(StructuredIdMappingObject)] as [java.util.Map], `false` otherwise.
  /// @return the unmarsahlled value. May be `null`.
  Object readValue(boolean recursive);

  /// This method may be called if the [current state][#getState()] is [StructuredState#START_OBJECT] to read the object
  /// into the given [Map]. After the call of this method the [state][#getState()] will point to the [next][#next()] one
  /// after the corresponding [StructuredState#END_OBJECT].
  ///
  /// @param map the [Map] where to add the properties. Unlike [#readValue(boolean)] this allows you to choose the
  ///        [Map] implementation.
  void readObject(Map<String, Object> map);

  /// This method may be called called if the [current state][#getState()] is [StructuredState#START_ARRAY] to read the
  /// array into the given [Collection]. After the call of this method the [state][#getState()] will point to the
  /// [next][#next()] one after the corresponding [StructuredState#END_ARRAY].
  ///
  /// @param array the [Collection] where to add the [values][#readValue(boolean)].
  void readArray(Collection<Object> array);

  /// Generic method to read and unmarshall a value. **ATTENTION:** This generic method only exists as convenience
  /// method for the `readValueAs*` methods. It therefore only supports standard Java value types as described in
  /// [io.github.mmm.marshall]. For other types it will throw an [IllegalArgumentException].
  ///
  /// @param <V> type of the value to read.
  /// @param type [Class] reflecting the value to read.
  /// @return the unmarsahlled value. May be `null`.
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

  /// @return reads the value as [String].
  /// @see #readValue(Class)
  String readValueAsString();

  /// @return reads the value as [Boolean].
  /// @see #readValue(Class)
  Boolean readValueAsBoolean();

  /// @param <E> type of the [Enum].
  /// @param enumType the [Class] reflecting the [Enum].
  /// @return the value as [Enum]
  /// @see #readValue(Class)
  <E extends Enum<E>> E readValueAsEnum(Class<E> enumType);

  /// @return reads the value as [Integer].
  /// @see #readValue(Class)
  Integer readValueAsInteger();

  /// @return reads the value as [Long].
  /// @see #readValue(Class)
  Long readValueAsLong();

  /// @return reads the value as [Double].
  /// @see #readValue(Class)
  Double readValueAsDouble();

  /// @return reads the value as [Float].
  /// @see #readValue(Class)
  Float readValueAsFloat();

  /// @return reads the value as [Short].
  /// @see #readValue(Class)
  Short readValueAsShort();

  /// @return reads the value as [Byte].
  /// @see #readValue(Class)
  Byte readValueAsByte();

  /// @return reads the value as [BigInteger].
  /// @see #readValue(Class)
  BigInteger readValueAsBigInteger();

  /// @return reads the value as [BigDecimal].
  /// @see #readValue(Class)
  BigDecimal readValueAsBigDecimal();

  /// @return reads the value as [Instant].
  /// @see #readValue(Class)
  Instant readValueAsInstant();

  /// @return reads the value as [LocalDate].
  /// @see #readValue(Class)
  LocalDate readValueAsLocalDate();

  /// @return reads the value as [LocalDateTime].
  /// @see #readValue(Class)
  LocalDateTime readValueAsLocalDateTime();

  /// @return reads the value as [LocalTime].
  /// @see #readValue(Class)
  LocalTime readValueAsLocalTime();

  /// @return reads the value as [ZonedDateTime].
  /// @see #readValue(Class)
  ZonedDateTime readValueAsZonedDateTime();

  /// @return reads the value as [OffsetDateTime].
  /// @see #readValue(Class)
  OffsetDateTime readValueAsOffsetDateTime();

  /// @return reads the value as [OffsetTime].
  /// @see #readValue(Class)
  OffsetTime readValueAsOffsetTime();

  /// Skips the current value. Should be called after [#readName()].
  ///
  /// - If currently at [start of object][#readStartObject(StructuredIdMappingObject)], skip that entire object.
  /// - If currently at [start of array][#readStartArray()], skip the entire array.
  /// - If currently at a [single value][#readValue(Class)], skip that value.
  void skipValue();

  /// @return `true` if the end of an [array][#readStartArray()] or
  ///         [object][#readStartObject(StructuredIdMappingObject)] has been reached.
  boolean readEnd();

  /// @return `true` if the end of an [object][#readStartObject(StructuredIdMappingObject)] has been reached.
  default boolean readEndObject() {

    if (getState() == StructuredState.END_OBJECT) {
      next();
      return true;
    }
    return false;
  }

  /// @return `true` if the end of an [object][#readStartObject(StructuredIdMappingObject)] has been reached.
  default boolean readEndArray() {

    if (getState() == StructuredState.END_ARRAY) {
      next();
      return true;
    }
    return false;
  }

  /// @return `true` if all data has been read and the end of the stream has been reached, `false` otherwise.
  boolean isDone();

  /// @return `true` if the [current state][#getState()] is [StructuredState#VALUE] and the value is encoded as
  ///         [String], `false` otherwise. This can be helpful whilst reading numbers to distinguish between values such
  ///         as `"1"` and `1`.
  boolean isStringValue();

  /// Proceeds to the next [StructuredState] skipping the current information.
  ///
  /// @return the new [StructuredState].
  StructuredState next();

  /// @param expected the required [StructuredState] expect for the current [state][#getState()].
  /// @throws ObjectMismatchException if the [current state][#getState()] is not the same as the given
  ///         `expected` [StructuredState].
  default void require(StructuredState expected) {

    require(expected, false);
  }

  /// @param expected the required [StructuredState] expect for the current [state][#getState()].
  /// @param next - `true` to advance to the [next][#next()] [state][#getState()], `false` otherwise (keep current state
  ///        and only peek the name).
  /// @return the [next][#next()] [StructuredState] after successfully matching the [current state][#getState()] if
  ///         `next` was `true`. Otherwise the unmodified [current state][#getState()].
  /// @throws ObjectMismatchException if the [current state][#getState()] is not the same as the given
  ///         `expected` [StructuredState].
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

  /// Reads the comment at the current position. Comments are intentionally not part of the [state][#getState()] and can
  /// be read at any time. Implementations that support comments shall allow to access a comment from
  /// [StructuredState#NAME] still in the [following][#next()] [StructuredState]. After a comment is read using this
  /// method it will be set to `null` until the next comment has been parsed.
  ///
  /// @return the comment at the current position or `null` if no comment is present. Will always be `null` for formats
  ///         that do not support comments such as JSON or protoBuf.
  /// @see StructuredFormat#isSupportingComments()
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
