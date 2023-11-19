package io.github.mmm.marshall.id.impl;

import io.github.mmm.marshall.id.AbstractStructuredIdMapping;
import io.github.mmm.marshall.id.StructuredIdMapping;

/**
 * Implementation of {@link StructuredIdMapping} using identity mapping ({@code  1 <-> "1"}).
 */
public final class StructuredIdMappingIdentity extends AbstractStructuredIdMapping {

  private static final StructuredIdMappingIdentity INSTANCE = new StructuredIdMappingIdentity();

  private final String[] names;

  private StructuredIdMappingIdentity() {

    super();
    this.names = new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16",
    "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35",
    "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "50", "51", "52", "53", "54",
    "55", "56", "57", "58", "59", "60", "61", "62", "63", "64" };
  }

  @Override
  public String name(int id) {

    if (id <= 0) {
      return null;
    }
    if (id <= this.names.length) {
      return this.names[id - 1];
    }
    String name = super.name(id);
    if (name == null) {
      name = Integer.toString(id);
    }
    return name;
  }

  @Override
  public int id(String name) {

    int id = super.id(name);
    if (id == 0) {
      id = Integer.parseInt(name);
    }
    return id;
  }

  /**
   * @return the singleton instance.
   */
  public static StructuredIdMappingIdentity get() {

    return INSTANCE;
  }

}
