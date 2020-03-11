/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.marshall;

/**
 * Interface for an object that can {@link #read(StructuredReader) read} its data from and
 * {@link #write(StructuredWriter) write} its data to structured formats such as JSON or XML.
 *
 * @since 1.0.0
 */
public interface MarshallingObject extends Marshalling<Object>, MarshallableObject, UnmarshallableObject {

}
