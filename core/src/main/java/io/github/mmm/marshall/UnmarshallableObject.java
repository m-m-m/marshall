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
   * ATTENTION: This object will be modified such that the read data will be applied. For complex objects (e.g. beans
   * with multiple properties) those properties not defined in the data that is read will not be overwritten. In order
   * to get a clean representation of the data from the given reader you should invoke this method on an empty object.
   *
   * @see #read(StructuredReader)
   */
  @Override
  default UnmarshallableObject readObject(StructuredReader reader) {

    read(reader);
    return this;
  }

  /**
   * @param reader the {@link StructuredReader} where to read the data from. This object will be modified such that the
   *        read data will be applied. For complex objects (e.g. beans with multiple properties) those properties not
   *        defined in the data that is read will not be overwritten. In order to get a clean representation of the data
   *        from the given reader you should invoke this method on an empty object.
   */
  void read(StructuredReader reader);

}
