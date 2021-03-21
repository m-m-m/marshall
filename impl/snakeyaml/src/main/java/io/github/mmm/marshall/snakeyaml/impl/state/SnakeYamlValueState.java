package io.github.mmm.marshall.snakeyaml.impl.state;

import io.github.mmm.marshall.StructuredReader.State;

/**
 * {@link SnakeYamlState} for {@link State#VALUE}.
 *
 * @since 1.0.0
 */
public class SnakeYamlValueState extends SnakeYamlState {

  private final Object value;

  SnakeYamlValueState(String name, SnakeYamlParentState parent, Object value) {

    super(name, parent);
    this.value = value;
    if (this.state == null) {
      next();
    }
  }

  @Override
  public SnakeYamlState next() {

    if (this.state != State.VALUE) {
      this.state = State.VALUE;
      return this;
    }
    this.state = null;
    return this.parent;
  }

  @Override
  public Object getValue() {

    return this.value;
  }

  @Override
  public boolean isValue() {

    return true;
  }

}
