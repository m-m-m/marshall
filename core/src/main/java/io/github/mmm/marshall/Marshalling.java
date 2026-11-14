/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.marshall;

/// A combination of [Marshaller] and [Unmarshaller].
///
/// @param <T> type of the object to [read][#readObject(StructuredReader)] or
///        [write][#writeObject(StructuredWriter, Object)].
/// @see Unmarshaller
/// @see MarshallableObject
/// @since 1.0.0
public interface Marshalling<T> extends Marshaller<T>, Unmarshaller<T> {

}
