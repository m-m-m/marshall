/*
 * Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0
 */

/**
 * Provides an implementation of {@code mmm-marshall} for YAML without any 3rd-party lib.
 *
 * @provides io.github.mmm.marshall.StructuredFormatProvider
 */
module io.github.mmm.marshall.snakeyaml {

  requires transitive io.github.mmm.marshall;

  requires org.yaml.snakeyaml;

  exports io.github.mmm.marshall.snakeyaml;

  provides io.github.mmm.marshall.StructuredFormatProvider with //
      io.github.mmm.marshall.snakeyaml.SnakeYamlFormatProvider;
}
