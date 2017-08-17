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
import java.util.List;
import java.util.zip.ZipEntry;

/**
 * Interface that describes ability to generate a set of files in the resulting split APK for a
 * given variant. Currently, the notion of a variant is very simplified: one dimension, and a
 * string.
 */
public interface VariantProcessor {

  /**
   * Returns the list of files from the module zip to be included in the split for a variant.
   *
   * @param variant a simple string representing a variant, currently we support only one
   * dimension.
   * @return A list of pairs: {@code ZipEntry} from the module zip to be included in the split,
   * {@code string} destination path in the split.
   */
  List<Pair<ZipEntry, String>> generateForVariant(String variant);

  /**
   * Returns list of all variants detected by this processor.
   */
  List<String> getAllVariants();
}
