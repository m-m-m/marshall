/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.marshall;

import io.github.mmm.marshall.id.StructuredIdMappingObject;

/// Base interface for [StructuredReader] and [StructuredWriter].
///
/// @since 1.0.0
public abstract interface StructuredProcessor extends AutoCloseable {

  /// [Name][StructuredReader#readName()] of a virtual property containing the type of a polymorphic
  /// [object][StructuredReader#readStartObject(StructuredIdMappingObject)].
  /// As an example lets assume you have an entity `Order` that contains a list of `Food` objects. However,
  /// `Food` may be abstract and have the sub-classes `Drink` and `Meal`. Here we call `Food` a
  /// polymorphic object. Now maybe marshalling your data to e.g. JSON might be simple but if you want to unmarshall the
  /// data back to Java objects, you need to know if you have to instantiate a `Food` object as `Drink` or
  /// `Meal` while reading. Therefore the idea is to use the first property of the `Food` object in the
  /// marshalled data as a virtual property containing the according meta-information (e.g. "drink" or "meal"). Do not
  /// use [(qualified) class names][Class#getName()] as values for this property but an explicit mapping for
  /// stability and security.
  /// This [#TYPE] constant holds the reserved name to be used for this virtual property.
  ///
  /// @see StructuredWriter#writeStartObject(StructuredIdMappingObject)
  /// @see StructuredReader#readStartObject(StructuredIdMappingObject)
  /// @see StructuredReader#specializeObject(StructuredIdMappingObject)
  String TYPE = "@type";

  /// @return the current state of marshalling. Will initially be [StructuredState#NULL] until anything has been
  ///         written.
  StructuredState getState();

  /// @return the name of the current property or element. This method does not change the [state][#getState()] and
  ///         could be called multiple times.
  String getName();

  /// @return the owning [StructuredFormat] that [created][StructuredFormat#reader(java.io.InputStream)] this
  ///         writer.
  StructuredFormat getFormat();

  @Override
  void close();

}
