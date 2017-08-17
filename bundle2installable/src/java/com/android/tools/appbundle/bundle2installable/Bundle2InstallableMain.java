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

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Main entry point of the bundle2installable tool
 */
public class Bundle2InstallableMain {

  public static void main(String[] args) throws IOException {
    if (args.length < 1) {
      throw new IllegalStateException(
          "Incorrect number of args. Use bundle2installable [command] ...");
    }
    String command = args[0];
    switch (command) {
      case "split-module":
        ModuleSplitter.doSplitModule(args);
        break;
      case "generate":
        ModuleZipMaker.generateModuleZips(args[1], args[2]);
        break;
      case "link":
        DeliverableLinker.makeDeliverable(args[1], args[2], makeList(args[3]),
            makeList(args[4]));
        break;
    }
  }

  private static List<String> makeList(String flag) {
    String[] equal = flag.split("=");
    String equalVal = equal[0];
    if (equal.length > 1) { // assignment
      equalVal = equal[1];
    }
    return Arrays.asList(equalVal.split(","));
  }
}
