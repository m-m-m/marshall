/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.marshall.grpc;

import java.util.Map;

import io.github.mmm.marshall.MarshallingConfig;
import io.github.mmm.marshall.StructuredBinaryFormat;
import io.github.mmm.marshall.StructuredFormat;
import io.github.mmm.marshall.grpc.impl.GrpcFormatImpl;

/**
 * Provides {@link StructuredFormat} for JSON based on TeaVM.
 *
 * @since 1.0.0
 */
public final class GrpcMarshalling {

  private GrpcMarshalling() {

    super();
  }

  /**
   * @return the default instance of {@link StructuredFormat} for JSON based on JSON-P.
   */
  public static StructuredBinaryFormat of() {

    return GrpcFormatImpl.of();
  }

  /**
   * @param config the {@link Map} with the configuration properties for the JSON vendor implementation.
   * @return a new instance of {@link StructuredFormat} for JSON based on JSON-P.
   */
  public static StructuredBinaryFormat of(MarshallingConfig config) {

    return GrpcFormatImpl.of(config);
  }

}
