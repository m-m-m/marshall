/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.marshall;

/**
 * A combination of {@link Marshaller} and {@link Unmarshaller}.
 *
 * @param <T> type of the object to {@link #readObject(StructuredReader) read} or
 *        {@link #writeObject(StructuredWriter, Object) write}.
 * @see Unmarshaller
 * @see MarshallableObject
 * @since 1.0.0
 */
public interface Marshalling<T> extends Marshaller<T>, Unmarshaller<T> {

}
