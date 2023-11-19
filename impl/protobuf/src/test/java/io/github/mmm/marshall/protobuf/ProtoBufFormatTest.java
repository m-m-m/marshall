/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.marshall.protobuf;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.junit.jupiter.api.Test;

import com.google.protobuf.CodedOutputStream;
import com.google.protobuf.WireFormat;

import io.github.mmm.binary.BinaryType;
import io.github.mmm.marshall.MarshallingConfig;
import io.github.mmm.marshall.StructuredBinaryFormatProvider;
import io.github.mmm.marshall.StructuredFormat;
import io.github.mmm.marshall.StructuredFormatFactory;
import io.github.mmm.marshall.StructuredFormatProvider;
import io.github.mmm.marshall.StructuredReader;
import io.github.mmm.marshall.StructuredState;
import io.github.mmm.marshall.id.StructuredIdMapping;
import io.github.mmm.marshall.protobuf.impl.ProtoBufFormat;
import io.github.mmm.marshall.test.StructuredBinaryFormatTest;

/**
 * Test of {@link ProtoBufFormatProvider} and {@link io.github.mmm.marshall.protobuf.impl.ProtoBufFormat}.
 */
public class ProtoBufFormatTest extends StructuredBinaryFormatTest {

  @Override
  protected String getExpectedData() {

    return "0a03626172121b313939392d31322d33315432333a35393a35392e3939393939395a18011801180118e9f0e0fd5b1d6666864019f6285c8fc23545401a1f302e31323334353637383930313233343536373839303132333435363738391a28313233343536373839303132333435363738393031323334353637383930313233343536373839301a04312e31301e0b0a0576616c75650c1c";
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
  protected void checkState(StructuredReader reader, StructuredState state) {

    // TODO temporary disabled unless all is fixed
    super.checkState(reader, state);
  }

  /**
   * Internal test to keep {@link #getExpectedData()} transparent and maintainable.
   *
   * @throws Exception on error.
   */
  @Test
  public void testExcpectedValue() throws Exception {

    String expectedData = getExpectedData();
    ByteArrayOutputStream baos = new ByteArrayOutputStream(expectedData.length() / 2);
    CodedOutputStream out = CodedOutputStream.newInstance(baos);
    out.writeString(1, P1_FOO_VALUE);
    out.writeString(2, P2_INSTANT_VALUE.toString());
    out.writeSInt32(3, P3_LIST_VALUE1.byteValue()); // regular array as repeatable field
    out.writeSInt32(3, P3_LIST_VALUE2.shortValue());
    out.writeSInt32(3, P3_LIST_VALUE3.intValue());
    out.writeSInt64(3, P3_LIST_VALUE4.longValue());
    out.writeFloat(3, P3_LIST_VALUE5.floatValue());
    out.writeDouble(3, P3_LIST_VALUE6.doubleValue());
    out.writeString(3, P3_LIST_VALUE7.toString());
    out.writeString(3, P3_LIST_VALUE8.toString());
    out.writeString(3, P3_LIST_VALUE9.toString());
    out.writeTag(3, ProtoBufFormat.TYPE_START_ARRAY); // array inside array
    out.writeTag(1, ProtoBufFormat.TYPE_START_OBJECT); // object inside array inside array
    out.writeString(1, P3_LIST_VALUE10_ARRAY_P1_KEY_VALUE);
    out.writeTag(1, ProtoBufFormat.TYPE_END); // end object
    out.writeTag(3, ProtoBufFormat.TYPE_END); // end array
    out.flush();
    byte[] payload = baos.toByteArray();
    String actual = BinaryType.formatHex(payload);
    assertThat(actual).isEqualTo(expectedData);
  }

  /**
   * Test that {@link ProtoBufFormatProvider} is registered.
   */
  @Test
  public void testProtoBufFormat() {

    StructuredFormatProvider provider = StructuredFormatFactory.get().getProvider(StructuredFormat.ID_PROTOBUF);
    assertThat(provider).isNotNull().isInstanceOf(ProtoBufFormatProvider.class);
    assertThat(newFormat(provider, MarshallingConfig.DEFAULTS)).isInstanceOf(ProtoBufFormat.class);
  }

  /**
   * Ensure that {@link StructuredIdMapping#TYPE} fits in a tag of 2 bytes.
   *
   * @throws IOException on error.
   */
  @Test
  public void testTagTypeSize() throws IOException {

    assertThat(CodedOutputStream.computeTagSize(StructuredIdMapping.TYPE)).isEqualTo(2);
    ByteArrayOutputStream baos = new ByteArrayOutputStream(4);
    CodedOutputStream out = CodedOutputStream.newInstance(baos);
    out.writeTag(StructuredIdMapping.TYPE, WireFormat.WIRETYPE_VARINT);
    assertThat(out.getTotalBytesWritten()).isEqualTo(2);
    out.flush();
    byte[] payload = baos.toByteArray();
    assertThat(payload.length).isEqualTo(2);
  }

}
