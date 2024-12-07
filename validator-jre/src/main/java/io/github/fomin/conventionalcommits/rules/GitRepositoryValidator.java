package io.github.fomin.conventionalcommits.rules;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;

public final class GitRepositoryValidator {
  private GitRepositoryValidator() {}

  public static List<ValidationResult> validate(
      File repoDir, String startRef, String endRef, RuleSet ruleSet) {
    List<ValidationResult> validationResults = new ArrayList<>();
    try (Git git = Git.open(repoDir)) {
      Repository repository = git.getRepository();

      ObjectId startObjectid = repository.resolve(startRef);
      if (startObjectid == null) {
        throw new RuntimeException("Start ref not found: " + startRef);
      }
      ObjectId endObjectId = repository.resolve(endRef);

      try (RevWalk revWalk = new RevWalk(repository)) {
        revWalk.markStart(revWalk.parseCommit(startObjectid));
        if (endObjectId != null) {
          revWalk.markUninteresting(revWalk.parseCommit(endObjectId));
        }

        for (RevCommit commit : revWalk) {
          String fullMessage = commit.getFullMessage();
          commit.getId().getName();
          List<String> errors = CommitMessageValidator.validate(ruleSet, fullMessage);
          if (!errors.isEmpty()) {
            validationResults.add(
                new ValidationResult(commit.getId().getName(), fullMessage, errors));
          }
        }
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return validationResults;
  }
}
