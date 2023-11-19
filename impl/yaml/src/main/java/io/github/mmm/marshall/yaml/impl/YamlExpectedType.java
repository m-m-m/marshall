/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.marshall.yaml.impl;

import io.github.mmm.marshall.StructuredState;
import io.github.mmm.marshall.spi.StructuredNodeType;

/**
 * Enum with groups of expected {@link StructuredState}s.
 */
public enum YamlExpectedType {

  /** {@link StructuredState#NAME} or {@link StructuredState#END_OBJECT}. */
  NAME_OR_END {
    @Override
    public boolean accepts(StructuredState state) {

      return (state == StructuredState.NAME) || (state == StructuredState.END_OBJECT);
    }
  },

  /** {@link StructuredState#VALUE} */
  VALUE {
    @Override
    public boolean accepts(StructuredState state) {

      return (state == StructuredState.VALUE);
    }
  },

  /** {@link StructuredState#VALUE}, {@link StructuredState#START_ARRAY}, or {@link StructuredState#START_OBJECT}. */
  ANY_VALUE {
    @Override
    public boolean accepts(StructuredState state) {

      return (state == StructuredState.VALUE) || (state.isStart());
    }
  },

  /**
   * {@link StructuredState#VALUE}, {@link StructuredState#START_ARRAY}, {@link StructuredState#START_OBJECT},
   * {@link StructuredState#END_ARRAY}, or {@link StructuredState#END_OBJECT}.
   */
  ANY_VALUE_OR_END {
    @Override
    public boolean accepts(StructuredState state) {

      return (state == StructuredState.VALUE) || state.isStart() || state.isEnd();
    }
  },

  /** {@link StructuredState#DONE} */
  DONE {
    @Override
    public boolean accepts(StructuredState state) {

      return (state == StructuredState.DONE);
    }
  };

  abstract boolean accepts(StructuredState state);

  /**
   * @param actualState the actual {@link StructuredState} to verify.
   */
  public void verify(StructuredState actualState) {

    if (!accepts(actualState)) {
      throw new IllegalStateException("Expected state " + this + " but found " + actualState);
    }
  }

  /**
   * @param yamlState the current {@link YamlNode}.
   * @param state the current {@link StructuredState}.
   * @return the resulting {@link YamlExpectedType} for the next {@link StructuredState}.
   */
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
