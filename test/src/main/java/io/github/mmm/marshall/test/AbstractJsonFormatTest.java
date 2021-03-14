package io.github.mmm.marshall.test;

import org.junit.jupiter.api.Test;

import io.github.mmm.marshall.JsonFormat;
import io.github.mmm.marshall.StructuredFormat;
import io.github.mmm.marshall.StructuredFormatFactory;
import io.github.mmm.marshall.StructuredFormatProvider;
import io.github.mmm.marshall.StructuredTextFormat;

/**
 * Abstract test of {@link StructuredTextFormat}.
 */
public abstract class AbstractJsonFormatTest extends StructuredTextFormatTest {

  /**
   * @return {@code true} for smart indentation, {@code false} otherwise (stupid indentation that also wraps empty
   *         arrays, etc.).
   */
  protected boolean isSmartIndent() {

    return true;
  }

  @Override
  protected String getExpectedData(String indent, String newline) {

    return getExpectedData(indent, newline, true);
  }

  /**
   * @param indent the {@link #getIndentation() indentation}.
   * @param newline the {@link #getNewline() newline}.
   * @param quoteProperties - {@code true} to quote properties, {@code false} otherwise.
   * @return the expected payload data.
   */
  protected String getExpectedData(String indent, String newline, boolean quoteProperties) {

    String space = " ";
    if (indent.isEmpty()) {
      space = "";
    }
    String emptyArrayIndent = newline + indent;
    if (isSmartIndent()) {
      emptyArrayIndent = "";
    }
    String quote = "";
    if (quoteProperties) {
      quote = "\"";
    }
    return "{" + newline //
        + indent + quote + "foo" + quote + ":" + space + "\"bar\"," + newline //
        + indent + quote + "instant" + quote + ":" + space + "\"1999-12-31T23:59:59.999999Z\"," + newline //
        + indent + quote + "list" + quote + ":" + space + "[" + newline //
        + indent + indent + "-1," + newline //
        + indent + indent + "-1," + newline //
        + indent + indent + "-1," + newline //
        + indent + indent + "-12345678901," + newline //
        + indent + indent + "4.2," + newline //
        + indent + indent + "42.42," + newline //
        + indent + indent + "\"0.12345678901234567890123456789\"," + newline //
        + indent + indent + "\"1234567890123456789012345678901234567890\"," + newline //
        + indent + indent + "\"1.10\"," + newline //
        + indent + indent + "[" + newline //
        + indent + indent + indent + "{" + newline //
        + indent + indent + indent + indent + quote + "key" + quote + ":" + space + "\"value\"" + newline //
        + indent + indent + indent + "}" + newline //
        + indent + indent + "]" + newline //
        + indent + "]," + newline //
        + indent + quote + "empty" + quote + ":" + space + "[" //
        + emptyArrayIndent + "]" + newline //
        + "}"; //
  }

  @Override
  protected String getExpectedDataForAtomicLong() {

    return "42";
  }

  /**
   * Test with {@link StructuredFormat#ID_JSON} and {@link JsonFormat#of()}.
   */
  @Test
  public void testJsonFormat() {

    StructuredFormatProvider provider = StructuredFormatFactory.get().getProvider(StructuredFormat.ID_JSON);
    assertThat(provider.create()).isSameAs(getProvider().create()).isSameAs(JsonFormat.of());
  }

}
