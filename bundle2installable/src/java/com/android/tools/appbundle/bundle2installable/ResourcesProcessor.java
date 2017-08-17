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
import com.android.tools.appbundle.bundle2installable.util.ProtoUtils;
import com.android.tools.appbundle.bundle2installable.util.ZipWalker;
import com.android.tools.aapt2.FormatProto;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Detects variants in the res directory of the module zip.
 *
 * Currently, this class detects only one variant that will contain all resources.
 */
public class ResourcesProcessor implements ZipWalker.ZipEntryListener, VariantProcessor {

  public static final String FORMAT_PROTO_NAME = "format.textpb";
  public static final String RES_DIRECTORY = "res/";

  /**
   * For now we just copy all resources
   */
  private List<ZipEntry> allVariants;

  public ResourcesProcessor() {
    this.allVariants = new ArrayList<ZipEntry>();
  }

  public void notify(ZipFile bundle, ZipEntry entry) throws IOException {
    if (entry.getName().equals(FORMAT_PROTO_NAME)) {
      FormatProto.ResourceTable resourceProto = ProtoUtils.extractResourceProto(bundle, entry);
      // ... processing
    }
    if (entry.getName().startsWith(RES_DIRECTORY)) {
      if (!entry.isDirectory()) {
        allVariants.add(entry);
      }
    }
  }

  /**
   * For now, just generate everything
   */
  public List<Pair<ZipEntry, String>> generateForVariant(String variant) {
    List<Pair<ZipEntry, String>> res = new ArrayList<>();
    for (ZipEntry entry : allVariants) {
      res.add(Pair.of(entry, entry.getName()));
    }
    return res;
  }

  /**
   * Recognizes only one default variant
   */
  public List<String> getAllVariants() {
    return Arrays.asList("default");
  }

}
