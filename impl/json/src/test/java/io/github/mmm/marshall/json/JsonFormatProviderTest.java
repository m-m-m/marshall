/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.marshall.json;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import io.github.mmm.marshall.StructuredFormat;
import io.github.mmm.marshall.StructuredFormatFactory;
import io.github.mmm.marshall.StructuredFormatProvider;

/**
 * Test of {@link JsonFormatProvider}.
 */
public class JsonFormatProviderTest extends Assertions {

  /**
   * Test that {@link JsonFormatProvider} is registered.
   */
  @Test
  public void testJsonFormat() {

    StructuredFormatProvider provider = StructuredFormatFactory.get().getProvider(StructuredFormat.ID_JSON);
    assertThat(provider).isNotNull().isInstanceOf(JsonFormatProvider.class);
    assertThat(provider.create()).isSameAs(JsonMarshalling.of());
  }

}
