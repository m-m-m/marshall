package io.github.mmm.marshall;

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

}
