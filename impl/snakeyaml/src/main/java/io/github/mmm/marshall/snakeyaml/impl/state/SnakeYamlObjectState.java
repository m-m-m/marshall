package io.github.mmm.marshall.snakeyaml.impl.state;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import io.github.mmm.marshall.StructuredReader.State;

/**
 * {@link SnakeYamlParentState} for objects as key/value pairs.
 *
 * @since 1.0.0
 */
public class SnakeYamlObjectState extends SnakeYamlParentState {

  final Map<String, Object> map;

  private Iterator<Entry<String, Object>> iterator;

  SnakeYamlObjectState(String name, SnakeYamlParentState parent, Map<String, Object> map) {

    super(name, parent);
    this.map = map;
    if (this.state == null) {
      next();
    }
  }

  @Override
  public SnakeYamlState next() {

    if (this.iterator == null) {
      if (this.map == null) {
        return null;
      }
      this.state = State.START_OBJECT;
      this.iterator = this.map.entrySet().iterator();
      return this;
    } else if (this.iterator.hasNext()) {
      this.state = null;
      Entry<String, Object> entry = this.iterator.next();
      return of(entry.getKey(), entry.getValue());
    } else if (this.state == State.END_OBJECT) {
      return this.parent;
    } else {
      this.state = State.END_OBJECT;
      return this;
    }
  }

  @Override
  public Map<String, Object> getValue() {

    return this.map;
  }

  @Override
  public void addValue(Object value) {

    String childName = getChildName();
    if (childName == null) {
      throw new IllegalStateException("Missing name");
    }
    this.map.put(childName, value);
  }

  @Override
  public boolean isObject() {

    return true;
  }

}
