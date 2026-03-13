# Conventional Commits Validator

Validate commit messages using the Conventional Commits format with YAML configuration.

## Installation

Download prebuilt binaries from [GitHub Releases](https://github.com/andrey-fomin/conventional-commits-validator/releases) for Linux, macOS, and Windows.

## Usage

```bash
ccval [options]
```

### Input Modes

| Mode | Example |
|------|---------|
| stdin (default) | `printf 'feat: new feature\n' \| ccval` |
| file | `ccval --file .git/COMMIT_EDITMSG` |
| git | `ccval -- HEAD` (arguments after `--` passed to `git log`) |

### Options

| Option | Description |
|--------|-------------|
| `-c, --config <path>` | Custom config file path |
| `-f, --file <path>` | Read commit message from file |
| `-h, --help` | Show help |
| `-- <args>` | Pass arguments to `git log` (e.g., `HEAD`, `main..HEAD --no-merges`) |

### Exit Codes

| Code | Meaning |
|------|---------|
| 0 | Success |
| 1 | Validation failed |
| 2 | Parse error |
| 3 | Config error |
| 4 | CLI usage error |
| 5 | I/O error |
| 6 | Git error |

## Examples

Validate stdin:

```bash
printf 'feat(api): add login endpoint\n' | ccval
```

Validate the last commit:

```bash
ccval -- -1
```

Validate a commit message file (Git hook):

```bash
ccval --file .git/COMMIT_EDITMSG
```

Validate all commits on a branch (excluding merges):

```bash
ccval -- origin/main..HEAD --no-merges
```

Use a custom config:

```bash
ccval -c conventional-commits.yaml --file .git/COMMIT_EDITMSG
```

Exclude bot commits in CI:

```bash
ccval -- --no-merges --invert-grep --grep 'dependabot' origin/main..HEAD
```

## Configuration

Create `conventional-commits.yaml` in your working directory:

```yaml
preset: strict

scope:
  required: true
  values:
    - api
    - core
    - ui

type:
  values:
    - feat
    - fix
    - docs
    - refactor
```

### Presets

- `default` - formatting rules for description spacing
- `strict` - `default` plus header length limits and common type restrictions

### Available Fields

```yaml
preset: default

message: <RULES>
header: <RULES>
type: <RULES>
scope: <RULES>
description: <RULES>
body: <RULES>
footer-token: <RULES>
footer-value: <RULES>
footers:
  Closes: <RULES>
```

### Validation Rules

| Rule | Description |
|------|-------------|
| `max-length` | Maximum total length (including newlines) |
| `max-line-length` | Maximum line length (excluding newlines) |
| `required` | Field must be present |
| `forbidden` | Field must not be present |
| `regexes` | List of regexes (all must match) |
| `values` | List of allowed values |

## Building from Source

```bash
cargo build --release
```

The binary will be at `./target/release/ccval`.