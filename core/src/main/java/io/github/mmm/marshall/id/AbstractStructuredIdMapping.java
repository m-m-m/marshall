package io.github.mmm.marshall.id;

import io.github.mmm.marshall.StructuredProcessor;

/**
 * Abstract base implementation of {@link StructuredIdMapping}.
 */
public abstract class AbstractStructuredIdMapping implements StructuredIdMapping {

  /**
   * The constructor.
   */
  protected AbstractStructuredIdMapping() {

    super();
  }

  @Override
  public String name(int id) {

    if (id == TYPE) {
      return StructuredProcessor.TYPE;
    }
    return null;
  }

  @Override
  public int id(String name) {

    if (StructuredProcessor.TYPE.equals(name)) {
      return TYPE;
    }
    return 0;
  }

}
