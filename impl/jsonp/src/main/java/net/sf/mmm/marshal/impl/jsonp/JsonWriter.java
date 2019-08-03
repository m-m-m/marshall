package net.sf.mmm.marshal.impl.jsonp;

import java.math.BigDecimal;
import java.math.BigInteger;

import javax.json.stream.JsonGenerator;

import net.sf.mmm.marshal.api.StructuredWriter;

/**
 * Implementation of {@link StructuredWriter} for JSON using {@link JsonGenerator}.
 *
 * @see JsonFormat
 *
 * @since 1.0.0
 */
public class JsonWriter implements StructuredWriter {

  private final JsonGenerator json;

  private String name;

  /**
   * The constructor.
   *
   * @param json the underlying {@link JsonGenerator} to write to.
   */
  public JsonWriter(JsonGenerator json) {

    super();
    this.json = json;
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
  public void writeValueAsString(String value) {

    if (value == null) {
      writeValueAsNull();
    } else if (this.name == null) {
      this.json.write(value);
    } else {
      this.json.write(this.name, value);
      this.name = null;
    }
  }

  @Override
  public void writeValueAsBigDecimal(BigDecimal value) {

    if (value == null) {
      writeValueAsNull();
    } else if (this.name == null) {
      this.json.write(value);
    } else {
      this.json.write(this.name, value);
      this.name = null;
    }
  }

  @Override
  public void writeValueAsBigInteger(BigInteger value) {

    if (value == null) {
      writeValueAsNull();
    } else if (this.name == null) {
      this.json.write(value);
    } else {
      this.json.write(this.name, value);
      this.name = null;
    }
  }

  @Override
  public void writeValueAsLong(long longValue) {

    if (this.name == null) {
      this.json.write(longValue);
    } else {
      this.json.write(this.name, longValue);
      this.name = null;
    }
  }

  @Override
  public void writeValueAsInteger(int intValue) {

    if (this.name == null) {
      this.json.write(intValue);
    } else {
      this.json.write(this.name, intValue);
      this.name = null;
    }
  }

  @Override
  public void writeValueAsDouble(double doubleValue) {

    if (this.name == null) {
      this.json.write(doubleValue);
    } else {
      this.json.write(this.name, doubleValue);
      this.name = null;
    }
  }

  @Override
  public void writeStartArray() {

    if (this.name == null) {
      this.json.writeStartArray();
    } else {
      this.json.writeStartArray(this.name);
      this.name = null;
    }
  }

  @Override
  public void writeStartObject() {

    if (this.name == null) {
      this.json.writeStartObject();
    } else {
      this.json.writeStartObject(this.name);
      this.name = null;
    }
  }

  @Override
  public void writeEnd() {

    this.json.writeEnd();
  }

  @Override
  public void writeValueAsNull() {

    if (this.name == null) {
      this.json.writeNull();
    } else {
      this.json.writeNull(this.name);
      this.name = null;
    }
  }

  @Override
  public void writeValueAsBoolean(boolean value) {

    if (this.name == null) {
      this.json.write(value);
    } else {
      this.json.write(this.name, value);
      this.name = null;
    }
  }

  @Override
  public void close() {

    this.json.close();
  }

}
