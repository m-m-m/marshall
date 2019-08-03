package net.sf.mmm.marshal.impl.stax;

import java.math.BigDecimal;
import java.math.BigInteger;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import net.sf.mmm.marshal.api.StructuredWriter;

/**
 * Implementation of {@link StructuredWriter} for XML using {@link XMLStreamWriter}.
 *
 */
public class XmlWriter implements StructuredWriter {

  static final String TAG_ROOT = "json";

  static final String NS_PREFIX_ARRAY = "a";

  static final String NS_URI_ARRAY = "array";

  static final String NS_PREFIX_OBJECT = "o";

  static final String NS_URI_OBJECT = "object";

  static final String TAG_ITEM = "i";

  static final String ART_VALUE = "v";

  private XMLStreamWriter xml;

  private String name;

  /**
   * The constructor.
   *
   * @param xml the {@link XMLStreamWriter} to wrap.
   */
  public XmlWriter(XMLStreamWriter xml) {

    super();
    this.xml = xml;
    try {
      this.xml.writeStartDocument();
      this.xml.setPrefix(NS_PREFIX_ARRAY, NS_URI_ARRAY);
      this.xml.setPrefix(NS_PREFIX_OBJECT, NS_URI_OBJECT);
      this.name = TAG_ROOT;
    } catch (XMLStreamException e) {
      throw new IllegalStateException(e);
    }
  }

  @Override
  public void close() throws Exception {

    if (this.xml != null) {
      this.xml.writeEndDocument();
      this.xml.close();
      this.xml = null;
    }
  }

  @Override
  public void writeStartArray() {

    try {
      this.xml.writeStartElement(NS_PREFIX_ARRAY, requireName(), NS_URI_ARRAY);
      if (this.name == TAG_ROOT) {
        this.xml.writeNamespace(NS_PREFIX_ARRAY, NS_URI_ARRAY);
        this.xml.writeNamespace(NS_PREFIX_OBJECT, NS_URI_OBJECT);
      }
      this.name = null;
    } catch (XMLStreamException e) {
      throw new IllegalStateException(e);
    }
  }

  @Override
  public void writeStartObject() {

    try {
      this.xml.writeStartElement(NS_PREFIX_OBJECT, requireName(), NS_URI_OBJECT);
      if (this.name == TAG_ROOT) {
        this.xml.writeNamespace(NS_PREFIX_ARRAY, NS_URI_ARRAY);
        this.xml.writeNamespace(NS_PREFIX_OBJECT, NS_URI_OBJECT);
      }
      this.name = null;
    } catch (XMLStreamException e) {
      throw new IllegalStateException(e);
    }
  }

  private String requireName() {

    if (this.name == null) {
      throw new IllegalStateException("writeName has to be called after writeEnd before new start can be written");
    }
    return this.name;
  }

  @Override
  public void writeEnd() {

    try {
      this.xml.writeEndElement();
    } catch (XMLStreamException e) {
      throw new IllegalStateException(e);
    }
  }

  @Override
  public void writeName(String newName) {

    if (this.name != null) {
      throw new IllegalStateException("Cannot write name " + newName + " while previous name " + this.name
          + " has not been processed - forgot to call writeStartObject()!");
    }
    this.name = newName;
  }

  @Override
  public void writeValueAsNull() {

    writeValueAsString(null);
  }

  @Override
  public void writeValueAsString(String value) {

    if (value == null) {
      // TODO make configurable if null values are omitted...
      return;
    }
    try {
      String tag = this.name;
      if (tag == null) {
        tag = TAG_ITEM;
      }
      this.xml.writeEmptyElement(tag);
      this.xml.writeAttribute(ART_VALUE, value);
      this.name = null;
    } catch (XMLStreamException e) {
      throw new IllegalStateException(e);
    }
  }

  private void write(Object value) {

    if (value == null) {
      writeValueAsString(null);
    } else {
      writeValueAsString(value.toString());
    }
  }

  @Override
  public void writeValueAsBoolean(boolean value) {

    if (value) {
      writeValueAsString("true");
    } else {
      writeValueAsString("false");
    }
  }

  @Override
  public void writeValueAsBigDecimal(BigDecimal value) {

    write(value);
  }

  @Override
  public void writeValueAsBigInteger(BigInteger value) {

    write(value);
  }

  @Override
  public void writeValueAsLong(long value) {

    writeValueAsString(Long.toString(value));
  }

  @Override
  public void writeValueAsInteger(int value) {

    writeValueAsString(Integer.toString(value));
  }

  @Override
  public void writeValueAsDouble(double value) {

    writeValueAsString(Double.toString(value));
  }

  @Override
  public void writeValueAsFloat(float value) {

    writeValueAsString(Float.toString(value));
  }

}
