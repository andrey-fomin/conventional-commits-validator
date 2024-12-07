# Gradle plugin for validating conventional commits

## Introduction

This plugin validates your project's commit messages according to
the [Conventional Commits specification](https://www.conventionalcommits.org/en/v1.0.0/).

## Setup

Add the following to your `build.gradle.kts` file:

```kotlin
plugins {
    id("io.github.fomin.conventional-commits") version "plugin-version"
}
```

Replace `plugin-version` with the latest plugin version.

## Tasks

The plugin adds the `checkCommits` task to your project. This task validates the commits in the project according to the
configuration. This task is added as a dependency to the `check` task.

## Configuration

```kotlin
conventionalCommits {
    startRef = "HEAD"
    endRef = "HEAD^"
    repoDir = projectDir
    configurationFile = file("conventional-commits.yaml")
}
```

- `startRef` - the commit to start validation from. Default is `HEAD`.
- `endRef` - the commit to end validation at. Default is `HEAD^`.
- `repoDir` - the directory of the repository. Default is `projectDir`.
- `configurationFile` - the path to the configuration file. Default is `conventional-commits.yaml`.
  This file can be used to define extra validation rules. If the file is not found, no extra rules are checked.

## Extra validation rules

The configuration file should be in the following format:

```yaml
base-rule-set: <ID>
message: <VALIDATION_RULES>
header: <VALIDATION_RULES>
type: <VALIDATION_RULES>
scope: <VALIDATION_RULES>
description: <VALIDATION_RULES>
body: <VALIDATION_RULES>
footer-token: <VALIDATION_RULES>
footer-value: <VALIDATION_RULES>
footers:
  footer1: <VALIDATION_RULES>
  footer2: <VALIDATION_RULES>
```

- `base-rule-set` - the ID of the base rule set. This rule set is merged with the current one. Default rule sets can be
  found in the `validator-jre/src/main/resources/io/github/fomin/conventionalcommits/rules` directory.
- `message` - validation rules for the whole commit message.
- `header` - validation rules for the header.
- `type` - validation rules for the type.
- `scope` - validation rules for the scope.
- `description` - validation rules for the description.
- `body` - validation rules for the body.
- `footer-token` - validation rules for the footer tokens.
- `footer-value` - validation rules for the footer values.
- `footers` - validation rules for the specific footers. The key is the footer token, and the value is the validation
  rules.

`message`, `header`, `body`, and `footer-value` always end with a newline.

`type`, `scope`, `description`, and `footer-token` never end with a newline.

If a field is not present in the configuration file, no extra validation rules are applied.

`<VALIDATION_RULES>` can contain the following:

```yaml
max-length: <INTEGER>
max-line-length: <INTEGER>
required: <BOOLEAN>
forbidden: <BOOLEAN>
regex: <STRING>
values:
  - <STRING>
  - <STRING>
```

- `max-length` - the maximum length of the field, including newline characters.
- `max-line-length` - the maximum length of a line, excluding newline characters.
- `required` - whether the field is required.
- `forbidden` - whether the field is forbidden.
- `regex` - the regex pattern the field should match.
- `values` - the list of allowed values.

If a rule is nod defined, it is not applied.

## Example

```yaml
base-rule-set: default

scope:
  required: true
  values:
    - cache
    - db
    - jobs
    - utils

footer-token:
  values:
    - Closes
    - Co-authored-by

footers:
  Close:
    required: true
    regex: ^[A-Z][A-Z0-9]*-[0-9]+$
  Co-authored-by:
    required: true
    regex: ^[^<>]+ <[^<>]+@[^<>]+>$
```
