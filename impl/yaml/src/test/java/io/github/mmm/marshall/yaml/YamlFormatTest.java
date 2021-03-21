/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.marshall.yaml;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.assertj.core.data.Offset;
import org.junit.jupiter.api.Test;

import io.github.mmm.marshall.StructuredTextFormatProvider;
import io.github.mmm.marshall.test.AbstractYamlFormatTest;

/**
 * Test of {@link io.github.mmm.marshall.yaml.impl.YamlFormat} via {@link YamlFormatProvider}.
 */
public class YamlFormatTest extends AbstractYamlFormatTest {

  private static final Offset<BigDecimal> EPSILON = Offset.offset(new BigDecimal("0.00000000000000001"));

  @Override
  protected StructuredTextFormatProvider getProvider() {

    return new YamlFormatProvider();
  }

  @Override
  protected String getExpectedDataRaw(boolean read) {

    if (read) {
      return getExpectedJsonData("", "", true);
    }
    return getExpectedYamlData("  ", getNewline(), "'");
  }

  @Override
  protected String getExpectedData() {

    return getExpectedYamlData(getIndentation(), getNewline(), "'");
  }

  @Override
  protected boolean isSmartIndent() {

    return false;
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

    if ((value instanceof BigDecimal) || (value instanceof BigInteger)) {
      return value.toString();
    }
    return super.getGenericValue(value);
  }

  @Test
  @Override
  public void testYamlFormat() {

    super.testYamlFormat();
  }

}
