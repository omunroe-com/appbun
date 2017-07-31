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

import com.android.tools.aapt2.FormatProto;
import com.google.protobuf.TextFormat;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Protobuf utility functions used throughout the tool.
 */
public class ProtoUtils {

  /**
   * Reads format.proto from the aapt2 output. For now using the textproto format.
   */
  public static FormatProto.ResourceTable extractResourceProto(ZipFile bundle, ZipEntry entry)
      throws IOException {
    System.out.println(String.format("Extracting %s", entry.getName()));

    FormatProto.ResourceTable.Builder tableBuilder = FormatProto.ResourceTable.newBuilder();
    TextFormat.merge(new InputStreamReader(bundle.getInputStream(entry)), tableBuilder);
    return tableBuilder.build();
  }

}
