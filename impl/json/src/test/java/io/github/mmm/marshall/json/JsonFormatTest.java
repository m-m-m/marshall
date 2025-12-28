/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.marshall.json;

import org.junit.jupiter.api.Test;

import io.github.mmm.marshall.MarshallingConfig;
import io.github.mmm.marshall.StructuredReader;
import io.github.mmm.marshall.StructuredTextFormatProvider;
import io.github.mmm.marshall.StructuredWriter;
import io.github.mmm.marshall.test.AbstractJsonFormatTest;

/**
 * Test of {@link io.github.mmm.marshall.json.impl.JsonFormat} via {@link JsonFormatProvider}.
 */
class JsonFormatTest extends AbstractJsonFormatTest {

  @Override
  protected StructuredTextFormatProvider getProvider() {

    return new JsonFormatProvider();
  }

  /**
   * Test of writing JSON with unquoted properties.
   */
  @Test
  void testWriteUnquoted() {

    // arrange
    StructuredWriter writer = newWriter(MarshallingConfig.DEFAULTS
        .with(MarshallingConfig.VAR_UNQUOTED_PROPERTIES, Boolean.TRUE).with(MarshallingConfig.VAR_INDENTATION, null));
    // act
    writeTestData(writer);
    // assert
    assertThat(getActualData()).isEqualTo(getExpectedJsonData("", "", false));
  }

  /**
   * Test of reading JSON with unquoted properties.
   */
  @Test
  void testReadUnquoted() {

    StructuredReader reader = newReader(getExpectedJsonData("", "", false));
    readTestData(reader);
  }

}
