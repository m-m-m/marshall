package io.github.mmm.marshall.id;

/**
 * Interface for an object that {@link #defineIdMapping() defines} its own {@link StructuredIdMapping}. Typically
 * implemented by {@link io.github.mmm.marshall.MarshallingObject}s.
 *
 * @see io.github.mmm.marshall.StructuredFormat#isIdBased()
 * @see io.github.mmm.marshall.StructuredReader#readStartObject(StructuredIdMappingObject)
 * @see io.github.mmm.marshall.StructuredWriter#writeStartObject(StructuredIdMappingObject)
 */
public interface StructuredIdMappingObject {

  /**
   * This method defines the {@link io.github.mmm.marshall.id.StructuredIdMapping} for this object. Implementations have
   * to be stateless that is instances with the {@link Object#equals(Object) same} {@link #asTypeKey() type} always need
   * to produce the equivalent result. Typically this method will be called only once per {@link #asTypeKey() type}.
   * There is no need that implementations store the result and return the exact same instance on sub-sequent method
   * calls. Typical implementations will look like this:
   *
   * <pre>
   * public {@link StructuredIdMapping} defineIdMapping() {
   *   return {@link StructuredIdMapping}.{@link StructuredIdMapping#of(String...) of}("foo", "bar", "some", "other");
   * }
   * </pre>
   *
   * @return the {@link StructuredIdMapping} for this object.
   */
  StructuredIdMapping defineIdMapping();

  /**
   * @return an object that uniquely identifies the type of this instance.
   * @see #defineIdMapping()
   */
  default Object asTypeKey() {

    return getClass();
  }

}
