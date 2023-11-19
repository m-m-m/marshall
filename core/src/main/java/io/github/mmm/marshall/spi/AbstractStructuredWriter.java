/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.marshall.spi;

import io.github.mmm.marshall.EnumFormat;
import io.github.mmm.marshall.MarshallingConfig;
import io.github.mmm.marshall.StructuredFormat;
import io.github.mmm.marshall.StructuredReader;
import io.github.mmm.marshall.StructuredState;
import io.github.mmm.marshall.StructuredWriter;
import io.github.mmm.marshall.id.StructuredIdMappingObject;

/**
 * Abstract base implementation of {@link StructuredWriter}.
 *
 * @param <S> type of the {@link StructuredNode}.
 * @since 1.0.0
 */
public abstract class AbstractStructuredWriter<S extends StructuredNode<S>> extends AbstractStructuredProcessor<S>
    implements StructuredWriter {

  static final String XML_COMMENT_DASHES = "--";

  static final String XML_COMMENT_DASHES_ESCAPED = "\\-_-/";

  /** @see #writeValueAsNull() */
  protected final boolean writeNullValues;

  /** @see MarshallingConfig#VAR_INDENTATION */
  protected final String indentation;

  /** The current indentation count. */
  protected int indentCount;

  /**
   * The constructor.
   *
   * @param format the {@link #getFormat() format}.
   */
  public AbstractStructuredWriter(StructuredFormat format) {

    super(format);
    this.writeNullValues = this.config.get(MarshallingConfig.VAR_WRITE_NULL_VALUES).booleanValue();
    this.indentation = normalizeIndentation(this.config.get(MarshallingConfig.VAR_INDENTATION));
  }

  @Override
  public final void writeStartArray() {

    StructuredNodeType type = StructuredNodeType.ARRAY;
    doWriteStart(type, null);
    setState(StructuredState.START_ARRAY);
    this.node = newNode(type, null);
    this.indentCount++;
  }

  @Override
  public final void writeStartObject(StructuredIdMappingObject object) {

    StructuredNodeType type = StructuredNodeType.OBJECT;
    doWriteStart(type, object);
    setState(StructuredState.START_OBJECT);
    this.node = newNode(type, object);
    this.indentCount++;
  }

  /**
   * Called from {@link #writeStartArray()} and {@link #writeStartObject(StructuredIdMappingObject)}.
   *
   * @param type the {@link StructuredNodeType}.
   * @param object the {@link StructuredIdMappingObject}. Will be {@code null} for {@link StructuredNodeType#ARRAY}.
   */
  protected abstract void doWriteStart(StructuredNodeType type, StructuredIdMappingObject object);

  @Override
  public final void writeEnd() {

    if ((this.node.parent == null) || (this.node.type == null)) {
      throw new IllegalStateException("Cannon end root or atomic value state - too many writeEnd calls!");
    }
    this.indentCount--;
    doWriteEnd(this.node.type);
    setState(this.node.type.getEnd());
    this.node = this.node.end();
  }

  /**
   * Called from {@link #writeEnd()}.
   *
   * @param type the {@link StructuredNodeType} to end.
   */
  protected abstract void doWriteEnd(StructuredNodeType type);

  /**
   * @param indent the {@link MarshallingConfig#VAR_INDENTATION indentation}.
   * @return the given {@code indent} or a normalized form (e.g. if {@code null} was given).
   */
  protected String normalizeIndentation(String indent) {

    return indent;
  }

  /**
   * @param currentComment the {@link #writeComment(String) comment}.
   * @return the given comment escaped for XML.
   */
  protected String escapeXmlComment(String currentComment) {

    return currentComment.replace(XML_COMMENT_DASHES, XML_COMMENT_DASHES_ESCAPED);
  }

  @Override
  public final void writeName(String newName) {

    writeName(newName, 0);
  }

  /**
   * @param newName the name of the property.
   * @param newId the ID of the property.
   * @see StructuredFormat#isIdBased()
   */
  protected void writeName(String newName, int newId) {

    if (this.name != null) {
      throw new IllegalStateException("Cannot write name " + newName + " while previous name " + this.name
          + " has not been processed! Forgot to call writeStartObject?");
    }
    this.name = newName;
    setState(StructuredState.NAME);
  }

  @Override
  public void writeValueAsEnum(Enum<?> value) {

    if (value == null) {
      writeValueAsNull();
      return;
    }
    if (this.enumFormat == EnumFormat.ORDINAL) {
      writeValueAsInteger(Integer.valueOf(value.ordinal()));
    } else {
      writeValueAsString(this.enumFormat.toString(value));
    }
  }

  @Override
  public final void writeValueAsBoolean(Boolean value) {

    if (value == null) {
      writeValueAsNull();
    } else {
      writeValueAsBoolean(value.booleanValue());
    }
  }

  @Override
  public final void writeValueAsLong(Long value) {

    if (value == null) {
      writeValueAsNull();
    } else {
      writeValueAsLong(value.longValue());
    }
  }

  @Override
  public final void writeValueAsInteger(Integer value) {

    if (value == null) {
      writeValueAsNull();
    } else {
      writeValueAsInteger(value.intValue());
    }
  }

  @Override
  public final void writeValueAsDouble(Double value) {

    if (value == null) {
      writeValueAsNull();
    } else {
      writeValueAsDouble(value.doubleValue());
    }
  }

  @Override
  public final void writeValueAsFloat(Float value) {

    if (value == null) {
      writeValueAsNull();
    } else {
      writeValueAsFloat(value.floatValue());
    }
  }

  @Override
  public final void writeValueAsShort(Short value) {

    if (value == null) {
      writeValueAsNull();
    } else {
      writeValueAsShort(value.shortValue());
    }
  }

  @Override
  public final void writeValueAsByte(Byte value) {

    if (value == null) {
      writeValueAsNull();
    } else {
      writeValueAsByte(value.byteValue());
    }
  }

  @Override
  public void write(StructuredReader reader2copyFrom) {

    AbstractStructuredReader<?> reader = (AbstractStructuredReader<?>) reader2copyFrom;
    boolean idBasedReader = reader.getFormat().isIdBased();
    boolean idBasedWriter = getFormat().isIdBased();
    boolean todo = true;
    while (todo) {
      String comment = reader.readComment();
      if (comment != null) {
        writeComment(comment);
      }
      StructuredState readerState = reader.getState();
      if (readerState == StructuredState.NULL) {
        // protobuf is limited so we need to guess...
        readerState = StructuredState.START_OBJECT;
      }
      if (readerState == StructuredState.START_OBJECT) {
        reader.readStartObject(reader);
        writeStartObject(reader);
      } else if (readerState == StructuredState.END_OBJECT) {
        reader.readEndObject();
        writeEnd();
      } else if (readerState == StructuredState.START_ARRAY) {
        reader.readStartArray();
        writeStartArray();
      } else if (readerState == StructuredState.END_ARRAY) {
        reader.readEndArray();
        writeEnd();
      } else if (readerState == StructuredState.NAME) {
        reader.next(false);
        if (idBasedReader && idBasedWriter) {
          int id = reader.getId();
          writeName("?", id);
        } else {
          writeName(reader.getName());
        }
      } else if (readerState == StructuredState.VALUE) {
        Object value = reader.readValue();
        writeValue(value);
      } else if (readerState == StructuredState.DONE) {
        todo = false;
      }
    }
  }

}
