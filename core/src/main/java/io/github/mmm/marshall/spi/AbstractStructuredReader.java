/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.marshall.spi;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import io.github.mmm.base.number.NumberType;
import io.github.mmm.marshall.StructuredFormat;
import io.github.mmm.marshall.StructuredReader;
import io.github.mmm.marshall.StructuredState;
import io.github.mmm.marshall.id.StructuredIdMappingObject;
import io.github.mmm.marshall.impl.EnumMapping;
import io.github.mmm.marshall.impl.EnumMappings;

/**
 * Abstract base implementation of {@link StructuredReader}.
 *
 * @param <S> type of the {@link StructuredNode}.
 * @since 1.0.0
 */
public abstract class AbstractStructuredReader<S extends StructuredNode<S>> extends AbstractStructuredProcessor<S>
    implements StructuredReader {

  private String comment;

  private boolean done;

  /**
   * The constructor.
   *
   * @param format the {@link #getFormat() format}.
   */
  public AbstractStructuredReader(StructuredFormat format) {

    super(format);
  }

  @Override
  public String getName() {

    return this.name;
  }

  @Override
  public boolean readStartObject(StructuredIdMappingObject object) {

    if (this.state == StructuredState.START_OBJECT) {
      next();
      return true;
    }
    return false;
  }

  @Override
  public boolean readStartArray() {

    if (this.state == StructuredState.START_ARRAY) {
      next();
      return true;
    }
    return false;
  }

  @Override
  public boolean readEnd() {

    if (this.state == StructuredState.DONE) {
      boolean result = !this.done;
      this.done = true;
      return result;
    }
    if ((this.state == StructuredState.END_ARRAY) || (this.state == StructuredState.END_OBJECT)) {
      next();
      return true;
    }
    return false;
  }

  @Override
  public boolean isDone() {

    return (this.state == StructuredState.DONE);
  }

  /**
   * @param type the {@link StructuredNodeType} for the {@link StructuredNode} to start.
   * @return the new {@link StructuredState} we just entered ({@link StructuredNodeType#getStart()}).
   */
  protected StructuredState start(StructuredNodeType type) {

    StructuredState newState = type.getStart();
    setState(newState);
    this.node = newNode(type, null);
    return newState;
  }

  /**
   * @param type the {@link StructuredNodeType} of the {@link StructuredNode} to end.
   * @return the new {@link StructuredState} we just entered ({@link StructuredNodeType#getEnd()}).
   */
  protected StructuredState end(StructuredNodeType type) {

    if ((type != null) && (this.node.type != type)) {
      throw new IllegalStateException();
    }
    StructuredState newState;
    if (this.node.type != null) {
      newState = this.node.type.getEnd();
      setState(newState);
      this.node = this.node.parent;
    } else {
      assert (this.node.parent == null);
      newState = setState(StructuredState.DONE);
    }
    return newState;
  }

  /**
   * Verifies that the {@link #getState() current state} is one of the given states.
   *
   * @param expected first accepted state.
   * @param expected2 second accepted state.
   */
  protected void require(StructuredState expected, StructuredState expected2) {

    if ((this.state != expected) && (this.state != expected2)) {
      error("Expecting event " + expected + " or " + expected2 + " but found " + this.state + ".");
    }
  }

  /**
   * Verifies that the {@link #getState() current state} is one of the given states.
   *
   * @param expected first accepted state.
   * @param expected2 second accepted state.
   * @param expected3 third accepted state.
   */
  protected void require(StructuredState expected, StructuredState expected2, StructuredState expected3) {

    if ((this.state != expected) && (this.state != expected2) && (this.state != expected3)) {
      error("Expecting event " + expected + ", " + expected2 + ",  or " + expected3 + " but found " + this.state + ".");
    }
  }

  /**
   * Verifies that the {@link #getState() current state} is not the same as the given state.
   *
   * @param unexpected the unexpected state.
   */
  protected void requireNot(StructuredState unexpected) {

    if (this.state == unexpected) {
      error("Unexpected event " + unexpected + ".");
    }
  }

  @Override
  public String readValueAsString() {

    Object v = readValue();
    if (v == null) {
      return null;
    }
    return v.toString();
  }

  @Override
  public Boolean readValueAsBoolean() {

    Object v = readValue();
    if (v == null) {
      return null;
    } else if (v instanceof Boolean) {
      return (Boolean) v;
    } else if (v instanceof String) {
      return parseBoolean((String) v);
    } else {
      throw error(v, Boolean.class);
    }
  }

  @Override
  public <E extends Enum<E>> E readValueAsEnum(Class<E> enumType) {

    EnumMapping<E> mapping = EnumMappings.get().getMapping(enumType);
    if (isStringValue()) {
      String stringValue = readValueAsString();
      E e = mapping.fromString(stringValue);
      if ((e == null) && (stringValue != null)) {
        throw error("The string value '" + stringValue + "' is not an enum of type " + enumType.getName());
      }
      return e;
    } else {
      Integer ordinalValue = readValueAsInteger();
      if (ordinalValue == null) {
        return null;
      }
      E e = mapping.fromOrdinal(ordinalValue);
      if ((e == null) && (ordinalValue != null)) {
        throw error("The integer value '" + ordinalValue + "' is not an ordinal of enum type " + enumType.getName());
      }
      return e;
    }
  }

  @Override
  public Long readValueAsLong() {

    return readValueAsNumber(NumberType.LONG);
  }

  @Override
  public Integer readValueAsInteger() {

    return readValueAsNumber(NumberType.INTEGER);
  }

  @Override
  public Short readValueAsShort() {

    return readValueAsNumber(NumberType.SHORT);
  }

  @Override
  public Byte readValueAsByte() {

    return readValueAsNumber(NumberType.BYTE);
  }

  @Override
  public Double readValueAsDouble() {

    return readValueAsNumber(NumberType.DOUBLE);
  }

  @Override
  public Float readValueAsFloat() {

    return readValueAsNumber(NumberType.FLOAT);
  }

  @Override
  public BigInteger readValueAsBigInteger() {

    return readValueAsNumber(NumberType.BIG_INTEGER);
  }

  @Override
  public BigDecimal readValueAsBigDecimal() {

    return readValueAsNumber(NumberType.BIG_DECIMAL);
  }

  /**
   * @param <N> type of the number to read.
   * @param type the {@link NumberType} to read.
   * @return the read and parsed number value. May be {@code null}.
   */
  protected abstract <N extends Number> N readValueAsNumber(NumberType<N> type);

  /**
   * @param string the number as {@link String}.
   * @return the parsed {@link Number}.
   */
  protected Number parseNumber(String string) {

    boolean decimal = (string.indexOf('.') >= 0);
    if (decimal) {
      BigDecimal bd = new BigDecimal(string);
      if (string.endsWith("0")) {
        return bd; // preserve leading zeros
      }
      return NumberType.simplify(bd, NumberType.FLOAT);
    } else {
      BigInteger integer = new BigInteger(string);
      int bitLength = integer.bitLength();
      if (bitLength > 63) {
        return integer;
      } else if (bitLength < 31) {
        return Integer.valueOf(integer.intValue());
      } else {
        return Long.valueOf(integer.longValue());
      }
    }
  }

  /**
   * @param string the boolean value as {@link String}.
   * @return the parsed {@link Boolean}.
   */
  protected Boolean parseBoolean(String string) {

    if (string == null) {
      return null;
    } else if ("true".equalsIgnoreCase(string)) {
      return Boolean.TRUE;
    } else if ("false".equalsIgnoreCase(string)) {
      return Boolean.FALSE;
    } else if (string.isEmpty()) {
      return null;
    } else {
      throw error(string, Boolean.class, null);
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
      throw error(value, Instant.class, e);
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
      throw error(value, LocalDate.class, e);
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
      throw error(value, LocalDateTime.class, e);
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
      throw error(value, LocalTime.class, e);
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
      throw error(value, ZonedDateTime.class, e);
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
      throw error(value, OffsetDateTime.class, e);
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
      throw error(value, OffsetDateTime.class, e);
    }
  }

  @Override
  public Object readValue(boolean recursive) {

    Object value;
    if ((this.state == StructuredState.VALUE) || !recursive) {
      return readValue();
    } else {
      if (this.state == StructuredState.START_ARRAY) {
        Collection<Object> array = new ArrayList<>();
        readArray(array);
        value = array;
      } else if (this.state == StructuredState.START_OBJECT) {
        Map<String, Object> map = new HashMap<>();
        readObject(map);
        value = map;
      } else {
        require(StructuredState.VALUE, StructuredState.START_ARRAY, StructuredState.START_OBJECT);
        throw errorIllegalState();
      }
    }
    return value;
  }

  @Override
  public void readObject(Map<String, Object> map) {

    require(StructuredState.START_OBJECT);
    next();
    while (this.state != StructuredState.END_OBJECT) {
      String key = readName();
      Object value = readValue(true);
      map.put(key, value);
    }
    next();
  }

  @Override
  public void readArray(Collection<Object> array) {

    require(StructuredState.START_ARRAY);
    next();
    while (this.state != StructuredState.END_ARRAY) {
      Object value = readValue(true);
      array.add(value);
    }
    next();
  }

  @Override
  public String readComment() {

    String result = this.comment;
    if (result != null) {
      this.comment = null;
    }
    return result;
  }

  /**
   * @param currentComment the raw {@link #readComment() comment}.
   * @return the given comment unescaped from XML.
   * @see AbstractStructuredWriter#escapeXmlComment(String)
   */
  protected String unescapeXmlComment(String currentComment) {

    return currentComment.replace(AbstractStructuredWriter.XML_COMMENT_DASHES_ESCAPED,
        AbstractStructuredWriter.XML_COMMENT_DASHES);
  }

  /**
   * @param newComment the new comment to append.
   * @see #readComment()
   */
  protected void addComment(String newComment) {

    if (this.comment == null) {
      this.comment = newComment;
    } else {
      this.comment = this.comment + "\n" + newComment;
    }
  }

  @Override
  public final StructuredState next() {

    return next(false);
  }

  /**
   * Proceeds to the next {@link StructuredState} skipping the current information.
   *
   * @param skip {@code false} for regular {@link #next()} usage, and {@code true} to {@link #skipValue() skip} from the
   *        start of an array or object.
   * @return the new {@link StructuredState}.
   * @see #next()
   */
  protected abstract StructuredState next(boolean skip);

  @Override
  public void skipValue() {

    require(StructuredState.VALUE, StructuredState.START_ARRAY, StructuredState.START_OBJECT);
    if (this.state == StructuredState.VALUE) {
      next(false);
      return;
    }
    next(true);
  }

  /**
   * @return nothing, just to ensure exit you may throw result of this method.
   */
  protected RuntimeException errorIllegalState() {

    return error("Illegal state");
  }

  /**
   * @param message the error message.
   * @return nothing, just to ensure exit you may throw result of this method.
   */
  protected RuntimeException error(String message) {

    return error(message, (Throwable) null);
  }

  /**
   * @param message the error message.
   * @param cause the optional {@link Throwable} causing this error.
   * @return nothing, just to ensure exit you may throw result of this method.
   */
  protected RuntimeException error(String message, Throwable cause) {

    throw new IllegalStateException(appendContextDetails(message), cause);
  }

  /**
   * @param message the message to report (e.g. error or warning).
   * @return the given {@code message} with contextual details appended (e.g. the line number in the content to parse).
   */
  protected String appendContextDetails(String message) {

    if (this.name == null) {
      return message;
    }
    return message + "(at property '" + this.name + "')";
  }

  /**
   * @param value the value that was read.
   * @param typeClass the expected type.
   * @return nothing. Will always throw {@link IllegalStateException}. However to signal the compiler that the program
   *         flow will exit with the call of this function you may prefix it with "{@code throw}".
   */
  protected RuntimeException error(Object value, Class<?> typeClass) {

    return error(value, typeClass, null);
  }

  /**
   * @param value the value that was read.
   * @param typeClass the expected type.
   * @param cause a potential error that occurred or {@code null} if no cause.
   * @return nothing. Will always throw {@link IllegalStateException}. However to signal the compiler that the program
   *         flow will exit with the call of this function you may prefix it with "{@code throw}".
   */
  protected RuntimeException error(Object value, Class<?> typeClass, Throwable cause) {

    StringBuilder message = new StringBuilder("Failed to parse ");
    if (value == null) {
      message.append("null");
    } else {
      message.append("'");
      message.append(value);
      message.append("'");
      if (!(value instanceof String)) {
        message.append(" of type ");
        message.append(value.getClass().getSimpleName());
      }
    }
    message.append(" as ");
    message.append(typeClass.getSimpleName());
    return error(message.toString(), cause);
  }

}
