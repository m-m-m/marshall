/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.marshall;

/**
 * Interface for an object that can {@link #write(StructuredWriter) write itself} to structured formats such as JSON or
 * XML. The generic type is bound to {@link Object} since Java does not properly support {@literal <SELF>} leading to
 * trouble in usage.
 *
 * @see UnmarshallableObject
 * @since 1.0.0
 */
public interface MarshallableObject extends Marshaller<Object> {

  @Override
  default void writeObject(StructuredWriter writer, Object object) {

    if (object != this) {
      throw new IllegalStateException();
    }
    write(writer);
  }

  /**
   * @param writer the {@link StructuredWriter} where to marshall (serialize) the data of this object to.
   * @see #writeObject(StructuredWriter, Object)
   */
  void write(StructuredWriter writer);

}
