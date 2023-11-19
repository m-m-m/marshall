package io.github.mmm.marshall.snakeyaml.impl.state;

import java.nio.channels.IllegalSelectorException;

import io.github.mmm.marshall.StructuredState;

/**
 * {@link SnakeYamlNode} for {@link StructuredState#VALUE}.
 *
 * @since 1.0.0
 */
public class SnakeYamlValueNode extends SnakeYamlNode {

  private final Object value;

  SnakeYamlValueNode(SnakeYamlParentNode parent, String name, Object value) {

    super(parent, null, name);
    this.value = value;
    if (this.state == null) {
      next();
    }
  }

  @Override
  public SnakeYamlNode next() {

    if (this.state != StructuredState.VALUE) {
      this.state = StructuredState.VALUE;
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
  public void addValue(String childName, Object childValue) {

    throw new IllegalSelectorException();
  }

  @Override
  public boolean isValue() {

    return true;
  }

}
