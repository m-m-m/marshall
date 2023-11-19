package io.github.mmm.marshall.snakeyaml.impl.state;

import io.github.mmm.marshall.spi.StructuredNodeType;

/**
 * {@link SnakeYamlNode} that contains children as either {@link SnakeYamlObjectNode} or {@link SnakeYamlArrayNode}.
 *
 * @since 1.0.0
 */
public abstract class SnakeYamlParentNode extends SnakeYamlNode {

  SnakeYamlParentNode(SnakeYamlNode parent, StructuredNodeType type, String name) {

    super(parent, type, name);
  }

  /**
   * @param childName the {@link #getName() name}. May be {@code null}.
   * @param childValue the {@link #getValue() value}. May be {@code null}.
   * @return the new child {@link SnakeYamlNode}.
   */
  public SnakeYamlNode of(String childName, Object childValue) {

    return of(childName, this, childValue);
  }

  /**
   * @return the parent state.
   */
  @Override
  public SnakeYamlNode end() {

    this.parent.addValue(this.name, getValue());
    return this.parent;
  }

}
