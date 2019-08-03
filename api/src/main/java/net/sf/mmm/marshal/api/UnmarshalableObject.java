package net.sf.mmm.marshal.api;

/**
 * Extends {@link MarshalableObject} for a mutable object that can both be {@link #write(StructuredWriter)
 * written} and {@link #read(StructuredReader) read}.
 *
 * @since 1.0.0
 */
public interface UnmarshalableObject extends MarshalableObject {

  /**
   * @param reader the {@link StructuredReader} where to read the data from. This object where this method is invoked on
   *        will be modified such that the read data will be applied. This process is also called deserialization or
   *        unmarshaling. For complex objects (e.g. beans with multiple properties) those properties not defined in the
   *        data that is read will not be overwritten. In order to get a clean representation of the data from the given
   *        reader you should invoke this method on an empty object.
   */
  void read(StructuredReader reader);

}
