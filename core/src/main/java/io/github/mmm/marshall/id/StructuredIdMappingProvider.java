/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.marshall.id;

/// Interface for a provider of [StructuredIdMapping]s.
public interface StructuredIdMappingProvider {

  /// @param object the [StructuredIdMappingObject] (e.g. bean or record) to get the [StructuredIdMapping]
  ///        for.
  /// @return the [StructuredIdMapping] for the given [Object].
  StructuredIdMapping getMapping(StructuredIdMappingObject object);

}
