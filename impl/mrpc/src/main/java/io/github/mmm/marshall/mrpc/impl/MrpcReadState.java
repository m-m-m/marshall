/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.marshall.mrpc.impl;

import io.github.mmm.marshall.id.StructuredIdMapping;
import io.github.mmm.marshall.spi.StructuredNodeType;

/**
 * State for {@link MrpcReader}.
 *
 * @since 1.0.0
 */
public class MrpcReadState {

  final StructuredNodeType type;

  StructuredIdMapping idMapping;

  final MrpcReadState parent;

  MrpcReadState() {

    this(null, null);
  }

  MrpcReadState(StructuredNodeType type, MrpcReadState parent) {

    super();
    this.type = type;
    this.parent = parent;
  }

}