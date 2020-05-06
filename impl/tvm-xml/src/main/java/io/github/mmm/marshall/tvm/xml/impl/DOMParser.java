/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.marshall.tvm.xml.impl;

import org.teavm.jso.JSBody;
import org.teavm.jso.JSObject;
import org.teavm.jso.dom.xml.Document;

/**
 * {@link JSObject} for {@code DOMParser}.
 *
 * @since 1.0.0
 */
public abstract class DOMParser implements JSObject {

  /**
   * @param xml the XML to parse.
   * @param mimeType the mimetype (typically "application/xml").
   * @return the parsed {@link Document}.
   */
  public abstract Document parseFromString(String xml, String mimeType);

  /**
   * @return a new instance of {@link DOMParser}.
   */
  @JSBody(script = "return new DOMParser();")
  public static native DOMParser of();

}
