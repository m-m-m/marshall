package io.github.mmm.marshall;

/**
 * {@link StructuredFormatProvider} for {@link StructuredTextFormat}.
 *
 * @since 1.0.0
 */
public interface StructuredTextFormatProvider extends StructuredFormatProvider {

  @Override
  StructuredTextFormat create();

  @Override
  StructuredTextFormat create(MarshallingConfig config);

}
