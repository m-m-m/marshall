/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.marshall;

/**
 * Abstract base implementation of {@link StructuredWriter}.
 */
public abstract class AbstractStructuredWriter extends AbstractStructuredProcessor implements StructuredWriter {

  static final String XML_COMMENT_DASHES = "--";

  static final String XML_COMMENT_DASHES_ESCAPED = "\\-_-/";

  /** @see #writeValueAsNull() */
  protected final boolean writeNullValues;

  /** @see MarshallingConfig#OPT_INDENTATION */
  protected final String indentation;

  /** @see #writeName(String) */
  protected String name;

  /**
   * The constructor.
   *
   * @param format the {@link #getFormat() format}.
   */
  public AbstractStructuredWriter(StructuredFormat format) {

    super(format);
    this.writeNullValues = this.config.get(MarshallingConfig.OPT_WRITE_NULL_VALUES).booleanValue();
    this.indentation = normalizeIndentation(this.config.get(MarshallingConfig.OPT_INDENTATION));
  }

  /**
   * @param indent the {@link MarshallingConfig#OPT_INDENTATION indentation}.
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
  public void writeName(String newName, int newId) {

    if (this.name != null) {
      throw new IllegalStateException("Cannot write name " + newName + " while previous name " + this.name
          + " has not been processed! Forgot to call writeStartObject()?");
    }
    this.name = newName;
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

}
