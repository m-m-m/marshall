/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.marshall.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

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
    ServiceLoader<StructuredFormatProvider> serviceLoader = ServiceLoader.load(StructuredFormatProvider.class);
    for (StructuredFormatProvider provider : serviceLoader) {
      registerProvider(provider);
    }
    // if (this.providerMap.isEmpty()) {
    // throw new IllegalStateException("No StructuredFormatProvider available!");
    // }
  }

  /**
   * @param provider the {@link StructuredFormatProvider} to register.
   */
  public void registerProvider(StructuredFormatProvider provider) {

    StructuredFormatProvider duplicate = this.providerMap.put(provider.getName(), provider);
    if (duplicate != null) {

    }
  }

  @Override
  public StructuredFormatProvider getProvider(String format) {

    return this.providerMap.get(format);
  }

}
