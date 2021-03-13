/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.marshall.json.impl;

/**
 * State object for reading or writing JSON.
 *
 * @since 1.0.0
 */
public class JsonState {

  final JsonState parent;

  final JsonNodeType type;

  int valueCount;

  JsonState() {

    this(null, null);
  }

  JsonState(JsonState parent, JsonNodeType type) {

    super();
    this.parent = parent;
    this.type = type;
  }

}