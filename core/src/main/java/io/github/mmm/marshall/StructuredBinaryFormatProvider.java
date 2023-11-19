/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.marshall;

/**
 * {@link StructuredFormatProvider} for {@link StructuredBinaryFormat}.
 *
 * @since 1.0.0
 */
public interface StructuredBinaryFormatProvider extends StructuredFormatProvider {

  @Override
  StructuredBinaryFormat create();

  @Override
  StructuredBinaryFormat create(MarshallingConfig config);

}
