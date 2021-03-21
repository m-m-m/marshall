/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.marshall.yaml.impl;

import io.github.mmm.marshall.spi.StructuredNodeType;

/**
 * State object for reading or writing JSON.
 *
 * @since 1.0.0
 */
public class YamlState {

  final YamlState parent;

  final StructuredNodeType type;

  /** the number of spaces the line is indented with. */
  final int column;

  final boolean json;

  int valueCount;

  YamlState() {

    this(null, null);
  }

  YamlState(YamlState parent, StructuredNodeType type) {

    this(parent, type, false);
  }

  YamlState(YamlState parent, StructuredNodeType type, boolean json) {

    this(parent, type, json, 0);
  }

  YamlState(YamlState parent, StructuredNodeType type, boolean json, int column) {

    super();
    this.parent = parent;
    this.type = type;
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