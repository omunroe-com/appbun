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

package com.android.tools.appbundle.bundle2installable;

import com.android.tools.appbundle.bundle2installable.util.Pair;
import com.android.tools.appbundle.bundle2installable.util.PathUtils;
import com.android.tools.appbundle.bundle2installable.util.ZipWalker;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Processes native library directory and discovers cpu variants.
 *
 * Currently, expects ABI to be expressed as a subdirectory name inside "native" directory.
 * The libraries are outputted to the "lib" directory as per APK standard.
 */
public class NativeProcessor implements ZipWalker.ZipEntryListener, VariantProcessor {

  private static final String NATIVE_DIR = "native";
  private static final String DESTINATION_DIR = "lib";

  private Map<String, List<ZipEntry>> variantLibsMap;
  private List<ZipEntry> nonVariantLibs;

  public NativeProcessor() {
    this.variantLibsMap = new HashMap<>();
    this.nonVariantLibs = new ArrayList<>();
  }

  public void notify(ZipFile bundle, ZipEntry entry) throws IOException {
    Path p = Paths.get(entry.getName());
    if (PathUtils.isInsideTopDirectory(p, NATIVE_DIR)) {
      String variant = PathUtils.resolveVariant(p);
      if (variant != null) {
        if (!variantLibsMap.containsKey(variant)) {
          variantLibsMap.put(variant, new ArrayList<>());
        }
        variantLibsMap.get(variant).add(entry);
      } else { // inside the libs directory
        if (!entry.isDirectory()) {
          // aapt2 doesn't strip any files in libs/ top-level. So we should
          // include them in each variant as well.
          nonVariantLibs.add(entry);
        }
      }
    }
  }

  public List<String> getAllVariants() {
    return new ArrayList<>(variantLibsMap.keySet());
  }

  /**
   * Returns pair of zip entries and the destination path in the split for the variant
   */
  public List<Pair<ZipEntry, String>> generateForVariant(String variant) {
    List<Pair<ZipEntry, String>> res = new ArrayList<>();
    Set<String> coveredFiles = new HashSet<>();
    if (!variantLibsMap.containsKey(variant)) {
      throw new IllegalStateException(
          String.format("Variant '%s' doesn't exist for the native libraries.", variant));
    } else {
      for (ZipEntry entry : variantLibsMap.get(variant)) {
        String pathWithoutVariant = PathUtils.getFilePathWithoutVariant(entry.getName());
        coveredFiles.add(pathWithoutVariant);
        // our destination path needs to include architecture/variant though
        Path destinationPath = Paths.get(DESTINATION_DIR, variant, pathWithoutVariant);
        res.add(Pair.of(entry, destinationPath.toString()));
      }
    }
    for (ZipEntry entry : nonVariantLibs) {
      String pathWithoutVariant = PathUtils.getFilePathWithoutTopLevel(entry.getName());
      if (!coveredFiles.contains(pathWithoutVariant)) {
        res.add(Pair.of(entry, Paths.get(DESTINATION_DIR, pathWithoutVariant).toString()));
      }
    }
    return res;
  }
}
