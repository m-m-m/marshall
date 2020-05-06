/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.marshall.jsonp;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import io.github.mmm.marshall.StructuredFormat;
import io.github.mmm.marshall.StructuredFormatFactory;
import io.github.mmm.marshall.StructuredFormatProvider;

/**
 * Test of {@link JsonpFormatProvider}.
 */
public class JsonpFormatProviderTest extends Assertions {

  /**
   * Test that {@link JsonpFormatProvider} is registered.
   */
  @Test
  public void testJsonFormat() {

    StructuredFormatProvider provider = StructuredFormatFactory.get().getProvider(StructuredFormat.ID_JSON);
    assertThat(provider).isNotNull().isInstanceOf(JsonpFormatProvider.class);
    assertThat(provider.create()).isSameAs(JsonpMarshalling.of());
  }

}
