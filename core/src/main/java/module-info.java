
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

  exports io.github.mmm.marshall;

  exports io.github.mmm.marshall.standard;

  uses io.github.mmm.marshall.StructuredFormatProvider;
}
