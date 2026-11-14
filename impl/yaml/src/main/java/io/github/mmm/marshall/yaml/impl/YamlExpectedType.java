/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.marshall.yaml.impl;

import io.github.mmm.marshall.StructuredState;
import io.github.mmm.marshall.spi.StructuredNodeType;

/// Enum with groups of expected [StructuredState]s.
public enum YamlExpectedType {

  /// [StructuredState#NAME] or [StructuredState#END_OBJECT].
  NAME_OR_END {
    @Override
    public boolean accepts(StructuredState state) {

      return (state == StructuredState.NAME) || (state == StructuredState.END_OBJECT);
    }
  },

  /// [StructuredState#VALUE]
  VALUE {
    @Override
    public boolean accepts(StructuredState state) {

      return (state == StructuredState.VALUE);
    }
  },

  /// [StructuredState#VALUE], [StructuredState#START_ARRAY], or [StructuredState#START_OBJECT].
  ANY_VALUE {
    @Override
    public boolean accepts(StructuredState state) {

      return (state == StructuredState.VALUE) || (state.isStart());
    }
  },

  /// [StructuredState#VALUE], [StructuredState#START_ARRAY], [StructuredState#START_OBJECT],
  /// [StructuredState#END_ARRAY], or [StructuredState#END_OBJECT].
  ANY_VALUE_OR_END {
    @Override
    public boolean accepts(StructuredState state) {

      return (state == StructuredState.VALUE) || state.isStart() || state.isEnd();
    }
  },

  /// [StructuredState#DONE]
  DONE {
    @Override
    public boolean accepts(StructuredState state) {

      return (state == StructuredState.DONE);
    }
  };

  abstract boolean accepts(StructuredState state);

  /// @param actualState the actual [StructuredState] to verify.
  public void verify(StructuredState actualState) {

    if (!accepts(actualState)) {
      throw new IllegalStateException("Expected state " + this + " but found " + actualState);
    }
  }

  /// @param yamlState the current [YamlNode].
  /// @param state the current [StructuredState].
  /// @return the resulting [YamlExpectedType] for the next [StructuredState].
  public static YamlExpectedType of(YamlNode yamlState, StructuredState state) {

    if (yamlState.type == StructuredNodeType.ARRAY) {
      return ANY_VALUE_OR_END;
    } else if (yamlState.type == StructuredNodeType.OBJECT) {
      if (state == StructuredState.START_OBJECT) {
        return NAME_OR_END;
      } else if (state == StructuredState.NAME) {
        return ANY_VALUE;
      } else {
        return NAME_OR_END;
      }
    } else {
      assert (yamlState.type == null); // root
      if (state == null) {
        return ANY_VALUE;
      } else if (state == StructuredState.END_OBJECT) {
        return DONE;
      } else if (state == StructuredState.VALUE) {
        return DONE;
      } else if (state == StructuredState.DONE) {
        return null;
      }
    }
    throw new IllegalStateException();
  }

}
