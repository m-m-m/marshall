package io.github.mmm.marshall.snakeyaml.impl.state;

import java.util.Collection;
import java.util.Iterator;

import io.github.mmm.marshall.StructuredState;
import io.github.mmm.marshall.spi.StructuredNodeType;

/**
 * {@link SnakeYamlParentNode} for {@link StructuredNodeType#ARRAY}.
 */
public class SnakeYamlArrayNode extends SnakeYamlParentNode {

  final Collection<Object> array;

  private Iterator<?> iterator;

  /**
   * The constructor.
   *
   * @param parent the {@link #parent}.
   * @param name the property name.
   * @param array the {@link Collection} representing the {@link #getValue() value} of the array.
   */
  public SnakeYamlArrayNode(SnakeYamlNode parent, String name, Collection<Object> array) {

    super(parent, StructuredNodeType.ARRAY, name);
    this.array = array;
    if (this.state == null) {
      next();
    }
  }

  @Override
  public SnakeYamlNode next() {

    if (this.iterator == null) {
      if (this.array == null) {
        return null;
      }
      this.state = StructuredState.START_ARRAY;
      this.iterator = this.array.iterator();
      return this;
    } else if (this.iterator.hasNext()) {
      this.state = null;
      return of(null, this.iterator.next());
    } else if (this.state == StructuredState.END_ARRAY) {
      return this.parent;
    } else {
      this.state = StructuredState.END_ARRAY;
      return this;
    }
  }

  @Override
  public Collection<?> getValue() {

    return this.array;
  }

  @Override
  public void addValue(String childName, Object childValue) {

    assert (childName == null);
    this.array.add(childValue);
  }

  @Override
  public boolean isArray() {

    return true;
  }

}
