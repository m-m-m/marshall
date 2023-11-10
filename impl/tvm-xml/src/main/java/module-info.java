/*
 * Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0
 */
/**
 * Provides an implementation of {@code mmm-marshall} for XML based on StAX.
 *
 * @provides io.github.mmm.marshall.StructuredFormatProvider
 */
module io.github.mmm.marshall.tvm.xml {

  requires transitive io.github.mmm.marshall;

  requires org.teavm.jso;

  requires org.teavm.jso.apis;

  exports io.github.mmm.marshall.tvm.xml;

  provides io.github.mmm.marshall.StructuredFormatProvider with //
      io.github.mmm.marshall.tvm.xml.TvmXmlFormatProvider;
}
