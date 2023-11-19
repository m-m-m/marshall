/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.marshall.spi;

import io.github.mmm.marshall.MarshallingConfig;
import io.github.mmm.marshall.StructuredBinaryFormat;
import io.github.mmm.marshall.StructuredBinaryFormatProvider;
import io.github.mmm.marshall.StructuredFormatProvider;
import io.github.mmm.marshall.id.StructuredIdMappingProvider;

/**
 * {@link StructuredFormatProvider} for {@link StructuredBinaryFormat}.
 *
 * @since 1.0.0
 */
public interface StructuredBinaryIdBasedFormatProvider extends StructuredBinaryFormatProvider {

  /**
   * @param idMappingProvider the {@link StructuredIdMappingProvider}.
   * @return the {@link StructuredBinaryFormat} for mRPC.
   */
  default StructuredBinaryFormat create(StructuredIdMappingProvider idMappingProvider) {

    return create(MarshallingConfig.DEFAULTS.with(MarshallingConfig.VAR_ID_MAPPING_PROVIDER, idMappingProvider));
  }

}
