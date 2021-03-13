package io.github.mmm.marshall.test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.junit.jupiter.api.Test;

import io.github.mmm.binary.BinaryType;
import io.github.mmm.marshall.MarshallingConfig;
import io.github.mmm.marshall.StructuredBinaryFormat;
import io.github.mmm.marshall.StructuredBinaryFormatProvider;
import io.github.mmm.marshall.StructuredReader;
import io.github.mmm.marshall.StructuredReader.State;
import io.github.mmm.marshall.StructuredWriter;

/**
 * Abstract test of {@link StructuredBinaryFormat}.
 */
public abstract class StructuredBinaryFormatTest extends StructuredFormatTest {

  private ByteArrayOutputStream baos;

  @Override
  protected abstract StructuredBinaryFormatProvider getProvider();

  @Override
  protected StructuredReader newReader(String data) {

    byte[] bytes = BinaryType.parseHex(data);
    return getProvider().create().reader(new ByteArrayInputStream(bytes));
  }

  @Override
  protected StructuredWriter newWriter(MarshallingConfig config) {

    this.baos = new ByteArrayOutputStream();
    StructuredBinaryFormatProvider provider = getProvider();
    StructuredBinaryFormat format;
    if (config == null) {
      format = provider.create();
    } else {
      format = provider.create(config);
    }
    return format.writer(this.baos);
  }

  @Override
  protected String getActualData() {

    return BinaryType.formatHex(this.baos.toByteArray());
  }

  @Override
  protected void checkState(StructuredReader reader, State state) {

    reader.require(state);
  }

  /**
   * Test of {@link StructuredBinaryFormat#isBinary()}.
   */
  @Test
  public void testFormatFlags() {

    StructuredBinaryFormat format = getProvider().create();
    assertThat(format.isBinary()).isTrue();
    assertThat(format.isText()).isFalse();
  }

}
