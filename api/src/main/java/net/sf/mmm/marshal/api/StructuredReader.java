package net.sf.mmm.marshal.api;

/**
 * Interface for a reader to parse a {@link StructuredFormat structured format} such as JSON or XML.
 *
 * @since 1.0.0
 */
public interface StructuredReader {

  /**
   * @return the name of the current property or element.
   */
  String readName();

  /**
   * @return {@code true} if pointing to the start of an object, {@code false} otherwise.
   * @see StructuredWriter#writeStartObject()
   */
  boolean readStartObject();

  /**
   * @return {@code true} if pointing to the start of an object, {@code false} otherwise.
   * @see StructuredWriter#writeStartArray()
   */
  boolean readStartArray();

  /**
   * @param <V> type of the value to read.
   * @param type {@link Class} reflecting the value to read.
   * @return the parsed value.
   */
  <V> V readValue(Class<V> type);

  /**
   * @return {@code true} if the end of an {@link #readStartArray() array} or {@link #readStartObject() object} has been
   *         reached.
   */
  boolean readEnd();

  /**
   * @return {@code true} if all data has been read and the end of the stream has been reached, {@code false} otherwise.
   */
  boolean isDone();

}
