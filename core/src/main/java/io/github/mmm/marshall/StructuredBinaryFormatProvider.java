package io.github.mmm.marshall;

/**
 * {@link StructuredFormatProvider} for {@link StructuredBinaryFormat}.
 *
 * @since 1.0.0
 */
public interface StructuredBinaryFormatProvider extends StructuredFormatProvider {

  @Override
  StructuredBinaryFormat create();

  @Override
  StructuredBinaryFormat create(MarshallingConfig config);

}
