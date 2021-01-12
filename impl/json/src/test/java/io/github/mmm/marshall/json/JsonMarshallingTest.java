/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.marshall.json;

import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.util.Map;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import io.github.mmm.marshall.StructuredReader;
import io.github.mmm.marshall.StructuredReader.State;
import io.github.mmm.marshall.StructuredWriter;
import io.github.mmm.marshall.json.impl.JsonFormatImpl;

/**
 * Test of {@link JsonMarshalling}.
 */
public class JsonMarshallingTest extends Assertions {

  private static final String JSON = "{\n" //
      + "  \"foo\": \"bar\",\n" //
      + "  \"instant\": \"1999-12-31T23:59:59.999999Z\",\n" //
      + "  \"list\": [\n" //
      + "    -1,\n" //
      + "    -1,\n" //
      + "    -1,\n" //
      + "    -42,\n" //
      + "    4.2,\n" //
      + "    42.42,\n" //
      + "    \"0.12345678901234567890123456789\",\n" //
      + "    \"1234567890123456789012345678901234567890\"\n" //
      + "  ],\n" //
      + "  \"empty\": []\n" //
      + "}";

  /**
   * Test {@link JsonFormatImpl#writer(Appendable) writing JSON}.
   *
   * @throws Exception on error.
   */
  @Test
  public void testWriteJson() throws Exception {

    StringWriter stringWriter = new StringWriter();
    StructuredWriter writer = JsonMarshalling.of().writer(stringWriter);
    writer.writeStartObject();
    writer.writeName("foo");
    writer.writeValue("bar");
    writer.writeName("instant");
    writer.writeValueAsInstant(Instant.parse("1999-12-31T23:59:59.999999Z"));
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
    writer.writeName("empty");
    writer.writeStartArray();
    writer.writeEnd();
    writer.writeEnd();
    writer.close();
    assertThat(stringWriter.toString()).isEqualTo(JSON);
  }

  /**
   * Test {@link JsonFormatImpl#reader(Reader) reading JSON}.
   *
   * @throws Exception on error.
   */
  @Test
  public void testReadJson() throws Exception {

    Reader stringReader = new StringReader(JSON);
    StructuredReader reader = JsonMarshalling.of().reader(stringReader);
    assertThat(reader.isDone()).isFalse();
    assertThat(reader.getState()).isSameAs(State.START_OBJECT);
    assertThat(reader.readStartObject()).isTrue();
    assertThat(reader.getState()).isSameAs(State.NAME);
    assertThat(reader.readName()).isEqualTo("foo");
    assertThat(reader.getState()).isSameAs(State.VALUE);
    assertThat(reader.readValue(String.class)).isEqualTo("bar");
    assertThat(reader.getState()).isSameAs(State.NAME);
    assertThat(reader.getState()).isSameAs(State.NAME);
    assertThat(reader.readName()).isEqualTo("instant");
    assertThat(reader.getState()).isSameAs(State.VALUE);
    assertThat(reader.readValueAsInstant()).isEqualTo(Instant.parse("1999-12-31T23:59:59.999999Z"));
    assertThat(reader.getState()).isSameAs(State.NAME);
    assertThat(reader.readName()).isEqualTo("list");
    assertThat(reader.getState()).isSameAs(State.START_ARRAY);
    assertThat(reader.readStartArray()).isTrue();
    assertThat(reader.getState()).isSameAs(State.VALUE);
    assertThat(reader.readValue(Byte.class)).isEqualTo(Byte.valueOf((byte) -1));
    assertThat(reader.getState()).isSameAs(State.VALUE);
    assertThat(reader.readValue(Short.class)).isEqualTo(Short.valueOf((short) -1));
    assertThat(reader.getState()).isSameAs(State.VALUE);
    assertThat(reader.readValue(Integer.class)).isEqualTo(-1);
    assertThat(reader.getState()).isSameAs(State.VALUE);
    assertThat(reader.readValue(Long.class)).isEqualTo(-42L);
    assertThat(reader.getState()).isSameAs(State.VALUE);
    assertThat(reader.readValue(Float.class)).isEqualTo(4.2F);
    assertThat(reader.getState()).isSameAs(State.VALUE);
    assertThat(reader.readValue(Double.class)).isEqualTo(42.42);
    assertThat(reader.getState()).isSameAs(State.VALUE);
    assertThat(reader.readValue(BigDecimal.class)).isEqualTo(new BigDecimal("0.12345678901234567890123456789"));
    assertThat(reader.getState()).isSameAs(State.VALUE);
    assertThat(reader.readValue(BigInteger.class))
        .isEqualTo(new BigInteger("1234567890123456789012345678901234567890"));
    assertThat(reader.getState()).isSameAs(State.END_ARRAY);
    assertThat(reader.readEnd()).isTrue();
    assertThat(reader.getState()).isSameAs(State.NAME);
    assertThat(reader.readName()).isEqualTo("empty");
    assertThat(reader.getState()).isSameAs(State.START_ARRAY);
    assertThat(reader.readStartArray()).isTrue();
    assertThat(reader.getState()).isSameAs(State.END_ARRAY);
    assertThat(reader.readEnd()).isTrue();
    assertThat(reader.getState()).isSameAs(State.END_OBJECT);
    assertThat(reader.isDone()).isFalse();
    assertThat(reader.readEnd()).isTrue();
    assertThat(reader.getState()).isSameAs(State.DONE);
    assertThat(reader.isDone()).isTrue();
  }

  /**
   * Test of {@link StructuredReader#readValue(boolean)}.
   */
  @Test
  public void testSkipValue() {

    Reader stringReader = new StringReader(JSON);
    StructuredReader reader = JsonMarshalling.of().reader(stringReader);
    reader.skipValue();
    assertThat(reader.isDone());
  }

  /**
   * Test of {@link StructuredReader#readValue(boolean)}.
   */
  @Test
  public void testReadValue() {

    Reader stringReader = new StringReader(JSON);
    StructuredReader reader = JsonMarshalling.of().reader(stringReader);
    Object value = reader.readValue(true);
    assertThat(value).isInstanceOf(Map.class);
  }

  /** Test of {@link StructuredWriter#writeValue(Object)} in atomic way (without start object). */
  @Test
  public void testWriteAtomicValue() {

    StringBuilder sb = new StringBuilder();
    StructuredWriter writer = JsonMarshalling.of().writer(sb);
    writer.writeValue(Long.valueOf(42));
    writer.close();
    assertThat(sb.toString()).isEqualTo("42");
  }

  /** Test of {@link StructuredReader#readValue()} in atomic way (without start object). */
  @Test
  public void testReadAtomicValue() {

    Reader stringReader = new StringReader("42");
    StructuredReader reader = JsonMarshalling.of().reader(stringReader);
    Object value = reader.readValue();
    assertThat(value).isEqualTo(Long.valueOf(42));
    assertThat(reader.getState()).isEqualTo(State.DONE);
  }

}
