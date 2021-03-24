package io.github.mmm.marshall.protobuf.impl;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;

import com.google.protobuf.CodedOutputStream;

import io.github.mmm.marshall.size.StructuredFormatSizeComputor;

/**
 * Implementation of {@link StructuredFormatSizeComputor} for ProtoBuf.
 *
 * @since 1.0.0
 */
public class ProtoBufSizeComputor implements StructuredFormatSizeComputor {

  private static final ProtoBufSizeComputor INSTANCE = new ProtoBufSizeComputor();

  @Override
  public int sizeOfProperty(String name, int id) {

    return CodedOutputStream.computeTagSize(id);
  }

  @Override
  public int sizeOfString(String value) {

    if (value == null) {
      return 0;
    }
    return CodedOutputStream.computeStringSizeNoTag(value);
  }

  @Override
  public int sizeOfBoolean(Boolean value) {

    if (value == null) {
      return 0;
    }
    return CodedOutputStream.computeBoolSizeNoTag(true);
  }

  @Override
  public int sizeOfInteger(Integer value) {

    if (value == null) {
      return 0;
    }
    return CodedOutputStream.computeSInt32SizeNoTag(value.intValue());
  }

  @Override
  public int sizeOfLong(Long value) {

    if (value == null) {
      return 0;
    }
    return CodedOutputStream.computeSInt64SizeNoTag(value.longValue());
  }

  @Override
  public int sizeOfBigDecimal(BigDecimal value) {

    if (value == null) {
      return 0;
    }
    return CodedOutputStream.computeStringSizeNoTag(value.toString());
  }

  @Override
  public int sizeOfBigInteger(BigInteger value) {

    if (value == null) {
      return 0;
    }
    return CodedOutputStream.computeStringSizeNoTag(value.toString());
  }

  @Override
  public int sizeOfDouble(Double value) {

    if (value == null) {
      return 0;
    }
    return CodedOutputStream.computeDoubleSizeNoTag(value.doubleValue());
  }

  @Override
  public int sizeOfShort(Short value) {

    if (value == null) {
      return 0;
    }
    return CodedOutputStream.computeSInt32SizeNoTag(value.intValue());
  }

  @Override
  public int sizeOfByte(Byte value) {

    if (value == null) {
      return 0;
    }
    return CodedOutputStream.computeSInt32SizeNoTag(value.intValue());
  }

  @Override
  public int sizeOfFloat(Float value) {

    if (value == null) {
      return 0;
    }
    return CodedOutputStream.computeFloatSizeNoTag(value.floatValue());
  }

  @Override
  public int sizeOfInstant(Instant value) {

    if (value == null) {
      return 0;
    }
    int size = 0;
    long seconds = value.getEpochSecond();
    if (seconds != 0L) {
      size += CodedOutputStream.computeInt64Size(1, seconds);
    }
    int nanos = value.getNano();
    if (nanos != 0) {
      size += CodedOutputStream.computeInt32Size(2, nanos);
    }
    return size;
  }

  /**
   * @return the singleton instance of this class.
   */
  public static ProtoBufSizeComputor get() {

    return INSTANCE;
  }

}
