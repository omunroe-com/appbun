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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.zip.ZipOutputStream;

/**
 * A linker that can create an installable from the module splits.
 */
public class DeliverableLinker {

  public static void makeDeliverable(String deliverablePath, String splitDirectory,
      List<String> modules, List<String> splits) throws IOException {
    ZipOutputStream outputStream = new ZipOutputStream(new FileOutputStream(deliverablePath));
    for (String module : modules) {
      for (String split : splits) {
        Path p = Paths.get(splitDirectory, PathUtils.makeSplitName(module, split));
        if (Files.isRegularFile(p)) {
          System.out.println(String.format("Linking %s", p.toString()));
          ZipUtils.writeEntry(PathUtils.stripDirectories(p.toString()), outputStream,
              new FileInputStream(p.toString()));
        }
      }
    }
    outputStream.close();
  }

}
