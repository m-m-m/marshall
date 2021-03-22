package io.github.mmm.marshall.test;

import org.junit.jupiter.api.Test;

import io.github.mmm.marshall.StructuredFormat;
import io.github.mmm.marshall.StructuredFormatFactory;
import io.github.mmm.marshall.StructuredFormatProvider;
import io.github.mmm.marshall.StructuredReader;
import io.github.mmm.marshall.StructuredTextFormat;

/**
 * Abstract test of {@link StructuredTextFormat} for {@link StructuredFormat#ID_YAML YAML}.
 */
public abstract class AbstractYamlFormatTest extends AbstractJsonBasedFormatTest {

  @Override
  protected String getExpectedData(String indent, String newline) {

    return getExpectedYamlData(indent, newline);
  }

  protected String getExpectedYamlData(String indent, String newline) {

    return getExpectedYamlData(indent, newline, "", "'", "", isSmartYamlIndent());
  }

  protected String getExpectedYamlData(String indent, String newline, String quote) {

    return getExpectedYamlData(indent, newline, quote, quote, quote, isSmartYamlIndent());
  }

  protected boolean isSmartYamlIndent() {

    return true;
  }

  protected String getExpectedYamlData(String indent, String newline, String quoteString, String quoteInstant,
      String quoteBigNum, boolean smartIndent) {

    String smartStart = "";
    String smartEnd = "";
    if (!smartIndent) {
      smartStart = newline + indent;
      smartEnd = newline;
    }
    String quoteJsonString = "";
    if (!quoteString.isEmpty()) {
      quoteJsonString = "\"";
    }
    return "foo: " + quoteString + "bar" + quoteString + newline //
        + "instant: " + quoteInstant + "1999-12-31T23:59:59.999999Z" + quoteInstant + newline //
        + "list:" + newline //
        + "- -1" + newline //
        + "- -1" + newline //
        + "- -1" + newline //
        + "- -12345678901" + newline //
        + "- 4.2" + newline //
        + "- 42.42" + newline //
        + "- " + quoteBigNum + "0.12345678901234567890123456789" + quoteBigNum + newline //
        + "- " + quoteBigNum + "1234567890123456789012345678901234567890" + quoteBigNum + newline //
        + "- " + quoteBigNum + "1.10" + quoteBigNum + newline //
        + "- - {" + smartStart + "key: " + quoteJsonString + "value" + quoteJsonString + smartEnd + "}" + newline //
        + "empty: []";
  }

  /**
   * Test with {@link StructuredFormat#ID_YAML}.
   */
  @Test
  public void testYamlFormat() {

    StructuredFormatProvider provider = StructuredFormatFactory.get().getProvider(StructuredFormat.ID_YAML);
    assertThat(provider.create()).isSameAs(getProvider().create());
  }

  /**
   * Test of {@link StructuredReader} using JSON as this is also YAML.
   */
  @Test
  public void testReadJson() {

    // without indentation
    StructuredReader reader = newReader(getExpectedJsonData("", "", true));
    readTestData(reader);

    // with indentation
    reader = newReader(getExpectedJsonData("  ", "\n", false));
    readTestData(reader);
  }

}
