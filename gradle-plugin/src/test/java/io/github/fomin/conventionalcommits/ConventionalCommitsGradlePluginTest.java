package io.github.fomin.conventionalcommits;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.gradle.internal.impldep.org.eclipse.jgit.api.Git;
import org.gradle.internal.impldep.org.eclipse.jgit.api.errors.GitAPIException;
import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.BuildTask;
import org.gradle.testkit.runner.GradleRunner;
import org.gradle.testkit.runner.TaskOutcome;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class ConventionalCommitsGradlePluginTest {

  @Test
  void ok_default_configuration(@TempDir Path projectDir) throws IOException, GitAPIException {
    try (Git git = Git.init().setDirectory(projectDir.toFile()).call()) {
      git.commit().setMessage("type(scope): description1\n").call();
      git.commit().setMessage("type(scope): description2\n").call();
    }

    Path gradleBuildFile = projectDir.resolve("build.gradle.kts");
    String gradleBuildFileContent =
        ""
            + "plugins {\n"
            + "    id(\"io.github.fomin.conventional-commits\")\n"
            + "}\n"
            + "repositories {\n"
            + "    mavenLocal()\n"
            + "    mavenCentral()\n"
            + "}\n";
    Files.write(gradleBuildFile, gradleBuildFileContent.getBytes(StandardCharsets.UTF_8));

    BuildResult buildResult =
        GradleRunner.create()
            .withProjectDir(projectDir.toFile())
            .withArguments("checkCommits", "--stacktrace")
            .withPluginClasspath()
            .build();
    BuildTask task = buildResult.task(":checkCommits");
    assertNotNull(task);
    assertEquals(TaskOutcome.SUCCESS, task.getOutcome());
  }

  @Test
  void ok_custom_configuration(@TempDir Path projectDir) throws IOException, GitAPIException {
    try (Git git = Git.init().setDirectory(projectDir.toFile()).call()) {
      git.commit().setMessage("type(scope): description1\n").call();
      git.commit().setMessage("type(scope): description2\n").call();
    }

    Path conventionalCommitRulesFile = projectDir.resolve("conventional-commits-rules.yaml");
    String conventionalCommitRulesFileContent =
        "" //
            + "header:\n" //
            + "  max-length: 50\n";
    Files.write(
        conventionalCommitRulesFile,
        conventionalCommitRulesFileContent.getBytes(StandardCharsets.UTF_8));

    Path gradleBuildFile = projectDir.resolve("build.gradle.kts");
    String gradleBuildFileContent =
        ""
            + "plugins {\n"
            + "    id(\"io.github.fomin.conventional-commits\")\n"
            + "}\n"
            + "repositories {\n"
            + "    mavenLocal()\n"
            + "    mavenCentral()\n"
            + "}\n"
            + "conventionalCommits {\n"
            + "    startRef.set(\"HEAD\")\n"
            + "    endRef.set(\"HEAD^\")\n"
            + "    repoDir.set(file(\".\"))\n"
            + "    configurationFile.set(file(\"conventional-commits-rules.yaml\"))\n"
            + "}\n";
    Files.write(gradleBuildFile, gradleBuildFileContent.getBytes(StandardCharsets.UTF_8));

    BuildResult buildResult =
        GradleRunner.create()
            .withProjectDir(projectDir.toFile())
            .withArguments("checkCommits", "--stacktrace")
            .withPluginClasspath()
            .build();
    BuildTask task = buildResult.task(":checkCommits");
    assertNotNull(task);
    assertEquals(TaskOutcome.SUCCESS, task.getOutcome());
  }

  @Test
  void ko_custom_configuration(@TempDir Path projectDir) throws IOException, GitAPIException {
    try (Git git = Git.init().setDirectory(projectDir.toFile()).call()) {
      git.commit().setMessage("type(scope): description1\n").call();
      git.commit().setMessage("type(scope): description2\n").call();
      git.commit().setMessage("type(scope): description3\n").call();
    }

    Path conventionalCommitRulesFile = projectDir.resolve("conventional-commits-rules.yaml");
    String conventionalCommitRulesFileContent =
        "" //
            + "header:\n" //
            + "  max-length: 10\n";
    Files.write(
        conventionalCommitRulesFile,
        conventionalCommitRulesFileContent.getBytes(StandardCharsets.UTF_8));

    Path gradleBuildFile = projectDir.resolve("build.gradle.kts");
    String gradleBuildFileContent =
        ""
            + "plugins {\n"
            + "    id(\"io.github.fomin.conventional-commits\")\n"
            + "}\n"
            + "repositories {\n"
            + "    mavenLocal()\n"
            + "    mavenCentral()\n"
            + "}\n"
            + "conventionalCommits {\n"
            + "    startRef.set(\"HEAD\")\n"
            + "    endRef.set(\"HEAD^^\")\n"
            + "    repoDir.set(file(\".\"))\n"
            + "    configurationFile.set(file(\"conventional-commits-rules.yaml\"))\n"
            + "}\n";
    Files.write(gradleBuildFile, gradleBuildFileContent.getBytes(StandardCharsets.UTF_8));

    BuildResult buildResult =
        GradleRunner.create()
            .withProjectDir(projectDir.toFile())
            .withArguments("checkCommits", "--stacktrace")
            .withPluginClasspath()
            .buildAndFail();
    BuildTask task = buildResult.task(":checkCommits");
    assertNotNull(task);
    assertEquals(TaskOutcome.FAILED, task.getOutcome());
    assertTrue(
        buildResult
            .getOutput()
            .contains(
                "has invalid message:\n"
                    + "---\n"
                    + "type(scope): description3\n"
                    + "\n"
                    + "---\n"
                    + "Errors:\n"
                    + "header length 26 is greater than 10\n"
                    + "---\n"
                    + "\n"));
    assertTrue(
        buildResult
            .getOutput()
            .contains(
                "has invalid message:\n"
                    + "---\n"
                    + "type(scope): description2\n"
                    + "\n"
                    + "---\n"
                    + "Errors:\n"
                    + "header length 26 is greater than 10\n"
                    + "---\n"
                    + "\n"));
  }
}
