/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.marshall.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

import io.github.mmm.base.exception.ObjectNotFoundException;
import io.github.mmm.base.service.ServiceHelper;
import io.github.mmm.marshall.StructuredFormatFactory;
import io.github.mmm.marshall.StructuredFormatProvider;

/**
 * Implementation of {@link StructuredFormatFactory}.
 *
 * @since 1.0.0
 */
public class StructuredFormatFactoryImpl implements StructuredFormatFactory {

  /** The singleton instance. */
  public static final StructuredFormatFactoryImpl INSTANCE = new StructuredFormatFactoryImpl();

  private final Map<String, StructuredFormatProvider> providerMap;

  /**
   * The constructor.
   */
  protected StructuredFormatFactoryImpl() {

    super();
    this.providerMap = new HashMap<>();
    ServiceHelper.all(ServiceLoader.load(StructuredFormatProvider.class), this.providerMap,
        StructuredFormatProvider::getId);
  }

  @Override
  public StructuredFormatProvider getProvider(String format) {

    StructuredFormatProvider provider = this.providerMap.get(format);
    if (provider == null) {
      throw new ObjectNotFoundException("StructuredFormatProvider", format);
    }
    return provider;
  }

}
