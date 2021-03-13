/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.marshall.json;

import io.github.mmm.marshall.StructuredTextFormatProvider;
import io.github.mmm.marshall.test.AbstractJsonFormatTest;

/**
 * Test of {@link io.github.mmm.marshall.JsonFormat} via {@link JsonFormatProvider}.
 */
public class JsonFormatTest extends AbstractJsonFormatTest {

  @Override
  protected StructuredTextFormatProvider getProvider() {

    return new JsonFormatProvider();
  }

}
