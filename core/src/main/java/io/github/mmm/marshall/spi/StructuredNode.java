package io.github.mmm.marshall.spi;

/**
 * Represents the current node of a stack for an {@link AbstractStructuredReader} or {@link AbstractStructuredWriter}.
 *
 * @param <SELF> this type itself.
 */
public abstract class StructuredNode<SELF extends StructuredNode<SELF>> {

  /** The parent {@link StructuredNode} node on the stack. */
  public final SELF parent;

  /** @see #getType() */
  public final StructuredNodeType type;

  /** The number of values or properties that have been written in this array or object. */
  public int elementCount;

  /**
   * The constructor.
   *
   * @param parent the parent node.
   * @param type the {@link StructuredNodeType}. Should be {@code null} for the {@link #isRoot() root node} or for a
   *        virtual child node marking a single atomic value that was written.
   */
  public StructuredNode(SELF parent, StructuredNodeType type) {

    super();
    this.parent = parent;
    this.type = type;
  }

  /**
   * @return {@code true} if this is the root node, {@code false} otherwise.
   */
  public final boolean isRoot() {

    return this.parent == null;
  }

  /**
   * @return {@code true} if this is the top-level {@link StructuredNodeType#OBJECT object}, {@code false} otherwise.
   */
  public final boolean isTopObject() {

    return ((this.type == StructuredNodeType.OBJECT) && (this.parent.parent == null));
  }

  /**
   * @return the {@link StructuredNodeType}.
   */
  public StructuredNodeType getType() {

    if ((this.type == null) && (this.parent != null)) {
      return ((StructuredNode<?>) this.parent).type; // for value node get parent object/array type
    }
    return this.type;
  }

  /**
   * End this state and return the parent.
   *
   * @return the parent state.
   */
  public SELF end() {

    return this.parent;
  }

  @Override
  public String toString() {

    StringBuilder sb = new StringBuilder();
    StructuredNode<?> node = this;
    do {
      if (!sb.isEmpty()) {
        sb.append(">");
      }
      if (node.type == null) {
        if (node.parent == null) {
          sb.append("ROOT");
        } else {
          sb.append("VALUE");
        }
      } else {
        sb.append(node.type);
      }
      node = node.parent;
    } while (node != null);
    return sb.toString();
  }

}
