/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.marshall.protobuf;

import java.io.StringWriter;

import org.junit.jupiter.api.Test;

import io.github.mmm.marshall.MarshallingConfig;
import io.github.mmm.marshall.StructuredBinaryFormatProvider;
import io.github.mmm.marshall.StructuredFormat;
import io.github.mmm.marshall.StructuredFormatFactory;
import io.github.mmm.marshall.StructuredFormatProvider;
import io.github.mmm.marshall.StructuredReader;
import io.github.mmm.marshall.StructuredState;
import io.github.mmm.marshall.StructuredWriter;
import io.github.mmm.marshall.id.impl.StructuredIdMappingIdentity;
import io.github.mmm.marshall.mrpc.MrpcFormatProvider;
import io.github.mmm.marshall.mrpc.impl.MrpcFormat;
import io.github.mmm.marshall.test.StructuredBinaryFormatTest;

/**
 * Test of {@link MrpcFormatProvider} and {@link io.github.mmm.marshall.mrpc.impl.MrpcFormat}.
 */
public class MrpcFormatTest extends StructuredBinaryFormatTest {

  private static final Integer INTEGER_0 = Integer.valueOf(0);

  private static final Integer INTEGER_1 = Integer.valueOf(1);

  private static final Integer INTEGER_2 = Integer.valueOf(2);

  private static final Integer INTEGER_42 = Integer.valueOf(42);

  private static final Integer INTEGER_150 = Integer.valueOf(150);

  private static final String STRING_TESTING = "testing";

  @Override
  protected String getExpectedData() {

    return "0a03626172121b313939392d31322d33315432333a35393a35392e3939393939395a1e20010101e9f0e0fd5b0d6666864009f6285c8fc23545401a1f302e3132333435363738393031323334353637383930313233343536373839283132333435363738393031323334353637383930313233343536373839303132333435363738393004312e313006030a0576616c75650404042604";
  }

  @Override
  protected String getExpectedDataForAtomicLong() {

    return "54";
  }

  @Override
  protected StructuredBinaryFormatProvider getProvider() {

    return new MrpcFormatProvider();
  }

  /**
   * Test that {@link MrpcFormatProvider} is registered.
   */
  @Test
  public void testMrpcFormat() {

    StructuredFormatProvider provider = StructuredFormatFactory.get().getProvider(StructuredFormat.ID_MRPC);
    assertThat(provider).isNotNull().isInstanceOf(MrpcFormatProvider.class);
    assertThat(newFormat(provider, MarshallingConfig.DEFAULTS)).isInstanceOf(MrpcFormat.class);
  }

  /**
   * Test that the example from our README is correct.
   */
  @Test
  public void testReadme() {

    // arrange
    String expectedTestdata = "08ac02120774657374696e671e180002040a0774657374696e670304000423085404";
    StructuredIdMappingIdentity object = StructuredIdMappingIdentity.get();
    // act
    StructuredWriter writer = newWriter(MarshallingConfig.NO_INDENTATION);
    writeReadmeTestData(writer);
    // assert
    assertThat(getActualData()).isEqualTo(expectedTestdata);

    // act
    StructuredReader reader = newReader(expectedTestdata);
    // assert
    assertThat(reader.isDone()).isFalse();
    checkState(reader, StructuredState.START_OBJECT);
    assertThat(reader.readStartObject(object)).isTrue();
    assertThat(reader.getState()).isSameAs(StructuredState.NAME);
    checkName(reader, "1");
    assertThat(reader.getState()).isSameAs(StructuredState.VALUE);
    assertThat(reader.isStringValue()).isFalse();
    assertThat(reader.readValueAsInteger()).isEqualTo(INTEGER_150);
    assertThat(reader.getState()).isSameAs(StructuredState.NAME);
    checkName(reader, "2");
    assertThat(reader.getState()).isSameAs(StructuredState.VALUE);
    assertThat(reader.isStringValue()).isTrue();
    assertThat(reader.readValueAsString()).isEqualTo(STRING_TESTING);
    assertThat(reader.getState()).isSameAs(StructuredState.NAME);
    checkName(reader, "3");
    assertThat(reader.getState()).isSameAs(StructuredState.START_ARRAY);
    assertThat(reader.readStartArray()).isTrue();
    assertThat(reader.getState()).isSameAs(StructuredState.VALUE);
    assertThat(reader.isStringValue()).isFalse();
    assertThat(reader.readValueAsInteger()).isEqualTo(INTEGER_0);
    assertThat(reader.getState()).isSameAs(StructuredState.VALUE);
    assertThat(reader.isStringValue()).isFalse();
    assertThat(reader.readValueAsInteger()).isEqualTo(INTEGER_1);
    assertThat(reader.getState()).isSameAs(StructuredState.VALUE);
    assertThat(reader.isStringValue()).isFalse();
    assertThat(reader.readValueAsInteger()).isEqualTo(INTEGER_2);
    assertThat(reader.getState()).isSameAs(StructuredState.VALUE);
    assertThat(reader.isStringValue()).isTrue();
    assertThat(reader.readValueAsString()).isEqualTo(STRING_TESTING);
    assertThat(reader.getState()).isSameAs(StructuredState.START_OBJECT);
    assertThat(reader.readStartObject(object)).isTrue();
    assertThat(reader.getState()).isSameAs(StructuredState.END_OBJECT);
    assertThat(reader.readEndObject()).isTrue();
    assertThat(reader.getState()).isSameAs(StructuredState.VALUE);
    assertThat(reader.readValue()).isNull();
    assertThat(reader.getState()).isSameAs(StructuredState.END_ARRAY);
    assertThat(reader.readEndArray()).isTrue();
    assertThat(reader.getState()).isSameAs(StructuredState.NAME);
    checkName(reader, "4");
    assertThat(reader.getState()).isSameAs(StructuredState.START_OBJECT);
    assertThat(reader.readStartObject(object)).isTrue();
    assertThat(reader.getState()).isSameAs(StructuredState.NAME);
    checkName(reader, "1");
    assertThat(reader.getState()).isSameAs(StructuredState.VALUE);
    assertThat(reader.readValueAsInteger()).isEqualTo(INTEGER_42);
    assertThat(reader.getState()).isSameAs(StructuredState.END_OBJECT);
    assertThat(reader.readEndObject()).isTrue();
    assertThat(reader.getState()).isSameAs(StructuredState.END_OBJECT);
    assertThat(reader.isDone()).isFalse();
    assertThat(reader.readEndObject()).isTrue();
    assertThat(reader.getState()).isSameAs(StructuredState.DONE);
    assertThat(reader.isDone()).isTrue();
  }

  /**
   * @param writer the {@link StringWriter} where to write the test-data to.
   */
  protected void writeReadmeTestData(StructuredWriter writer) {

    StructuredIdMappingIdentity object = StructuredIdMappingIdentity.get();
    writer.writeStartObject(object);
    writer.writeName("1");
    writer.writeValue(INTEGER_150);
    writer.writeName("2");
    writer.writeValueAsString(STRING_TESTING);
    writer.writeName("3");
    writer.writeStartArray();
    writer.writeValueAsInteger(INTEGER_0);
    writer.writeValueAsInteger(INTEGER_1);
    writer.writeValueAsInteger(INTEGER_2);
    writer.writeValueAsString(STRING_TESTING);
    writer.writeStartObject(object);
    writer.writeEnd();
    writer.writeValue(null);
    writer.writeEnd();
    writer.writeName("4");
    writer.writeStartObject(object);
    writer.writeName("1");
    writer.writeValue(INTEGER_42);
    writer.writeEnd();
    writer.writeEnd();
    writer.close();
  }

}
