package io.github.mmm.marshall.mrpc.impl;

import io.github.mmm.marshall.id.StructuredIdMapping;
import io.github.mmm.marshall.spi.StructuredNode;
import io.github.mmm.marshall.spi.StructuredNodeType;

/**
 * State for {@link MrpcWriter}.
 *
 * @since 1.0.0
 */
public class MrpcNode extends StructuredNode<MrpcNode> {

  StructuredIdMapping idMapping;

  MrpcNode(MrpcNode parent, StructuredNodeType type, StructuredIdMapping idMapping) {

    super(parent, type);
    this.idMapping = idMapping;
  }

  MrpcNode getParent() {

    return this.parent;
  }

}
