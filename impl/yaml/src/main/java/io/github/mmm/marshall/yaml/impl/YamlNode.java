/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.marshall.yaml.impl;

import io.github.mmm.marshall.spi.StructuredNodeType;
import io.github.mmm.marshall.spi.StructuredNode;

/**
 * State object for reading or writing JSON.
 *
 * @since 1.0.0
 */
public class YamlNode extends StructuredNode<YamlNode> {

  final boolean json;

  /** the number of spaces the line is indented with. */
  final int column;

  YamlNode(YamlNode parent, StructuredNodeType type) {

    this(parent, type, false);
  }

  YamlNode(YamlNode parent, StructuredNodeType type, boolean json) {

    this(parent, type, json, 0);
  }

  YamlNode(YamlNode parent, StructuredNodeType type, boolean json, int column) {

    super(parent, type);
    this.json = json;
    this.column = column;
  }

  boolean isJsonArray() {

    return (this.type == StructuredNodeType.ARRAY) && this.json;
  }

  boolean isYamlArray() {

    return (this.type == StructuredNodeType.ARRAY) && !this.json;
  }

}