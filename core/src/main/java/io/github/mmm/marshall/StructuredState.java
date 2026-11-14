package io.github.mmm.marshall;

import io.github.mmm.marshall.id.StructuredIdMappingObject;
import io.github.mmm.marshall.spi.StructuredNodeType;

/// Enum with the possible states of a [StructuredReader] or [StructuredWriter].
///
/// @see StructuredReader#getState()
public enum StructuredState {

  /// The initial state of a [StructuredWriter] before anything has been written. May also be used in edge-cases
  /// when a [StructuredReader] is in a state that can not be determined exactly, what can only happen in formats
  /// like gRPC/protobuf that are actually not designed to read generic data.
  NULL() {
    @Override
    public boolean isValidTransition(StructuredState target, StructuredNodeType type) {

      return (target == VALUE) || (target == DONE) | target.isStart();
    }
  },

  /// Start of an array.
  ///
  /// @see StructuredReader#readStartArray()
  /// @see StructuredWriter#writeStartArray()
  START_ARRAY() {
    @Override
    public boolean isValidTransition(StructuredState target, StructuredNodeType type) {

      return (target == VALUE) || (target == END_ARRAY) || target.isStart();
    }
  },

  /// Start of an object.
  ///
  /// @see StructuredReader#readStartObject(StructuredIdMappingObject)
  /// @see StructuredWriter#writeStartObject(StructuredIdMappingObject)
  START_OBJECT() {
    @Override
    public boolean isValidTransition(StructuredState target, StructuredNodeType type) {

      return (target == NAME) || (target == END_OBJECT);
    }
  },

  /// A regular value. Can either be an atomic value (only a single value in payload without any [start][#isStart()]), an [array][#START_ARRAY] value or a [property][#NAME] value of an [object][#START_OBJECT].
  ///
  /// @see StructuredReader#readValue()
  /// @see StructuredWriter#writeValue(Object)
  VALUE,

  /// Name of a property.
  ///
  /// @see StructuredReader#readName()
  NAME() {
    @Override
    public boolean isValidTransition(StructuredState target, StructuredNodeType type) {

      return (target == VALUE) || target.isStart();
    }
  },

  /// End of an array.
  ///
  /// @see StructuredReader#readEnd()
  /// @see StructuredWriter#writeEnd()
  /// @see #START_ARRAY
  END_ARRAY,

  /// End of an object.
  ///
  /// @see StructuredReader#readEnd()
  /// @see StructuredReader#readStartObject(StructuredIdMappingObject)
  END_OBJECT,

  /// End of data.
  ///
  /// @see StructuredReader#isDone()
  DONE() {
    @Override
    public boolean isValidTransition(StructuredState target, StructuredNodeType type) {

      return false;
    }
  };

  /// @return `true` if a start [StructuredState] such as [#START_ARRAY] or [#START_OBJECT],
  ///         `false` otherwise.
  public boolean isStart() {

    switch (this) {
      case START_ARRAY:
      case START_OBJECT:
        return true;
      default:
        return false;
    }
  }

  /// @return `true` if an end [StructuredState] such as [#END_ARRAY] or [#END_OBJECT],
  ///         `false` otherwise.
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

  /// A value to add to the current depth when entering this state.
  ///
  /// @return `1` if [start][#isStart()], `-1` if [end][#isEnd()], and `0` otherwise.
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

  /// @param target the new [StructuredState] to verify.
  /// @param type the [StructuredNodeType] of the parent or `null` if we are in a root object ([#NULL]
  ///        or atomic [#VALUE]).
  /// @return `true` if the transition from this [StructuredState] to the given `target`
  ///         [state][StructuredState] is valid, `false` otherwise.
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
