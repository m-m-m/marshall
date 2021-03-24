package io.github.mmm.marshall;

import io.github.mmm.marshall.size.StructuredFormatSizeComputor;
import io.github.mmm.marshall.size.StructuredFormatSizeComputorNone;

/**
 * {@link StructuredFormat} that is {@link #isBinary() binary}.
 *
 * @since 1.0.0
 */
public interface StructuredBinaryFormat extends StructuredFormat {

  @Override
  default boolean isBinary() {

    return true;
  }

  /**
   * @return the {@link StructuredFormatSizeComputor} used to pre-calculate the message size.
   * @see StructuredWriter#writeStartObject(int)
   * @see StructuredWriter#writeStartArray(int)
   */
  default StructuredFormatSizeComputor getSizeComputor() {

    return StructuredFormatSizeComputorNone.get();
  }

}
