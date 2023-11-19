/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.marshall.tvm.xml.impl;

import java.util.Objects;

import io.github.mmm.marshall.spi.StructuredNodeType;
import io.github.mmm.marshall.spi.StructuredNode;

/**
 * Simple node of a stack.
 */
class TvmXmlState extends StructuredNode<TvmXmlState> {

  final String tag;

  /**
   * The constructor.
   *
   * @param parent the parent node.
   * @param tag the closing tag.
   */
  TvmXmlState(TvmXmlState parent, StructuredNodeType type, String tag) {

    super(parent, type);
    Objects.requireNonNull(tag);
    this.tag = tag;
  }

}
