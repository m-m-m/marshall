/*
 * Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0
 */

/**
 * Provides an implementation of {@code mmm-marshall} for JSON using JSON-P.
 *
 * @provides io.github.mmm.marshall.StructuredFormatProvider
 */
module io.github.mmm.marshall.jsonp {

  requires transitive io.github.mmm.marshall;

  requires transitive java.json;

  exports io.github.mmm.marshall.jsonp;

  provides io.github.mmm.marshall.StructuredFormatProvider with //
      io.github.mmm.marshall.jsonp.JsonpFormatProvider;
}
