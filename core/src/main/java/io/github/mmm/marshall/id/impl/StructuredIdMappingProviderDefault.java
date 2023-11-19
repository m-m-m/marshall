package io.github.mmm.marshall.id.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.github.mmm.marshall.id.StructuredIdMapping;
import io.github.mmm.marshall.id.StructuredIdMappingObject;
import io.github.mmm.marshall.id.StructuredIdMappingProvider;

/**
 * Default implementation of {@link StructuredIdMappingProvider}.
 */
public class StructuredIdMappingProviderDefault implements StructuredIdMappingProvider {

  private static final StructuredIdMappingProviderDefault INSTANCE = new StructuredIdMappingProviderDefault();

  private final Map<Object, StructuredIdMapping> idMappings;

  /**
   * The constructor.
   */
  public StructuredIdMappingProviderDefault() {

    super();
    this.idMappings = new ConcurrentHashMap<>();
  }

  @Override
  public StructuredIdMapping getMapping(StructuredIdMappingObject object) {

    Object key = object.asTypeKey();
    if (key == null) {
      return object.defineIdMapping();
    }
    StructuredIdMapping idMapping = this.idMappings.computeIfAbsent(key, k -> object.defineIdMapping());
    return idMapping;
  }

  /**
   * @return the singleton instance.
   */
  public static StructuredIdMappingProviderDefault get() {

    return INSTANCE;
  }

}
