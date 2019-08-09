/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package net.sf.mmm.marshall.jsonp;

import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.BigInteger;

import net.sf.mmm.marshall.StructuredReader;
import net.sf.mmm.marshall.StructuredWriter;
import net.sf.mmm.marshall.jsonp.impl.JsonFormat;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test of {@link JsonpMarshalling}.
 */
public class JsonpMarshallingTest extends Assertions {

  /**
   * Test {@link JsonFormat#writer(java.io.Writer) writing JSON}.
   *
   * @throws Exception on error.
   */
  @Test
  public void testWriteJson() throws Exception {

    StringWriter stringWriter = new StringWriter();
    StructuredWriter writer = JsonpMarshalling.of().writer(stringWriter);
    writer.writeStartObject();
    writer.writeName("foo");
    writer.writeValue("bar");
    writer.writeName("list");
    writer.writeStartArray();
    writer.writeValueAsByte((byte) -1);
    writer.writeValueAsShort((short) -1);
    writer.writeValueAsInteger(-1);
    writer.writeValueAsLong(Long.valueOf(-42));
    writer.writeValueAsFloat(4.2F);
    writer.writeValueAsDouble(42.42);
    writer.writeValueAsBigDecimal(new BigDecimal("0.12345678901234567890123456789"));
    writer.writeValueAsBigInteger(new BigInteger("1234567890123456789012345678901234567890"));
    writer.writeEnd();
    writer.writeEnd();
    writer.close();
    assertThat(stringWriter.toString()).isEqualTo(
        "{\"foo\":\"bar\",\"list\":[-1,-1,-1,-42,4.2,42.42,0.12345678901234567890123456789,1234567890123456789012345678901234567890]}");
  }

  /**
   * Test {@link JsonFormat#reader(Reader) reading JSON}.
   *
   * @throws Exception on error.
   */
  @Test
  public void testReadJson() throws Exception {

    Reader stringReader = new StringReader(
        "{\"foo\":\"bar\",\"list\":[-1,-1,-1,-42,4.2,42.42,0.12345678901234567890123456789,1234567890123456789012345678901234567890]}");
    StructuredReader reader = JsonpMarshalling.of().reader(stringReader);
    assertThat(reader.isDone()).isFalse();
    assertThat(reader.readStartObject()).isTrue();
    assertThat(reader.readName()).isEqualTo("foo");
    assertThat(reader.readValue(String.class)).isEqualTo("bar");
    assertThat(reader.readName()).isEqualTo("list");
    assertThat(reader.readStartArray()).isTrue();
    assertThat(reader.readValue(Byte.class)).isEqualTo(Byte.valueOf((byte) -1));
    assertThat(reader.readValue(Short.class)).isEqualTo(Short.valueOf((short) -1));
    assertThat(reader.readValue(Integer.class)).isEqualTo(-1);
    assertThat(reader.readValue(Long.class)).isEqualTo(-42L);
    assertThat(reader.readValue(Float.class)).isEqualTo(4.2F);
    assertThat(reader.readValue(Double.class)).isEqualTo(42.42);
    assertThat(reader.readValue(BigDecimal.class)).isEqualTo(new BigDecimal("0.12345678901234567890123456789"));
    assertThat(reader.readValue(BigInteger.class))
        .isEqualTo(new BigInteger("1234567890123456789012345678901234567890"));
    assertThat(reader.readEnd()).isTrue();
    assertThat(reader.isDone()).isFalse();
    assertThat(reader.readEnd()).isTrue();
    assertThat(reader.isDone()).isTrue();
  }

}
