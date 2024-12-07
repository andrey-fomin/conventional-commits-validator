package io.github.fomin.conventionalcommits;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.artifacts.Dependency;

public class ConventionalCommitsGradlePlugin implements Plugin<Project> {

  @Override
  public void apply(Project project) {
    project.getPlugins().apply("base");
    ConventionalCommitsExtension conventionalCommitsExtension =
        project.getExtensions().create("conventionalCommits", ConventionalCommitsExtension.class);
    String dependencyNotation =
        "io.github.fomin.conventional-commits:validator-jre:" + Version.VERSION;
    Dependency validatorDependency = project.getDependencies().create(dependencyNotation);
    ConfigurationContainer configurations = project.getConfigurations();
    Configuration configuration =
        configurations.create(
            "conventionalCommits",
            c -> {
              c.setVisible(false);
              c.setCanBeConsumed(false);
              c.setCanBeResolved(true);
              c.setDescription("Runtime dependencies for the conventional commits plugin");
              c.defaultDependencies(
                  d -> {
                    d.add(validatorDependency);
                  });
            });
    project.getTasks().register("checkCommits", CheckCommitsTask.class);
    project
        .getTasks()
        .withType(CheckCommitsTask.class)
        .configureEach(
            task -> {
              task.getClasspath().from(configuration);
              task.setGroup("verification");
              task.setDescription("Check commits for compliance with conventional commits");
              task.getStartRef().convention(conventionalCommitsExtension.getStartRef());
              task.getEndRef().convention(conventionalCommitsExtension.getEndRef());
              task.getRepoDir().convention(conventionalCommitsExtension.getRepoDir());
              task.getConfigurationFile()
                  .convention(conventionalCommitsExtension.getConfigurationFile());
            });
    project.getTasks().named("check", task -> task.dependsOn("checkCommits"));
  }
}
