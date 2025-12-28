package io.github.mmm.marshall.tvm.xml;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.teavm.junit.TeaVMTestRunner;

import io.github.mmm.marshall.StructuredTextFormatProvider;
import io.github.mmm.marshall.test.AbstractXmlFormatTest;
import io.github.mmm.marshall.tvm.xml.impl.TvmXmlFormat;

/**
 * Test of {@link TvmXmlFormat}.
 */
@RunWith(TeaVMTestRunner.class)
class TvmXmlFormatTest extends AbstractXmlFormatTest {

  @Override
  protected StructuredTextFormatProvider getProvider() {

    return new TvmXmlFormatProvider();
  }

  // Downgrade to JUnit4 as TeaVM still does not support JUnit5

  @Test
  @Override
  protected void testRead() {

    super.testRead();
  }

  @Test
  @Override
  protected void testReadAtomicLong() {

    super.testReadAtomicLong();
  }

  @Test
  @Override
  protected void testReadValueRecursive() {

    super.testReadValueRecursive();
  }

  @Test
  @Override
  protected void testReadWithoutIndentation() {

    super.testReadWithoutIndentation();
  }

  @Test
  @Override
  protected void testWrite() {

    super.testWrite();
  }

  @Test
  @Override
  protected void testWriteAtomicLong() {

    super.testWriteAtomicLong();
  }

  @Test
  @Override
  protected void testWriteWithoutIndentation() {

    super.testWriteWithoutIndentation();
  }

  @Test
  @Override
  protected void testSkipValueAll() {

    super.testSkipValueAll();
  }

  @Test
  @Override
  protected void testSkipValuePerProperty() {

    super.testSkipValuePerProperty();
  }

  @Test
  @Override
  protected void testFormatFlags() {

    super.testFormatFlags();
  }

}
