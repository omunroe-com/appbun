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
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.expectThrows;

import com.android.tools.appbundle.bundletool.utils.FlagParser;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class BuildModuleCommandTest {

  @Rule public TemporaryFolder tmp = new TemporaryFolder();

  private Path outputPath;
  private Path manifestPath;
  private Path manifestDirPath;
  private Path dexPath;
  private Path dexDirPath;
  private Path resourcesDirPath;
  private Path assetsDirPath;
  private Path nativeDirPath;

  private Path pathThatDoesNotExist;

  @Before
  public void setUp() throws IOException {
    outputPath = Paths.get(tmp.getRoot().getPath(), "bundle");
    manifestPath = tmp.newFile("AndroidManifest.flat").toPath();
    manifestDirPath = tmp.newFolder("manifest").toPath();
    dexPath = tmp.newFile("classes.dex").toPath();
    dexDirPath = tmp.newFolder("dex").toPath();
    resourcesDirPath = tmp.newFolder("resources").toPath();
    assetsDirPath = tmp.newFolder("assets").toPath();
    nativeDirPath = tmp.newFolder("native").toPath();

    pathThatDoesNotExist = Paths.get(tmp.getRoot().getPath(), "path-that-does-not-exist");
  }

  @Test
  public void validConfig_viaFlags() {
    FlagParser flagParser =
        new FlagParser()
            .parse(
                new String[] {
                  "--output=" + outputPath,
                  "--manifest=" + manifestPath,
                  "--dexPath=" + dexPath,
                  "--resourcesDirPath=" + resourcesDirPath,
                  "--assetsDirPath=" + assetsDirPath,
                  "--nativeDirPath=" + nativeDirPath,
                });
    BuildModuleCommand.fromFlags(flagParser).execute();
  }

  @Test
  public void validConfig_viaBuilder() {
    BuildModuleCommand.builder()
        .setOutputPath(outputPath)
        .setManifestPath(manifestPath)
        .setDexPath(dexPath)
        .setResourcesDirPath(resourcesDirPath)
        .setAssetsDirPath(assetsDirPath)
        .setNativeDirPath(nativeDirPath)
        .build()
        .execute();
  }

  @Test
  public void outputPathNotSetThrowsException_viaFlags() {
    FlagParser flagParser = new FlagParser().parse(new String[] {"--manifest=" + manifestPath});
    IllegalArgumentException exception =
        expectThrows(
            IllegalArgumentException.class,
            () -> BuildModuleCommand.fromFlags(flagParser).execute());

    assertThat(exception).hasMessageThat().contains("--output");
  }

  @Test
  public void outputPathNotSetThrowsException_viaBuilder() {
    assertThrows(
        IllegalStateException.class,
        () -> BuildModuleCommand.builder().setManifestPath(manifestPath).build().execute());
  }

  @Test
  public void manifestAndManifestDirNotSetThrowsException_viaFlags() {
    FlagParser flagParser = new FlagParser().parse(new String[] {"--output=" + outputPath});
    IllegalArgumentException exception =
        expectThrows(
            IllegalArgumentException.class,
            () -> BuildModuleCommand.fromFlags(flagParser).execute());

    assertThat(exception).hasMessageThat().contains("--manifest");
  }

  @Test
  public void manifestAndManifestDirNotSetThrowsException_viaBuilder() {
    IllegalArgumentException exception =
        expectThrows(
            IllegalArgumentException.class,
            () -> BuildModuleCommand.builder().setOutputPath(outputPath).build().execute());

    assertThat(exception).hasMessageThat().contains("--manifest");
  }

  @Test
  public void manifestAndManifestDirBothSetThrowsException_viaFlags() {
    FlagParser flagParser =
        new FlagParser()
            .parse(
                new String[] {
                  "--output=" + outputPath,
                  "--manifest=" + manifestPath,
                  "--manifest-dir=" + manifestDirPath
                });
    IllegalArgumentException exception =
        expectThrows(
            IllegalArgumentException.class,
            () -> BuildModuleCommand.fromFlags(flagParser).execute());

    assertThat(exception).hasMessageThat().contains("--manifest");
  }

  @Test
  public void manifestAndManifestDirBothSetThrowsException_viaBuilder() {
    IllegalArgumentException exception =
        expectThrows(
            IllegalArgumentException.class,
            () ->
                BuildModuleCommand.builder()
                    .setOutputPath(outputPath)
                    .setManifestPath(manifestPath)
                    .setManifestDirPath(manifestDirPath)
                    .build()
                    .execute());

    assertThat(exception).hasMessageThat().contains("--manifest");
  }

  @Test
  public void dexAndDexDirBothSetThrowsException_viaFlags() {
    FlagParser flagParser =
        new FlagParser()
            .parse(
                new String[] {
                  "--output=" + outputPath,
                  "--manifest=" + manifestPath,
                  "--dex=" + dexPath,
                  "--dex-dir=" + dexDirPath
                });
    IllegalArgumentException exception =
        expectThrows(
            IllegalArgumentException.class,
            () -> BuildModuleCommand.fromFlags(flagParser).execute());

    assertThat(exception).hasMessageThat().contains("--dex");
  }

  @Test
  public void dexAndDexDirBothSetThrowsException_viaBuilder() {
    IllegalArgumentException exception =
        expectThrows(
            IllegalArgumentException.class,
            () ->
                BuildModuleCommand.builder()
                    .setOutputPath(outputPath)
                    .setManifestPath(manifestPath)
                    .setDexPath(dexPath)
                    .setDexDirPath(dexDirPath)
                    .build()
                    .execute());

    assertThat(exception).hasMessageThat().contains("--dex");
  }

  @Test
  public void outputExistsThrowsException_viaFlags() throws IOException {
    outputPath = tmp.newFile("bundle").toPath();
    FlagParser flagParser =
        new FlagParser()
            .parse(
                new String[] {
                  "--output=" + outputPath, "--manifest=" + manifestPath, "--dex=" + dexPath
                });
    IllegalArgumentException exception =
        expectThrows(
            IllegalArgumentException.class,
            () -> BuildModuleCommand.fromFlags(flagParser).execute());

    assertThat(exception).hasMessageThat().contains("already exists");
  }

  @Test
  public void outputExistsThrowsException_viaBuilder() throws IOException {
    outputPath = tmp.newFile("bundle").toPath();
    IllegalArgumentException exception =
        expectThrows(
            IllegalArgumentException.class,
            () ->
                BuildModuleCommand.builder()
                    .setOutputPath(outputPath)
                    .setManifestPath(manifestPath)
                    .setDexPath(dexPath)
                    .build()
                    .execute());

    assertThat(exception).hasMessageThat().contains("already exists");
  }

  @Test
  public void manifestDoesNotExistThrowsException_viaFlags() throws IOException {
    FlagParser flagParser =
        new FlagParser()
            .parse(
                new String[] {
                  "--output=" + outputPath, "--manifest=" + pathThatDoesNotExist, "--dex=" + dexPath
                });
    IllegalArgumentException exception =
        expectThrows(
            IllegalArgumentException.class,
            () -> BuildModuleCommand.fromFlags(flagParser).execute());

    assertThat(exception).hasMessageThat().containsMatch("File '.*' was not found");
  }

  @Test
  public void manifestDoesNotExistThrowsException_viaBuilder() throws IOException {
    IllegalArgumentException exception =
        expectThrows(
            IllegalArgumentException.class,
            () ->
                BuildModuleCommand.builder()
                    .setOutputPath(outputPath)
                    .setManifestPath(pathThatDoesNotExist)
                    .setDexPath(dexPath)
                    .build()
                    .execute());

    assertThat(exception).hasMessageThat().containsMatch("File '.*' was not found");
  }

  @Test
  public void manifestDirDoesNotExistThrowsException_viaFlags() throws IOException {
    FlagParser flagParser =
        new FlagParser()
            .parse(
                new String[] {
                  "--output=" + outputPath,
                  "--manifest-dir=" + pathThatDoesNotExist,
                  "--dex=" + dexPath
                });
    IllegalArgumentException exception =
        expectThrows(
            IllegalArgumentException.class,
            () -> BuildModuleCommand.fromFlags(flagParser).execute());

    assertThat(exception).hasMessageThat().containsMatch("Directory '.*' was not found");
  }

  @Test
  public void manifestDirDoesNotExistThrowsException_viaBuilder() throws IOException {
    IllegalArgumentException exception =
        expectThrows(
            IllegalArgumentException.class,
            () ->
                BuildModuleCommand.builder()
                    .setOutputPath(outputPath)
                    .setManifestDirPath(pathThatDoesNotExist)
                    .setDexPath(dexPath)
                    .build()
                    .execute());

    assertThat(exception).hasMessageThat().containsMatch("Directory '.*' was not found");
  }

  @Test
  public void dexDoesNotExistThrowsException_viaFlags() throws IOException {
    FlagParser flagParser =
        new FlagParser()
            .parse(
                new String[] {
                  "--output=" + outputPath,
                  "--manifest=" + manifestPath,
                  "--dex=" + pathThatDoesNotExist
                });
    IllegalArgumentException exception =
        expectThrows(
            IllegalArgumentException.class,
            () -> BuildModuleCommand.fromFlags(flagParser).execute());

    assertThat(exception).hasMessageThat().containsMatch("File '.*' was not found");
  }

  @Test
  public void dexDoesNotExistThrowsException_viaBuilder() throws IOException {
    IllegalArgumentException exception =
        expectThrows(
            IllegalArgumentException.class,
            () ->
                BuildModuleCommand.builder()
                    .setOutputPath(outputPath)
                    .setManifestPath(manifestPath)
                    .setDexPath(pathThatDoesNotExist)
                    .build()
                    .execute());

    assertThat(exception).hasMessageThat().containsMatch("File '.*' was not found");
  }

  @Test
  public void dexDirDoesNotExistThrowsException_viaFlags() throws IOException {
    dexDirPath = Paths.get(tmp.getRoot().getPath(), "dir-that-does-not-exist");
    FlagParser flagParser =
        new FlagParser()
            .parse(
                new String[] {
                  "--output=" + outputPath,
                  "--manifest=" + manifestPath,
                  "--dex-dir=" + pathThatDoesNotExist
                });
    IllegalArgumentException exception =
        expectThrows(
            IllegalArgumentException.class,
            () -> BuildModuleCommand.fromFlags(flagParser).execute());

    assertThat(exception).hasMessageThat().containsMatch("Directory '.*' was not found");
  }

  @Test
  public void dexDirDoesNotExistThrowsException_viaBuilder() throws IOException {
    IllegalArgumentException exception =
        expectThrows(
            IllegalArgumentException.class,
            () ->
                BuildModuleCommand.builder()
                    .setOutputPath(outputPath)
                    .setManifestPath(manifestPath)
                    .setDexDirPath(pathThatDoesNotExist)
                    .build()
                    .execute());

    assertThat(exception).hasMessageThat().containsMatch("Directory '.*' was not found");
  }

  @Test
  public void resourcesDirDoesNotExistThrowsException_viaFlags() throws IOException {
    FlagParser flagParser =
        new FlagParser()
            .parse(
                new String[] {
                  "--output=" + outputPath,
                  "--manifest=" + manifestPath,
                  "--resources-dir=" + pathThatDoesNotExist
                });
    IllegalArgumentException exception =
        expectThrows(
            IllegalArgumentException.class,
            () -> BuildModuleCommand.fromFlags(flagParser).execute());

    assertThat(exception).hasMessageThat().containsMatch("Directory '.*' was not found");
  }

  @Test
  public void resourcesDirDoesNotExistThrowsException_viaBuilder() throws IOException {
    IllegalArgumentException exception =
        expectThrows(
            IllegalArgumentException.class,
            () ->
                BuildModuleCommand.builder()
                    .setOutputPath(outputPath)
                    .setManifestPath(manifestPath)
                    .setResourcesDirPath(pathThatDoesNotExist)
                    .build()
                    .execute());

    assertThat(exception).hasMessageThat().containsMatch("Directory '.*' was not found");
  }

  @Test
  public void assetsDirDoesNotExistThrowsException_viaFlags() throws IOException {
    FlagParser flagParser =
        new FlagParser()
            .parse(
                new String[] {
                  "--output=" + outputPath,
                  "--manifest=" + manifestPath,
                  "--assets-dir=" + pathThatDoesNotExist
                });
    IllegalArgumentException exception =
        expectThrows(
            IllegalArgumentException.class,
            () -> BuildModuleCommand.fromFlags(flagParser).execute());

    assertThat(exception).hasMessageThat().containsMatch("Directory '.*' was not found");
  }

  @Test
  public void assetsDirDoesNotExistThrowsException_viaBuilder() throws IOException {
    IllegalArgumentException exception =
        expectThrows(
            IllegalArgumentException.class,
            () ->
                BuildModuleCommand.builder()
                    .setOutputPath(outputPath)
                    .setManifestPath(manifestPath)
                    .setAssetsDirPath(pathThatDoesNotExist)
                    .build()
                    .execute());

    assertThat(exception).hasMessageThat().containsMatch("Directory '.*' was not found");
  }

  @Test
  public void nativeDirDoesNotExistThrowsException_viaFlags() throws IOException {
    FlagParser flagParser =
        new FlagParser()
            .parse(
                new String[] {
                  "--output=" + outputPath,
                  "--manifest=" + manifestPath,
                  "--native-dir=" + pathThatDoesNotExist
                });
    IllegalArgumentException exception =
        expectThrows(
            IllegalArgumentException.class,
            () -> BuildModuleCommand.fromFlags(flagParser).execute());

    assertThat(exception).hasMessageThat().containsMatch("Directory '.*' was not found");
  }

  @Test
  public void nativeDirDoesNotExistThrowsException_viaBuilder() throws IOException {
    IllegalArgumentException exception =
        expectThrows(
            IllegalArgumentException.class,
            () ->
                BuildModuleCommand.builder()
                    .setOutputPath(outputPath)
                    .setManifestPath(manifestPath)
                    .setNativeDirPath(pathThatDoesNotExist)
                    .build()
                    .execute());

    assertThat(exception).hasMessageThat().containsMatch("Directory '.*' was not found");
  }
}
