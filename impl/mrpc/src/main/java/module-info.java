/*
 * Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0
 */

/**
 * Provides an implementation of {@code mmm-marshall} for mRPC. mRPC is an analogy to gRPC but is a proprietary format
 * of the m-m-m project. So why should I use it? Generic formats like XML, JSON or YAML are well established but slow to
 * parse. However, gPRC/protobuf that addresses the parser performance well made design decisions that do not fit a
 * generic marshalling. E.g. the length has to be encoded at the beginning of every object or array causing design and
 * performance issues when writing the data into streams as you have to buffer the entire object or need computations to
 * determine the size. Even worse in ProtoBuf you cannot distinguish between string, object, or array when reading the
 * data. For static mappings this may be suitable but for dynamic structures this is causing severe problems. These
 * problems are addressed by mRPC to bring the flexible structure of JSON with the lightning fast processing of gRPC.
 *
 * @provides io.github.mmm.marshall.StructuredFormatProvider
 */
module io.github.mmm.marshall.mrpc {

  requires transitive io.github.mmm.marshall;

  requires com.google.protobuf;

  exports io.github.mmm.marshall.mrpc;

  provides io.github.mmm.marshall.StructuredFormatProvider with //
      io.github.mmm.marshall.mrpc.MrpcFormatProvider;
}
