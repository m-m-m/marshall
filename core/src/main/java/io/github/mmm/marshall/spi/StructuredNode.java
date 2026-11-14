package io.github.mmm.marshall.spi;

/// Represents the current node of a stack for an [AbstractStructuredReader] or [AbstractStructuredWriter].
///
/// @param <SELF> this type itself.
public abstract class StructuredNode<SELF extends StructuredNode<SELF>> {

  /// The parent [StructuredNode] node on the stack.
  public final SELF parent;

  /// @see #getType()
  public final StructuredNodeType type;

  /// The number of values or properties that have been written in this array or object.
  public int elementCount;

  /// The constructor.
  ///
  /// @param parent the parent node.
  /// @param type the [StructuredNodeType]. Should be `null` for the [root node][#isRoot()] or for a
  ///        virtual child node marking a single atomic value that was written.
  public StructuredNode(SELF parent, StructuredNodeType type) {

    super();
    this.parent = parent;
    this.type = type;
  }

  /// @return `true` if this is the root node, `false` otherwise.
  public final boolean isRoot() {

    return this.parent == null;
  }

  /// @return `true` if this is the top-level [object][StructuredNodeType#OBJECT], `false` otherwise.
  public final boolean isTopObject() {

    return ((this.type == StructuredNodeType.OBJECT) && (this.parent.parent == null));
  }

  /// @return the [StructuredNodeType].
  public StructuredNodeType getType() {

    if ((this.type == null) && (this.parent != null)) {
      return ((StructuredNode<?>) this.parent).type; // for value node get parent object/array type
    }
    return this.type;
  }

  /// End this state and return the parent.
  ///
  /// @return the parent state.
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
