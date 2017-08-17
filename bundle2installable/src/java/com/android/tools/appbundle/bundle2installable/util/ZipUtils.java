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

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Various zip utilities to avoid code duplication.
 */
public class ZipUtils {

  private static final int BUFFER = 2048;

  public static void writeEntry(String entryName, ZipOutputStream outputStream,
      InputStream inputStream) throws IOException {
    outputStream.putNextEntry(new ZipEntry(entryName));
    byte data[] = new byte[BUFFER];
    int count;
    while ((count = inputStream.read(data, 0, BUFFER)) != -1) {
      outputStream.write(data, 0, count);
    }
    inputStream.close();
    outputStream.closeEntry();
  }
}
