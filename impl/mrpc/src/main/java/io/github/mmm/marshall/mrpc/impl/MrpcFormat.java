/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.marshall.mrpc.impl;

import java.io.InputStream;
import java.io.OutputStream;

import com.google.protobuf.CodedOutputStream;
import com.google.protobuf.WireFormat;

import io.github.mmm.marshall.MarshallingConfig;
import io.github.mmm.marshall.StructuredFormat;
import io.github.mmm.marshall.StructuredReader;
import io.github.mmm.marshall.StructuredWriter;
import io.github.mmm.marshall.spi.AbstractStructuredBinaryIdBasedFormat;

/**
 * Implementation of {@link StructuredFormat} for mRPC.
 *
 * @since 1.0.0
 */
public class MrpcFormat extends AbstractStructuredBinaryIdBasedFormat {

  static final int TYPE_START_OBJECT = WireFormat.WIRETYPE_START_GROUP;

  static final int TYPE_START_ARRAY = 6;

  static final int TYPE_END = WireFormat.WIRETYPE_END_GROUP;

  private static final MrpcFormat DEFAULT = of(MarshallingConfig.DEFAULTS);

  /**
   * The constructor.
   *
   * @param config the {@link MarshallingConfig}.
   * @see io.github.mmm.marshall.StructuredFormatFactory#create(String, MarshallingConfig)
   */
  public MrpcFormat(MarshallingConfig config) {

    super(config);
  }

  @Override
  public String getId() {

    return ID_MRPC;
  }

  @Override
  public StructuredReader reader(InputStream in) {

    return new MrpcReader(in, this);
  }

  @Override
  public StructuredWriter writer(OutputStream out) {

    return new MrpcWriter(CodedOutputStream.newInstance(out), this);
  }

  /**
   * @return the default instance of {@link MrpcFormat}.
   */
  public static MrpcFormat of() {

    return DEFAULT;
  }

  /**
   * @param config the {@link MarshallingConfig} for the JSON vendor implementation.
   * @return the new instance of {@link MrpcFormat} with the given {@code config}.
   */
  public static MrpcFormat of(MarshallingConfig config) {

    return new MrpcFormat(config);
  }

}
