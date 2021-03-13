/*
 * Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0
 */

/**
 * Provides an implementation of {@code mmm-marshall} for ProtoBuf/gRPC.
 *
 * @provides io.github.mmm.marshall.StructuredFormatProvider
 */
module io.github.mmm.marshall.protobuf {

  requires transitive io.github.mmm.marshall;

  requires transitive protobuf.java;

  exports io.github.mmm.marshall.protobuf;

  provides io.github.mmm.marshall.StructuredFormatProvider with //
      io.github.mmm.marshall.protobuf.ProtoBufFormatProvider;
}
