/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.marshall.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.mmm.base.exception.ObjectNotFoundException;
import io.github.mmm.base.service.ServiceHelper;
import io.github.mmm.marshall.AbstractStructuredFormatProvider;
import io.github.mmm.marshall.StructuredFormatFactory;
import io.github.mmm.marshall.StructuredFormatProvider;

/**
 * Implementation of {@link StructuredFormatFactory}.
 *
 * @since 1.0.0
 */
public class StructuredFormatFactoryImpl implements StructuredFormatFactory {

  private static final Logger LOG = LoggerFactory.getLogger(StructuredFormatFactoryImpl.class);

  /** The singleton instance. */
  public static final StructuredFormatFactoryImpl INSTANCE = new StructuredFormatFactoryImpl();

  private final Map<String, StructuredFormatProvider> providerMap;

  private final Map<String, StructuredFormatProvider> aliasMap;

  /**
   * The constructor.
   */
  protected StructuredFormatFactoryImpl() {

    super();
    this.providerMap = new HashMap<>();
    this.aliasMap = new HashMap<>();
    ServiceHelper.all(ServiceLoader.load(StructuredFormatProvider.class), this.providerMap,
        StructuredFormatProvider::getId);
    for (StructuredFormatProvider provider : this.providerMap.values()) {
      if (provider instanceof AbstractStructuredFormatProvider asfp) {
        for (String alias : asfp.getAliases()) {
          StructuredFormatProvider duplicate = this.aliasMap.put(alias, provider);
          if (duplicate != null) {
            LOG.warn("Duplicate alias {} used by {} and {}", alias, duplicate, provider);
          }
        }
      }
    }
  }

  @Override
  public StructuredFormatProvider getProvider(String format) {

    StructuredFormatProvider provider = this.providerMap.get(format);
    if (provider == null) {
      provider = this.aliasMap.get(format);
      if (provider == null) {
        throw new ObjectNotFoundException("StructuredFormatProvider", format);
      }
    }
    return provider;
  }

}
