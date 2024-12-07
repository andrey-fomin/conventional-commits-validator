package io.github.fomin.conventionalcommits;

import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.Property;

public interface ConventionalCommitsExtension {
  Property<String> getStartRef();

  Property<String> getEndRef();

  DirectoryProperty getRepoDir();

  RegularFileProperty getConfigurationFile();
}
