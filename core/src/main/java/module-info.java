
/*
 * Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0
 */
/**
 * Provides the API for mashalling and unmarshalling data to structured formats.
 *
 * @uses io.github.mmm.marshall.StructuredFormatProvider
 */
module io.github.mmm.marshall {

  requires transitive io.github.mmm.base;

  requires static io.github.mmm.scanner;

  exports io.github.mmm.marshall;

  exports io.github.mmm.marshall.id;

  exports io.github.mmm.marshall.id.impl to io.github.mmm.marshall.protobuf, io.github.mmm.marshall.mrpc;

  exports io.github.mmm.marshall.spi;

  exports io.github.mmm.marshall.standard;

  uses io.github.mmm.marshall.StructuredFormatProvider;
}
