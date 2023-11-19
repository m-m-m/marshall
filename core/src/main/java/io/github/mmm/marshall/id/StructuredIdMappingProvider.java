/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.marshall.id;

/**
 * Interface for a provider of {@link StructuredIdMapping}s.
 */
public interface StructuredIdMappingProvider {

  /**
   * @param object the {@link StructuredIdMappingObject} (e.g. bean or record) to get the {@link StructuredIdMapping}
   *        for.
   * @return the {@link StructuredIdMapping} for the given {@link Object}.
   */
  StructuredIdMapping getMapping(StructuredIdMappingObject object);

}
