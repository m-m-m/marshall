package io.github.mmm.marshall.snakeyaml.impl.state;

import java.nio.channels.IllegalSelectorException;

/**
 * {@link SnakeYamlParentNode} for the root when writing to snake-yaml.
 *
 * @since 1.0.0
 */
public class SnakeYamlRootNode extends SnakeYamlParentNode {

  private static final SnakeYamlRootNode INSTANCE = new SnakeYamlRootNode();

  private Object value;

  /**
   * The constructor.
   */
  public SnakeYamlRootNode() {

    super(null, null, null);
  }

  @Override
  public void addValue(String childName, Object childValue) {

    if (this == INSTANCE) {
      throw new IllegalSelectorException();
    }
    assert (childName == null);
    assert (this.value == null);
    this.value = childValue;
  }

  @Override
  public SnakeYamlNode next() {

    return null;
  }

  @Override
  public Object getValue() {

    return this.value;
  }

  @Override
  public SnakeYamlParentNode end() {

    return null;
  }

  /**
   * @return the singleton instance.
   */
  public static SnakeYamlRootNode get() {

    return INSTANCE;
  }

}
