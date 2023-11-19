package io.github.mmm.marshall.test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.junit.jupiter.api.Test;

import io.github.mmm.binary.BinaryType;
import io.github.mmm.marshall.MarshallingConfig;
import io.github.mmm.marshall.StructuredBinaryFormat;
import io.github.mmm.marshall.StructuredBinaryFormatProvider;
import io.github.mmm.marshall.StructuredFormat;
import io.github.mmm.marshall.StructuredFormatProvider;
import io.github.mmm.marshall.StructuredReader;
import io.github.mmm.marshall.StructuredState;
import io.github.mmm.marshall.StructuredWriter;

/**
 * Abstract test of {@link StructuredBinaryFormat}.
 */
public abstract class StructuredBinaryFormatTest extends StructuredFormatTest {

  private ByteArrayOutputStream baos;

  @Override
  protected abstract StructuredBinaryFormatProvider getProvider();

  /**
   * @return a new instance of {@link StructuredBinaryFormat} for testing.
   */
  protected StructuredFormat newFormat() {

    return newFormat(MarshallingConfig.DEFAULTS);
  }

  /**
   * @param config the {@link MarshallingConfig}.
   * @return a new instance of {@link StructuredBinaryFormat} for testing.
   */
  protected StructuredFormat newFormat(MarshallingConfig config) {

    return newFormat(getProvider(), config);
  }

  /**
   * @param config the {@link MarshallingConfig}.
   * @param provider the {@link StructuredBinaryFormatProvider} to test.
   * @return a new instance of {@link StructuredBinaryFormat} for testing.
   */
  protected StructuredFormat newFormat(StructuredFormatProvider provider, MarshallingConfig config) {

    return provider.create(config);
  }

  @Override
  protected StructuredReader newReader(String data) {

    byte[] bytes = BinaryType.parseHex(data);
    return newFormat().reader(new ByteArrayInputStream(bytes));
  }

  @Override
  protected StructuredWriter newWriter(MarshallingConfig config) {

    this.baos = new ByteArrayOutputStream();
    StructuredFormat format;
    if (config == null) {
      format = newFormat();
    } else {
      format = newFormat(config);
    }
    return format.writer(this.baos);
  }

  @Override
  protected String getActualData() {

    return BinaryType.formatHex(this.baos.toByteArray());
  }

  @Override
  protected void checkState(StructuredReader reader, StructuredState state) {

    if ((reader.getState() == StructuredState.VALUE) && (state == StructuredState.START_ARRAY)) {
      // in protobuf regular "arrays" are encoded as repeated values
      // to prevent parsing and buffering lookahead we cannot distinguish between VALUE and START_ARRAY before
      // the unmarshalling code has to know if it expects an array or a single value.
      return;
    }
    reader.require(state);
  }

  /**
   * Test of {@link StructuredBinaryFormat#isBinary()}.
   */
  @Test
  public void testFormatFlags() {

    StructuredFormat format = newFormat();
    assertThat(format.isBinary()).isTrue();
    assertThat(format.isText()).isFalse();
  }

}
