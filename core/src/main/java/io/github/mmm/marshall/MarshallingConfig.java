/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.marshall;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import io.github.mmm.base.config.ConfigMap;
import io.github.mmm.base.config.ConfigOption;

/**
 * {@link ConfigMap} for marshalling.
 *
 * @since 1.0.0
 */
public final class MarshallingConfig extends ConfigMap {

  /** {@link ConfigOption} for the indentation used by {@link StructuredWriter} to format the output. */
  public static final ConfigOption<String> OPT_INDENTATION = new ConfigOption<>("indentation", "  ");

  /**
   * {@link ConfigOption} to configure if {@link StructuredWriter#writeValueAsNull()} should actually write {@code null}
   * values (if configured to {@link Boolean#TRUE}) or omit {@code null} values (if configured to
   * {@link Boolean#FALSE}).
   */
  public static final ConfigOption<Boolean> OPT_WRITE_NULL_VALUES = new ConfigOption<>("write-null", Boolean.TRUE);

  /**
   * {@link ConfigOption} to configure if names can be unquoted in JSON. The default is to write names quoted and to
   * read them with or without quotes. A value of {@link Boolean#TRUE} will make
   * {@link StructuredWriter#writeName(String)} to actually write the property name without quotes ("{ property: true
   * }"). A value of {@link Boolean#FALSE} disables that {@link StructuredReader#readName()} also accepts unquoted
   * property names.<br>
   * ATTENTION: Please note that unquoted property names are invalid according to official JSON specification and many
   * parsers will not accept this. However, for internal usage and formats this may be interesting as the quotes are
   * actually pointless (since a property name should never contain a colon) and produce waste. This option is only
   * supported by {@code mmm-marshall-json} and is ignored by all other implementations including
   * {@code mmm-marshall-jsonp}.
   */
  public static final ConfigOption<Boolean> OPT_UNQUOTED_PROPERTIES = new ConfigOption<>("unquoted-properties", null);

  /**
   * {@link ConfigOption} to configure how {@link Enum} values are mapped. By default the {@link Enum#ordinal() ordinal}
   * is written while reading supports both ordinal and {@link Enum#toString() to-string}.
   */
  public static final ConfigOption<EnumFormat> OPT_ENUM_FORMAT = new ConfigOption<>("enum-format", EnumFormat.ORDINAL);

  /** Immutable instance of {@link MarshallingConfig} with the default values. */
  public static final MarshallingConfig DEFAULTS = new MarshallingConfig(Collections.emptyMap());

  /** Immutable instance of {@link MarshallingConfig} with {@link #OPT_INDENTATION} disabled. */
  public static final MarshallingConfig NO_INDENTATION = DEFAULTS.with(OPT_INDENTATION, null);

  private static final Set<String> STANDARD_KEYS = Set.of(OPT_INDENTATION.getKey(), OPT_WRITE_NULL_VALUES.getKey());

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

  /**
   * @param key the key of the proprietary and vendor specific property.
   * @param value the value for the given {@code key}.
   * @return a new {@link MarshallingConfig} with the given {@code key} set to the given {@code value} or this
   *         {@link MarshallingConfig} if the given {@code value} is already configured.
   */
  public MarshallingConfig with(String key, Object value) {

    Object oldValue = getMap().get(key);
    if (Objects.equals(oldValue, value)) {
      return this;
    }
    Map<String, Object> map = new HashMap<>(getMap());
    map.put(key, value);
    return new MarshallingConfig(map);
  }

  /**
   * @param key the {@link ConfigOption#getKey() key} of the option to check.
   * @return {@code true} if proprietary (vendor specific) and {@code false} otherwise.
   */
  public static boolean isProprietary(String key) {

    return !STANDARD_KEYS.contains(key);
  }

}
