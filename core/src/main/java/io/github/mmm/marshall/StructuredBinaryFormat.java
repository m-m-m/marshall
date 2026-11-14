/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.marshall;

/// [StructuredFormat] that is [binary][#isBinary()].
///
/// @since 1.0.0
public interface StructuredBinaryFormat extends StructuredFormat {

  @Override
  default boolean isBinary() {

    return true;
  }

}
