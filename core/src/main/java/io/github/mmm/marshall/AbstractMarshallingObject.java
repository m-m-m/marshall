/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.marshall;

import io.github.mmm.marshall.StructuredReader.State;

/**
 * Abstract base implementation of {@link MarshallingObject} for objects
 */
public abstract class AbstractMarshallingObject implements MarshallingObject {

  @Override
  public void write(StructuredWriter writer) {

    writer.writeStartObject();
    writeProperties(writer);
    writer.writeEnd();
  }

  /**
   * Writes all properties of this {@link MarshallingObject}.
   *
   * @param writer the {@link StructuredWriter} to write to.
   */
  protected abstract void writeProperties(StructuredWriter writer);

  @Override
  public void read(StructuredReader reader) {

    reader.require(State.START_OBJECT);
    while (!reader.readEnd()) {
      String name = reader.readName();
      readProperty(reader, name);
    }
  }

  /**
   * Reads the property with the given {@code name} and applies it to this {@link MarshallingObject}.
   *
   * @param reader the {@link StructuredReader} to read from.
   * @param name the name of the property to read.
   */
  protected abstract void readProperty(StructuredReader reader, String name);

}
