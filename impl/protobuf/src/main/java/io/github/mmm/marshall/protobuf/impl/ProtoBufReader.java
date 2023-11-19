/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.marshall.protobuf.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

import com.google.protobuf.CodedInputStream;
import com.google.protobuf.WireFormat;

import io.github.mmm.base.exception.RuntimeIoException;
import io.github.mmm.base.number.NumberType;
import io.github.mmm.marshall.StructuredReader;
import io.github.mmm.marshall.StructuredState;
import io.github.mmm.marshall.id.StructuredIdMapping;
import io.github.mmm.marshall.id.StructuredIdMappingObject;
import io.github.mmm.marshall.protobuf.ProtoBufFormatProvider;
import io.github.mmm.marshall.spi.AbstractStructuredBinaryReader;
import io.github.mmm.marshall.spi.StructuredNodeType;

/**
 * Implementation of {@link StructuredReader} for gRPC/ProtoBuf.
 *
 * @see ProtoBufFormat
 *
 * @since 1.0.0
 */
public class ProtoBufReader extends AbstractStructuredBinaryReader<ProtoBufNode> {

  private final boolean useGroups;

  private CodedInputStream in;

  private InputStream is;

  private int tag;

  private int id;

  private int wireType;

  /**
   * The constructor.
   *
   * @param is the {@link InputStream} with the ProtoBuf content to parse.
   * @param format the {@link #getFormat() format}.
   */
  public ProtoBufReader(InputStream is, ProtoBufFormat format) {

    super(format);
    this.useGroups = format.getConfig().getBoolean(ProtoBufFormatProvider.VAR_USE_GROUPS);
    this.is = is;
    this.in = CodedInputStream.newInstance(is);
    this.wireType = -1;
  }

  @Override
  public int getId() {

    return this.id;
  }

  @Override
  protected ProtoBufNode newNode(StructuredNodeType type, StructuredIdMappingObject object) {

    ProtoBufNode newNode = new ProtoBufNode(this.node, type, null);
    newNode.end = Integer.MAX_VALUE;
    return newNode;
  }

  @Override
  protected StructuredState next(boolean skip) {

    StructuredState state = getState();
    if ((state == StructuredState.NULL) && (this.node.parent == null)) {
      if (skip) {
        state = setState(StructuredState.DONE);
      }
      return state;
    }
    try {
      int skipCount = skip ? 1 : 0;
      boolean todo;
      do {
        todo = false;
        if (state == StructuredState.NAME) {
          if (this.wireType == ProtoBufFormat.TYPE_START_OBJECT) {
            state = start(StructuredNodeType.OBJECT);
          } else if (this.wireType == ProtoBufFormat.TYPE_START_ARRAY) {
            state = start(StructuredNodeType.ARRAY);
            // } else if (!this.useGroups && (this.wireType == WireFormat.WIRETYPE_LENGTH_DELIMITED)) {
            // state = setState(StructuredState.START_OBJECT); // could also be string
          } else {
            state = setState(StructuredState.VALUE);
          }
        } else if (state == StructuredState.START_ARRAY && (this.wireType != ProtoBufFormat.TYPE_START_ARRAY)) {
          state = setState(StructuredState.VALUE);
        } else {
          if (state == StructuredState.VALUE) {
            if ((this.tag != 0) && !this.in.isAtEnd()) {
              this.in.skipField(this.tag);
            }
            // if (this.node.type == StructuredNodeType.OBJECT) {
            // state = setState(StructuredState.NAME); // avoid transition error
            // }
          }
          clearTag();
          state = readTag(skipCount > 0);
        }
        if (skipCount > 0) {
          skipCount += state.getDepthDelta();
          if (skipCount == 0) {
            todo = true;
          }
        }
      } while ((skipCount > 0) || todo);
    } catch (IOException e) {
      throw new RuntimeIoException(e);
    }
    return state;
  }

  private StructuredState readTag(boolean skip) throws IOException {

    StructuredState state = getState();
    if (!this.useGroups && (this.node.type == StructuredNodeType.OBJECT)
        && (this.encodeRootObject || (this.node.parent != null))) {
      int position = this.in.getTotalBytesRead();
      if (position >= this.node.end) {
        if (position != this.node.end) {
          throw new IllegalStateException("TODO: size mismatch");
        }
        state = setState(this.node.type.getEnd());
        this.node = this.node.parent;
        return state;
      }
    }
    if (this.in.isAtEnd()) {
      this.tag = 0;
    } else {
      this.tag = this.in.readTag();
    }
    if (this.tag == 0) {
      state = end(null);
    } else {
      this.wireType = WireFormat.getTagWireType(this.tag);
      this.id = WireFormat.getTagFieldNumber(this.tag);
      if (!skip) {
        this.name = this.node.getIdMapping().name(this.id);
      }
      if (this.wireType == ProtoBufFormat.TYPE_END) {
        assert (this.id == this.node.id) : this.id + "!=" + this.node.id; // TODO is this assertion correct?
        state = end(null);
      } else if (this.node.type == StructuredNodeType.ARRAY) {
        if (this.wireType == ProtoBufFormat.TYPE_START_OBJECT) {
          state = start(StructuredNodeType.OBJECT);
        } else if (this.wireType == ProtoBufFormat.TYPE_START_ARRAY) {
          state = start(StructuredNodeType.ARRAY);
        } else {
          if (this.node.explicit) {
            state = setState(StructuredState.VALUE);
          } else {
            if (this.node.id == this.id) {
              state = setState(StructuredState.VALUE);
            } else if (this.node.id != this.id) {
              state = end(StructuredNodeType.ARRAY);
            }
          }
        }
      } else if (this.node.type == StructuredNodeType.OBJECT) {
        if (state != StructuredState.NAME) { // otherwise we have just skipped value
          state = setState(StructuredState.NAME);
        }
      }
    }
    return state;
  }

  private void clearTag() {

    this.tag = 0;
    this.id = 0;
    this.wireType = -1;
    this.name = null;
  }

  @Override
  public boolean readStartObject(StructuredIdMappingObject object) {

    try {
      int end = Integer.MAX_VALUE;
      StructuredState state = getState();
      if (state == StructuredState.START_OBJECT) {
        // OK
      } else if ((state == StructuredState.NULL) && (this.node.parent == null)) {
        // OK
        if (this.useGroups && this.encodeRootObject) {
          readTag(true);
          require(StructuredState.START_OBJECT);
        }
        this.node = new ProtoBufNode(this.node, StructuredNodeType.OBJECT, null, end);
      } else if ((state == StructuredState.VALUE) && !this.useGroups
          && (this.wireType == WireFormat.WIRETYPE_LENGTH_DELIMITED)) {
        // OK we assumed a string value but now see that we have an object since we do not use groups
        if (this.node.type == StructuredNodeType.OBJECT) {
          setState(StructuredState.NAME); // only to prevent transition validation error
        }
        start(StructuredNodeType.OBJECT);
      } else {
        return false;
      }
      if (!this.useGroups && (this.encodeRootObject || this.node.parent != null)) {
        int len = this.in.readRawVarint32();
        end = this.in.getTotalBytesRead() + len;
      }
      StructuredIdMapping idMapping = this.idMappingProvider.getMapping(object);
      Objects.requireNonNull(idMapping);
      this.node.idMapping = idMapping;
      next(false);
    } catch (IOException e) {
      throw new RuntimeIoException(e);
    }
    return true;
  }

  @Override
  public boolean readStartArray() {

    StructuredState state = getState();
    if (state == StructuredState.VALUE) {
      if (this.node.type == StructuredNodeType.ARRAY) {
        throw new IllegalStateException("TODO nested arrays currently not supported!");
      }
      this.node = new ProtoBufNode(this.node, StructuredNodeType.ARRAY, null);
      assert (this.id > 0);
      this.node.id = this.id; // we read arrays as repeatable fields - mixed order of IDs is not supported
      return true;
    } else if (state == StructuredState.START_ARRAY) {
      next(false);
      return true;
    } else if (state == StructuredState.START_OBJECT) {
      // here we could handle nested arrays by e.g. adding a (proprietary code or wire-type)
    } else if (state == StructuredState.NULL) {
      // here we can support root array, however this could only work via some virtual tag ID or by completely handling
      // as special case in every readValue method...
    }
    return false;
  }

  @Override
  protected StructuredState start(StructuredNodeType type) {

    StructuredState state = super.start(type);
    this.node.id = this.id;
    if (type == StructuredNodeType.ARRAY) {
      this.node.explicit = (this.wireType == ProtoBufFormat.TYPE_START_ARRAY);
    } else {
      this.node.explicit = (this.wireType == ProtoBufFormat.TYPE_START_OBJECT);
    }
    return state;
  }

  private <V> V valueCompleted(V value) {

    clearTag();
    if (getState() != StructuredState.VALUE) {
      setState(StructuredState.VALUE);
    }
    next();
    return value;
  }

  @Override
  public boolean isStringValue() {

    return (getState() == StructuredState.VALUE) && (this.wireType == WireFormat.WIRETYPE_LENGTH_DELIMITED);
  }

  @Override
  public Object readValue() {

    require(StructuredState.VALUE);
    switch (this.wireType) {
      case WireFormat.WIRETYPE_LENGTH_DELIMITED:
        return readValueAsString();
      case WireFormat.WIRETYPE_FIXED32:
        return readValueAsFloat();
      case WireFormat.WIRETYPE_FIXED64:
        return readValueAsDouble();
      case WireFormat.WIRETYPE_VARINT:
        return readValueAsLong();
      default:
        throw error("Unknown wire type: " + this.wireType);
    }
  }

  private void expectType(int type) {

    if ((this.wireType != type) && (this.wireType != -1)) {
      error("Expected wire type " + type + " but actual type was " + this.wireType);
    }
  }

  @Override
  public String readValueAsString() {

    expectType(WireFormat.WIRETYPE_LENGTH_DELIMITED);
    try {
      return valueCompleted(this.in.readString());
    } catch (IOException e) {
      throw new RuntimeIoException(e);
    }
  }

  @Override
  public Boolean readValueAsBoolean() {

    try {
      if (this.wireType == WireFormat.WIRETYPE_LENGTH_DELIMITED) {
        return valueCompleted(parseBoolean(this.in.readString()));
      }
      expectType(WireFormat.WIRETYPE_VARINT);
      return valueCompleted(Boolean.valueOf(this.in.readBool()));
    } catch (IOException e) {
      throw new RuntimeIoException(e);
    }
  }

  @Override
  public Byte readValueAsByte() {

    expectType(WireFormat.WIRETYPE_VARINT);
    try {
      return valueCompleted(Byte.valueOf((byte) this.in.readSInt32()));
    } catch (IOException e) {
      throw new RuntimeIoException(e);
    }
  }

  @Override
  public Short readValueAsShort() {

    expectType(WireFormat.WIRETYPE_VARINT);
    try {
      return valueCompleted(Short.valueOf((short) this.in.readSInt32()));
    } catch (IOException e) {
      throw new RuntimeIoException(e);
    }
  }

  @Override
  public Integer readValueAsInteger() {

    expectType(WireFormat.WIRETYPE_VARINT);
    try {
      return valueCompleted(Integer.valueOf(this.in.readSInt32()));
    } catch (IOException e) {
      throw new RuntimeIoException(e);
    }
  }

  @Override
  public Long readValueAsLong() {

    expectType(WireFormat.WIRETYPE_VARINT);
    try {
      return valueCompleted(Long.valueOf(this.in.readSInt64()));
    } catch (IOException e) {
      throw new RuntimeIoException(e);
    }
  }

  @Override
  public Float readValueAsFloat() {

    expectType(WireFormat.WIRETYPE_FIXED32);
    try {
      return valueCompleted(Float.valueOf(this.in.readFloat()));
    } catch (IOException e) {
      throw new RuntimeIoException(e);
    }
  }

  @Override
  public Double readValueAsDouble() {

    expectType(WireFormat.WIRETYPE_FIXED64);
    try {
      return valueCompleted(Double.valueOf(this.in.readDouble()));
    } catch (IOException e) {
      throw new RuntimeIoException(e);
    }
  }

  @Override
  protected <N extends Number> N readValueAsNumber(NumberType<N> numberType) {

    // for BigInteger or BigDecimal
    String value = readValueAsString();
    if (value == null) {
      return null;
    }
    try {
      return numberType.valueOf(value);
    } catch (RuntimeException e) {
      throw error(value, numberType.getType(), e);
    }
  }

  @Override
  public void skipValue() {

    StructuredState state = getState();
    if (state == StructuredState.NULL) {
      setState(StructuredState.DONE);
    } else if ((state == StructuredState.VALUE) && (this.node.type == StructuredNodeType.OBJECT)) {
      int currentId = this.id;
      while ((currentId == this.id) && !state.isEnd()) {
        if (state == StructuredState.NAME) {
          state = next(false);
        }
        state = next(state != StructuredState.VALUE);
      }
    } else {
      super.skipValue();
    }
  }

  @Override
  protected void doClose() throws IOException {

    this.is.close();
    this.is = null;
    this.in = null;
  }

}
