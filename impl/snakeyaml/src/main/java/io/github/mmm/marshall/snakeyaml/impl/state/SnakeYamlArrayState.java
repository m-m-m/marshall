package io.github.mmm.marshall.snakeyaml.impl.state;

import java.util.Collection;
import java.util.Iterator;

import io.github.mmm.marshall.StructuredReader.State;

/**
 *
 */
public class SnakeYamlArrayState extends SnakeYamlParentState {

  final Collection<Object> array;

  private Iterator<?> iterator;

  SnakeYamlArrayState(String name, SnakeYamlParentState parent, Collection<Object> array) {

    super(name, parent);
    this.array = array;
    if (this.state == null) {
      next();
    }
  }

  @Override
  public SnakeYamlState next() {

    if (this.iterator == null) {
      if (this.array == null) {
        return null;
      }
      this.state = State.START_ARRAY;
      this.iterator = this.array.iterator();
      return this;
    } else if (this.iterator.hasNext()) {
      this.state = null;
      return of(null, this.iterator.next());
    } else if (this.state == State.END_ARRAY) {
      return this.parent;
    } else {
      this.state = State.END_ARRAY;
      return this;
    }
  }

  @Override
  public Collection<?> getValue() {

    return this.array;
  }

  @Override
  public void addValue(Object value) {

    this.array.add(value);
  }

  @Override
  public boolean isArray() {

    return true;
  }

  @Override
  public void setChildName(String childName) {

    throw new IllegalStateException("Name (" + childName + ") not supported in current state.");
  }

}
