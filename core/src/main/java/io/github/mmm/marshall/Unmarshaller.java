/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.marshall;

/**
 * Interface for an unmarshaller that can {@link #readObject(StructuredReader) read} (unmarshall or de-serialize) an
 * object from structured formats such as JSON or XML.
 *
 * @param <T> type of the object to {@link #readObject(StructuredReader) read}.
 * @since 1.0.0
 * @see Marshaller
 * @see UnmarshallableObject
 */
public interface Unmarshaller<T> {

  /**
   * @param reader the {@link StructuredReader} where to read the data from.
   * @return the unmarhsalled or de-serialized object. If this is an instance of {@link UnmarshallableObject} then the
   *         called instance is modified by filling in the unmarshalled data and will return itself.
   */
  T readObject(StructuredReader reader);

}
