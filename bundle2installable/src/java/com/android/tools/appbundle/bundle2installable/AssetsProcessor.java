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

import static com.android.tools.appbundle.bundle2installable.util.PathUtils.getFilePathWithoutTopLevel;
import static com.android.tools.appbundle.bundle2installable.util.PathUtils.getFilePathWithoutVariant;

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
 * Assets directory variant processor.
 *
 * Currently, scans the directory in the assets directory of the module zip. Every directory name
 * is considered a variant name, and the final split will contain files from the variant directory
 * with that directory elided: all goes directly in the assets directory. Each variant will include
 * files that are directly in the assets root directory.
 */
public class AssetsProcessor implements ZipWalker.ZipEntryListener, VariantProcessor {

  private static final String ASSETS_DIR = "assets";

  private Map<String, List<ZipEntry>> variantAssetsMap;
  private List<ZipEntry> nonVariantAssets;

  public AssetsProcessor() {
    this.variantAssetsMap = new HashMap<>();
    this.nonVariantAssets = new ArrayList<>();
  }

  public void notify(ZipFile bundle, ZipEntry entry) throws IOException {
    // TODO(b/64285122): fix the asset variants detection when there is only non-variant data.
    Path p = Paths.get(entry.getName());
    if (PathUtils.isInsideTopDirectory(p, ASSETS_DIR)) {
      String variant = PathUtils.resolveVariant(p);
      if (variant != null) { // inside the variant directory
        if (!variantAssetsMap.containsKey(variant)) {
          variantAssetsMap.put(variant, new ArrayList<>());
        }
        variantAssetsMap.get(variant).add(entry);
      } else { // inside the assets directory
        System.out.println("Entry name: " + entry.getName());
        if (!entry.isDirectory()) {
          System.out.println("..is not a directory");
          nonVariantAssets.add(entry);
        }
      }
    }
  }

  public List<Pair<ZipEntry, String>> generateForVariant(String variant) {
    List<Pair<ZipEntry, String>> res = new ArrayList<>();
    Set<String> coveredFiles = new HashSet<>();
    if (!variantAssetsMap.containsKey(variant)) {
      System.out.println(
          String.format("Warning! The '%s' variant has no dedicated assets.",
              variant));
    } else {
      for (ZipEntry entry : variantAssetsMap.get(variant)) {
        String pathWithoutVariant = getFilePathWithoutVariant(entry.getName());
        coveredFiles.add(pathWithoutVariant);
        res.add(Pair.of(entry, Paths.get(ASSETS_DIR, pathWithoutVariant).toString()));
      }
    }
    for (ZipEntry entry : nonVariantAssets) {
      String pathWithoutVariant = getFilePathWithoutTopLevel(entry.getName());
      if (!coveredFiles.contains(pathWithoutVariant)) {
        res.add(Pair.of(entry, Paths.get(ASSETS_DIR, pathWithoutVariant).toString()));
      }
    }
    return res;
  }

  public List<String> getAllVariants() {
    return new ArrayList<>(variantAssetsMap.keySet());
  }
}
