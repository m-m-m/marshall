/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.marshall.protobuf.impl;

import java.util.Objects;

import io.github.mmm.marshall.StructuredState;
import io.github.mmm.marshall.id.StructuredIdMapping;

/**
 * State object for reading or writing ProtoBuf/gRPC.
 *
 * @since 1.0.0
 */
public class ProtoBufState {

  final ProtoBufState parent;

  final StructuredState state;

  final int end;

  StructuredIdMapping idMapping;

  ProtoBufState() {

    this(null, null, Integer.MAX_VALUE);
  }

  ProtoBufState(ProtoBufState parent, StructuredState state) {

    this(parent, state, Integer.MAX_VALUE);
  }

  ProtoBufState(ProtoBufState parent, StructuredState state, int end) {

    super();
    this.parent = parent;
    this.state = state;
    this.end = end;
  }

  StructuredState getEnd() {

    if (this.state == StructuredState.START_OBJECT) {
      return StructuredState.END_OBJECT;
    } else if (this.state == StructuredState.START_ARRAY) {
      return StructuredState.END_ARRAY;
    } else {
      return StructuredState.DONE;
    }
  }

  ProtoBufState startObject(int newEnd, StructuredIdMapping mapping) {

    Objects.requireNonNull(mapping);
    ProtoBufState result = new ProtoBufState(this, StructuredState.START_OBJECT, newEnd);
    result.idMapping = mapping;
    return result;
  }

  ProtoBufState startArray(int newEnd) {

    return new ProtoBufState(this, StructuredState.START_ARRAY, newEnd);
  }

}