package io.github.mmm.marshall.protobuf.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.google.protobuf.CodedOutputStream;
import com.google.protobuf.WireFormat;

import io.github.mmm.base.exception.RuntimeIoException;

/**
 * Wrapper for {@link CodedOutputStream} that also supports writing to {@link ByteArrayOutputStream} if size is unknown.
 *
 * @since 1.0.0
 */
public class ProtoBufOut {

  private final ProtoBufOut parent;

  final CodedOutputStream out;

  private int idCounter;

  private final int end;

  private ByteArrayOutputStream baos;

  ProtoBufOut(CodedOutputStream out) {

    super();
    this.parent = null;
    this.out = out;
    this.end = -1;
  }

  ProtoBufOut(ProtoBufOut parent, int id, int size) {

    super();
    this.parent = parent;
    try {
      if (id > 0) {
        this.parent.out.writeTag(id, WireFormat.WIRETYPE_LENGTH_DELIMITED);
      }
      if (size == -1) {
        this.out = CodedOutputStream.newInstance(parent.newBaos());
        this.end = -1;
      } else {
        this.out = parent.out;
        this.out.writeUInt32NoTag(size);
        this.end = (this.out.getTotalBytesWritten() + size);
      }
    } catch (IOException e) {
      throw new RuntimeIoException(e);
    }
  }

  private ByteArrayOutputStream newBaos() {

    if (this.baos == null) {
      this.baos = new ByteArrayOutputStream(128);
    } else {
      this.baos.reset();
    }
    return this.baos;
  }

  ProtoBufOut end() {

    if (this.parent == null) {
      return null;
    }
    try {
      this.out.flush();
      if (this.end == -1) {
        byte[] payload = this.parent.baos.toByteArray();
        this.parent.out.writeByteArrayNoTag(payload);
      } else {
        assert (this.out.getTotalBytesWritten() == this.end);
      }
    } catch (IOException e) {
      throw new RuntimeIoException(e);
    }
    return this.parent;
  }

  int id(int id) {

    if (id == -1) {
      this.idCounter++;
      return this.idCounter;
    }
    if (this.idCounter <= id) {
      this.idCounter = id;
    }
    return id;
  }

  ProtoBufOut startArray(int id, int bytes) {

    return new ProtoBufOut(this, id, bytes);
  }

  ProtoBufOut startObject(int id, int bytes) {

    return new ProtoBufOut(this, id, bytes);
  }

}
