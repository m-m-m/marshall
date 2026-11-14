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

/// [VariableMap] for marshalling. It defines the standard [variables][VariableDefinition] to configure as
/// constants using the `VAR_` prefix. Please note that specific formats may also define additional variables. In
/// such case, they can be found in the format specific implementation of [StructuredFormatProvider].
///
/// @since 1.0.0
public final class MarshallingConfig extends VariableMap {

  /// [VariableDefinition] for the indentation used by [StructuredWriter] to format the output.
  public static final VariableDefinition<String> VAR_INDENTATION = VariableDefinition.ofString("indentation", "  ");

  /// [VariableDefinition] to configure if [StructuredWriter#writeValueAsNull()] should actually write
  /// `null` values (if configured to [Boolean#TRUE]) or omit `null` values (if configured to
  /// [Boolean#FALSE]).
  public static final VariableDefinition<Boolean> VAR_WRITE_NULL_VALUES = VariableDefinition.ofBoolean("write-null",
      Boolean.TRUE);

  /// [VariableDefinition] to configure if names can be unquoted in JSON. The default is to write names quoted and
  /// to read them with or without quotes. A value of [Boolean#TRUE] will make
  /// [StructuredWriter#writeName(String)] to actually write the property name without quotes ("{ property: true
  /// }"). A value of [Boolean#FALSE] disables that [StructuredReader#readName()] also accepts unquoted
  /// property names.
  /// ATTENTION: Please note that unquoted property names are invalid according to official JSON specification and many
  /// parsers will not accept this. However, for internal usage and formats this may be interesting as the quotes are
  /// actually pointless (since a property name should never contain a colon) and produce waste. This option is only
  /// supported by `mmm-marshall-json` and is ignored by all other implementations including
  /// `mmm-marshall-jsonp`. If you do not like quoted properties you should consider using YAML format that is an
  /// extension of JSON and officially supports unquoted properties.
  public static final VariableDefinition<Boolean> VAR_UNQUOTED_PROPERTIES = VariableDefinition
      .ofBoolean("unquoted-properties", null);

  /// [VariableDefinition] to configure how [Enum] values are mapped. By default the [ordinal][Enum#ordinal()] is written while reading supports both ordinal and [to-string][Enum#toString()].
  public static final VariableDefinition<EnumFormat> VAR_ENUM_FORMAT = VariableDefinition.ofEnum("enum-format",
      EnumFormat.ORDINAL);

  /// [VariableDefinition] to configure if the root [object][StructuredState#START_OBJECT] should be encoded
  /// explicitly (if the value is `true`). Otherwise if the value is `false` a potential root
  /// [object][StructuredState#START_OBJECT] is assumed and it will be omitted in the payload. The
  /// [default value][VariableDefinition#getDefaultValue()] is `null` and depends on the [format][StandardFormat]. For common [text formats][StructuredTextFormat] such as [StructuredFormat#ID_JSON],
  /// [StructuredFormat#ID_YAML], or [StructuredFormat#ID_XML], the default is obviously `true`.
  /// However e.g. the format protobuf has `false` as default here. This option allows to override this default and
  /// force the root object to be encoded explicitly (not recommended as it increases the message length with rather
  /// redundant information) or to omit it. This [VariableDefinition] is only supported by some
  /// [binary formats][StructuredBinaryFormat].
  public static final VariableDefinition<Boolean> VAR_ENCODE_ROOT_OBJECT = VariableDefinition.ofBoolean("root-object",
      null);

  /// [VariableDefinition] to configure [StructuredIdMappingProvider] what is required for
  /// [ID based formats][StructuredFormat#isIdBased()].
  public static final VariableDefinition<StructuredIdMappingProvider> VAR_ID_MAPPING_PROVIDER = new VariableDefinition<>(
      "id-mapping-provider", StructuredIdMappingProvider.class, null, true, _ -> {
        throw new UnsupportedOperationException();
      });

  /// [VariableDefinition] to configure if the `target` of a `Link` should be included. Its value has
  /// the following meaning:
  ///
  /// - `null`: If the `Id` is `null` or transient, the link target will be written. If the target is
  /// present, it will always be read. (default)
  /// - `true`: Always include link target. (mainly for debugging)
  /// - `false`: Never read or write link targets.
  public static final VariableDefinition<Boolean> VAR_LINK_TARGET = VariableDefinition.ofBoolean("link-target", null);

  /// Immutable instance of [MarshallingConfig] with the default values.
  public static final MarshallingConfig DEFAULTS = new MarshallingConfig(Collections.emptyMap());

  /// Immutable instance of [MarshallingConfig] with [#VAR_INDENTATION] disabled.
  public static final MarshallingConfig NO_INDENTATION = DEFAULTS.with(VAR_INDENTATION, null);

  private static final Set<String> STANDARD_KEYS = Set.of(VAR_INDENTATION.getName(), VAR_WRITE_NULL_VALUES.getName(),
      VAR_ENUM_FORMAT.getName(), VAR_UNQUOTED_PROPERTIES.getName());

  /// The constructor.
  public MarshallingConfig() {

    super();
  }

  /// The constructor.
  ///
  /// @param map the raw [Map] with the configuration values.
  public MarshallingConfig(Map<String, Object> map) {

    super(map);
  }

  /// @param <T> type of the value.
  /// @param option the [VariableDefinition] to set.
  /// @param value the value for the given [VariableDefinition].
  /// @return a new [MarshallingConfig] with the given [VariableDefinition] set to the given `value` or
  ///         this [MarshallingConfig] if the given `value` is already configured.
  public <T> MarshallingConfig with(VariableDefinition<T> option, T value) {

    T oldValue = get(option);
    if (Objects.equals(oldValue, value)) {
      return this;
    }
    Map<String, Object> map = new HashMap<>(getMap());
    map.put(option.getName(), value);
    return new MarshallingConfig(map);
  }

  /// @param key the key of the proprietary and vendor specific property.
  /// @param value the value for the given `key`.
  /// @return a new [MarshallingConfig] with the given `key` set to the given `value` or this
  ///         [MarshallingConfig] if the given `value` is already configured.
  public MarshallingConfig with(String key, Object value) {

    Object oldValue = getMap().get(key);
    if (Objects.equals(oldValue, value)) {
      return this;
    }
    Map<String, Object> map = new HashMap<>(getMap());
    map.put(key, value);
    return new MarshallingConfig(map);
  }

  /// @param key the [key][VariableDefinition#getName()] of the option to check.
  /// @return `true` if proprietary (vendor specific) and `false` otherwise.
  public static boolean isProprietary(String key) {

    return !STANDARD_KEYS.contains(key);
  }

}
