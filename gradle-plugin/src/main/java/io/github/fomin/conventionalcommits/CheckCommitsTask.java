package io.github.fomin.conventionalcommits;

import javax.inject.Inject;
import org.gradle.api.DefaultTask;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputDirectory;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.TaskAction;
import org.gradle.workers.WorkQueue;
import org.gradle.workers.WorkerExecutor;

public abstract class CheckCommitsTask extends DefaultTask {

  @Input
  @Optional
  public abstract Property<String> getStartRef();

  @Input
  @Optional
  public abstract Property<String> getEndRef();

  @InputDirectory
  @Optional
  public abstract DirectoryProperty getRepoDir();

  @InputFile
  @Optional
  public abstract RegularFileProperty getConfigurationFile();

  @InputFiles
  public abstract ConfigurableFileCollection getClasspath();

  @SuppressWarnings("JavaxInjectOnAbstractMethod")
  @Inject
  public abstract WorkerExecutor getWorkerExecutor();

  @TaskAction
  public void taskAction() {
    WorkQueue workQueue =
        getWorkerExecutor()
            .classLoaderIsolation(
                classLoaderWorkerSpec -> classLoaderWorkerSpec.getClasspath().from(getClasspath()));
    workQueue.submit(
        CheckCommitsWorkAction.class,
        parameters -> {
          parameters.getStartRef().set(getStartRef());
          parameters.getEndRef().set(getEndRef());
          parameters.getRepoDir().set(getRepoDir());
          parameters.getConfigurationFile().set(getConfigurationFile());
          parameters.getProjectDir().set(getProject().getProjectDir());
        });
  }
}
