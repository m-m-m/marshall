package io.github.mmm.marshall.snakeyaml.impl.state;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import io.github.mmm.marshall.StructuredState;
import io.github.mmm.marshall.spi.StructuredNodeType;

/**
 * {@link SnakeYamlParentNode} for objects as key/value pairs.
 *
 * @since 1.0.0
 */
public class SnakeYamlObjectNode extends SnakeYamlParentNode {

  final Map<String, Object> map;

  private Iterator<Entry<String, Object>> iterator;

  /**
   * The constructor.
   *
   * @param parent the {@link #parent}.
   * @param name the property name.
   * @param map the {@link Map} representing the {@link #getValue() value} of the object.
   */
  public SnakeYamlObjectNode(SnakeYamlNode parent, String name, Map<String, Object> map) {

    super(parent, StructuredNodeType.OBJECT, name);
    this.map = map;
    if (this.state == null) {
      next();
    }
  }

  @Override
  public SnakeYamlNode next() {

    if (this.iterator == null) {
      if (this.map == null) {
        return null;
      }
      this.state = StructuredState.START_OBJECT;
      this.iterator = this.map.entrySet().iterator();
      return this;
    } else if (this.iterator.hasNext()) {
      this.state = null;
      Entry<String, Object> entry = this.iterator.next();
      return of(entry.getKey(), entry.getValue());
    } else if (this.state == StructuredState.END_OBJECT) {
      return this.parent;
    } else {
      this.state = StructuredState.END_OBJECT;
      return this;
    }
  }

  @Override
  public Map<String, Object> getValue() {

    return this.map;
  }

  @Override
  public void addValue(String childName, Object childValue) {

    if (childName == null) {
      throw new IllegalStateException("Missing name");
    }
    this.map.put(childName, childValue);
  }

  @Override
  public boolean isObject() {

    return true;
  }

}
