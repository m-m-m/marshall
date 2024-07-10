/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.marshall;

import java.nio.file.Path;

/**
 * Helper to {@link #getProvider(Path) get} a {@link StructuredFormatProvider} from a {@link Path} or its filename.
 *
 * @since 1.0.0
 */
public interface StructuredFormatHelper {

  /**
   * @param path the {@link Path} pointing to a file where to store {@link StructuredFormat} payload (a marshalled
   *        object).
   * @return the {@link StructuredFormatProvider} determined from the file extension.
   */
  static StructuredFormatProvider getProvider(Path path) {

    return getProvider(path.getFileName().toString());
  }

  /**
   *
   * @param filename the {@link Path#getFileName() file name} as {@link String}.
   * @return the {@link StructuredFormatProvider} determined from the file extension.
   */
  static StructuredFormatProvider getProvider(String filename) {

    int lastDot = filename.lastIndexOf('.');
    String id = filename;
    if (lastDot >= 0) {
      id = filename.substring(lastDot + 1); // extension
    }
    return StructuredFormatFactory.get().getProvider(id);
  }

}
