/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.marshal.stax;

import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import io.github.mmm.marshall.StructuredReader;
import io.github.mmm.marshall.StructuredReader.State;
import io.github.mmm.marshall.StructuredWriter;
import io.github.mmm.marshall.stax.StaxMarshalling;
import io.github.mmm.marshall.stax.impl.StaxFormat;

/**
 * Test of {@link StaxMarshalling}.
 */
public class StaxMarshallingTest extends Assertions {

  private static final Byte VALUE_1 = Byte.valueOf((byte) -1);

  private static final Short VALUE_2 = Short.valueOf((short) -1);

  private static final Integer VALUE_3 = Integer.valueOf(-1);

  private static final Long VALUE_4 = Long.valueOf(-42);

  private static final Float VALUE_5 = Float.valueOf(4.2F);

  private static final Double VALUE_6 = Double.valueOf(42.42);

  private static final BigDecimal VALUE_7 = new BigDecimal("0.12345678901234567890123456789");

  private static final BigInteger VALUE_8 = new BigInteger("1234567890123456789012345678901234567890");

  private static final String XML_EXPECTED = "<?xml version=\"1.0\" ?>" //
      + "<o:json xmlns:a=\"array\" xmlns:o=\"object\">" //
      + "<foo s=\"bar\"/>" //
      + "<instant s=\"1999-12-31T23:59:59.999999Z\"/>" //
      + "<a:list>" //
      + "<i n=\"-1\"/>" //
      + "<i n=\"-1\"/>" //
      + "<i n=\"-1\"/>" //
      + "<i n=\"-42\"/>" //
      + "<i n=\"4.2\"/>" //
      + "<i n=\"42.42\"/>" //
      + "<i n=\"0.12345678901234567890123456789\"/>" //
      + "<i n=\"1234567890123456789012345678901234567890\"/>" //
      + "<a:i>" //
      + "<o:i>" //
      + "<key s=\"value\"/>" //
      + "</o:i>" //
      + "</a:i>" //
      + "</a:list>" //
      + "<a:empty>" //
      + "</a:empty>" //
      + "</o:json>";

  /**
   * Test {@link StaxFormat#writer(Appendable) writing as XML}.
   */
  @Test
  public void testWrite() {

    StringWriter stringWriter = new StringWriter();
    StructuredWriter writer = StaxMarshalling.of().writer(stringWriter);
    writer.writeStartObject();
    writer.writeName("foo", 1);
    writer.writeValue("bar");
    writer.writeName("instant", 2);
    writer.writeValueAsInstant(Instant.parse("1999-12-31T23:59:59.999999Z"));
    writer.writeName("list", 3);
    writer.writeStartArray();
    writer.writeValueAsByte(VALUE_1);
    writer.writeValueAsShort(VALUE_2);
    writer.writeValueAsInteger(VALUE_3);
    writer.writeValueAsLong(VALUE_4);
    writer.writeValueAsFloat(VALUE_5);
    writer.writeValueAsDouble(VALUE_6);
    writer.writeValueAsBigDecimal(VALUE_7);
    writer.writeValueAsBigInteger(VALUE_8);
    writer.writeStartArray();
    writer.writeStartObject();
    writer.writeName("key", 1);
    writer.writeValueAsString("value");
    writer.writeEnd();
    writer.writeEnd();
    writer.writeEnd();
    writer.writeName("empty");
    writer.writeStartArray();
    writer.writeEnd();
    writer.writeEnd();
    writer.close();
    assertThat(stringWriter.toString()).isEqualTo(XML_EXPECTED);
  }

  /**
   * Test {@link StaxFormat#reader(java.io.Reader) reading from XML}.
   */
  @Test
  public void testRead() {

    Reader stringReader = new StringReader(XML_EXPECTED);
    StructuredReader reader = StaxMarshalling.of().reader(stringReader);
    assertThat(reader.isDone()).isFalse();
    assertThat(reader.getState()).isSameAs(State.START_OBJECT);
    assertThat(reader.readStartObject()).isTrue();
    assertThat(reader.getState()).isSameAs(State.NAME);
    assertThat(reader.isName("foo", 1)).isTrue();
    assertThat(reader.getState()).isSameAs(State.VALUE);
    assertThat(reader.readValue(String.class)).isEqualTo("bar");
    assertThat(reader.getState()).isSameAs(State.NAME);
    assertThat(reader.isName("instant", 2)).isTrue();
    assertThat(reader.getState()).isSameAs(State.VALUE);
    assertThat(reader.readValueAsInstant()).isEqualTo(Instant.parse("1999-12-31T23:59:59.999999Z"));
    assertThat(reader.getState()).isSameAs(State.NAME);
    assertThat(reader.isName("list", 3)).isTrue();
    assertThat(reader.getState()).isSameAs(State.START_ARRAY);
    assertThat(reader.readStartArray()).isTrue();
    assertThat(reader.getState()).isSameAs(State.VALUE);
    assertThat(reader.readValue(Byte.class)).isEqualTo(Byte.valueOf(VALUE_1));
    assertThat(reader.getState()).isSameAs(State.VALUE);
    assertThat(reader.readValue(Short.class)).isEqualTo(Short.valueOf(VALUE_2));
    assertThat(reader.getState()).isSameAs(State.VALUE);
    assertThat(reader.readValue(Integer.class)).isEqualTo(VALUE_3);
    assertThat(reader.getState()).isSameAs(State.VALUE);
    assertThat(reader.readValue(Long.class)).isEqualTo(VALUE_4);
    assertThat(reader.getState()).isSameAs(State.VALUE);
    assertThat(reader.readValue(Float.class)).isEqualTo(VALUE_5);
    assertThat(reader.getState()).isSameAs(State.VALUE);
    assertThat(reader.readValue(Double.class)).isEqualTo(VALUE_6);
    assertThat(reader.getState()).isSameAs(State.VALUE);
    assertThat(reader.readValue(BigDecimal.class)).isEqualTo(VALUE_7);
    assertThat(reader.getState()).isSameAs(State.VALUE);
    assertThat(reader.readValue(BigInteger.class)).isEqualTo(VALUE_8);
    assertThat(reader.getState()).isSameAs(State.START_ARRAY);
    assertThat(reader.readStartArray()).isTrue();
    assertThat(reader.getState()).isSameAs(State.START_OBJECT);
    assertThat(reader.readStartObject()).isTrue();
    assertThat(reader.getState()).isSameAs(State.NAME);
    assertThat(reader.isName("key", 1)).isTrue();
    assertThat(reader.getState()).isSameAs(State.VALUE);
    assertThat(reader.readValue(String.class)).isEqualTo("value");
    assertThat(reader.getState()).isSameAs(State.END_OBJECT);
    assertThat(reader.readEnd()).isTrue();
    assertThat(reader.getState()).isSameAs(State.END_ARRAY);
    assertThat(reader.readEnd()).isTrue();
    assertThat(reader.getState()).isSameAs(State.END_ARRAY);
    assertThat(reader.readEnd()).isTrue();
    assertThat(reader.getState()).isSameAs(State.NAME);
    assertThat(reader.isName("empty", 4)).isTrue();
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
   * Test of {@link StructuredReader#skipValue()}.
   */
  @Test
  public void testSkip() {

    Reader stringReader = new StringReader(XML_EXPECTED);
    StructuredReader reader = StaxMarshalling.of().reader(stringReader);
    assertThat(reader.getState()).isSameAs(State.START_OBJECT);
    assertThat(reader.readStartObject()).isTrue();
    assertThat(reader.getState()).isSameAs(State.NAME);
    assertThat(reader.isName("foo", 1)).isTrue();
    assertThat(reader.getState()).isSameAs(State.VALUE);
    reader.skipValue();
    assertThat(reader.getState()).isSameAs(State.NAME);
    assertThat(reader.isName("instant", 2)).isTrue();
    assertThat(reader.getState()).isSameAs(State.VALUE);
    reader.skipValue();
    assertThat(reader.getState()).isSameAs(State.NAME);
    assertThat(reader.isName("list", 3)).isTrue();
    assertThat(reader.getState()).isSameAs(State.START_ARRAY);
    reader.skipValue();
    assertThat(reader.getState()).isSameAs(State.NAME);
    assertThat(reader.isName("empty", 4)).isTrue();
    assertThat(reader.getState()).isSameAs(State.START_ARRAY);
    reader.skipValue();
    assertThat(reader.getState()).isSameAs(State.END_OBJECT);
    assertThat(reader.isDone()).isFalse();
    assertThat(reader.readEnd()).isTrue();
    assertThat(reader.getState()).isSameAs(State.DONE);
    assertThat(reader.isDone()).isTrue();
  }

  /**
   * Test of {@link StructuredReader#readValue(boolean)}.
   */
  @SuppressWarnings("unchecked")
  @Test
  public void testReadValue() {

    Reader stringReader = new StringReader(XML_EXPECTED);
    StructuredReader reader = StaxMarshalling.of().reader(stringReader);
    assertThat(reader.getState()).isSameAs(State.START_OBJECT);
    Object value = reader.readValue(true);
    assertThat(reader.getState()).isSameAs(State.DONE);
    assertThat(reader.isDone()).isTrue();
    assertThat(value).isInstanceOf(Map.class);
    Map<String, Object> map = (Map<String, Object>) value;
    assertThat(map.get("foo")).isEqualTo("bar");
    List<Object> value9 = new ArrayList<>();
    Map<String, Object> object = new HashMap<>();
    object.put("key", "value");
    value9.add(object);
    assertThat((List<Object>) map.get("list")).containsExactlyInAnyOrder(VALUE_1.intValue(), VALUE_2.intValue(),
        VALUE_3.intValue(), VALUE_4.intValue(), new BigDecimal(VALUE_5.toString()), BigDecimal.valueOf(VALUE_6),
        VALUE_7, VALUE_8, value9);
    assertThat((List<Object>) map.get("empty")).isEmpty();
  }

  /**
   * Test {@link StaxFormat#reader(java.io.Reader) reading} a top-level array from XML.
   */
  @Test
  public void testReadRootArray() {

    String xml = "<?xml version=\"1.0\" ?>" //
        + "<a:json xmlns:a=\"array\" xmlns:o=\"object\">" //
        + "<i n=\"-1\"/>" //
        + "<i n=\"-1\"/>" //
        + "<i n=\"-1\"/>" //
        + "<i n=\"-42\"/>" //
        + "<i n=\"4.2\"/>" //
        + "<i n=\"42.42\"/>" //
        + "<i n=\"0.12345678901234567890123456789\"/>" //
        + "<i n=\"1234567890123456789012345678901234567890\"/>" //
        + "</a:json>";

    Reader stringReader = new StringReader(xml);
    StructuredReader reader = StaxMarshalling.of().reader(stringReader);
    assertThat(reader.isDone()).isFalse();
    assertThat(reader.readStartArray()).isTrue();
    assertThat(reader.readValue(Byte.class)).isEqualTo(Byte.valueOf(VALUE_1));
    assertThat(reader.readValue(Short.class)).isEqualTo(Short.valueOf(VALUE_2));
    assertThat(reader.readValue(Integer.class)).isEqualTo(VALUE_3);
    assertThat(reader.readValue(Long.class)).isEqualTo(-42L);
    assertThat(reader.readValue(Float.class)).isEqualTo(4.2F);
    assertThat(reader.readValue(Double.class)).isEqualTo(42.42);
    assertThat(reader.readValue(BigDecimal.class)).isEqualTo(VALUE_7);
    assertThat(reader.readValue(BigInteger.class)).isEqualTo(VALUE_8);
    assertThat(reader.readEnd()).isTrue();
    assertThat(reader.readEnd()).isTrue();
    assertThat(reader.isDone()).isTrue();
  }

}
