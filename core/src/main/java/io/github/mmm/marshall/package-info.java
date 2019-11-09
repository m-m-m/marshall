/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
/**
 * Provides the API for marshalling and unmarshalling data. <a name="documentation"></a>
 * <h2>Marshall API</h2> This package provides the API for marshalling (serializing) and unmarshalling (deserialzing)
 * objects to structured formats such as JSON or XML.<br>
 * <b>ATTENTION:</b><br>
 * Generic methods such as {@link io.github.mmm.marshall.StructuredWriter#writeValue(Object)} and especially
 * {@link io.github.mmm.marshall.StructuredReader#readValue(Class)} only exist for convenience and simple usage. They
 * only support the following standard Java value types:
 * <ul>
 * <li>{@link java.lang.String}</li>
 * <li>{@link java.lang.Boolean}</li>
 * <li>The standard JDK {@link java.lang.Number} types:
 * <ul>
 * <li>{@link java.lang.Integer}</li>
 * <li>{@link java.lang.Long}</li>
 * <li>{@link java.lang.Double}</li>
 * <li>{@link java.lang.Float}</li>
 * <li>{@link java.lang.Short}</li>
 * <li>{@link java.lang.Byte}</li>
 * <li>{@link java.math.BigInteger}</li>
 * <li>{@link java.math.BigDecimal}</li>
 * </ul>
 * </li>
 * <li>The standard JDK {@link java.time.temporal.Temporal} types from {@link java.time} package:
 * <ul>
 * <li>{@link java.time.Instant}</li>
 * <li>{@link java.time.LocalDate}</li>
 * <li>{@link java.time.LocalTime}</li>
 * <li>{@link java.time.LocalDateTime}</li>
 * <li>{@link java.time.ZonedDateTime}</li>
 * <li>{@link java.time.OffsetDateTime}</li>
 * <li>{@link java.time.OffsetTime}</li>
 * </ul>
 * </li>
 * </ul>
 */
package io.github.mmm.marshall;
