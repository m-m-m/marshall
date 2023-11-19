/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.marshall.spi;

import io.github.mmm.marshall.MarshallingConfig;
import io.github.mmm.marshall.StructuredFormat;
import io.github.mmm.marshall.StructuredWriter;
import io.github.mmm.marshall.id.StructuredIdMappingProvider;
import io.github.mmm.marshall.id.impl.StructuredIdMappingProviderDefault;

/**
 * Abstract base implementation of {@link StructuredWriter}.
 *
 * @param <S> type of the {@link StructuredNode}.
 * @since 1.0.0
 */
public abstract class AbstractStructuredBinaryWriter<S extends StructuredNode<S>> extends AbstractStructuredWriter<S> {

  /** @see MarshallingConfig#VAR_ENCODE_ROOT_OBJECT */
  protected final boolean encodeRootObject;

  /** The {@link StructuredIdMappingProvider} - see {@link MarshallingConfig#VAR_ID_MAPPING_PROVIDER}. */
  protected final StructuredIdMappingProvider idMappingProvider;

  /**
   * The constructor.
   *
   * @param format the {@link #getFormat() format}.
   */
  public AbstractStructuredBinaryWriter(StructuredFormat format) {

    super(format);
    this.encodeRootObject = this.config.getBoolean(MarshallingConfig.VAR_ENCODE_ROOT_OBJECT,
        isEncodeRootObjectDefault());
    StructuredIdMappingProvider provider = this.config.get(MarshallingConfig.VAR_ID_MAPPING_PROVIDER);
    if (provider == null) {
      provider = StructuredIdMappingProviderDefault.get();
    }
    this.idMappingProvider = provider;
  }

  /**
   * @return the default value for {@link MarshallingConfig#VAR_ENCODE_ROOT_OBJECT}.
   */
  protected boolean isEncodeRootObjectDefault() {

    return false;
  }

}
