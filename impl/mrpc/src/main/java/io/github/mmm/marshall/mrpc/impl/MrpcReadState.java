/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.marshall.mrpc.impl;

import io.github.mmm.marshall.spi.StructuredNodeType;

/**
 * State for {@link MrpcReader}.
 *
 * @since 1.0.0
 */
public class MrpcReadState {

  final MrpcReadState parent;

  final StructuredNodeType type;

  MrpcReadState() {

    this(null, null);
  }

  MrpcReadState(MrpcReadState parent, StructuredNodeType type) {

    super();
    this.parent = parent;
    this.type = type;
  }

}