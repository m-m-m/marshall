/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.marshall;

/// Interface for a marshaller that can [write][#writeObject(StructuredWriter, Object)] (marshall or serialize) an
/// object to structured formats such as JSON or XML.
///
/// @param <T> type of the object to [write][#writeObject(StructuredWriter, Object)].
/// @see Unmarshaller
/// @see MarshallableObject
/// @since 1.0.0
public interface Marshaller<T> {

  /// @param writer the [StructuredWriter] where to write the data of the given `object` to. Create via e.g.
  ///        `JsonFormat.get().writer(writer)` or `XmlFormat.get().writer(writer)`.
  /// @param object the object to serialize or marshall. If this is an instance of [MarshallableObject] then this
  ///        has to be the called instance itself.
  void writeObject(StructuredWriter writer, T object);

}
