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
import com.android.tools.appbundle.bundle2installable.util.ZipWalker;
import java.io.IOException;

/**
 * Implementation of the split-module command.
 */
public class ModuleSplitter {

  public static void doSplitModule(String[] args) throws IOException {
    if (args.length < 3) {
      throw new IllegalStateException(
          "Incorrect number of args. Use bundle2installable split-module [module.zip] [output-dir]");
    }
    String moduleFilename = args[1];
    String outputFilename = args[2];

    ZipWalker visitor = new ZipWalker(moduleFilename);
    ResourcesProcessor resourcesProcessor = new ResourcesProcessor();
    AssetsProcessor assetsProcessor = new AssetsProcessor();
    NativeProcessor nativeProcessor = new NativeProcessor();
    visitor.addListener(resourcesProcessor);
    visitor.addListener(assetsProcessor);
    visitor.addListener(nativeProcessor);
    visitor.walk();

    System.out.println("Detected asset variants: " + assetsProcessor.getAllVariants().toString());
    System.out.println("Detected native variants: " + nativeProcessor.getAllVariants().toString());
    System.out
        .println("Detected resource variants: " + resourcesProcessor.getAllVariants().toString());

    for (String variant : assetsProcessor.getAllVariants()) {
      String splitName = outputFilename + "/" + PathUtils
          .stripExtension(PathUtils.stripDirectories(moduleFilename)) + "-" + variant + ".zip";
      PreSplitGenerator outGen = new PreSplitGenerator(splitName);
      outGen.writePreSplit(visitor.getZipFile(), assetsProcessor.generateForVariant(variant));
    }
    for (String variant : nativeProcessor.getAllVariants()) {
      String splitName = outputFilename + "/" + PathUtils
          .stripExtension(PathUtils.stripDirectories(moduleFilename)) + "-" + variant + ".zip";
      PreSplitGenerator outGen = new PreSplitGenerator(splitName);
      outGen.writePreSplit(visitor.getZipFile(), nativeProcessor.generateForVariant(variant));
    }
    for (String variant : resourcesProcessor.getAllVariants()) {
      String splitName = outputFilename + "/" + PathUtils
          .stripExtension(PathUtils.stripDirectories(moduleFilename)) + "-res-" + variant + ".zip";
      PreSplitGenerator outGen = new PreSplitGenerator(splitName);
      outGen.writePreSplit(visitor.getZipFile(), resourcesProcessor.generateForVariant(variant));
    }
  }


}
