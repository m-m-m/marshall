package io.github.mmm.marshall;

import io.github.mmm.marshall.id.StructuredIdMappingObject;
import io.github.mmm.marshall.spi.StructuredNodeType;

/**
 * Enum with the possible states of a {@link StructuredReader} or {@link StructuredWriter}.
 *
 * @see StructuredReader#getState()
 */
public enum StructuredState {

  /**
   * The initial state of a {@link StructuredWriter} before anything has been written. May also be used in edge-cases
   * when a {@link StructuredReader} is in a state that can not be determined exactly, what can only happen in formats
   * like gRPC/protobuf that are actually not designed to read generic data.
   */
  NULL() {
    @Override
    public boolean isValidTransition(StructuredState target, StructuredNodeType type) {

      return (target == VALUE) || (target == DONE) | target.isStart();
    }
  },

  /**
   * Start of an array.
   *
   * @see StructuredReader#readStartArray()
   * @see StructuredWriter#writeStartArray()
   */
  START_ARRAY() {
    @Override
    public boolean isValidTransition(StructuredState target, StructuredNodeType type) {

      return (target == VALUE) || (target == END_ARRAY) || target.isStart();
    }
  },

  /**
   * Start of an object.
   *
   * @see StructuredReader#readStartObject(StructuredIdMappingObject)
   * @see StructuredWriter#writeStartObject(StructuredIdMappingObject)
   */
  START_OBJECT() {
    @Override
    public boolean isValidTransition(StructuredState target, StructuredNodeType type) {

      return (target == NAME) || (target == END_OBJECT);
    }
  },

  /**
   * A regular value. Can either be an atomic value (only a single value in payload without any {@link #isStart()
   * start}), an {@link #START_ARRAY array} value or a {@link #NAME property} value of an {@link #START_OBJECT object}.
   *
   * @see StructuredReader#readValue()
   * @see StructuredWriter#writeValue(Object)
   */
  VALUE,

  /**
   * Name of a property.
   *
   * @see StructuredReader#readName()
   */
  NAME() {
    @Override
    public boolean isValidTransition(StructuredState target, StructuredNodeType type) {

      return (target == VALUE) || target.isStart();
    }
  },

  /**
   * End of an array.
   *
   * @see StructuredReader#readEnd()
   * @see StructuredWriter#writeEnd()
   * @see #START_ARRAY
   */
  END_ARRAY,

  /**
   * End of an object.
   *
   * @see StructuredReader#readEnd()
   * @see StructuredReader#readStartObject(StructuredIdMappingObject)
   */
  END_OBJECT,

  /**
   * End of data.
   *
   * @see StructuredReader#isDone()
   */
  DONE() {
    @Override
    public boolean isValidTransition(StructuredState target, StructuredNodeType type) {

      return false;
    }
  };

  /**
   * @return {@code true} if a start {@link StructuredState} such as {@link #START_ARRAY} or {@link #START_OBJECT},
   *         {@code false} otherwise.
   */
  public boolean isStart() {

    switch (this) {
      case START_ARRAY:
      case START_OBJECT:
        return true;
      default:
        return false;
    }
  }

  /**
   * @return {@code true} if an end {@link StructuredState} such as {@link #END_ARRAY} or {@link #END_OBJECT},
   *         {@code false} otherwise.
   */
  public boolean isEnd() {

    switch (this) {
      case END_ARRAY:
      case END_OBJECT:
      case DONE:
        return true;
      default:
        return false;
    }
  }

  /**
   * A value to add to the current depth when entering this state.
   *
   * @return {@code 1} if {@link #isStart() start}, {@code -1} if {@link #isEnd() end}, and {@code 0} otherwise.
   */
  public int getDepthDelta() {

    switch (this) {
      case START_ARRAY:
      case START_OBJECT:
        return 1;
      case END_ARRAY:
      case END_OBJECT:
      case DONE:
        return -1;
      default:
        return 0;
    }
  }

  /**
   * @param target the new {@link StructuredState} to verify.
   * @param type the {@link StructuredNodeType} of the parent or {@code null} if we are in a root object ({@link #NULL}
   *        or atomic {@link #VALUE}).
   * @return {@code true} if the transition from this {@link StructuredState} to the given {@code target}
   *         {@link StructuredState state} is valid, {@code false} otherwise.
   */
  public boolean isValidTransition(StructuredState target, StructuredNodeType type) {

    // default implementation applies for VALUE, END_ARRAY, and END_OBJECT
    if ((type == null) && (target == DONE)) {
      return true; // from root node we can go to DONE
    }
    if (type == StructuredNodeType.ARRAY) {
      return (target == VALUE) || (target == END_ARRAY) || target.isStart();
    } else if (type == StructuredNodeType.OBJECT) {
      return (target == NAME) || (target == END_OBJECT);
    }
    return false;
  }

}
