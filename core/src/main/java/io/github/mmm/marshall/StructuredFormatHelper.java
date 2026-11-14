/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.marshall;

import java.nio.file.Path;

/// Helper to [get][#getProvider(Path)] a [StructuredFormatProvider] from a [Path] or its filename.
///
/// @since 1.0.0
public interface StructuredFormatHelper {

  /// @param path the [Path] pointing to a file where to store [StructuredFormat] payload (a marshalled
  ///        object).
  /// @return the [StructuredFormatProvider] determined from the file extension.
  static StructuredFormatProvider getProvider(Path path) {

    return getProvider(path.getFileName().toString());
  }

  /// @param filename the [file name][Path#getFileName()] as [String].
  /// @return the [StructuredFormatProvider] determined from the file extension.
  static StructuredFormatProvider getProvider(String filename) {

    int lastDot = filename.lastIndexOf('.');
    String id = filename;
    if (lastDot >= 0) {
      id = filename.substring(lastDot + 1); // extension
    }
    return StructuredFormatFactory.get().getProvider(id);
  }

}
