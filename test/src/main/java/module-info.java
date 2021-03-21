/*
 * Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0
 */

/**
 * Provides TCK for {@code mmm-marshall}.
 */
module io.github.mmm.marshall.test {

  requires transitive io.github.mmm.marshall;

  requires transitive io.github.mmm.binary;

  requires transitive org.junit.jupiter.api;

  requires transitive org.assertj.core;

  exports io.github.mmm.marshall.test;

}
