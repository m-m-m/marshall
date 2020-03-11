/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.marshall;

/**
 * Interface for a marshaller that can {@link #writeObject(StructuredWriter, Object) write} (marshall or serialize) an
 * object to structured formats such as JSON or XML.
 *
 * @param <T> type of the object to {@link #writeObject(StructuredWriter, Object) write}.
 * @see Unmarshaller
 * @see MarshallableObject
 * @since 1.0.0
 */
public interface Marshaller<T> {

  /**
   * @param writer the {@link StructuredWriter} where to write the data of the given {@code object} to. Create via e.g.
   *        {@code JsonFormat.get().writer(writer)} or {@code XmlFormat.get().writer(writer)}.
   * @param object the object to serialize or marshall. If this is an instance of {@link MarshallableObject} then this
   *        has to be the called instance itself.
   */
  void writeObject(StructuredWriter writer, T object);

}
