package io.github.mmm.marshall.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import io.github.mmm.marshall.MarshallingConfig;
import io.github.mmm.marshall.StructuredReader;
import io.github.mmm.marshall.StructuredState;
import io.github.mmm.marshall.StructuredTextFormat;
import io.github.mmm.marshall.StructuredTextFormatProvider;
import io.github.mmm.marshall.StructuredWriter;

/**
 * Abstract test of {@link StructuredTextFormat}.
 */
public abstract class StructuredTextFormatTest extends StructuredFormatTest {

  private StringBuilder sb;

  @Override
  protected String getExpectedData() {

    return getExpectedData(getIndentation(), getNewline());
  }

  /**
   * @return the indentation sequence.
   */
  protected String getIndentation() {

    return "  ";
  }

  /**
   * @return the line separator sequence.
   */
  protected String getNewline() {

    return "\n";
  }

  /**
   * @param read - {@code true} for data to read, {@code false} for expected written data.
   * @return the expected raw (unformatted) payload data.
   */
  protected String getExpectedDataRaw(boolean read) {

    return getExpectedData("", "");
  }

  /**
   * @param indent the {@link #getIndentation() indentation}.
   * @param newline the {@link #getNewline() newline}.
   * @return the expected payload data.
   */
  protected abstract String getExpectedData(String indent, String newline);

  @Override
  protected String getExpectedDataForAtomicLong() {

    return "42";
  }

  @Override
  protected abstract StructuredTextFormatProvider getProvider();

  @Override
  protected StructuredReader newReader(String data) {

    return getProvider().create().reader(data);
  }

  @Override
  protected StructuredWriter newWriter(MarshallingConfig config) {

    StructuredTextFormatProvider provider = getProvider();
    StructuredTextFormat format;
    if (config == null) {
      format = provider.create();
    } else {
      format = provider.create(config);
    }
    this.sb = new StringBuilder();
    return format.writer(this.sb);
  }

  @Override
  protected String getActualData() {

    return this.sb.toString();
  }

  /**
   * Test of {@link StructuredReader} without indendation.
   */
  @Test
  protected void testReadWithoutIndentation() {

    StructuredReader reader = newReader(getExpectedDataRaw(true));
    readTestData(reader);
  }

  /**
   * Test of {@link StructuredWriter} without indendation.
   */
  @Test
  protected void testWriteWithoutIndentation() {

    StructuredWriter writer = newWriter(MarshallingConfig.NO_INDENTATION);
    writeTestData(writer);
    assertThat(getActualData()).isEqualTo(getExpectedDataRaw(false));
  }

  /**
   * Test of {@link StructuredTextFormat#isText()}.
   */
  @Test
  protected void testFormatFlags() {

    StructuredTextFormat format = getProvider().create();
    assertThat(format.isText()).isTrue();
    assertThat(format.isBinary()).isFalse();
  }

  /**
   * Test of {@link StructuredReader#readValue(boolean)}.
   */
  @SuppressWarnings("unchecked")
  @Test
  protected void testReadValueRecursive() {

    StructuredReader reader = newReader();
    assertThat(reader.getState()).isSameAs(StructuredState.START_OBJECT);
    Object value = reader.readValue(true);
    assertThat(reader.getState()).isSameAs(StructuredState.DONE);
    assertThat(reader.isDone()).isTrue();

    assertThat(value).isInstanceOf(Map.class);
    Map<String, Object> map = (Map<String, Object>) value;
    assertThat(map.get(RootTestBean.PROPERTY_FOO)).isEqualTo("bar");
    Object v1 = getGenericValue(P3_LIST_VALUE1);
    Object v2 = getGenericValue(P3_LIST_VALUE2);
    Object v3 = getGenericValue(P3_LIST_VALUE3);
    Object v4 = getGenericValue(P3_LIST_VALUE4);
    Object v5 = getGenericValue(P3_LIST_VALUE5);
    Object v7 = getGenericValue(P3_LIST_VALUE7);
    Object v6 = getGenericValue(P3_LIST_VALUE6);
    Object v8 = getGenericValue(P3_LIST_VALUE8);
    Object v9 = getGenericValue(P3_LIST_VALUE9);
    List<Object> v10 = new ArrayList<>();
    Map<String, Object> object = new HashMap<>();
    object.put("key", "value");
    v10.add(object);
    assertThat((List<Object>) map.get("list")).containsExactlyInAnyOrder(v1, v2, v3, v4, v5, v6, v7, v8, v9, v10);
    assertThat((List<Object>) map.get("empty")).isEmpty();
  }

  /**
   * @param value the expected value.
   * @return the expected value or a generic form of it if the value is read in a generic way without type information
   *         available.
   */
  protected Object getGenericValue(Object value) {

    if ((value instanceof Byte) || (value instanceof Short)) {
      return Integer.valueOf(((Number) value).intValue());
    } else if (value instanceof Float) {
      return Double.valueOf(value.toString());
    }
    return value;
  }

}
