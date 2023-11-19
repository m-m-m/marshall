/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.marshall.protobuf;

import io.github.mmm.base.variable.VariableDefinition;
import io.github.mmm.marshall.MarshallingConfig;
import io.github.mmm.marshall.StructuredBinaryFormat;
import io.github.mmm.marshall.StructuredFormat;
import io.github.mmm.marshall.StructuredFormatProvider;
import io.github.mmm.marshall.StructuredState;
import io.github.mmm.marshall.protobuf.impl.ProtoBufFormat;
import io.github.mmm.marshall.spi.StructuredBinaryIdBasedFormatProvider;

/**
 * Implementation of {@link StructuredFormatProvider} for ProtoBuf/gRPC.
 *
 * @since 1.0.0
 */
public class ProtoBufFormatProvider implements StructuredBinaryIdBasedFormatProvider {

  /**
   * {@link VariableDefinition} to configure if {@link StructuredState#START_OBJECT objects} should be encoded as
   * groups. Otherwise they will be encoded as length delimited objects. Even though groups have been deprecated they
   * are used by default as this is a much better fit for this generic marhsalling.
   */
  public static final VariableDefinition<Boolean> VAR_USE_GROUPS = new VariableDefinition<>("use-groups", Boolean.TRUE);

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
