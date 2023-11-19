/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.marshall.protobuf.impl;

import java.io.InputStream;
import java.io.OutputStream;

import com.google.protobuf.WireFormat;

import io.github.mmm.marshall.MarshallingConfig;
import io.github.mmm.marshall.StructuredFormat;
import io.github.mmm.marshall.StructuredReader;
import io.github.mmm.marshall.StructuredWriter;
import io.github.mmm.marshall.spi.AbstractStructuredBinaryIdBasedFormat;

/**
 * Implementation of {@link StructuredFormat} for ProtoBuf/gRPC.
 *
 * @since 1.0.0
 */
public class ProtoBufFormat extends AbstractStructuredBinaryIdBasedFormat {

  /** Wire type for start of (nested) object. */
  public static final int TYPE_START_OBJECT = WireFormat.WIRETYPE_START_GROUP;

  /** Wire type for start of nested array. */
  public static final int TYPE_START_ARRAY = 6;

  /** Wire type for end of object or end of nested array. */
  public static final int TYPE_END = WireFormat.WIRETYPE_END_GROUP;

  private static final ProtoBufFormat DEFAULT = of(MarshallingConfig.DEFAULTS);

  /**
   * The constructor.
   *
   * @param config the {@link MarshallingConfig}.
   * @see io.github.mmm.marshall.StructuredFormatFactory#create(String, MarshallingConfig)
   */
  public ProtoBufFormat(MarshallingConfig config) {

    super(config);
  }

  @Override
  public String getId() {

    return ID_PROTOBUF;
  }

  @Override
  public StructuredReader reader(InputStream in) {

    return new ProtoBufReader(in, this);
  }

  @Override
  public StructuredWriter writer(OutputStream out) {

    return new ProtoBufWriter(out, this);
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
