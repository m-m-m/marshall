/*
 * Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0
 */
import io.github.mmm.marshall.StructuredFormatProvider;
import io.github.mmm.marshall.jsonp.JsonFormatProvider;

/**
 * Provides an implementation of {@code mmm-marshall} for JSON using JSON-P.
 */
module io.github.mmm.marshall.jsonp {

  requires transitive io.github.mmm.marshall;

  requires transitive java.json;

  exports io.github.mmm.marshall.jsonp;

  provides StructuredFormatProvider with JsonFormatProvider;
}
