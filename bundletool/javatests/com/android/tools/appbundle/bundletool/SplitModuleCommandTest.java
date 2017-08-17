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

import static org.junit.Assert.assertThrows;

import com.android.tools.appbundle.bundletool.Command.ExecutionException;
import com.android.tools.appbundle.bundletool.utils.FlagParser;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/** Tests for the SplitModuleCommand implementation. */
@RunWith(JUnit4.class)
public class SplitModuleCommandTest {

  @Test
  public void testMissingOutputFlag() throws Exception {
    expectFlagException(new String[] {"--bundle=b.zip", "--module=m1"});
  }

  @Test
  public void testMissingBundleFlag() throws Exception {
    expectFlagException(new String[] {"--output=/some/dir", "--module=m1"});
  }

  @Test
  public void testMissingModuleFlag() throws Exception {
    expectFlagException(new String[] {"--output=/some/dir", "--bundle=b.zip"});
  }

  @Test
  public void testMissingBundleFileFailsCommand() throws Exception {
    FlagParser flagParser = new FlagParser();
    flagParser.parse(new String[] {"--bundle=b.zip", "--output=/some/dir", "--module=m1"});
    SplitModuleCommand command = new SplitModuleCommand(flagParser);
    assertThrows(ExecutionException.class, () -> command.execute());
  }

  private void expectFlagException(String[] flags) throws Exception {
    FlagParser flagParser = new FlagParser();
    flagParser.parse(flags);
    assertThrows(IllegalArgumentException.class, () -> new SplitModuleCommand(flagParser));
  }
}
