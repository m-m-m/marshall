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
public class JsonFormatTest extends AbstractJsonFormatTest {

  @Override
  protected StructuredTextFormatProvider getProvider() {

    return new JsonFormatProvider();
  }

  /**
   * Test of writing JSON with unquoted properties.
   */
  @Test
  public void testWriteUnquoted() {

    // given
    StructuredWriter writer = newWriter(MarshallingConfig.DEFAULTS
        .with(MarshallingConfig.OPT_UNQUOTED_PROPERTIES, Boolean.TRUE).with(MarshallingConfig.OPT_INDENTATION, null));
    // when
    writeTestData(writer);
    // then
    assertThat(getActualData()).isEqualTo(getExpectedJsonData("", "", false));
  }

  /**
   * Test of reading JSON with unquoted properties.
   */
  @Test
  public void testReadUnquoted() {

    StructuredReader reader = newReader(getExpectedJsonData("", "", false));
    readTestData(reader);
  }

}
