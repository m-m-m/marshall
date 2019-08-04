package net.sf.mmm.marshal.impl.stax;

import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.BigInteger;

import net.sf.mmm.marshall.api.StructuredReader;
import net.sf.mmm.marshall.api.StructuredWriter;
import net.sf.mmm.marshall.impl.stax.XmlFormat;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test of {@link XmlFormat}.
 */
public class XmlFormatTest extends Assertions {

  private static final String XML_EXPECTED = "<?xml version=\"1.0\" ?>" //
      + "<o:json xmlns:a=\"array\" xmlns:o=\"object\">" //
      + "<foo v=\"bar\"/>" //
      + "<a:list>" //
      + "<i v=\"-1\"/>" //
      + "<i v=\"-1\"/>" //
      + "<i v=\"-1\"/>" //
      + "<i v=\"-42\"/>" //
      + "<i v=\"4.2\"/>" //
      + "<i v=\"42.42\"/>" //
      + "<i v=\"0.12345678901234567890123456789\"/>" //
      + "<i v=\"1234567890123456789012345678901234567890\"/>" //
      + "</a:list>" //
      + "</o:json>";

  /**
   * Test {@link XmlFormat#writer(java.io.Writer) writing as XML}.
   *
   * @throws Exception on error.
   */
  @Test
  public void testWrite() throws Exception {

    StringWriter stringWriter = new StringWriter();
    StructuredWriter writer = XmlFormat.of().writer(stringWriter);
    writer.writeStartObject();
    writer.writeName("foo");
    writer.writeValue("bar");
    writer.writeName("list");
    writer.writeStartArray();
    writer.writeValueAsByte((byte) -1);
    writer.writeValueAsShort((short) -1);
    writer.writeValueAsInteger(-1);
    writer.writeValueAsLong(Long.valueOf(-42));
    writer.writeValueAsFloat(4.2F);
    writer.writeValueAsDouble(42.42);
    writer.writeValueAsBigDecimal(new BigDecimal("0.12345678901234567890123456789"));
    writer.writeValueAsBigInteger(new BigInteger("1234567890123456789012345678901234567890"));
    writer.writeEnd();
    writer.writeEnd();
    writer.close();
    assertThat(stringWriter.toString()).isEqualTo(XML_EXPECTED);
  }

  /**
   * Test {@link XmlFormat#reader(java.io.Reader) reading from XML}.
   *
   * @throws Exception on error.
   */
  @Test
  public void testRead() throws Exception {

    Reader stringReader = new StringReader(XML_EXPECTED);
    StructuredReader jsonReader = XmlFormat.of().reader(stringReader);
    assertThat(jsonReader.isDone()).isFalse();
    assertThat(jsonReader.readStartObject()).isTrue();
    assertThat(jsonReader.readName()).isEqualTo("foo");
    assertThat(jsonReader.readValue(String.class)).isEqualTo("bar");
    assertThat(jsonReader.readName()).isEqualTo("list");
    assertThat(jsonReader.readStartArray()).isTrue();
    assertThat(jsonReader.readValue(Byte.class)).isEqualTo(Byte.valueOf((byte) -1));
    assertThat(jsonReader.readValue(Short.class)).isEqualTo(Short.valueOf((short) -1));
    assertThat(jsonReader.readValue(Integer.class)).isEqualTo(-1);
    assertThat(jsonReader.readValue(Long.class)).isEqualTo(-42L);
    assertThat(jsonReader.readValue(Float.class)).isEqualTo(4.2F);
    assertThat(jsonReader.readValue(Double.class)).isEqualTo(42.42);
    assertThat(jsonReader.readValue(BigDecimal.class)).isEqualTo(new BigDecimal("0.12345678901234567890123456789"));
    assertThat(jsonReader.readValue(BigInteger.class))
        .isEqualTo(new BigInteger("1234567890123456789012345678901234567890"));
    assertThat(jsonReader.readEnd()).isTrue();
    assertThat(jsonReader.isDone()).isFalse();
    assertThat(jsonReader.readEnd()).isTrue();
    assertThat(jsonReader.isDone()).isTrue();
  }

}
