/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.marshall.grpc;

import io.github.mmm.marshall.MarshallingConfig;
import io.github.mmm.marshall.StructuredBinaryFormat;
import io.github.mmm.marshall.StructuredBinaryFormatProvider;
import io.github.mmm.marshall.StructuredFormat;
import io.github.mmm.marshall.StructuredFormatProvider;

/**
 * Implementation of {@link StructuredFormatProvider} for JSON-P.
 *
 * @since 1.0.0
 */
public class GrpcFormatProvider implements StructuredBinaryFormatProvider {

  @Override
  public String getId() {

    return StructuredFormat.ID_PROTOBUF;
  }

  @Override
  public StructuredBinaryFormat create() {

    return GrpcMarshalling.of();
  }

  @Override
  public StructuredBinaryFormat create(MarshallingConfig config) {

    return GrpcMarshalling.of(config);
  }

}
