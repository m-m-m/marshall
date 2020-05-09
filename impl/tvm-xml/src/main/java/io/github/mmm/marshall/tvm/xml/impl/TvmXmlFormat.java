/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.marshall.tvm.xml.impl;

import java.io.IOException;
import java.io.Reader;

import org.teavm.jso.dom.xml.Document;

import io.github.mmm.marshall.MarshallableObject;
import io.github.mmm.marshall.MarshallingConfig;
import io.github.mmm.marshall.StructuredFormat;
import io.github.mmm.marshall.StructuredReader;
import io.github.mmm.marshall.StructuredWriter;

/**
 * Implementation of {@link StructuredFormat} for XML using TeaVM.
 *
 * @since 1.0.0
 */
public class TvmXmlFormat implements StructuredFormat {

  private static final TvmXmlFormat DEFAULT = new TvmXmlFormat(MarshallingConfig.DEFAULTS);

  private final MarshallingConfig config;

  /**
   * The constructor.
   *
   * @param config the {@link MarshallingConfig}.
   */
  public TvmXmlFormat(MarshallingConfig config) {

    super();
    this.config = config;
  }

  @Override
  public String getId() {

    return ID_XML;
  }

  @Override
  public StructuredReader reader(String xml) {

    Document document = DOMParser.of().parseFromString(xml, StructuredFormat.ID_XML);
    return new TvmXmlDocumentReader(document.getDocumentElement(), this.config);
  }

  @Override
  public StructuredReader reader(Object data) {

    if (data instanceof Document) {
      return new TvmXmlDocumentReader(((Document) data).getDocumentElement(), this.config);
    }
    return StructuredFormat.super.reader(data);
  }

  @Override
  public StructuredReader reader(Reader reader) {

    try {
      char[] buf = new char[1024];
      StringBuilder sb = new StringBuilder();
      int chars;
      do {
        chars = reader.read(buf, 0, buf.length);
        if (chars > 0) {
          sb.append(buf, 0, chars);
        }
      } while (chars >= 0);
      return reader(sb.toString());
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
  }

  @Override
  public String write(MarshallableObject object) {

    if (object == null) {
      return null;
    }
    StringBuilder writer = new StringBuilder(512);
    StructuredWriter structuredWriter = new TvmXmlStringWriter(writer, this.config);
    object.write(structuredWriter);
    return writer.toString();
  }

  @Override
  public StructuredWriter writer(Appendable writer) {

    return new TvmXmlStringWriter(writer, this.config);
  }

  /**
   * @return the default instance of {@link TvmXmlFormat}.
   */
  public static TvmXmlFormat of() {

    return DEFAULT;
  }

}
