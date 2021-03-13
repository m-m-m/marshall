package io.github.mmm.marshall.test;

import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import io.github.mmm.marshall.MarshallingConfig;
import io.github.mmm.marshall.StructuredFormat;
import io.github.mmm.marshall.StructuredFormatProvider;
import io.github.mmm.marshall.StructuredReader;
import io.github.mmm.marshall.StructuredReader.State;
import io.github.mmm.marshall.StructuredWriter;

/**
 * Abstract test for {@link StructuredFormat}.
 */
@SuppressWarnings("javadoc")
public abstract class StructuredFormatTest extends Assertions {

  protected static final Long ATOMIC_LONG_VALUE = Long.valueOf(42);

  protected static final int P1_ID = 1;

  protected static final String P1_NAME = "foo";

  protected static final String P1_VALUE = "bar";

  protected static final int P2_ID = 2;

  protected static final String P2_NAME = "instant";

  protected static final Instant P2_VALUE = Instant.parse("1999-12-31T23:59:59.999999Z");

  protected static final int P3_ID = 3;

  protected static final String P3_NAME = "list";

  protected static final Byte P3_VALUE1 = Byte.valueOf((byte) -1);

  protected static final Short P3_VALUE2 = Short.valueOf((short) -1);

  protected static final Integer P3_VALUE3 = Integer.valueOf(-1);

  protected static final Long P3_VALUE4 = Long.valueOf(-12345678901L);

  protected static final Float P3_VALUE5 = Float.valueOf(4.2F);

  protected static final Double P3_VALUE6 = Double.valueOf(42.42);

  protected static final BigDecimal P3_VALUE7 = new BigDecimal("0.12345678901234567890123456789");

  protected static final BigInteger P3_VALUE8 = new BigInteger("1234567890123456789012345678901234567890");

  protected static final BigDecimal P3_VALUE9 = new BigDecimal("1.10");

  protected static final int P3_VALUE10_ARRAY_P1_ID = 1;

  protected static final String P3_VALUE10_ARRAY_P1_NAME = "key";

  protected static final String P3_VALUE10_ARRAY_P1_VALUE = "value";

  protected static final int P4_ID = 4;

  protected static final String P4_NAME = "empty";

  private static final String[] PROPERTIES = { P1_NAME, P2_NAME, P3_NAME, P4_NAME };

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
  public void testWrite() {

    StructuredWriter writer = newWriter();
    writeTestData(writer);
    assertThat(getActualData()).isEqualTo(getExpectedData());
  }

  /**
   * @param writer the {@link StringWriter} where to write the test-data to.
   */
  protected void writeTestData(StructuredWriter writer) {

    writer.writeStartObject();
    writer.writeName(P1_NAME, P1_ID);
    writer.writeValue(P1_VALUE);
    writer.writeName(P2_NAME, P2_ID);
    writer.writeValueAsInstant(P2_VALUE);
    writer.writeName(P3_NAME, P3_ID);
    writer.writeStartArray();
    writer.writeValueAsByte(P3_VALUE1);
    writer.writeValueAsShort(P3_VALUE2);
    writer.writeValueAsInteger(P3_VALUE3);
    writer.writeValueAsLong(P3_VALUE4);
    writer.writeValueAsFloat(P3_VALUE5);
    writer.writeValueAsDouble(P3_VALUE6);
    writer.writeValueAsBigDecimal(P3_VALUE7);
    writer.writeValueAsBigInteger(P3_VALUE8);
    writer.writeValueAsBigDecimal(P3_VALUE9);
    writer.writeStartArray();
    writer.writeStartObject();
    writer.writeName(P3_VALUE10_ARRAY_P1_NAME, P3_VALUE10_ARRAY_P1_ID);
    writer.writeValueAsString(P3_VALUE10_ARRAY_P1_VALUE);
    writer.writeEnd();
    writer.writeEnd();
    writer.writeEnd();
    writer.writeName(P4_NAME, P4_ID);
    writer.writeStartArray();
    writer.writeEnd();
    writer.writeEnd();
    writer.close();
  }

  /**
   * Test of {@link StructuredReader}.
   */
  @Test
  public void testRead() {

    StructuredReader reader = newReader();
    readTestData(reader);
  }

  /**
   * @param reader the {@link StructuredReader} where to read the test-data from.
   */
  protected void readTestData(StructuredReader reader) {

    assertThat(reader.isDone()).isFalse();

    checkState(reader, State.START_OBJECT);
    assertThat(reader.readStartObject()).isTrue();
    checkState(reader, State.NAME);
    assertThat(reader.isName(P1_NAME, P1_ID)).isTrue();
    checkState(reader, State.VALUE);
    assertThat(reader.readValue(String.class)).isEqualTo(P1_VALUE);
    checkState(reader, State.NAME);
    assertThat(reader.isName(P2_NAME, P2_ID)).isTrue();
    checkState(reader, State.VALUE);
    assertThat(reader.readValueAsInstant()).isEqualTo(P2_VALUE);
    checkState(reader, State.NAME);
    assertThat(reader.isName(P3_NAME, P3_ID)).isTrue();
    checkState(reader, State.START_ARRAY);
    assertThat(reader.readStartArray()).isTrue();
    checkState(reader, State.VALUE);
    assertThat(reader.readValue(Byte.class)).isEqualTo(P3_VALUE1);
    checkState(reader, State.VALUE);
    assertThat(reader.readValue(Short.class)).isEqualTo(P3_VALUE2);
    checkState(reader, State.VALUE);
    assertThat(reader.readValue(Integer.class)).isEqualTo(P3_VALUE3);
    checkState(reader, State.VALUE);
    assertThat(reader.readValue(Long.class)).isEqualTo(P3_VALUE4);
    checkState(reader, State.VALUE);
    assertThat(reader.readValue(Float.class)).isEqualTo(P3_VALUE5);
    checkState(reader, State.VALUE);
    assertThat(reader.readValue(Double.class)).isEqualTo(P3_VALUE6);
    checkState(reader, State.VALUE);
    assertThat(reader.readValue(BigDecimal.class)).isEqualTo(P3_VALUE7);
    checkState(reader, State.VALUE);
    assertThat(reader.readValue(BigInteger.class)).isEqualTo(P3_VALUE8);
    checkState(reader, State.VALUE);
    assertThat(reader.readValueAsBigDecimal()).isEqualTo(P3_VALUE9);
    checkState(reader, State.START_ARRAY);
    assertThat(reader.readStartArray()).isTrue();
    checkState(reader, State.START_OBJECT);
    assertThat(reader.readStartObject()).isTrue();
    checkState(reader, State.NAME);
    assertThat(reader.isName(P3_VALUE10_ARRAY_P1_NAME, 1)).isTrue();
    checkState(reader, State.VALUE);
    assertThat(reader.readValue(String.class)).isEqualTo(P3_VALUE10_ARRAY_P1_VALUE);
    checkState(reader, State.END_OBJECT);
    assertThat(reader.readEnd()).isTrue();
    checkState(reader, State.END_ARRAY);
    assertThat(reader.readEnd()).isTrue();
    checkState(reader, State.END_ARRAY);
    assertThat(reader.readEnd()).isTrue();
    checkState(reader, State.NAME);
    assertThat(reader.isName(P4_NAME, P4_ID)).isTrue();
    checkState(reader, State.START_ARRAY);
    assertThat(reader.readStartArray()).isTrue();
    checkState(reader, State.END_ARRAY);
    assertThat(reader.readEnd()).isTrue();
    checkState(reader, State.END_OBJECT);
    assertThat(reader.isDone()).isFalse();
    assertThat(reader.readEnd()).isTrue();
    assertThat(reader.getState()).isSameAs(State.DONE);
    assertThat(reader.isDone()).isTrue();
  }

  protected void checkState(StructuredReader reader, State state) {

    assertThat(reader.getState()).isSameAs(state);
  }

  /**
   * Test of {@link StructuredReader#skipValue()}.
   */
  @Test
  public void testSkipValueAll() {

    StructuredReader reader = newReader();
    assertThat(reader.isDone()).isFalse();
    reader.skipValue();
    assertThat(reader.isDone()).isTrue();
  }

  /**
   * Test of {@link StructuredReader#skipValue()}.
   */
  @Test
  public void testSkipValuePerProperty() {

    StructuredReader reader = newReader();
    assertThat(reader.readStartObject()).isTrue();
    int i = 1;
    for (String name : PROPERTIES) {
      assertThat(reader.isDone()).isFalse();
      if (reader.getFormat().isIdBased()) {
        assertThat(reader.readId()).isEqualTo(i++);
      } else {
        assertThat(reader.readName()).isEqualTo(name);
      }
      reader.skipValue();
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
  public void testWriteAtomicLong() {

    StructuredWriter writer = newWriter(MarshallingConfig.NO_INDENTATION);
    writer.writeValue(ATOMIC_LONG_VALUE);
    writer.close();
    assertThat(getActualData()).isEqualTo(getExpectedDataForAtomicLong());
  }

  /** Test of {@link StructuredReader#readValue()} in atomic way (without start object). */
  @Test
  public void testReadAtomicLong() {

    StructuredReader reader = newReader(getExpectedDataForAtomicLong());
    assertThat(reader.readValueAsLong()).isEqualTo(ATOMIC_LONG_VALUE);
    assertThat(reader.getState()).isEqualTo(State.DONE);
  }

}
