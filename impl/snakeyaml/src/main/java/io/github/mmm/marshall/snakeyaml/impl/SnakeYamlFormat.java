/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.marshall.snakeyaml.impl;

import java.io.Reader;
import java.io.Writer;

import org.yaml.snakeyaml.Yaml;

import io.github.mmm.base.io.AppendableWriter;
import io.github.mmm.marshall.MarshallingConfig;
import io.github.mmm.marshall.StructuredFormat;
import io.github.mmm.marshall.StructuredReader;
import io.github.mmm.marshall.StructuredTextFormat;
import io.github.mmm.marshall.StructuredWriter;

/**
 * Implementation of {@link StructuredFormat} for JSON (JavaScript Object Notation).
 *
 * @since 1.0.0
 */
public class SnakeYamlFormat implements StructuredTextFormat {

  private static final SnakeYamlFormat DEFAULT = of(MarshallingConfig.DEFAULTS);

  private final MarshallingConfig config;

  private final Yaml yaml;

  /**
   * The constructor.
   *
   * @param config the {@link MarshallingConfig}.
   * @see io.github.mmm.marshall.StructuredFormatFactory#create(String, MarshallingConfig)
   */
  public SnakeYamlFormat(MarshallingConfig config) {

    super();
    this.config = config;
    this.yaml = new Yaml();
  }

  @Override
  public MarshallingConfig getConfig() {

    return this.config;
  }

  @Override
  public String getId() {

    return ID_YAML;
  }

  @Override
  public StructuredReader reader(Reader reader) {

    Object value = this.yaml.load(reader);
    return new SnakeYamlReader(value, this);
  }

  @Override
  public StructuredReader reader(String data) {

    Object value = this.yaml.load(data);
    return new SnakeYamlReader(value, this);
  }

  @Override
  public StructuredWriter writer(Appendable appendable) {

    Writer writer = AppendableWriter.asWriter(appendable);
    return new SnakeYamlWriter(writer, this, this.yaml);
  }

  /**
   * @return the default instance of {@link SnakeYamlFormat}.
   */
  public static SnakeYamlFormat of() {

    return DEFAULT;
  }

  /**
   * @param config the {@link MarshallingConfig} for the JSON vendor implementation.
   * @return the new instance of {@link SnakeYamlFormat} with the given {@code config}.
   */
  public static SnakeYamlFormat of(MarshallingConfig config) {

    if (config == null) {
      return DEFAULT;
    }
    return new SnakeYamlFormat(config);
  }

}
