/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.marshall.grpc.impl;

import java.io.InputStream;
import java.io.OutputStream;

import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;

import io.github.mmm.marshall.MarshallingConfig;
import io.github.mmm.marshall.StructuredBinaryFormat;
import io.github.mmm.marshall.StructuredFormat;
import io.github.mmm.marshall.StructuredReader;
import io.github.mmm.marshall.StructuredWriter;

/**
 * Implementation of {@link StructuredFormat} for JSON (JavaScript Object Notation).
 *
 * @since 1.0.0
 */
public class GrpcFormatImpl implements StructuredBinaryFormat {

  private static final GrpcFormatImpl DEFAULT = of(MarshallingConfig.DEFAULTS);

  private final MarshallingConfig config;

  /**
   * The constructor.
   *
   * @param config the {@link MarshallingConfig}.
   * @see io.github.mmm.marshall.StructuredFormatFactory#create(String, MarshallingConfig)
   */
  public GrpcFormatImpl(MarshallingConfig config) {

    super();
    this.config = config;
  }

  @Override
  public MarshallingConfig getConfig() {

    return this.config;
  }

  @Override
  public String getId() {

    return ID_PROTOBUF;
  }

  @Override
  public boolean isIdBased() {

    return true;
  }

  @Override
  public StructuredReader reader(InputStream in) {

    return new GrpcReader(CodedInputStream.newInstance(in), this);
  }

  @Override
  public StructuredWriter writer(OutputStream out) {

    return new GrpcWriter(CodedOutputStream.newInstance(out), this);
  }

  /**
   * @return the default instance of {@link GrpcFormatImpl}.
   */
  public static GrpcFormatImpl of() {

    return DEFAULT;
  }

  /**
   * @param config the {@link MarshallingConfig} for the JSON vendor implementation.
   * @return the new instance of {@link GrpcFormatImpl} with the given {@code config}.
   */
  public static GrpcFormatImpl of(MarshallingConfig config) {

    return new GrpcFormatImpl(config);
  }

}
