/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.marshall;

/**
 * {@link StructuredFormatProvider} for {@link StructuredTextFormat}.
 *
 * @since 1.0.0
 */
public interface StructuredTextFormatProvider extends StructuredFormatProvider {

  @Override
  StructuredTextFormat create();

  @Override
  StructuredTextFormat create(MarshallingConfig config);

}
