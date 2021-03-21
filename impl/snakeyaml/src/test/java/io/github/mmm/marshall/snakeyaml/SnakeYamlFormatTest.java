/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.marshall.snakeyaml;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import org.assertj.core.data.Offset;
import org.junit.jupiter.api.Test;

import io.github.mmm.marshall.StructuredTextFormatProvider;
import io.github.mmm.marshall.test.AbstractYamlFormatTest;

/**
 * Test of {@link io.github.mmm.marshall.snakeyaml.impl.SnakeYamlFormat} via {@link SnakeYamlFormatProvider}.
 */
public class SnakeYamlFormatTest extends AbstractYamlFormatTest {

  private static final Offset<BigDecimal> EPSILON = Offset.offset(new BigDecimal("0.00000000000000001"));

  @Override
  protected StructuredTextFormatProvider getProvider() {

    return new SnakeYamlFormatProvider();
  }

  @Override
  protected String getExpectedDataForAtomicLong() {

    return super.getExpectedDataForAtomicLong() + getNewline();
  }

  @Override
  protected String getExpectedDataRaw(boolean read) {

    if (read) {
      return getExpectedJsonData("", "", true);
    }
    return getExpectedYamlData("", getNewline());
  }

  @Override
  protected String getExpectedYamlData(String indent, String newline, String quoteString, String quoteInstant,
      String quoteBigNum, boolean smartIndent) {

    return super.getExpectedYamlData(indent, newline, quoteString, quoteInstant, quoteBigNum, smartIndent) + newline;
  }

  @Override
  protected void checkBigDecimal(BigDecimal actual, BigDecimal expected) {

    // snakeyaml has a design flaw, you can not reliably write big decimals and read them back:
    // strings are only quoted if they contain special chars like colon, hence on reading
    // snakeyaml will guess the type and decides for double instead of BigDecimal
    assertThat(actual).isCloseTo(expected, EPSILON);
  }

  @Override
  protected Object getGenericValue(Object value) {

    if (value == P3_VALUE7) {
      return Double.valueOf(P3_VALUE7.doubleValue());
    } else if (value == P3_VALUE9) {
      return Double.valueOf(P3_VALUE9.doubleValue());
    }
    return super.getGenericValue(value);
  }

  @Test
  @Override
  public void testYamlFormat() {

    super.testYamlFormat();
  }

}
