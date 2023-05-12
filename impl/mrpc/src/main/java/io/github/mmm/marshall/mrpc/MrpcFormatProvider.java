/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.marshall.mrpc;

import io.github.mmm.marshall.MarshallingConfig;
import io.github.mmm.marshall.StructuredBinaryFormat;
import io.github.mmm.marshall.StructuredBinaryFormatProvider;
import io.github.mmm.marshall.StructuredFormat;
import io.github.mmm.marshall.StructuredFormatProvider;
import io.github.mmm.marshall.mrpc.impl.MrpcFormat;

/**
 * Implementation of {@link StructuredFormatProvider} for mRPC.
 *
 * @since 1.0.0
 */
public class MrpcFormatProvider implements StructuredBinaryFormatProvider {

  @Override
  public String getId() {

    return StructuredFormat.ID_MRPC;
  }

  @Override
  public StructuredBinaryFormat create() {

    return MrpcFormat.of();
  }

  @Override
  public StructuredBinaryFormat create(MarshallingConfig config) {

    return MrpcFormat.of(config);
  }

}
