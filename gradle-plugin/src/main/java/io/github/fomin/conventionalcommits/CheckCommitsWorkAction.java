package io.github.fomin.conventionalcommits;

import io.github.fomin.conventionalcommits.rules.GitRepositoryValidator;
import io.github.fomin.conventionalcommits.rules.RuleSet;
import io.github.fomin.conventionalcommits.rules.RulesParser;
import io.github.fomin.conventionalcommits.rules.ValidationResult;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import org.gradle.api.GradleException;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.workers.WorkAction;

public abstract class CheckCommitsWorkAction implements WorkAction<CheckCommitsWorkParameters> {

  private static final Logger LOGGER = Logging.getLogger(CheckCommitsWorkAction.class);

  @Override
  public void execute() {
    CheckCommitsWorkParameters parameters = getParameters();
    RegularFileProperty configurationFileProperty = parameters.getConfigurationFile();
    File projectDir = parameters.getProjectDir().get().getAsFile();
    String configuration;
    if (configurationFileProperty.isPresent()) {
      File configurationFile = configurationFileProperty.get().getAsFile();
      configuration = readFile(configurationFile);
    } else {
      File configurationFile = new File(projectDir, "conventional-commits.yaml");
      if (configurationFile.exists()) {
        configuration = readFile(configurationFile);
      } else {
        configuration = "";
      }
    }

    RuleSet ruleSet = RulesParser.parse(configuration);
    File repoDir = parameters.getRepoDir().getAsFile().getOrElse(projectDir);
    String startRef = parameters.getStartRef().getOrElse("HEAD");
    String endRef = parameters.getEndRef().getOrElse("HEAD^");
    List<ValidationResult> validationResults =
        GitRepositoryValidator.validate(repoDir, startRef, endRef, ruleSet);
    if (!validationResults.isEmpty()) {
      for (ValidationResult validationResult : validationResults) {
        LOGGER.error(
            "Commit {} has invalid message:\n---\n{}\n---\nErrors:\n{}\n---\n",
            validationResult.getCommitSha1(),
            validationResult.getMessage(),
            String.join("\n", validationResult.getErrors()));
      }
      throw new GradleException("Invalid commit messages found");
    }
  }

  private static String readFile(File file) {
    try {
      return new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
