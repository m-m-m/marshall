/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.marshall.yaml;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

import org.assertj.core.data.Offset;
import org.junit.jupiter.api.Test;

import io.github.mmm.marshall.StandardFormat;
import io.github.mmm.marshall.StructuredReader;
import io.github.mmm.marshall.StructuredTextFormatProvider;
import io.github.mmm.marshall.StructuredWriter;
import io.github.mmm.marshall.test.AbstractYamlFormatTest;

/**
 * Test of {@link io.github.mmm.marshall.yaml.impl.YamlFormat} via {@link YamlFormatProvider}.
 */
class YamlFormatTest extends AbstractYamlFormatTest {

  private static final Offset<BigDecimal> EPSILON = Offset.offset(new BigDecimal("0.00000000000000001"));

  @Override
  protected StructuredTextFormatProvider getProvider() {

    return new YamlFormatProvider();
  }

  @Override
  protected String getExpectedDataRaw(boolean read) {

    if (read) {
      return getExpectedJsonData("", "", true);
    }
    return getExpectedYamlData("  ", getNewline(), "'");
  }

  @Override
  protected String getExpectedData() {

    return getExpectedYamlData(getIndentation(), getNewline(), "'");
  }

  @Override
  protected boolean isSmartJsonIndent() {

    return true;
  }

  @Override
  protected boolean isSmartYamlIndent() {

    return false;
  }

  @Override
  protected void checkBigDecimal(BigDecimal actual, BigDecimal expected) {

    // snakeyaml has a design flaw, you can not reliably write big decimals and read them back:
    // strings are only quoted if they contain special chars like colon, hence on reading
    // snakeyaml will guess the type and decides for double instead of BigDecimal
    assertThat(actual).isCloseTo(expected, EPSILON);
  }

  @Override
  protected Object getGenericValue(Object value) {

    if ((value instanceof BigDecimal) || (value instanceof BigInteger)) {
      return value.toString();
    }
    return super.getGenericValue(value);
  }

  /**
   * Test of {@link StructuredWriter#write(StructuredReader)} to convert YAML to JSON.
   */
  @Test
  void testYaml2Json() {

    // arrange
    String yaml = getExpectedData();
    StructuredReader reader = newReader(yaml);
    StringBuilder sb = new StringBuilder();
    StructuredWriter writer = StandardFormat.json().writer(sb);
    // act
    writer.write(reader);
    // assert
    String json = sb.toString();
    // System.out.println(yaml);
    // System.out.println("--- was converted to ---");
    // System.out.println(json);
    assertThat(json).isEqualTo(getExpectedJsonData("  ", "\n", true));
  }

  /**
   * Test reading a YAML literal block scalar ({@code |}) with default (clip) chomping.
   */
  @Test
  public void testReadBlockScalarLiteralClip() {

    // given
    String yaml = "text: |\n" //
        + "  line1\n" //
        + "  line2\n";
    // when
    StructuredReader reader = newReader(yaml);
    Map<String, Object> map = readMap(reader);
    // then
    assertThat(map.get("text")).isEqualTo("line1\nline2\n");
  }

  /**
   * Test reading a YAML literal block scalar ({@code |-}) with strip chomping (no trailing newline).
   */
  @Test
  public void testReadBlockScalarLiteralStrip() {

    // given
    String yaml = "text: |-\n" //
        + "  line1\n" //
        + "  line2\n";
    // when
    StructuredReader reader = newReader(yaml);
    Map<String, Object> map = readMap(reader);
    // then
    assertThat(map.get("text")).isEqualTo("line1\nline2");
  }

  /**
   * Test reading a YAML literal block scalar ({@code |+}) with keep chomping (preserve all trailing newlines).
   */
  @Test
  public void testReadBlockScalarLiteralKeep() {

    // given
    String yaml = "text: |+\n" //
        + "  line1\n" //
        + "  line2\n" //
        + "\n" //
        + "other: value\n";
    // when
    StructuredReader reader = newReader(yaml);
    Map<String, Object> map = readMap(reader);
    // then
    assertThat(map.get("text")).isEqualTo("line1\nline2\n\n");
    assertThat(map.get("other")).isEqualTo("value");
  }

  /**
   * Test reading a YAML folded block scalar ({@code >}) that folds newlines to spaces.
   */
  @Test
  public void testReadBlockScalarFolded() {

    // given
    String yaml = "text: >\n" //
        + "  line1\n" //
        + "  line2\n";
    // when
    StructuredReader reader = newReader(yaml);
    Map<String, Object> map = readMap(reader);
    // then
    assertThat(map.get("text")).isEqualTo("line1 line2\n");
  }

  /**
   * Test reading a YAML flow mapping (e.g. {@code {a: 1, b: 2}}) nested in block context.
   */
  @Test
  public void testReadFlowMapping() {

    // given
    String yaml = "map: {a: 1, b: 2}\n";
    // when
    StructuredReader reader = newReader(yaml);
    Map<String, Object> map = readMap(reader);
    // then
    @SuppressWarnings("unchecked")
    Map<String, Object> inner = (Map<String, Object>) map.get("map");
    assertThat(inner.get("a")).isEqualTo(Integer.valueOf(1));
    assertThat(inner.get("b")).isEqualTo(Integer.valueOf(2));
  }

  /**
   * Test reading a YAML flow sequence (e.g. {@code [1, 2, 3]}) nested in block context.
   */
  @Test
  @SuppressWarnings("unchecked")
  public void testReadFlowSequence() {

    // given
    String yaml = "list: [1, 2, 3]\n";
    // when
    StructuredReader reader = newReader(yaml);
    Map<String, Object> map = readMap(reader);
    // then
    assertThat((List<Integer>) map.get("list")).containsExactly(Integer.valueOf(1), Integer.valueOf(2),
        Integer.valueOf(3));
  }

  /**
   * Test plain, double-quoted, and single-quoted scalars in a YAML flow sequence.
   */
  @Test
  @SuppressWarnings("unchecked")
  public void testReadFlowScalars() {

    // given
    String yaml = "values: [plain, \"quoted, value\", 'single: value']\n";
    // when
    StructuredReader reader = newReader(yaml);
    Map<String, Object> map = readMap(reader);
    // then
    assertThat((List<String>) map.get("values")).containsExactly("plain", "quoted, value", "single: value");
  }

  /**
   * Test multiline quoted flow scalars with different continuation indentation.
   */
  @Test
  @SuppressWarnings("unchecked")
  public void testReadMultilineQuotedFlowScalars() {

    // given
    String yaml = "values: [\"first\n" //
        + "       second\", 'third \n" //
        + "second']\n";
    // when
    StructuredReader reader = newReader(yaml);
    Map<String, Object> map = readMap(reader);
    // then
    assertThat((List<String>) map.get("values")).containsExactly("first second", "third second");
  }

  /**
   * Test reading a YAML anchor ({@code &}) and referencing it via an alias ({@code *}).
   */
  @Test
  public void testReadAnchorAndAlias() {

    // given
    String yaml = "a: &anchor value\n" //
        + "b: *anchor\n";
    // when
    StructuredReader reader = newReader(yaml);
    Map<String, Object> map = readMap(reader);
    // then
    assertThat(map.get("a")).isEqualTo("value");
    assertThat(map.get("b")).isEqualTo("value");
  }

  /**
   * Test reading a multi-line plain scalar that gets folded into a single line separated by spaces.
   */
  @Test
  public void testReadMultilinePlainScalar() {

    // given
    String yaml = "text: this is\n" //
        + "  a folded\n" //
        + "  plain scalar\n";
    // when
    StructuredReader reader = newReader(yaml);
    Map<String, Object> map = readMap(reader);
    // then
    assertThat(map.get("text")).isEqualTo("this is a folded plain scalar");
  }

  /**
   * Test reading a double quoted string with escape sequences.
   */
  @Test
  public void testReadDoubleQuotedEscapes() {

    // given
    String yaml = "text: \"line1\\nline2\\ttab\"\n";
    // when
    StructuredReader reader = newReader(yaml);
    Map<String, Object> map = readMap(reader);
    // then
    assertThat(map.get("text")).isEqualTo("line1\nline2\ttab");
  }

  /**
   * Test reading a single quoted string with an escaped single quote ({@code ''}).
   */
  @Test
  public void testReadSingleQuotedEscapes() {

    // given
    String yaml = "text: 'it''s here'\n";
    // when
    StructuredReader reader = newReader(yaml);
    Map<String, Object> map = readMap(reader);
    // then
    assertThat(map.get("text")).isEqualTo("it's here");
  }

  @SuppressWarnings("unchecked")
  private Map<String, Object> readMap(StructuredReader reader) {

    return (Map<String, Object>) reader.readValue(true);
  }

}
