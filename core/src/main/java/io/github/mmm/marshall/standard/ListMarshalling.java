/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.marshall.standard;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import io.github.mmm.marshall.Marshalling;
import io.github.mmm.marshall.StructuredReader;
import io.github.mmm.marshall.StructuredWriter;

/**
 * Implementation of {@link Marshalling} for simple standard datatypes as specified by {@link io.github.mmm.marshall}.
 *
 * @param <T> type of the datatype.
 * @since 1.0.0
 * @see StructuredReader#readValue(Class)
 * @see StructuredWriter#writeValue(Object)
 */
public class ListMarshalling<T> implements Marshalling<Collection<T>> {

  private final Marshalling<T> itemMarshalling;

  /**
   * The constructor.
   *
   * @param itemMarshalling the {@link Marshalling} for the items in the {@link List}.
   */
  public ListMarshalling(Marshalling<T> itemMarshalling) {

    super();
    Objects.requireNonNull(itemMarshalling);
    this.itemMarshalling = itemMarshalling;
  }

  @Override
  public void writeObject(StructuredWriter writer, Collection<T> list) {

    if (list == null) {
      writer.writeValueAsNull();
    } else {
      writer.writeStartArray();
      for (T item : list) {
        this.itemMarshalling.writeObject(writer, item);
      }
      writer.writeEnd();
    }
  }

  @Override
  public List<T> readObject(StructuredReader reader) {

    List<T> list = null;
    if (reader.readStartArray()) {
      list = new ArrayList<>();
      while (!reader.readEnd()) {
        T item = this.itemMarshalling.readObject(reader);
        list.add(item);
      }
    } else {
      Object value = reader.readValue();
      if (value != null) {
        throw new IllegalStateException("" + value);
      }
    }
    return list;
  }

}
