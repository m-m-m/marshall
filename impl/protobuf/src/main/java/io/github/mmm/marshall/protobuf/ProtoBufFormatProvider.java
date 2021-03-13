/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.marshall.protobuf;

import io.github.mmm.marshall.MarshallingConfig;
import io.github.mmm.marshall.StructuredBinaryFormat;
import io.github.mmm.marshall.StructuredBinaryFormatProvider;
import io.github.mmm.marshall.StructuredFormat;
import io.github.mmm.marshall.StructuredFormatProvider;
import io.github.mmm.marshall.protobuf.impl.ProtoBufFormat;

/**
 * Implementation of {@link StructuredFormatProvider} for ProtoBuf/gRPC.
 *
 * @since 1.0.0
 */
public class ProtoBufFormatProvider implements StructuredBinaryFormatProvider {

  @Override
  public String getId() {

    return StructuredFormat.ID_PROTOBUF;
  }

  @Override
  public StructuredBinaryFormat create() {

    return ProtoBufFormat.of();
  }

  @Override
  public StructuredBinaryFormat create(MarshallingConfig config) {

    return ProtoBufFormat.of(config);
  }

}
