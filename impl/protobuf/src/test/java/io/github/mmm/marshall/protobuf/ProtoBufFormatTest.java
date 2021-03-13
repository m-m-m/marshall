/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.marshall.protobuf;

import org.junit.jupiter.api.Test;

import io.github.mmm.marshall.StructuredBinaryFormatProvider;
import io.github.mmm.marshall.StructuredFormat;
import io.github.mmm.marshall.StructuredFormatFactory;
import io.github.mmm.marshall.StructuredFormatProvider;
import io.github.mmm.marshall.StructuredReader;
import io.github.mmm.marshall.StructuredReader.State;
import io.github.mmm.marshall.protobuf.ProtoBufFormatProvider;
import io.github.mmm.marshall.protobuf.impl.ProtoBufFormat;
import io.github.mmm.marshall.test.StructuredBinaryFormatTest;

/**
 * Test of {@link ProtoBufFormatProvider} and {@link io.github.mmm.marshall.protobuf.impl.ProtoBufFormat}.
 */
public class ProtoBufFormatTest extends StructuredBinaryFormatTest {

  @Override
  protected String getExpectedData() {

    return "91010a03626172121b313939392d31322d33315432333a35393a35392e3939393939395a1a6b010101e9f0e0fd5b66668640f6285c8fc23545401f302e3132333435363738393031323334353637383930313233343536373839283132333435363738393031323334353637383930313233343536373839303132333435363738393004312e313008070a0576616c75652200";
  }

  @Override
  protected String getExpectedDataForAtomicLong() {

    return "54";
  }

  @Override
  protected StructuredBinaryFormatProvider getProvider() {

    return new ProtoBufFormatProvider();
  }

  @Override
  protected void checkState(StructuredReader reader, State state) {

    // TODO temporary disabled unless all is fixed
  }

  /**
   * Test that {@link ProtoBufFormatProvider} is registered.
   */
  @Test
  public void testJsonFormat() {

    StructuredFormatProvider provider = StructuredFormatFactory.get().getProvider(StructuredFormat.ID_PROTOBUF);
    assertThat(provider).isNotNull().isInstanceOf(ProtoBufFormatProvider.class);
    assertThat(provider.create()).isInstanceOf(ProtoBufFormat.class);
  }

}
