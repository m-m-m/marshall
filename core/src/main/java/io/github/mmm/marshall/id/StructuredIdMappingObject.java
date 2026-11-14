package io.github.mmm.marshall.id;

/// Interface for an object that [defines][#defineIdMapping()] its own [StructuredIdMapping]. Typically
/// implemented by [io.github.mmm.marshall.MarshallingObject]s.
///
/// @see io.github.mmm.marshall.StructuredFormat#isIdBased()
/// @see io.github.mmm.marshall.StructuredReader#readStartObject(StructuredIdMappingObject)
/// @see io.github.mmm.marshall.StructuredWriter#writeStartObject(StructuredIdMappingObject)
public interface StructuredIdMappingObject {

  /// This method defines the [io.github.mmm.marshall.id.StructuredIdMapping] for this object. Implementations have
  /// to be stateless that is instances with the [same][Object#equals(Object)] [type][#asTypeKey()] always need
  /// to produce the equivalent result. Typically this method will be called only once per [type][#asTypeKey()].
  /// There is no need that implementations store the result and return the exact same instance on sub-sequent method
  /// calls. Typical implementations will look like this:
  ///
  /// ```
  /// public [StructuredIdMapping] defineIdMapping() {
  ///   return [StructuredIdMapping].[of][StructuredIdMapping#of(String...)]("foo", "bar", "some", "other");
  /// }
  /// ```
  ///
  /// @return the [StructuredIdMapping] for this object.
  StructuredIdMapping defineIdMapping();

  /// @return an object that uniquely identifies the type of this instance.
  /// @see #defineIdMapping()
  default Object asTypeKey() {

    return getClass();
  }

}
