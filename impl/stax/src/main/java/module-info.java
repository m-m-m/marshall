/*
 * Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0
 */

/**
 * Provides an implementation of {@code mmm-marshall} for XML based on StAX.
 */
module io.github.mmm.marshall.stax {

  requires transitive io.github.mmm.marshall;

  // JPMS bug with transitive dependencies...
  requires transitive io.github.mmm.base;

  requires transitive java.xml;

  exports io.github.mmm.marshall.stax;

  provides io.github.mmm.marshall.StructuredFormatProvider with //
      io.github.mmm.marshall.stax.StaxFormatProvider;
}
