/*
 * Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0
 */
import io.github.mmm.marshall.StructuredFormatProvider;
import io.github.mmm.marshall.stax.XmlFormatProvider;

/**
 * Provides an implementation of {@code mmm-marshall} for XML based on StAX.
 */
module io.github.mmm.marshall.stax {

  requires transitive io.github.mmm.marshall;

  requires transitive java.xml;

  exports io.github.mmm.marshall.stax;

  provides StructuredFormatProvider with XmlFormatProvider;
}
