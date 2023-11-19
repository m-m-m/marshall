/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.marshall.spi;

/**
 * Default implementation of {@link StructuredNode} to be used if no further state information is required.
 *
 * @since 1.0.0
 */
public class StructuredNodeDefault extends StructuredNode<StructuredNodeDefault> {

  /**
   * The constructor.
   *
   * @param parent the parent node.
   * @param type the {@link StructuredNodeType}. Should be {@code null} for the {@link #isRoot() root node} or for a
   *        virtual child node marking a single atomic value that was written.
   */
  public StructuredNodeDefault(StructuredNodeDefault parent, StructuredNodeType type) {

    super(parent, type);
  }

}