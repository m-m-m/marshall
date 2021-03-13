/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.marshall.protobuf.impl;

import io.github.mmm.marshall.StructuredReader.State;

/**
 * State object for reading or writing ProtoBuf/gRPC.
 *
 * @since 1.0.0
 */
public class ProtoBufState {

  final ProtoBufState parent;

  final State state;

  final int end;

  ProtoBufState() {

    this(null, null, Integer.MAX_VALUE);
  }

  ProtoBufState(ProtoBufState parent, State state, int end) {

    super();
    this.parent = parent;
    this.state = state;
    this.end = end;
  }

  State getEnd() {

    if (this.state == State.START_OBJECT) {
      return State.END_OBJECT;
    } else if (this.state == State.START_ARRAY) {
      return State.END_ARRAY;
    } else {
      return State.DONE;
    }
  }

  ProtoBufState startObject(int newEnd) {

    return new ProtoBufState(this, State.START_OBJECT, newEnd);
  }

  ProtoBufState startArray(int newEnd) {

    return new ProtoBufState(this, State.START_ARRAY, newEnd);
  }

}