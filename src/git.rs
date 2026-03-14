use std::process::Command;

use thiserror::Error;

const RECORD_SEPARATOR: char = '\u{001e}';
const FIELD_SEPARATOR: char = '\u{001f}';

#[derive(Debug, PartialEq, Clone)]
pub struct GitCommit {
    pub id: String,
    pub message: String,
}

#[derive(Debug, Error)]
pub enum GitError {
    #[error("failed to run git: {0}")]
    Io(#[from] std::io::Error),
    #[error("git command failed with code {code:?}: {stderr}")]
    GitFailed { code: Option<i32>, stderr: String },
    #[error("invalid git output: {0}")]
    InvalidOutput(String),
}

pub trait GitLoader {
    fn load_commits(&self, args: &[String]) -> Result<Vec<GitCommit>, GitError>;
}

pub struct GitSubprocess;

impl GitLoader for GitSubprocess {
    fn load_commits(&self, args: &[String]) -> Result<Vec<GitCommit>, GitError> {
        load_commits(args)
    }
}

fn load_commits(git_args: &[String]) -> Result<Vec<GitCommit>, GitError> {
    let format = "%x1e%H%x1f%B";
    let output = Command::new("git")
        .arg("log")
        .args(git_args)
        .arg(format!("--format={format}"))
        .output()?;

    if !output.status.success() {
        return Err(GitError::GitFailed {
            code: output.status.code(),
            stderr: String::from_utf8_lossy(&output.stderr).trim().to_string(),
        });
    }

    parse_git_output(&String::from_utf8_lossy(&output.stdout))
}

fn parse_git_output(output: &str) -> Result<Vec<GitCommit>, GitError> {
    let mut commits = Vec::new();

    for record in output
        .split(RECORD_SEPARATOR)
        .filter(|record| !record.is_empty())
    {
        let Some((id, message)) = record.split_once(FIELD_SEPARATOR) else {
            return Err(GitError::InvalidOutput(
                "missing commit field separator".to_string(),
            ));
        };

        let message = message
            .strip_suffix('\n')
            .expect("Git %B output should end with newline");

        commits.push(GitCommit {
            id: id.to_string(),
            message: message.to_string(),
        });
    }

    Ok(commits)
}

#[cfg(test)]
mod tests {
    use super::{parse_git_output, GitCommit, GitError};

    #[test]
    fn parse_git_output_empty() {
        assert_eq!(parse_git_output("").unwrap(), Vec::<GitCommit>::new());
    }

    #[test]
    fn parse_git_output_single_commit() {
        let commits = parse_git_output("\u{001e}abc123\u{001f}feat: subject\n\n").unwrap();
        assert_eq!(
            commits,
            vec![GitCommit {
                id: "abc123".to_string(),
                message: "feat: subject\n".to_string(),
            }]
        );
    }

    #[test]
    fn parse_git_output_multiple_commits() {
        let commits = parse_git_output(
            "\u{001e}abc123\u{001f}feat: subject\n\n\u{001e}def456\u{001f}fix: bug\n\n",
        )
        .unwrap();
        assert_eq!(commits.len(), 2);
        assert_eq!(commits[0].id, "abc123");
        assert_eq!(commits[1].id, "def456");
    }

    #[test]
    fn parse_git_output_invalid_record() {
        let error = parse_git_output("\u{001e}abc123").unwrap_err();
        assert!(matches!(error, GitError::InvalidOutput(_)));
    }
}
