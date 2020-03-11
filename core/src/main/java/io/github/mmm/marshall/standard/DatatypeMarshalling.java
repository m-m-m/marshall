/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.marshall.standard;

import java.util.HashMap;
import java.util.Map;

import io.github.mmm.marshall.Marshalling;
import io.github.mmm.marshall.StructuredReader;
import io.github.mmm.marshall.StructuredWriter;
import io.github.mmm.marshall.impl.MashallingDatatypes;

/**
 * Implementation of {@link Marshalling} for simple standard datatypes as specified by {@link io.github.mmm.marshall}.
 *
 * @param <T> type of the datatype.
 * @since 1.0.0
 * @see StructuredReader#readValue(Class)
 * @see StructuredWriter#writeValue(Object)
 */
public class DatatypeMarshalling<T> implements Marshalling<T> {

  private static final Map<Class<?>, DatatypeMarshalling<?>> MARSHALLING_MAP = new HashMap<>();

  private final Class<T> type;

  /**
   * The constructor.
   *
   * @param type the {@link Class} reflecting the datatype.
   */
  protected DatatypeMarshalling(Class<T> type) {

    super();
    this.type = type;
  }

  @Override
  public void writeObject(StructuredWriter writer, T object) {

    writer.writeValue(object);
  }

  @Override
  public T readObject(StructuredReader reader) {

    return reader.readValue(this.type);
  }

  /**
   * @param <T> type of the datatype.
   * @param type {@link Class} reflecting the datatype.
   * @return the {@link DatatypeMarshalling} for the given {@link Class}.
   */
  @SuppressWarnings("unchecked")
  public static <T> DatatypeMarshalling<T> of(Class<T> type) {

    DatatypeMarshalling<T> marshalling = (DatatypeMarshalling<T>) MARSHALLING_MAP.get(type);
    if (marshalling == null) {
      if (!MashallingDatatypes.isSupported(type)) {
        throw new IllegalArgumentException("" + type);
      }
      marshalling = new DatatypeMarshalling<>(type);
      MARSHALLING_MAP.put(type, marshalling);
    }
    return marshalling;
  }

}
