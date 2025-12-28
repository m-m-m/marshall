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

  /**
   * @param indent the indentation (e.g. " ").
   * @param newline the newline (e.g. "\n").
   * @return the expected YAML result after marshalling or to use as input for unmarshalling when reading the expected
   *         structure.
   */
  protected String getExpectedYamlData(String indent, String newline) {

    return getExpectedYamlData(indent, newline, "", "'", "", isSmartYamlIndent(), isSupportingComments());
  }

  /**
   * @param indent the indentation (e.g. " ").
   * @param newline the newline (e.g. "\n").
   * @param quote the quote non trivial values (e.g. "'").
   * @return the expected YAML result after marshalling or to use as input for unmarshalling when reading the expected
   *         structure.
   */
  protected String getExpectedYamlData(String indent, String newline, String quote) {

    return getExpectedYamlData(indent, newline, quote, quote, quote, isSmartYamlIndent(), isSupportingComments());
  }

  /**
   * @param indent the indentation (e.g. " ").
   * @param newline the newline (e.g. "\n").
   * @param quoteString the quote for {@link String} values (e.g. "'").
   * @param quoteInstant the quote for {@link java.time.Instant} values (e.g. "'").
   * @param quoteBigNum the quote for {@link java.math.BigDecimal} or {@link java.math.BigInteger} values (e.g. "'").
   * @param smartIndent the {@link #isSmartYamlIndent() smart YAML indentation flag}.
   * @param comments the {@link StructuredFormat#isSupportingComments() supporting comments flag}.
   * @return the expected YAML result after marshalling or to use as input for unmarshalling when reading the expected
   *         structure.
   */
  protected String getExpectedYamlData(String indent, String newline, String quoteString, String quoteInstant,
      String quoteBigNum, boolean smartIndent, boolean comments) {

    String smartStart = "";
    String smartEnd = "";
    if (!smartIndent) {
      smartStart = indent;
      if (!comments) {
        smartStart = newline + smartStart;
      }
      smartEnd = newline;
    }
    String quoteJsonString = "";
    if (!quoteString.isEmpty()) {
      quoteJsonString = "\"";
    }
    return (comments ? "# header comment" + newline : "") //
        + (comments ? "# foo starts here\n# --- second line of comment." + newline : "") //
        + "foo: " + quoteString + "bar" + quoteString + newline //
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
        + "- - {" //
        + (comments ? newline + indent + "# an object inside an array within an array" + newline : "") //
        + smartStart + "key: " + quoteJsonString + "value" + quoteJsonString + smartEnd + "}" + newline //
        + "empty: []";
  }

  /**
   * @return {@code true} for smart YAML indentation, {@code false} otherwise.
   */
  protected boolean isSmartYamlIndent() {

    return true;
  }

  /**
   * Test with {@link StructuredFormat#ID_YAML}.
   */
  @Test
  void testYamlFormat() {

    StructuredFormatProvider provider = StructuredFormatFactory.get().getProvider(StructuredFormat.ID_YAML);
    StructuredFormat format = provider.create();
    assertThat(format).isSameAs(getProvider().create());
    assertThat(format.isSupportingComments()).isEqualTo(isSupportingComments());
  }

  /**
   * @return the expected value of {@link StructuredFormat#isSupportingComments()}.
   */
  protected boolean isSupportingComments() {

    return true;
  }

  /**
   * Test of {@link StructuredReader} using JSON as this is also YAML.
   */
  @Test
  void testReadJson() {

    // without indentation
    StructuredReader reader = newReader(getExpectedJsonData("", "", true));
    readTestData(reader);

    // with indentation
    reader = newReader(getExpectedJsonData("  ", "\n", false));
    readTestData(reader);
  }

}
