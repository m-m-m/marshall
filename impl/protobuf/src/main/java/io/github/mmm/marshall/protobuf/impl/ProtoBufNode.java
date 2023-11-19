package io.github.mmm.marshall.protobuf.impl;

import io.github.mmm.marshall.id.StructuredIdMapping;
import io.github.mmm.marshall.spi.StructuredNode;
import io.github.mmm.marshall.spi.StructuredNodeType;

final class ProtoBufNode extends StructuredNode<ProtoBufNode> {

  StructuredIdMapping idMapping;

  int id;

  int start;

  int end;

  int sizeOverhead;

  /** {@code true} if this is an explicitly encoded object or array that needs to be terminated with END_GROUP. */
  boolean explicit;

  ProtoBufNode(ProtoBufNode parent, StructuredNodeType type, StructuredIdMapping idMapping) {

    this(parent, type, idMapping, Integer.MAX_VALUE);
  }

  ProtoBufNode(ProtoBufNode parent, StructuredNodeType type, StructuredIdMapping idMapping, int end) {

    super(parent, type);
    this.idMapping = idMapping;
    this.end = end;
  }

  /**
   * @return the {@link StructuredIdMapping}.
   */
  public StructuredIdMapping getIdMapping() {

    ProtoBufNode node = this;
    do {
      if (node.idMapping != null) {
        return node.idMapping;
      }
      if (node.type == StructuredNodeType.ARRAY) {
        node = node.parent;
      } else {
        node = null; // break
      }
    } while (node != null);
    return null;
  }

}