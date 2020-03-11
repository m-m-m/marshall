/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.marshal.stax;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import io.github.mmm.marshall.StructuredFormatFactory;
import io.github.mmm.marshall.StructuredFormatProvider;
import io.github.mmm.marshall.stax.XmlFormatProvider;

/**
 * Test of {@link XmlFormatProvider}.
 */
public class XmlFormatProviderTest extends Assertions {

  /**
   * Test that {@link XmlFormatProvider} is registered.
   */
  @Test
  public void testJsonFormat() {

    StructuredFormatProvider provider = StructuredFormatFactory.get().getProvider(StructuredFormatFactory.NAME_XML);
    assertThat(provider).isNotNull().isInstanceOf(XmlFormatProvider.class);
  }

}
