package io.github.fomin.conventionalcommits.rules;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class GitRepositoryValidatorTest {
  @Test
  void ko_no_commits(@TempDir File projectDir) throws GitAPIException {
    try (Git ignored = Git.init().setDirectory(projectDir).call()) {}
    RuleSet ruleSet = RulesParser.parse("");
    RuntimeException exception =
        assertThrows(
            RuntimeException.class,
            () -> GitRepositoryValidator.validate(projectDir, "HEAD", "HEAD^", ruleSet));
    assertEquals("Start ref not found: HEAD", exception.getMessage());
  }

  @Test
  void ok_single_commit(@TempDir Path projectDir) throws GitAPIException {
    try (Git git = Git.init().setDirectory(projectDir.toFile()).call()) {
      git.commit().setMessage("type(scope): description\n").call();
    }
    RuleSet ruleSet = RulesParser.parse("");
    List<ValidationResult> validationResults =
        GitRepositoryValidator.validate(projectDir.toFile(), "HEAD", "HEAD^", ruleSet);
    assertTrue(validationResults.isEmpty());
  }

  @Test
  void ok_two_commits(@TempDir Path projectDir) throws GitAPIException {
    try (Git git = Git.init().setDirectory(projectDir.toFile()).call()) {
      git.commit().setMessage("type(scope): description1\n").call();
      git.commit().setMessage("type(scope): description2\n").call();
    }
    RuleSet ruleSet = RulesParser.parse("");
    List<ValidationResult> validationResults =
        GitRepositoryValidator.validate(projectDir.toFile(), "HEAD", "HEAD^", ruleSet);
    assertTrue(validationResults.isEmpty());
  }
}
