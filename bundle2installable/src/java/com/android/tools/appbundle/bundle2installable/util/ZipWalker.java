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
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ZipWalker {

  private String zipFilename;
  private List<ZipEntryListener> listeners;
  private ZipFile zipFile;

  public ZipWalker(String zipFilename) {
    this.zipFilename = zipFilename;
    this.listeners = new ArrayList<ZipEntryListener>();
    this.zipFile = null;
  }

  public void walk() throws IOException {
    zipFile = new ZipFile(zipFilename);
    Enumeration<? extends ZipEntry> e = zipFile.entries();
    ZipEntry entry;
    while (e.hasMoreElements()) {
      entry = e.nextElement();
      System.out.println(String.format("Entry : %s", entry.getName()));
      for (ZipEntryListener listener : listeners) {
        listener.notify(zipFile, entry);
      }
    }
  }

  public void addListener(ZipEntryListener listener) {
    listeners.add(listener);
  }

  public ZipFile getZipFile() {
    return zipFile;
  }

  public interface ZipEntryListener {

    void notify(ZipFile bundle, ZipEntry entry) throws IOException;
  }
}
