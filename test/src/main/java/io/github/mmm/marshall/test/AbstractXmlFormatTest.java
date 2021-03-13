package io.github.mmm.marshall.test;

import org.junit.jupiter.api.Test;

import io.github.mmm.marshall.StructuredFormat;
import io.github.mmm.marshall.StructuredFormatFactory;
import io.github.mmm.marshall.StructuredFormatProvider;
import io.github.mmm.marshall.StructuredTextFormat;
import io.github.mmm.marshall.XmlFormat;

/**
 * Abstract test of {@link StructuredTextFormat}.
 */
public abstract class AbstractXmlFormatTest extends StructuredTextFormatTest {

  @Override
  protected String getExpectedData(String indent, String newline) {

    return "<?xml version=\"1.0\" ?>" + newline //
        + "<o:json xmlns:a=\"array\" xmlns:o=\"object\">" + newline //
        + indent + "<foo s=\"bar\"/>" + newline //
        + indent + "<instant s=\"1999-12-31T23:59:59.999999Z\"/>" + newline //
        + indent + "<a:list>" + newline //
        + indent + indent + "<i n=\"-1\"/>" + newline //
        + indent + indent + "<i n=\"-1\"/>" + newline //
        + indent + indent + "<i n=\"-1\"/>" + newline //
        + indent + indent + "<i n=\"-12345678901\"/>" + newline //
        + indent + indent + "<i n=\"4.2\"/>" + newline //
        + indent + indent + "<i n=\"42.42\"/>" + newline //
        + indent + indent + "<i n=\"0.12345678901234567890123456789\"/>" + newline //
        + indent + indent + "<i n=\"1234567890123456789012345678901234567890\"/>" + newline //
        + indent + indent + "<i n=\"1.10\"/>" + newline //
        + indent + indent + "<a:i>" + newline //
        + indent + indent + indent + "<o:i>" + newline //
        + indent + indent + indent + indent + "<key s=\"value\"/>" + newline //
        + indent + indent + indent + "</o:i>" + newline //
        + indent + indent + "</a:i>" + newline //
        + indent + "</a:list>" + newline //
        + indent + "<a:empty>" + newline //
        + indent + "</a:empty>" + newline //
        + "</o:json>";
  }

  @Override
  protected String getExpectedDataForAtomicLong() {

    return "<?xml version=\"1.0\" ?><json n=\"42\"/>";
  }

  /**
   * Test with {@link StructuredFormat#ID_XML} and {@link XmlFormat#of()}.
   */
  @Test
  public void testJsonFormat() {

    StructuredFormatProvider provider = StructuredFormatFactory.get().getProvider(StructuredFormat.ID_XML);
    assertThat(provider.create()).isSameAs(getProvider().create()).isSameAs(XmlFormat.of());
  }

}
