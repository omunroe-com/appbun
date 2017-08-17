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

import com.android.tools.appbundle.bundle2installable.util.PathUtils;
import com.android.tools.appbundle.bundle2installable.util.ZipUtils;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 * Class that takes a bundle and outputs the zipped modules from it.
 *
 * As modules are being processed separately, this will put them in the right
 * input format.
 *
 * By default it outputs all modules contained in the bundle.
 */
public class ModuleZipMaker {

  public static void generateModuleZips(String bundleFile, String outputDirectory)
      throws IOException {
    ZipFile bundleZip = new ZipFile(bundleFile);
    Enumeration<? extends ZipEntry> e = bundleZip.entries();
    Map<String, List<ZipEntry>> moduleEntries = new HashMap<>();
    ZipEntry entry;
    while (e.hasMoreElements()) {
      entry = e.nextElement();
      System.out.println(String.format("Entry: %s", entry.getName()));
      Path p = Paths.get(entry.getName());
      if (p.getNameCount() > 1) {
        String moduleName = p.getName(0).toString();
        if (!moduleEntries.containsKey(moduleName)) {
          moduleEntries.put(moduleName, new ArrayList<ZipEntry>());
        }
        if (!entry.isDirectory()) {
          moduleEntries.get(moduleName).add(entry);
        }
      }
    }
    for (String moduleDirectory : moduleEntries.keySet()) {
      System.out.println(String.format("Module: %s", moduleDirectory));
      for (ZipEntry moduleEntry : moduleEntries.get(moduleDirectory)) {
        System.out.println(String.format("Entry : %s", moduleEntry.getName()));
      }
      deflateModule(Paths.get(outputDirectory, moduleDirectory + ".zip").toString(),
          bundleZip, moduleEntries.get(moduleDirectory));
    }
  }

  private static void deflateModule(String moduleZipOutput, ZipFile bundleFile,
      List<ZipEntry> entries) throws IOException {
    ZipOutputStream outputStream = new ZipOutputStream(new FileOutputStream(moduleZipOutput));
    for (ZipEntry entry : entries) {
      ZipUtils.writeEntry(PathUtils.getFilePathWithoutTopLevel(entry.getName()), outputStream,
          bundleFile.getInputStream(entry));
    }
    outputStream.close();
  }

  private static boolean isModuleDirectory(ZipEntry entry) {
    throw new UnsupportedOperationException("Not implemented yet");
  }
}
