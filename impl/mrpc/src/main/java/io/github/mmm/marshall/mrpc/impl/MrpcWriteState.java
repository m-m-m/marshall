package io.github.mmm.marshall.mrpc.impl;

import io.github.mmm.marshall.spi.StructuredNodeType;

/**
 * State for {@link MrpcWriter}.
 *
 * @since 1.0.0
 */
public class MrpcWriteState {

  final StructuredNodeType type;

  final MrpcWriteState parent;

  private int idCounter;

  MrpcWriteState() {

    this(null, null);
  }

  MrpcWriteState(StructuredNodeType type, MrpcWriteState parent) {

    super();
    this.type = type;
    this.parent = parent;
  }

  MrpcWriteState getParent() {

    return this.parent;
  }

  int id(int id) {

    if (id <= 0) {
      this.idCounter++;
      return this.idCounter;
    }
    if (this.idCounter <= id) {
      this.idCounter = id;
    }
    return id;
  }

}
