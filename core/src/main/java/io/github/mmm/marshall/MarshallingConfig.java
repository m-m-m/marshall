/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.marshall;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import io.github.mmm.base.variable.VariableDefinition;
import io.github.mmm.base.variable.VariableMap;
import io.github.mmm.marshall.id.StructuredIdMappingProvider;

/**
 * {@link VariableMap} for marshalling. It defines the standard {@link VariableDefinition variables} to configure as
 * constants using the {@code VAR_} prefix. Please note that specific formats may also define additional variables. In
 * such case, they can be found in the format specific implementation of {@link StructuredFormatProvider}.
 *
 * @since 1.0.0
 */
public final class MarshallingConfig extends VariableMap {

  /** {@link VariableDefinition} for the indentation used by {@link StructuredWriter} to format the output. */
  public static final VariableDefinition<String> VAR_INDENTATION = new VariableDefinition<>("indentation", "  ");

  /**
   * {@link VariableDefinition} to configure if {@link StructuredWriter#writeValueAsNull()} should actually write
   * {@code null} values (if configured to {@link Boolean#TRUE}) or omit {@code null} values (if configured to
   * {@link Boolean#FALSE}).
   */
  public static final VariableDefinition<Boolean> VAR_WRITE_NULL_VALUES = new VariableDefinition<>("write-null",
      Boolean.TRUE);

  /**
   * {@link VariableDefinition} to configure if names can be unquoted in JSON. The default is to write names quoted and
   * to read them with or without quotes. A value of {@link Boolean#TRUE} will make
   * {@link StructuredWriter#writeName(String)} to actually write the property name without quotes ("{ property: true
   * }"). A value of {@link Boolean#FALSE} disables that {@link StructuredReader#readName()} also accepts unquoted
   * property names.<br>
   * ATTENTION: Please note that unquoted property names are invalid according to official JSON specification and many
   * parsers will not accept this. However, for internal usage and formats this may be interesting as the quotes are
   * actually pointless (since a property name should never contain a colon) and produce waste. This option is only
   * supported by {@code mmm-marshall-json} and is ignored by all other implementations including
   * {@code mmm-marshall-jsonp}. If you do not like quoted properties you should consider using YAML format that is an
   * extension of JSON and officially supports unquoted properties.
   */
  public static final VariableDefinition<Boolean> VAR_UNQUOTED_PROPERTIES = new VariableDefinition<>(
      "unquoted-properties", null);

  /**
   * {@link VariableDefinition} to configure how {@link Enum} values are mapped. By default the {@link Enum#ordinal()
   * ordinal} is written while reading supports both ordinal and {@link Enum#toString() to-string}.
   */
  public static final VariableDefinition<EnumFormat> VAR_ENUM_FORMAT = new VariableDefinition<>("enum-format",
      EnumFormat.ORDINAL);

  /**
   * {@link VariableDefinition} to configure if the root {@link StructuredState#START_OBJECT object} should be encoded
   * explicitly (if the value is {@code true}). Otherwise if the value is {@code false} a potential root
   * {@link StructuredState#START_OBJECT object} is assumed and it will be omitted in the payload. The
   * {@link VariableDefinition#getDefaultValue() default value} is {@code null} and depends on the {@link StandardFormat
   * format}. For common {@link StructuredTextFormat text formats} such as {@link StructuredFormat#ID_JSON},
   * {@link StructuredFormat#ID_YAML}, or {@link StructuredFormat#ID_XML}, the default is obviously {@code true}.
   * However e.g. the format protobuf has {@code false} as default here. This option allows to override this default and
   * force the root object to be encoded explicitly (not recommended as it increases the message length with rather
   * redundant information) or to omit it. This {@link VariableDefinition} is only supported by some
   * {@link StructuredBinaryFormat binary formats}.
   */
  public static final VariableDefinition<Boolean> VAR_ENCODE_ROOT_OBJECT = new VariableDefinition<>("root-object",
      Boolean.class);

  /**
   * {@link VariableDefinition} to configure {@link StructuredIdMappingProvider} what is required for
   * {@link StructuredFormat#isIdBased() ID based formats}.
   */
  public static final VariableDefinition<StructuredIdMappingProvider> VAR_ID_MAPPING_PROVIDER = new VariableDefinition<>(
      "id-mapping-provider", null);

  /** Immutable instance of {@link MarshallingConfig} with the default values. */
  public static final MarshallingConfig DEFAULTS = new MarshallingConfig(Collections.emptyMap());

  /** Immutable instance of {@link MarshallingConfig} with {@link #VAR_INDENTATION} disabled. */
  public static final MarshallingConfig NO_INDENTATION = DEFAULTS.with(VAR_INDENTATION, null);

  private static final Set<String> STANDARD_KEYS = Set.of(VAR_INDENTATION.getName(), VAR_WRITE_NULL_VALUES.getName(),
      VAR_ENUM_FORMAT.getName(), VAR_UNQUOTED_PROPERTIES.getName());

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
   * @param option the {@link VariableDefinition} to set.
   * @param value the value for the given {@link VariableDefinition}.
   * @return a new {@link MarshallingConfig} with the given {@link VariableDefinition} set to the given {@code value} or
   *         this {@link MarshallingConfig} if the given {@code value} is already configured.
   */
  public <T> MarshallingConfig with(VariableDefinition<T> option, T value) {

    T oldValue = get(option);
    if (Objects.equals(oldValue, value)) {
      return this;
    }
    Map<String, Object> map = new HashMap<>(getMap());
    map.put(option.getName(), value);
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
   * @param key the {@link VariableDefinition#getName() key} of the option to check.
   * @return {@code true} if proprietary (vendor specific) and {@code false} otherwise.
   */
  public static boolean isProprietary(String key) {

    return !STANDARD_KEYS.contains(key);
  }

}
