package io.github.mmm.marshall.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import io.github.mmm.marshall.MarshallingConfig;
import io.github.mmm.marshall.StructuredFormat;
import io.github.mmm.marshall.StructuredReader;
import io.github.mmm.marshall.StructuredReader.State;
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

    return getExpectedData(getIndentation(), "\n");
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
   * @return the expected raw (unformatted) payload data.
   */
  protected String getExpectedDataRaw() {

    return getExpectedData("", "");
  }

  /**
   * @param indent the {@link #getIndentation() indentation}.
   * @param newline the {@link #getNewline() newline}.
   * @return the expected payload data.
   */
  protected abstract String getExpectedData(String indent, String newline);

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
  public void testReadWithoutIndentation() {

    StructuredReader reader = newReader(getExpectedDataRaw());
    readTestData(reader);
  }

  /**
   * Test of {@link StructuredWriter} without indendation.
   */
  @Test
  public void testWriteWithoutIndentation() {

    StructuredWriter writer = newWriter(MarshallingConfig.NO_INDENTATION);
    writeTestData(writer);
    assertThat(getActualData()).isEqualTo(getExpectedDataRaw());
  }

  /**
   * Test of {@link StructuredTextFormat#isText()}.
   */
  @Test
  public void testFormatFlags() {

    StructuredTextFormat format = getProvider().create();
    assertThat(format.isText()).isTrue();
    assertThat(format.isBinary()).isFalse();
  }

  /**
   * Test of {@link StructuredReader#readValue(boolean)}.
   */
  @SuppressWarnings("unchecked")
  @Test
  public void testReadValueRecursive() {

    StructuredReader reader = newReader();
    assertThat(reader.getState()).isSameAs(State.START_OBJECT);
    Object value = reader.readValue(true);
    assertThat(reader.getState()).isSameAs(State.DONE);
    assertThat(reader.isDone()).isTrue();

    assertThat(value).isInstanceOf(Map.class);
    Map<String, Object> map = (Map<String, Object>) value;
    assertThat(map.get(P1_NAME)).isEqualTo("bar");
    Object v7 = P3_VALUE7;
    Object v8 = P3_VALUE8;
    Object v9 = P3_VALUE9;
    if (reader.getFormat().getId().equals(StructuredFormat.ID_JSON)) {
      v7 = v7.toString();
      v8 = v8.toString();
      v9 = v9.toString();
    }
    List<Object> v10 = new ArrayList<>();
    Map<String, Object> object = new HashMap<>();
    object.put("key", "value");
    v10.add(object);
    assertThat((List<Object>) map.get("list")).containsExactlyInAnyOrder(i(P3_VALUE1), i(P3_VALUE2), i(P3_VALUE3),
        l(P3_VALUE4), d(P3_VALUE5), P3_VALUE6, v7, v8, v9, v10);
    assertThat((List<Object>) map.get("empty")).isEmpty();
  }

  private Double d(Number n) {

    return Double.valueOf(n.toString());
  }

  private Long l(Number n) {

    return Long.valueOf(n.longValue());
  }

  private Integer i(Number n) {

    return Integer.valueOf(n.intValue());
  }

}
