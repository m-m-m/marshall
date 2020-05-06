/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.marshall.tvm.xml.impl;

/**
 *
 */
class StackNode {

  final String tag;

  final StackNode parent;

  /**
   * The constructor.
   *
   * @param tag the closing tag.
   */
  public StackNode(String tag) {

    this(tag, null);
  }

  /**
   * The constructor.
   *
   * @param tag the closing tag.
   * @param parent the parent node.
   */
  StackNode(String tag, StackNode parent) {

    super();
    this.tag = tag;
    this.parent = parent;
  }

  StackNode append(String nextTag) {

    return new StackNode(nextTag, this);
  }

}
