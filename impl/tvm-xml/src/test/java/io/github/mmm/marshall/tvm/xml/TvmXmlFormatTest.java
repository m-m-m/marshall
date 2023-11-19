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
public class TvmXmlFormatTest extends AbstractXmlFormatTest {

  @Override
  protected StructuredTextFormatProvider getProvider() {

    return new TvmXmlFormatProvider();
  }

  // Downgrade to JUnit4 as TeaVM still does not support JUnit5

  @Test
  @Override
  public void testRead() {

    super.testRead();
  }

  @Test
  @Override
  public void testReadAtomicLong() {

    super.testReadAtomicLong();
  }

  @Test
  @Override
  public void testReadValueRecursive() {

    super.testReadValueRecursive();
  }

  @Test
  @Override
  public void testReadWithoutIndentation() {

    super.testReadWithoutIndentation();
  }

  @Test
  @Override
  public void testWrite() {

    super.testWrite();
  }

  @Test
  @Override
  public void testWriteAtomicLong() {

    super.testWriteAtomicLong();
  }

  @Test
  @Override
  public void testWriteWithoutIndentation() {

    super.testWriteWithoutIndentation();
  }

  @Test
  @Override
  public void testSkipValueAll() {

    super.testSkipValueAll();
  }

  @Test
  @Override
  public void testSkipValuePerProperty() {

    super.testSkipValuePerProperty();
  }

  @Test
  @Override
  public void testFormatFlags() {

    super.testFormatFlags();
  }

}
