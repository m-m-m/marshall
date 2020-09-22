/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.marshall;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import io.github.mmm.base.config.ConfigMap;
import io.github.mmm.base.config.ConfigOption;

/**
 * {@link ConfigMap} for marshalling.
 *
 * @since 1.0.0
 */
public final class MarshallingConfig extends ConfigMap {

  /** {@link ConfigOption} for the indendation used by {@link StructuredWriter} to format the output. */
  public static final ConfigOption<String> INDENDATION = new ConfigOption<>("indendation", "  ");

  /**
   * {@link ConfigOption} to configure if {@link StructuredWriter#writeValueAsNull()} should actually write {@code null}
   * values (if configured to {@link Boolean#TRUE}) or omit {@code null} values (if configured to
   * {@link Boolean#FALSE}).
   */
  public static final ConfigOption<Boolean> WRITE_NULL_VALUES = new ConfigOption<>("indendation", Boolean.TRUE);

  /** Immutable instance of {@link MarshallingConfig} with the default values. */
  public static final MarshallingConfig DEFAULTS = new MarshallingConfig(Collections.emptyMap());

  /**
   * The constructor.
   */
  public MarshallingConfig() {

    super();
  }

  /**
   * The constructor.
   *
   * @param map the raw {@link Map} with the configuration values.
   */
  public MarshallingConfig(Map<String, Object> map) {

    super(map);
  }

  /**
   * @param <T> type of the value.
   * @param option the {@link ConfigOption} to set.
   * @param value the value for the given {@link ConfigOption}.
   * @return a new {@link MarshallingConfig} with the given {@link ConfigOption} set to the given {@code value} or this
   *         {@link MarshallingConfig} if the given {@code value} is already configured.
   */
  public <T> MarshallingConfig with(ConfigOption<T> option, T value) {

    T oldValue = get(option);
    if (Objects.equals(oldValue, value)) {
      return this;
    }
    Map<String, Object> map = new HashMap<>(getMap());
    map.put(option.getKey(), value);
    return new MarshallingConfig(map);
  }

}
