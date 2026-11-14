package io.github.mmm.marshall.snakeyaml.impl.state;

import io.github.mmm.marshall.spi.StructuredNodeType;

/// [SnakeYamlNode] that contains children as either [SnakeYamlObjectNode] or [SnakeYamlArrayNode].
///
/// @since 1.0.0
public abstract class SnakeYamlParentNode extends SnakeYamlNode {

  SnakeYamlParentNode(SnakeYamlNode parent, StructuredNodeType type, String name) {

    super(parent, type, name);
  }

  /// @param childName the [name][#getName()]. May be `null`.
  /// @param childValue the [value][#getValue()]. May be `null`.
  /// @return the new child [SnakeYamlNode].
  public SnakeYamlNode of(String childName, Object childValue) {

    return of(childName, this, childValue);
  }

  /// @return the parent state.
  @Override
  public SnakeYamlNode end() {

    this.parent.addValue(this.name, getValue());
    return this.parent;
  }

}
