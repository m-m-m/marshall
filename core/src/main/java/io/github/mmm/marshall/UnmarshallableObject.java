/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.marshall;

/**
 * Interface for an object that can {@link #read(StructuredReader) read its data} from structured formats such as JSON
 * or XML.
 *
 * @since 1.0.0
 */
public interface UnmarshallableObject extends Unmarshaller<Object> {

  /**
   * <b>ATTENTION:</b> Will read the data from the reader into this object instance. This can cause unexpected results
   * if this object already contains data as described in {@link #read(StructuredReader)}.
   *
   * @see #read(StructuredReader)
   */
  @Override
  default UnmarshallableObject readObject(StructuredReader reader) {

    return read(reader);
  }

  /**
   * @param reader the {@link StructuredReader} where to read the data from. This object will be modified such that the
   *        read data will be applied. Only properties present in the read data will be set (overridden). In order to
   *        get a clean representation of the data from the given reader you should invoke this method on an empty
   *        object (new instance).
   * @return typically this object itself (however, for polymorphic unmarshalling it may also be a sub-type instance).
   */
  UnmarshallableObject read(StructuredReader reader);

}
