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

package com.android.tools.appbundle.bundletool.utils;

import static com.google.common.truth.Truth.assertThat;
import static junit.framework.TestCase.fail;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/** Tests for the FlagParser class. */
@RunWith(JUnit4.class)
public class FlagParserTest {

  @Test
  public void testParsesSingleCommand() throws Exception {
    String[] args = {"command1"};
    FlagParser fp = new FlagParser();
    fp.parse(args);
    assertThat(fp.getCommands()).containsExactly("command1");
  }

  @Test
  public void testEmptyCommandLine() throws Exception {
    String[] args = {};
    FlagParser fp = new FlagParser();
    fp.parse(args);
    assertThat(fp.getCommands()).isEmpty();
  }

  @Test
  public void testParsesCommandLineFlags() throws Exception {
    String[] args = {"command", "--flag1=value1", "--flag2=value2"};
    FlagParser fp = new FlagParser();
    fp.parse(args);
    assertThat(fp.getCommands()).containsExactly("command");
    assertThat(fp.getFlagValueOrDefault("flag1", "")).isEqualTo("value1");
    assertThat(fp.getFlagValueOrDefault("flag2", "")).isEqualTo("value2");
  }

  @Test
  public void testIncorrectCommandLineFlagsThrows() throws Exception {
    String[] args = {"command", "--flag1=value1", "-flag2=value2"};
    FlagParser fp = new FlagParser();
    try {
      fp.parse(args);
      fail("Expected ParseException but nothing was thrown.");
    } catch (FlagParser.ParseException e) {
      assertThat(e.getMessage()).contains("-flag2=value2");
    }
  }

  @Test
  public void testFlagListValuesNotSet() throws Exception {
    String[] args = {"command"};
    FlagParser fp = new FlagParser();
    fp.parse(args);
    assertThat(fp.getCommands()).containsExactly("command");
    assertThat(fp.getFlagListValue("flag1")).isEmpty();
  }

  @Test
  public void testFlagListValuesSetWithDefault() throws Exception {
    String[] args = {"command", "--flag1"};
    FlagParser fp = new FlagParser();
    fp.parse(args);
    assertThat(fp.getCommands()).containsExactly("command");
    assertThat(fp.getFlagListValue("flag1")).containsExactly("");
  }

  @Test
  public void testFlagListValuesSingleValue() throws Exception {
    String[] args = {"command", "--flag1=val1"};
    FlagParser fp = new FlagParser();
    fp.parse(args);
    assertThat(fp.getFlagListValue("flag1")).containsExactly("val1");
  }

  @Test
  public void testFlagListValuesMultiple() throws Exception {
    String[] args = {"command", "--flag1=v1,v2,value3"};
    FlagParser fp = new FlagParser();
    fp.parse(args);
    assertThat(fp.getCommands()).containsExactly("command");
    assertThat(fp.getFlagValueOrDefault("flag1", "")).isEqualTo("v1,v2,value3");
    assertThat(fp.getFlagListValue("flag1")).containsExactly("v1", "v2", "value3").inOrder();
  }

  @Test
  public void testHandlingAbsentFlags() throws Exception {
    String[] args = {"command", "--flag1=v1"};
    FlagParser fp = new FlagParser();
    fp.parse(args);
    assertThat(fp.getCommands()).containsExactly("command");
    assertThat(fp.getFlagValueOrDefault("flag2", "default")).isEqualTo("default");
    assertThat(fp.getFlagListValue("flag2")).isEmpty();
    assertThat(fp.isFlagSet("flag2")).isFalse();
  }

  @Test
  public void testHandlingMultipleCommands() throws Exception {
    String[] args = {"help", "command1"};
    FlagParser fp = new FlagParser();
    fp.parse(args);
    assertThat(fp.getCommands()).containsExactly("help", "command1").inOrder();
  }

  @Test
  public void testUsingFlagMoreThanOnceThrows() throws Exception {
    String[] args = {"command", "--flag1=v1", "--flag1=v2"};
    FlagParser fp = new FlagParser();
    try {
      fp.parse(args);
      fail("Expected ParseException but nothing was thrown.");
    } catch (FlagParser.ParseException e) {
      assertThat(e.getMessage()).contains("flag1");
    }
  }
}
