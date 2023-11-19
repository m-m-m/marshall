/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.marshall.protobuf.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Objects;

import com.google.protobuf.CodedOutputStream;
import com.google.protobuf.WireFormat;

import io.github.mmm.base.exception.RuntimeIoException;
import io.github.mmm.marshall.StructuredState;
import io.github.mmm.marshall.StructuredWriter;
import io.github.mmm.marshall.id.StructuredIdMapping;
import io.github.mmm.marshall.id.StructuredIdMappingObject;
import io.github.mmm.marshall.protobuf.ProtoBufFormatProvider;
import io.github.mmm.marshall.spi.AbstractStructuredBinaryWriter;
import io.github.mmm.marshall.spi.StructuredNodeType;

/**
 * Implementation of {@link StructuredWriter} for ProtoBuf/gRPC.
 *
 * @see ProtoBufFormat
 *
 * @since 1.0.0
 */
public class ProtoBufWriter extends AbstractStructuredBinaryWriter<ProtoBufNode> {

  private static final byte ZERO = 0;

  private final boolean useGroups;

  private /* final */ OutputStream os;

  private /* final */ CodedOutputStream finalOut;

  private CodedOutputStream out;

  private /* final */ ByteArrayOutputStream baos;

  private int id;

  private final SizeFix firstFix;

  /**
   * The constructor.
   *
   * @param os the {@link OutputStream} to write the data to.
   * @param format the {@link #getFormat()}.
   */
  public ProtoBufWriter(OutputStream os, ProtoBufFormat format) {

    super(format);
    this.useGroups = format.getConfig().getBoolean(ProtoBufFormatProvider.VAR_USE_GROUPS);
    this.os = os;
    this.finalOut = CodedOutputStream.newInstance(os);
    this.out = this.finalOut;
    this.firstFix = new SizeFix(0, -1); // dummy for the first size
  }

  @Override
  protected ProtoBufNode newNode(StructuredNodeType type, StructuredIdMappingObject object) {

    StructuredIdMapping idMapping = null;
    if (type == StructuredNodeType.OBJECT) {
      idMapping = this.idMappingProvider.getMapping(object);
      Objects.requireNonNull(idMapping);
    }
    ProtoBufNode newState = new ProtoBufNode(this.node, type, idMapping);
    newState.id = getTagId();
    try {
      if (type == StructuredNodeType.ARRAY) {
        if ((this.node != null) && (this.node.type == StructuredNodeType.ARRAY)) {
          // nested array must be encoded explicitly
          this.out.writeTag(newState.id, ProtoBufFormat.TYPE_START_ARRAY);
        }
      } else if ((type == StructuredNodeType.OBJECT) && (this.encodeRootObject || (this.node.parent != null))) {
        if (this.useGroups) {
          this.out.writeTag(newState.id, ProtoBufFormat.TYPE_START_OBJECT);
        } else {
          this.out.writeTag(newState.id, WireFormat.WIRETYPE_LENGTH_DELIMITED);
          this.out.flush();
          if (this.baos == null) {
            this.baos = new ByteArrayOutputStream(512);
            this.out = CodedOutputStream.newInstance(this.baos);
          }
          newState.start = this.out.getTotalBytesWritten();
          if (this.node.parent != null) {
            // This is the size that we do not know upfront, we write a zero byte as placeholder into our buffer.
            // If we are lucky the actual size will fit into a single byte and we can later just replace it.
            // Otherwise, we create an instance of SizeFix to record the position together with the correct size.
            // At the end we have to stop array copy at these recorded byte positions and instead of these zero bytes
            // insert the correctly encoded size.
            this.out.write(ZERO);
          }
        }
      }
    } catch (IOException e) {
      throw new RuntimeIoException(e);
    }
    clearProperty();
    return newState;
  }

  @Override
  protected void doWriteStart(StructuredNodeType type, StructuredIdMappingObject object) {

    // all done in newNode method...
  }

  @Override
  protected void doWriteEnd(StructuredNodeType type) {

    try {
      if (type == StructuredNodeType.OBJECT) {
        boolean rootObject = this.node.parent.parent == null;
        if (rootObject && !this.encodeRootObject) {
          // nothing to if root object is not encoded
          return;
        }
        if (this.useGroups) {
          this.out.writeTag(this.node.id, ProtoBufFormat.TYPE_END);
        } else {
          this.out.flush();
          this.node.end = this.out.getTotalBytesWritten();
          int size = this.node.end - this.node.start + this.node.sizeOverhead;
          if (rootObject || (this.node.parent.parent.parent == null)) {
            // payload was only written to buffer, we need to write it to finalOut now
            // write size of object
            this.finalOut.writeUInt32NoTag(size);
            byte[] payload = this.baos.toByteArray();
            int startIndex = 0;
            SizeFix fix = this.firstFix.next;
            while (fix != null) {
              if (fix.size < 128) {
                // size fits into a single byte so we can just update that byte directly
                payload[fix.offset] = (byte) fix.size;
              } else {
                int endIndex = fix.offset;
                this.finalOut.write(payload, startIndex, endIndex - startIndex);
                this.finalOut.writeUInt32NoTag(fix.size);
                startIndex = endIndex + 1;
              }
              fix = fix.next;
            }
            // write till the end
            this.finalOut.write(payload, startIndex, payload.length - startIndex);
            if (!rootObject) {
              this.baos.reset();
              this.firstFix.next = null;
            }
          } else {
            int bytes = CodedOutputStream.computeUInt32SizeNoTag(size);
            if (bytes > 1) { // size > 127 ?
              this.node.parent.sizeOverhead += (bytes - 1) + this.node.sizeOverhead;
            }
            this.firstFix.append(this.node.start, size);
          }
        }
      } else if (type == StructuredNodeType.ARRAY) {
        if ((this.node.parent != null) && (this.node.parent.type == StructuredNodeType.ARRAY)) { // nested array?
          this.out.writeTag(this.node.id, ProtoBufFormat.TYPE_END);
        }
        // else nothing to do (repeated values have been written for array items or nothing in case of empty array)
      } else {
        this.finalOut.flush();
        this.node = null;
        setState(StructuredState.DONE);
      }
    } catch (IOException e) {
      throw new RuntimeIoException(e);
    }
  }

  @Override
  public int getId() {

    if ((this.node != null) && (this.node.type == StructuredNodeType.ARRAY)) {
      if ((this.node.parent != null) && (this.node.parent.type == StructuredNodeType.ARRAY)) {
        return 1; // in nested arrays we always use ID 1
      }
      return this.node.id;
    }
    return this.id;
  }

  private int getTagId() {

    if ((this.node != null) && (this.node.type == StructuredNodeType.ARRAY)) {
      if ((this.node.parent != null) && (this.node.parent.type == StructuredNodeType.ARRAY)) {
        return 1; // in nested arrays we always use ID 1
      }
      return this.node.id;
    }
    if (this.id == -1) {
      throw new IllegalStateException("");
    } else if (this.id == 0) {
      this.id = -1;
      return 0;
    }
    return this.id;
  }

  @Override
  protected void writeName(String newName, int newId) {

    super.writeName(newName, newId);
    if (newId <= 0) {
      newId = this.node.idMapping.id(newName);
    }
    if (newId <= 0) {
      throw new IllegalStateException("Invalid tag ID " + newId + " for name '" + newName + "'");
    }
    this.id = newId;
  }

  private int flushIfEos() throws IOException {

    if (this.baos != null) {
    }
    this.out.flush();
    this.node.end = this.out.getTotalBytesWritten();
    int size = this.node.end - this.node.start + this.node.sizeOverhead;
    if (this.node.parent == null) {
      if (this.baos != null) {
        // payload was only written to buffer, we need to write it to finalOut now
        // write size of root array/object
        this.finalOut.writeUInt32NoTag(size);
        byte[] payload = this.baos.toByteArray();
        int startIndex = 0;
        SizeFix fix = this.firstFix.next;
        while (fix != null) {
          if (fix.size < 128) {
            // size fits into a single byte so we can just update that byte directly
            payload[fix.offset] = (byte) fix.size;
          } else {
            int endIndex = fix.offset;
            this.finalOut.write(payload, startIndex, endIndex - startIndex);
            this.finalOut.writeUInt32NoTag(fix.size);
            startIndex = endIndex + 1;
          }
          fix = fix.next;
        }
        // write till the end
        this.finalOut.write(payload, startIndex, payload.length - startIndex);
      }
      this.finalOut.flush();
      this.node = null;
      setState(StructuredState.DONE);
    }
    return size;
  }

  private void clearProperty() {

    this.name = null;
    this.id = 0;
  }

  @Override
  public void writeValueAsNull() {

    // in gRPC null values are simply omitted... except in arrays where they are encoded as empty length delimited value
    if ((this.node != null) && (this.node.type == StructuredNodeType.ARRAY)) {
      writeValueAsString("");
    }
  }

  @Override
  public void writeValueAsString(String value) {

    try {
      if (value == null) {
        writeValueAsNull();
      } else {
        int tagId = getTagId();
        if (tagId == 0) {
          this.out.writeStringNoTag(value);
        } else {
          this.out.writeString(tagId, value);
        }
        setState(StructuredState.VALUE);
        clearProperty();
      }
    } catch (IOException e) {
      throw new RuntimeIoException(e);
    }
  }

  @Override
  public void writeValueAsBoolean(boolean value) {

    try {
      int tagId = getTagId();
      if (tagId == 0) {
        this.out.writeBoolNoTag(value);
      } else {
        this.out.writeBool(tagId, value);
      }
      setState(StructuredState.VALUE);
      clearProperty();
    } catch (IOException e) {
      throw new RuntimeIoException(e);
    }
  }

  @Override
  public void writeValueAsBigDecimal(BigDecimal value) {

    if (value == null) {
      writeValueAsNull();
    } else {
      // could be written more efficiently, but interoperability is also important
      writeValueAsString(value.toString());
    }
  }

  @Override
  public void writeValueAsBigInteger(BigInteger value) {

    if (value == null) {
      writeValueAsNull();
    } else {
      // could be written more efficiently, but interoperability is also important
      writeValueAsString(value.toString());
    }
  }

  @Override
  public void writeValueAsLong(long value) {

    try {
      int tagId = getTagId();
      if (tagId == 0) {
        this.out.writeSInt64NoTag(value);
      } else {
        this.out.writeSInt64(tagId, value);
      }
      setState(StructuredState.VALUE);
    } catch (IOException e) {
      throw new RuntimeIoException(e);
    }
    clearProperty();
  }

  @Override
  public void writeValueAsInteger(int value) {

    try {
      int tagId = getTagId();
      if (tagId == 0) {
        this.out.writeSInt32NoTag(value);
      } else {
        this.out.writeSInt32(tagId, value);
      }
      setState(StructuredState.VALUE);
    } catch (IOException e) {
      throw new RuntimeIoException(e);
    }
    clearProperty();
  }

  @Override
  public void writeValueAsDouble(double value) {

    try {
      int tagId = getTagId();
      if (tagId == 0) {
        this.out.writeDoubleNoTag(value);
      } else {
        this.out.writeDouble(tagId, value);
      }
      setState(StructuredState.VALUE);
    } catch (IOException e) {
      throw new RuntimeIoException(e);
    }
    clearProperty();
  }

  @Override
  public void writeValueAsFloat(float value) {

    try {
      int tagId = getTagId();
      if (tagId == 0) {
        this.out.writeFloatNoTag(value);
      } else {
        this.out.writeFloat(tagId, value);
      }
      setState(StructuredState.VALUE);
    } catch (IOException e) {
      throw new RuntimeIoException(e);
    }
    clearProperty();
  }

  @Override
  protected void doClose() throws IOException {

    try {
      flushIfEos();
      this.os.close();
    } catch (IOException e) {
      throw new RuntimeIoException(e);
    }
    this.out = null;
    this.baos = null;
    this.finalOut = null;
    this.node = null;
    this.firstFix.next = null;
  }

  private static class SizeFix {

    private final int offset;

    private final int size;

    private SizeFix next;

    private SizeFix(int offset, int size) {

      super();
      this.offset = offset;
      this.size = size;
    }

    public void append(int newOffset, int newSize) {

      SizeFix current = this;
      SizeFix newFix = new SizeFix(newOffset, newSize);
      while (true) {
        if (current.next == null) {
          // append to end of chain
          current.next = newFix;
          return;
        } else if (newOffset > current.next.offset) {
          // proceeding to next as larger
          current = current.next;
        } else {
          // insert into chain
          newFix.next = current.next;
          current.next = newFix;
          return;
        }
      }
    }

  }

}
