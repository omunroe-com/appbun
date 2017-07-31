/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.android.tools.appbundle.bundle2installable.util;

import java.nio.file.Path;
import java.nio.file.Paths;
import javax.annotation.Nullable;

/**
 * Various utilities for path processing.
 */
public class PathUtils {

  private static final int TOP_LEVEL = 0;
  private static final int VARIANT_LEVEL = 1;
  private static final int LEVEL_IN_VARIANT = 2;
  private static final int LEVEL_IN_DEFAULT = 1;

  public static String stripExtension(String filename) {
    return filename.substring(0, filename.lastIndexOf('.'));
  }

  public static String makeSplitName(String module, String split) {
    return module + "-" + split + ".zip";
  }

  public static String stripDirectories(String fullPath) {
    Path p = Paths.get(fullPath);
    return p.getName(p.getNameCount() - 1).toString();
  }

  public static boolean isInsideTopDirectory(Path p, String directoryName) {
    return p.getNameCount() > 1 && p.getName(TOP_LEVEL).toString().equals(directoryName);
  }

  @Nullable
  public static String resolveVariant(Path p) {
    if (p.getNameCount() > 2) { // inside the variant directory
      return p.getName(VARIANT_LEVEL).toString();
    }
    return null;
  }

  public static String getFilePathWithoutVariant(String entryPath) {
    return removeTopLevelPath(entryPath, LEVEL_IN_VARIANT);
  }

  public static String getFilePathWithoutTopLevel(String entryPath) {
    return removeTopLevelPath(entryPath, LEVEL_IN_DEFAULT);
  }

  public static String removeTopLevelPath(String entryPath, int numLevels) {
    Path p = Paths.get(entryPath);
    System.out.println(entryPath);
    return p.subpath(numLevels, p.getNameCount()).toString();
  }

}
