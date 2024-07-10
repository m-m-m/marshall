/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.marshall;

/**
 * Abstract base implementation of {@link StructuredFormatProvider}.
 */
public abstract class AbstractStructuredFormatProvider implements StructuredFormatProvider {

  /**
   * @return an array of aliases of this provider.
   * @see #getId()
   * @see StructuredFormatFactory#getProvider(String)
   */
  public abstract String[] getAliases();

}
