/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.marshall;

/**
 * Interface for an object with read-access that can therefore be {@link #write(StructuredWriter) written} to structured
 * formats such as JSON or XmlFormat.
 *
 * @see UnmarshallableObject
 * @since 1.0.0
 */
public interface MarshallableObject {

  /**
   * @param writer the {@link StructuredWriter} where to write the data of this object to. This process is also called
   *        serialization or marshalling. Create via e.g. {@code JsonFormat.get().writer(writer)} or
   *        {@code XmlFormat.get().writer(writer)}.
   */
  void write(StructuredWriter writer);

}
