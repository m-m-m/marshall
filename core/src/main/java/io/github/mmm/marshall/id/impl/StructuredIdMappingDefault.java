package io.github.mmm.marshall.id.impl;

import io.github.mmm.base.exception.DuplicateObjectException;
import io.github.mmm.marshall.StructuredProcessor;
import io.github.mmm.marshall.id.AbstractStructuredIdMapping;
import io.github.mmm.marshall.id.StructuredIdMapping;
import io.github.mmm.marshall.id.StructuredIdMappingMap;

/**
 * Default implementation of {@link StructuredIdMapping}.
 */
public class StructuredIdMappingDefault extends AbstractStructuredIdMapping implements StructuredIdMappingMap {

  private final Entry[] name2idMap;

  private final Entry[] id2nameMap;

  private int seq;

  /**
   * The constructor.
   *
   * @param capacity the fixed capacity of the map. Should be initialized with the number of properties.
   */
  public StructuredIdMappingDefault(int capacity) {

    super();
    int len = computeSize(capacity);
    this.id2nameMap = new Entry[len];
    this.name2idMap = new Entry[len];
    this.seq = 1;
  }

  static int computeSize(int capacity) {

    int len = -1 >>> Integer.numberOfLeadingZeros(capacity - 1);
    len++;
    if (len < 16) {
      len = 16;
    } else if (len > 2048) {
      len = 2048;
    }
    return len;
  }

  @Override
  public String name(int id) {

    if (id <= 0) {
      return null;
    }
    int hash = hashId(id);
    Entry entry = this.id2nameMap[hash];
    while (entry != null) {
      if (entry.id == id) {
        return entry.name;
      }
      entry = entry.nextId2name;
    }
    return super.name(id);
  }

  private int hashId(int id) {

    return id & (this.id2nameMap.length - 1);
  }

  @Override
  public int id(String name) {

    if (name == null) {
      return 0;
    }
    int hash = hashName(name);
    Entry entry = this.name2idMap[hash];
    while (entry != null) {
      if (name.equals(entry.name)) {
        return entry.id;
      }
      entry = entry.nextName2id;
    }
    return super.id(name);
  }

  private int hashName(String name) {

    return name.hashCode() & (this.name2idMap.length - 1);
  }

  /**
   * @param id the {@link #id(String) ID}.
   * @param name the {@link #name(int) name}.
   */
  @Override
  public void put(int id, String name) {

    boolean typeId = (id == TYPE);
    boolean typeName = (StructuredProcessor.TYPE.equals(name));
    if (typeId || typeName) {
      if (typeId && typeName) {
        return;
      } else if (!typeName) {
        throw new IllegalArgumentException("ID " + id + " cannot be used for name '" + name
            + "' as it is reserved for '" + StructuredProcessor.TYPE + "'.");
      } else {
        throw new IllegalArgumentException(
            "Name '" + name + "' cannot be mapped to ID " + id + " as it is implicitly mapped to " + TYPE + ".");
      }
    }
    Entry entry = new Entry(id, name);
    int idHash = hashId(id);
    if (this.id2nameMap[idHash] == null) {
      this.id2nameMap[idHash] = entry;
    } else {
      this.id2nameMap[idHash].addId2name(entry);
    }
    int nameHash = hashName(name);
    if (this.name2idMap[nameHash] == null) {
      this.name2idMap[nameHash] = entry;
    } else {
      this.name2idMap[nameHash].addName2id(entry);
    }
    if (id >= this.seq) {
      this.seq = id + 1;
      if (this.seq == TYPE) {
        this.seq++;
      }
    }
  }

  @Override
  public void put(String name) {

    put(this.seq, name);
  }

  static class Entry {

    final int id;

    final String name;

    Entry nextName2id;

    Entry nextId2name;

    Entry(int id, String name) {

      super();
      if (id <= 0) {
        throw new IllegalArgumentException("ID " + id + " is invalid - must be positive!");
      }
      this.id = id;
      this.name = name;
    }

    void addId2name(Entry entry) {

      Entry e;
      Entry next = this;
      do {
        e = next;
        if (e.id == entry.id) {
          throw new DuplicateObjectException(e, Integer.valueOf(e.id), entry);
        }
        next = e.nextId2name;
      } while (next != null);
      e.nextId2name = entry;
    }

    void addName2id(Entry entry) {

      Entry e;
      Entry next = this;
      do {
        e = next;
        if (entry.name.equals(e.name)) {
          throw new DuplicateObjectException(e, e.name, entry);
        }
        next = e.nextName2id;
      } while (next != null);
      e.nextName2id = entry;
    }

    @Override
    public String toString() {

      return this.id + "=" + this.name;
    }
  }

}
