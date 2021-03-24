/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.marshall.protobuf.impl;

import java.io.InputStream;
import java.io.OutputStream;

import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;

import io.github.mmm.marshall.MarshallingConfig;
import io.github.mmm.marshall.StructuredBinaryFormat;
import io.github.mmm.marshall.StructuredFormat;
import io.github.mmm.marshall.StructuredReader;
import io.github.mmm.marshall.StructuredWriter;
import io.github.mmm.marshall.size.StructuredFormatSizeComputor;

/**
 * Implementation of {@link StructuredFormat} for ProtoBuf/gRPC.
 *
 * @since 1.0.0
 */
public class ProtoBufFormat implements StructuredBinaryFormat {

  private static final ProtoBufFormat DEFAULT = of(MarshallingConfig.DEFAULTS);

  private final MarshallingConfig config;

  /**
   * The constructor.
   *
   * @param config the {@link MarshallingConfig}.
   * @see io.github.mmm.marshall.StructuredFormatFactory#create(String, MarshallingConfig)
   */
  public ProtoBufFormat(MarshallingConfig config) {

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
  public StructuredFormatSizeComputor getSizeComputor() {

    return ProtoBufSizeComputor.get();
  }

  @Override
  public StructuredReader reader(InputStream in) {

    return new ProtoBufReader(CodedInputStream.newInstance(in), this);
  }

  @Override
  public StructuredWriter writer(OutputStream out) {

    return new ProtoBufWriter(CodedOutputStream.newInstance(out), this);
  }

  /**
   * @return the default instance of {@link ProtoBufFormat}.
   */
  public static ProtoBufFormat of() {

    return DEFAULT;
  }

  /**
   * @param config the {@link MarshallingConfig} for the JSON vendor implementation.
   * @return the new instance of {@link ProtoBufFormat} with the given {@code config}.
   */
  public static ProtoBufFormat of(MarshallingConfig config) {

    return new ProtoBufFormat(config);
  }

}
