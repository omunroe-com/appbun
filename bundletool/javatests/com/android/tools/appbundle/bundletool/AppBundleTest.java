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

package com.android.tools.appbundle.bundletool;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/** Tests for the AppBundle class. */
@RunWith(JUnit4.class)
public class AppBundleTest {

  private ZipFile bundleFile;
  private Vector<ZipEntry> entries = new Vector<>();

  @Before
  public void setUp() {
    bundleFile = mock(ZipFile.class);
  }

  @Test
  public void testSingleModuleBundle() {
    putEntry("/module1/classes.dex");
    doReturn(entries.elements()).when(bundleFile).entries();

    AppBundle appBundle = new AppBundle(bundleFile);
    assertThat(appBundle.getModules().keySet()).containsExactly("module1");
  }

  @Test
  public void testNoModulesWhenFilesAtRoot() {
    putEntry("/deliverables.pb");
    putEntry("variants.pb");
    doReturn(entries.elements()).when(bundleFile).entries();

    AppBundle appBundle = new AppBundle(bundleFile);
    assertThat(appBundle.getModules().keySet()).isEmpty();
  }

  @Test
  public void testMultipleModules() {
    putEntry("base/AndroidManifest.flat");
    putEntry("base/Format.flat");
    putEntry("base/classes.dex");
    putEntry("base/assets/textures.etc1");
    putEntry("base/res/drawable-hdpi/title.jpg");
    putEntry("detail/AndroidManifest.flat");
    putEntry("detail/Format.flat");
    doReturn(entries.elements()).when(bundleFile).entries();

    AppBundle appBundle = new AppBundle(bundleFile);
    assertThat(appBundle.getModules().keySet()).containsExactly("base", "detail");
  }

  private void putEntry(String fakeFile) {
    entries.add(new ZipEntry(fakeFile));
  }
}
