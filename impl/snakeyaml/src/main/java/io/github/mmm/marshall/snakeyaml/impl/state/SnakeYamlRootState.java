package io.github.mmm.marshall.snakeyaml.impl.state;

/**
 * {@link SnakeYamlParentState} for the root when writing to snake-yaml.
 *
 * @since 1.0.0
 */
public class SnakeYamlRootState extends SnakeYamlParentState {

  private Object value;

  /**
   * The constructor.
   */
  public SnakeYamlRootState() {

    super(null, null);
  }

  @Override
  public void addValue(Object newValue) {

    assert (this.value == null);
    this.value = newValue;
  }

  @Override
  public void setChildName(String childName) {

    throw new IllegalStateException("Name (" + childName + ") not supported in current state.");
  }

  @Override
  public SnakeYamlState next() {

    return this;
  }

  @Override
  public Object getValue() {

    return this.value;
  }

  @Override
  public SnakeYamlParentState end() {

    return null;
  }

}
