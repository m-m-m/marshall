package net.sf.mmm.marshal.api;

/**
 * Interface for an object with read-access that can therefore be {@link #write(StructuredWriter) written} to structured
 * formats such as JSON or XmlFormat.
 *
 * @see UnmarshalableObject
 * @since 1.0.0
 */
public interface MarshalableObject {

  /**
   * @param writer the {@link StructuredWriter} where to write the data of this object to. This process is also called
   *        serialization or marshaling. Create via e.g. {@code JsonFormat.get().writer(writer)} or
   *        {@code XmlFormat.get().writer(writer)}.
   */
  void write(StructuredWriter writer);

}
