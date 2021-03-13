/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.marshall.jsonp;

import io.github.mmm.marshall.StructuredTextFormatProvider;
import io.github.mmm.marshall.test.AbstractJsonFormatTest;

/**
 * Test of {@link io.github.mmm.marshall.jsonp.impl.JsonpFormat} via {@link JsonpFormatProvider}.
 */
public class JsonpFormatTest extends AbstractJsonFormatTest {

  @Override
  protected boolean isSmartIndent() {

    return false;
  }

  @Override
  protected String getIndentation() {

    return "    ";
  }

  @Override
  protected String getNewline() {

    return System.lineSeparator();
  }

  @Override
  protected String getExpectedData() {

    return getNewline() + super.getExpectedData();
  }

  @Override
  protected StructuredTextFormatProvider getProvider() {

    return new JsonpFormatProvider();
  }

}
