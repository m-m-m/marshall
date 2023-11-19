/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.marshall;

import io.github.mmm.marshall.id.StructuredIdMappingObject;

/**
 * Base interface for {@link StructuredReader} and {@link StructuredWriter}.
 *
 * @since 1.0.0
 */
public abstract interface StructuredProcessor extends AutoCloseable {

  /**
   * {@link StructuredReader#readName() Name} of a virtual property containing the type of a polymorphic
   * {@link StructuredReader#readStartObject(StructuredIdMappingObject) object}.<br>
   * As an example lets assume you have an entity {@code Order} that contains a list of {@code Food} objects. However,
   * {@code Food} may be abstract and have the sub-classes {@code Drink} and {@code Meal}. Here we call {@code Food} a
   * polymorphic object. Now maybe marshalling your data to e.g. JSON might be simple but if you want to unmarshall the
   * data back to Java objects, you need to know if you have to instantiate a {@code Food} object as {@code Drink} or
   * {@code Meal} while reading. Therefore the idea is to use the first property of the {@code Food} object in the
   * marshalled data as a virtual property containing the according meta-information (e.g. "drink" or "meal"). Do not
   * use {@link Class#getName() (qualified) class names} as values for this property but an explicit mapping for
   * stability and security.<br>
   * This {@link #TYPE} constant holds the reserved name to be used for this virtual property.
   *
   * @see StructuredWriter#writeStartObject(StructuredIdMappingObject)
   * @see StructuredReader#readStartObject(StructuredIdMappingObject)
   * @see StructuredReader#specializeObject(StructuredIdMappingObject)
   */
  String TYPE = "@type";

  /**
   * @return the current state of marshalling. Will initially be {@link StructuredState#NULL} until anything has been
   *         written.
   */
  StructuredState getState();

  /**
   * @return the name of the current property or element. This method does not change the {@link #getState() state} and
   *         could be called multiple times.
   */
  String getName();

  /**
   * @return the owning {@link StructuredFormat} that {@link StructuredFormat#reader(java.io.InputStream) created} this
   *         writer.
   */
  StructuredFormat getFormat();

  @Override
  void close();

}
