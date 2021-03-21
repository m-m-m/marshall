/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.marshall.yaml.impl;

import io.github.mmm.marshall.StructuredReader.State;
import io.github.mmm.marshall.spi.StructuredNodeType;

/**
 *
 */
public enum YamlExpectedType {

  /** {@link State#NAME} or {@link State#END_OBJECT}. */
  NAME_OR_END {
    @Override
    public boolean accepts(State state) {

      return (state == State.NAME) || (state == State.END_OBJECT);
    }
  },

  /** {@link State#VALUE} */
  VALUE {
    @Override
    public boolean accepts(State state) {

      return (state == State.VALUE);
    }
  },

  /** {@link State#VALUE}, {@link State#START_ARRAY}, or {@link State#START_OBJECT}. */
  ANY_VALUE {
    @Override
    public boolean accepts(State state) {

      return (state == State.VALUE) || (state.isStart());
    }
  },

  /**
   * {@link State#VALUE}, {@link State#START_ARRAY}, {@link State#START_OBJECT}, {@link State#END_ARRAY}, or
   * {@link State#END_OBJECT}.
   */
  ANY_VALUE_OR_END {
    @Override
    public boolean accepts(State state) {

      return (state == State.VALUE) || state.isStart() || state.isEnd();
    }
  },

  /** {@link State#DONE} */
  DONE {
    @Override
    public boolean accepts(State state) {

      return (state == State.DONE);
    }
  };

  abstract boolean accepts(State state);

  /**
   * @param actualState the actual {@link State} to verify.
   */
  public void verify(State actualState) {

    if (!accepts(actualState)) {
      throw new IllegalStateException("Expected state " + this + " but found " + actualState);
    }
  }

  /**
   * @param yamlState the current {@link YamlState}.
   * @param state the current {@link State}.
   * @return the resulting {@link YamlExpectedType} for the next {@link State}.
   */
  public static YamlExpectedType of(YamlState yamlState, State state) {

    if (yamlState.type == StructuredNodeType.ARRAY) {
      return ANY_VALUE_OR_END;
    } else if (yamlState.type == StructuredNodeType.OBJECT) {
      if (state == State.START_OBJECT) {
        return NAME_OR_END;
      } else if (state == State.NAME) {
        return ANY_VALUE;
      } else {
        return NAME_OR_END;
      }
    } else {
      assert (yamlState.type == null); // root
      if (state == null) {
        return ANY_VALUE;
      } else if (state == State.END_OBJECT) {
        return DONE;
      } else if (state == State.VALUE) {
        return DONE;
      } else if (state == State.DONE) {
        return null;
      }
    }
    throw new IllegalStateException();
  }

}
