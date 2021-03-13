package io.github.mmm.marshall.stax.impl;

import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

/**
 * {@link XMLStreamWriter} with indentation support.
 *
 * @since 1.0.0
 */
public class IndentingXmlStreamWriter implements XMLStreamWriter {

  private final XMLStreamWriter xml;

  private final String indentation;

  private boolean chars;

  private int indentCount;

  /**
   * The constructor.
   *
   * @param xml the {@link XMLStreamWriter} to wrap and delegate to.
   * @param indentation the {@link io.github.mmm.marshall.MarshallingConfig#OPT_INDENTATION indentation}.
   */
  public IndentingXmlStreamWriter(XMLStreamWriter xml, String indentation) {

    super();
    this.xml = xml;
    this.indentation = indentation;
  }

  private void newline() throws XMLStreamException {

    writeCharacters("\n");
  }

  @Override
  public void writeStartDocument() throws XMLStreamException {

    this.xml.writeStartDocument();
  }

  @Override
  public void writeStartDocument(String version) throws XMLStreamException {

    this.xml.writeStartDocument(version);
  }

  @Override
  public void writeStartDocument(String encoding, String version) throws XMLStreamException {

    this.xml.writeStartDocument(encoding, version);
  }

  @Override
  public void writeEndDocument() throws XMLStreamException {

    this.xml.writeEndDocument();
  }

  private void writeIndent(int count) throws XMLStreamException {

    if (this.indentation == null) {
      return;
    }
    newline();
    for (int i = count; i > 0; i--) {
      writeCharacters(this.indentation);
    }
  }

  private void indent() throws XMLStreamException {

    writeIndent(this.indentCount);
    this.indentCount++;
    this.chars = false;
  }

  private void outdent() throws XMLStreamException {

    if (this.chars) {
      return;
    }
    this.indentCount--;
    writeIndent(this.indentCount);
  }

  @Override
  public void writeStartElement(String localName) throws XMLStreamException {

    indent();
    this.xml.writeStartElement(localName);
  }

  @Override
  public void writeStartElement(String namespaceURI, String localName) throws XMLStreamException {

    indent();
    this.xml.writeStartElement(namespaceURI, localName);

  }

  @Override
  public void writeStartElement(String prefix, String localName, String namespaceURI) throws XMLStreamException {

    indent();
    this.xml.writeStartElement(prefix, localName, namespaceURI);
  }

  @Override
  public void writeEmptyElement(String localName) throws XMLStreamException {

    writeIndent(this.indentCount);
    this.xml.writeEmptyElement(localName);
  }

  @Override
  public void writeEmptyElement(String namespaceURI, String localName) throws XMLStreamException {

    writeIndent(this.indentCount);
    this.xml.writeEmptyElement(namespaceURI, localName);
  }

  @Override
  public void writeEmptyElement(String prefix, String localName, String namespaceURI) throws XMLStreamException {

    writeIndent(this.indentCount);
    this.xml.writeEmptyElement(prefix, namespaceURI, localName);
  }

  @Override
  public void writeEndElement() throws XMLStreamException {

    outdent();
    this.xml.writeEndElement();
  }

  @Override
  public void writeAttribute(String localName, String value) throws XMLStreamException {

    this.xml.writeAttribute(localName, value);
  }

  @Override
  public void writeAttribute(String namespaceURI, String localName, String value) throws XMLStreamException {

    this.xml.writeAttribute(namespaceURI, localName, value);
  }

  @Override
  public void writeAttribute(String prefix, String namespaceURI, String localName, String value)
      throws XMLStreamException {

    this.xml.writeAttribute(prefix, namespaceURI, localName, value);
  }

  @Override
  public void writeCharacters(String text) throws XMLStreamException {

    this.xml.writeCharacters(text);
  }

  @Override
  public void writeCharacters(char[] text, int start, int len) throws XMLStreamException {

    this.xml.writeCharacters(text, start, len);
  }

  @Override
  public void writeCData(String data) throws XMLStreamException {

    this.xml.writeCData(data);
    this.chars = true;
  }

  @Override
  public void writeComment(String data) throws XMLStreamException {

    this.xml.writeComment(data);
    this.chars = false;
  }

  @Override
  public void writeNamespace(String prefix, String namespaceURI) throws XMLStreamException {

    this.xml.writeNamespace(prefix, namespaceURI);
  }

  @Override
  public void writeDefaultNamespace(String namespaceURI) throws XMLStreamException {

    this.xml.writeDefaultNamespace(namespaceURI);
  }

  @Override
  public void writeProcessingInstruction(String target) throws XMLStreamException {

    this.xml.writeProcessingInstruction(target);
  }

  @Override
  public void writeProcessingInstruction(String target, String data) throws XMLStreamException {

    this.xml.writeProcessingInstruction(target, data);
  }

  @Override
  public void writeDTD(String dtd) throws XMLStreamException {

    this.xml.writeDTD(dtd);
  }

  @Override
  public void writeEntityRef(String name) throws XMLStreamException {

    this.xml.writeEntityRef(name);
  }

  @Override
  public String getPrefix(String uri) throws XMLStreamException {

    return this.xml.getPrefix(uri);
  }

  @Override
  public void setPrefix(String prefix, String uri) throws XMLStreamException {

    this.xml.setPrefix(prefix, uri);
  }

  @Override
  public void setDefaultNamespace(String uri) throws XMLStreamException {

    this.xml.setDefaultNamespace(uri);
  }

  @Override
  public void setNamespaceContext(NamespaceContext context) throws XMLStreamException {

    this.xml.setNamespaceContext(context);
  }

  @Override
  public NamespaceContext getNamespaceContext() {

    return this.xml.getNamespaceContext();
  }

  @Override
  public Object getProperty(String name) throws IllegalArgumentException {

    return this.xml.getProperty(name);
  }

  @Override
  public void close() throws XMLStreamException {

    this.xml.close();
  }

  @Override
  public void flush() throws XMLStreamException {

    this.xml.flush();
  }

}
