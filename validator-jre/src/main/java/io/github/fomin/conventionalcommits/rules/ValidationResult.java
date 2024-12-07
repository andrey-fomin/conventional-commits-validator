package io.github.fomin.conventionalcommits.rules;

import java.util.List;

public final class ValidationResult {
  private final String commitSha1;
  private final String message;
  private final List<String> errors;

  public ValidationResult(String commitSha1, String message, List<String> errors) {
    this.commitSha1 = commitSha1;
    this.message = message;
    this.errors = errors;
  }

  public String getCommitSha1() {
    return commitSha1;
  }

  public String getMessage() {
    return message;
  }

  public List<String> getErrors() {
    return errors;
  }
}
