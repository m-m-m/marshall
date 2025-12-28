package io.github.mmm.marshall.test;

import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import io.github.mmm.marshall.MarshallingConfig;
import io.github.mmm.marshall.StructuredFormat;
import io.github.mmm.marshall.StructuredFormatProvider;
import io.github.mmm.marshall.StructuredReader;
import io.github.mmm.marshall.StructuredState;
import io.github.mmm.marshall.StructuredWriter;

/**
 * Abstract test for {@link StructuredFormat}.
 */
@SuppressWarnings("javadoc")
public abstract class StructuredFormatTest extends Assertions {

  protected static final Long ATOMIC_LONG_VALUE = Long.valueOf(42);

  protected static final String COMMENT_HEADER = "header comment";

  protected static final String COMMENT_P1 = "foo starts here\n--- second line of comment.";

  protected static final String P1_FOO_VALUE = "bar";

  protected static final Instant P2_INSTANT_VALUE = Instant.parse("1999-12-31T23:59:59.999999Z");

  protected static final Byte P3_LIST_VALUE1 = Byte.valueOf((byte) -1);

  protected static final Short P3_LIST_VALUE2 = Short.valueOf((short) -1);

  protected static final Integer P3_LIST_VALUE3 = Integer.valueOf(-1);

  protected static final Long P3_LIST_VALUE4 = Long.valueOf(-12345678901L);

  protected static final Float P3_LIST_VALUE5 = Float.valueOf(4.2F);

  protected static final Double P3_LIST_VALUE6 = Double.valueOf(42.42);

  protected static final BigDecimal P3_LIST_VALUE7 = new BigDecimal("0.12345678901234567890123456789");

  protected static final BigInteger P3_LIST_VALUE8 = new BigInteger("1234567890123456789012345678901234567890");

  protected static final BigDecimal P3_LIST_VALUE9 = new BigDecimal("1.10");

  protected static final String COMMENT_P3_VALUE10 = "an object inside an array within an array";

  protected static final String P3_LIST_VALUE10_ARRAY_P1_KEY_VALUE = "value";

  private static final String[] PROPERTIES = { RootTestBean.PROPERTY_FOO, RootTestBean.PROPERTY_INSTANT,
  RootTestBean.PROPERTY_LIST, RootTestBean.PROPERTY_EMPTY };

  /**
   * @return the payload data expected by this test in the {@link #getProvider() format} to test.
   * @see #testWrite()
   * @see #testRead()
   */
  protected abstract String getExpectedData();

  /**
   * @return the actual payload data after being written to {@link #newWriter()}.
   */
  protected abstract String getActualData();

  /**
   * @return the {@link StructuredFormatProvider} to test.
   */
  protected abstract StructuredFormatProvider getProvider();

  /**
   * @return a new {@link StringWriter} for the {@link #getProvider() format} to test.
   * @see #getActualData()
   */
  protected StructuredWriter newWriter() {

    return newWriter(null);
  }

  /**
   * @param config the {@link MarshallingConfig} to use.
   * @return a new {@link StringWriter} for the {@link #getProvider() format} to test.
   * @see #getActualData()
   */
  protected abstract StructuredWriter newWriter(MarshallingConfig config);

  /**
   * @return a new {@link StructuredReader} reading the {@link #getExpectedData() expected data}.
   */
  protected StructuredReader newReader() {

    return newReader(getExpectedData());
  }

  /**
   * @param data the data to read.
   * @return a new {@link StructuredReader} reading the given data.
   */
  protected abstract StructuredReader newReader(String data);

  /**
   * Test of {@link StructuredWriter}.
   */
  @Test
  protected void testWrite() {

    StructuredWriter writer = newWriter();
    writeTestData(writer);
    assertThat(getActualData()).isEqualTo(getExpectedData());
  }

  /**
   * @param writer the {@link StringWriter} where to write the test-data to.
   */
  protected void writeTestData(StructuredWriter writer) {

    // arrange (dummy objects)
    RootTestBean root = createRoot();
    ChildTestBean child = new ChildTestBean();

    assertThat(writer.getState()).isSameAs(StructuredState.NULL);
    // act
    writer.writeComment(COMMENT_HEADER);
    writer.writeStartObject(root);
    assertThat(writer.getState()).isSameAs(StructuredState.START_OBJECT);
    writer.writeComment(COMMENT_P1);
    writer.writeName(RootTestBean.PROPERTY_FOO);
    assertThat(writer.getState()).isSameAs(StructuredState.NAME);
    writer.writeValue(root.getFoo());
    assertThat(writer.getState()).isSameAs(StructuredState.VALUE);
    writer.writeName(RootTestBean.PROPERTY_INSTANT);
    assertThat(writer.getState()).isSameAs(StructuredState.NAME);
    writer.writeValueAsInstant(root.getInstant());
    assertThat(writer.getState()).isSameAs(StructuredState.VALUE);
    writer.writeName(RootTestBean.PROPERTY_LIST);
    assertThat(writer.getState()).isSameAs(StructuredState.NAME);
    writer.writeStartArray();
    assertThat(writer.getState()).isSameAs(StructuredState.START_ARRAY);
    writer.writeValueAsByte(P3_LIST_VALUE1);
    assertThat(writer.getState()).isSameAs(StructuredState.VALUE);
    writer.writeValueAsShort(P3_LIST_VALUE2);
    assertThat(writer.getState()).isSameAs(StructuredState.VALUE);
    writer.writeValueAsInteger(P3_LIST_VALUE3);
    assertThat(writer.getState()).isSameAs(StructuredState.VALUE);
    writer.writeValueAsLong(P3_LIST_VALUE4);
    assertThat(writer.getState()).isSameAs(StructuredState.VALUE);
    writer.writeValueAsFloat(P3_LIST_VALUE5);
    assertThat(writer.getState()).isSameAs(StructuredState.VALUE);
    writer.writeValueAsDouble(P3_LIST_VALUE6);
    assertThat(writer.getState()).isSameAs(StructuredState.VALUE);
    writer.writeValueAsBigDecimal(P3_LIST_VALUE7);
    assertThat(writer.getState()).isSameAs(StructuredState.VALUE);
    writer.writeValueAsBigInteger(P3_LIST_VALUE8);
    assertThat(writer.getState()).isSameAs(StructuredState.VALUE);
    writer.writeValueAsBigDecimal(P3_LIST_VALUE9);
    assertThat(writer.getState()).isSameAs(StructuredState.VALUE);
    writer.writeStartArray();
    assertThat(writer.getState()).isSameAs(StructuredState.START_ARRAY);
    writer.writeStartObject(child);
    assertThat(writer.getState()).isSameAs(StructuredState.START_OBJECT);
    writer.writeComment(COMMENT_P3_VALUE10);
    writer.writeName(ChildTestBean.PROPERTY_KEY);
    assertThat(writer.getState()).isSameAs(StructuredState.NAME);
    writer.writeValueAsString(P3_LIST_VALUE10_ARRAY_P1_KEY_VALUE);
    assertThat(writer.getState()).isSameAs(StructuredState.VALUE);
    writer.writeEnd(); // end child object
    assertThat(writer.getState()).isSameAs(StructuredState.END_OBJECT);
    writer.writeEnd(); // end array (value in list)
    assertThat(writer.getState()).isSameAs(StructuredState.END_ARRAY);
    writer.writeEnd(); // end array (list)
    assertThat(writer.getState()).isSameAs(StructuredState.END_ARRAY);
    writer.writeName(RootTestBean.PROPERTY_EMPTY);
    assertThat(writer.getState()).isSameAs(StructuredState.NAME);
    writer.writeStartArray();
    assertThat(writer.getState()).isSameAs(StructuredState.START_ARRAY);
    writer.writeEnd(); // end array
    assertThat(writer.getState()).isSameAs(StructuredState.END_ARRAY);
    writer.writeEnd(); // end root object
    assertThat(writer.getState()).isSameAs(StructuredState.END_OBJECT);
    writer.close();
    assertThat(writer.getState()).isSameAs(StructuredState.DONE);
  }

  /**
   * Test of {@link StructuredReader}.
   */
  @Test
  protected void testRead() {

    StructuredReader reader = newReader();
    readTestData(reader);
  }

  protected RootTestBean createRoot() {

    RootTestBean root = new RootTestBean();
    root.setFoo(P1_FOO_VALUE);
    root.setInstant(P2_INSTANT_VALUE);
    List<Object> list = new ArrayList<>();
    list.add(P3_LIST_VALUE1);
    list.add(P3_LIST_VALUE2);
    list.add(P3_LIST_VALUE3);
    list.add(P3_LIST_VALUE4);
    list.add(P3_LIST_VALUE5);
    list.add(P3_LIST_VALUE6);
    list.add(P3_LIST_VALUE7);
    list.add(P3_LIST_VALUE8);
    list.add(P3_LIST_VALUE9);
    List<Object> childList = new ArrayList<>();
    ChildTestBean child = new ChildTestBean();
    child.setKey(P3_LIST_VALUE10_ARRAY_P1_KEY_VALUE);
    childList.add(child);
    list.add(childList);
    root.setList(list);
    return root;
  }

  /**
   * @param reader the {@link StructuredReader} where to read the test-data from.
   */
  protected void readTestData(StructuredReader reader) {

    // arrange (dummy objects)
    RootTestBean root = createRoot();
    ChildTestBean child = new ChildTestBean();

    // act
    assertThat(reader.isDone()).isFalse();

    checkState(reader, StructuredState.START_OBJECT);
    assertThat(reader.readStartObject(root)).isTrue();
    checkState(reader, StructuredState.NAME);
    checkName(reader, RootTestBean.PROPERTY_FOO);
    checkState(reader, StructuredState.VALUE);
    assertThat(reader.readValue(String.class)).isEqualTo(root.getFoo());
    checkState(reader, StructuredState.NAME);
    checkName(reader, RootTestBean.PROPERTY_INSTANT);
    checkState(reader, StructuredState.VALUE);
    assertThat(reader.readValueAsInstant()).isEqualTo(root.getInstant());
    checkState(reader, StructuredState.NAME);
    checkName(reader, RootTestBean.PROPERTY_LIST);
    checkState(reader, StructuredState.START_ARRAY);
    assertThat(reader.readStartArray()).isTrue();
    checkState(reader, StructuredState.VALUE);
    assertThat(reader.readValue(Byte.class)).isEqualTo(P3_LIST_VALUE1);
    checkState(reader, StructuredState.VALUE);
    assertThat(reader.readValue(Short.class)).isEqualTo(P3_LIST_VALUE2);
    checkState(reader, StructuredState.VALUE);
    assertThat(reader.readValue(Integer.class)).isEqualTo(P3_LIST_VALUE3);
    checkState(reader, StructuredState.VALUE);
    assertThat(reader.readValue(Long.class)).isEqualTo(P3_LIST_VALUE4);
    checkState(reader, StructuredState.VALUE);
    assertThat(reader.readValue(Float.class)).isEqualTo(P3_LIST_VALUE5);
    checkState(reader, StructuredState.VALUE);
    assertThat(reader.readValue(Double.class)).isEqualTo(P3_LIST_VALUE6);
    checkState(reader, StructuredState.VALUE);
    checkBigDecimal(reader.readValue(BigDecimal.class), P3_LIST_VALUE7);
    checkState(reader, StructuredState.VALUE);
    assertThat(reader.readValue(BigInteger.class)).isEqualTo(P3_LIST_VALUE8);
    checkState(reader, StructuredState.VALUE);
    checkBigDecimal(reader.readValueAsBigDecimal(), P3_LIST_VALUE9);
    checkState(reader, StructuredState.START_ARRAY);
    assertThat(reader.readStartArray()).isTrue();
    checkState(reader, StructuredState.START_OBJECT);
    assertThat(reader.readStartObject(child)).isTrue();
    checkState(reader, StructuredState.NAME);
    checkName(reader, ChildTestBean.PROPERTY_KEY);
    checkState(reader, StructuredState.VALUE);
    assertThat(reader.readValueAsString()).isEqualTo(P3_LIST_VALUE10_ARRAY_P1_KEY_VALUE);
    checkState(reader, StructuredState.END_OBJECT);
    assertThat(reader.readEndObject()).isTrue();
    assertThat(reader.readEndArray()).isTrue();
    checkState(reader, StructuredState.END_ARRAY);
    assertThat(reader.readEndArray()).isTrue();
    if (reader.isName(RootTestBean.PROPERTY_EMPTY)) {
      checkState(reader, StructuredState.START_ARRAY);
      assertThat(reader.readStartArray()).isTrue();
      checkState(reader, StructuredState.END_ARRAY);
      assertThat(reader.readEndArray()).isTrue();
    } else {
      // binary formats may omit property with empty array value
      assertThat(reader.getFormat().isBinary());
    }
    checkState(reader, StructuredState.END_OBJECT);
    assertThat(reader.isDone()).isFalse();
    assertThat(reader.readEndObject()).isTrue();
    assertThat(reader.getState()).isSameAs(StructuredState.DONE);
    assertThat(reader.isDone()).isTrue();
  }

  protected void checkBigDecimal(BigDecimal actual, BigDecimal expected) {

    assertThat(actual).isEqualTo(expected);
  }

  protected void checkName(StructuredReader reader, String name) {

    if (reader.isName(name, true)) {
      return;
    } else {
      assertThat(reader.getName()).isEqualTo(name);
    }
  }

  protected void checkState(StructuredReader reader, StructuredState state) {

    assertThat(reader.getState()).isSameAs(state);
  }

  /**
   * Test of {@link StructuredReader#skipValue()}.
   */
  @Test
  protected void testSkipValueAll() {

    StructuredReader reader = newReader();
    assertThat(reader.isDone()).isFalse();
    reader.skipValue();
    assertThat(reader.isDone()).isTrue();
  }

  /**
   * Test of {@link StructuredReader#skipValue()}.
   */
  @Test
  protected void testSkipValuePerProperty() {

    // arrange (dummy object)
    RootTestBean root = new RootTestBean();

    // act
    StructuredReader reader = newReader();
    assertThat(reader.readStartObject(root)).isTrue();
    for (String name : PROPERTIES) {
      assertThat(reader.isDone()).isFalse();
      if (reader.getState() == StructuredState.NAME) {
        assertThat(reader.isName(name)).as(name).isTrue();
        reader.skipValue();
      } else {
        assertThat(name).isEqualTo(RootTestBean.PROPERTY_EMPTY);
        assertThat(reader.getFormat().isBinary()).isTrue(); // only binary formats may omit empty array property
      }
    }
    assertThat(reader.readEnd()).isTrue();
    assertThat(reader.isDone()).isTrue();
  }

  /**
   * @return the payload data expected for the atomic long value 42 in the {@link #getProvider() format} to test.
   */
  protected abstract String getExpectedDataForAtomicLong();

  /** Test of {@link StructuredWriter#writeValue(Object)} in atomic way (without start object). */
  @Test
  protected void testWriteAtomicLong() {

    StructuredWriter writer = newWriter(MarshallingConfig.NO_INDENTATION);
    writer.writeValue(ATOMIC_LONG_VALUE);
    writer.close();
    assertThat(getActualData()).isEqualTo(getExpectedDataForAtomicLong());
  }

  /** Test of {@link StructuredReader#readValue()} in atomic way (without start object). */
  @Test
  protected void testReadAtomicLong() {

    StructuredReader reader = newReader(getExpectedDataForAtomicLong());
    checkState(reader, StructuredState.VALUE);
    assertThat(reader.readValueAsLong()).isEqualTo(ATOMIC_LONG_VALUE);
    assertThat(reader.getState()).isEqualTo(StructuredState.DONE);
  }

}
