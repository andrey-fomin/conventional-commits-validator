pub const HELP_TEXT: &str = "Usage: ccval [-c <path>] [-r <path>] [-- <git-log-args>...]\n       ccval [-c <path>] --stdin\n       ccval [-c <path>] -f <path>\n       ccval -h\n\nValidates commit messages from stdin, a file, or Git.\n\nModes:\n  (default)            Validate commit(s) from git log\n                       Use -- <git-log-args>... to pass arguments to git log\n                       Default: -1 (last commit)\n\n  --stdin              Read commit message from stdin\n  -f, --file <path>    Read commit message from a file\n  -h, --help           Show this help message\n\nOptions:\n  -c, --config <path>  Use a custom config file path\n  -r, --repository <path>\n                       Path to Git repository working tree\n                       Cannot be used with --stdin or --file\n\nExamples:\n  ccval                              # validate last commit\n  ccval -- origin/main..HEAD         # validate commits on branch\n  ccval -r /path/to/repo             # validate last commit in specific repo\n  printf 'feat: msg\\n' | ccval --stdin\n  ccval --file .git/COMMIT_EDITMSG\n  ccval -c config.yaml --stdin\n";

const HELP_HINT: &str = "Run with --help or -h for usage information.";

#[derive(Debug, PartialEq)]
pub enum InputMode {
    Stdin,
    File { path: String },
    Git { git_args: Vec<String> },
}

#[derive(Debug, PartialEq)]
pub struct CliOptions {
    pub config_path: Option<String>,
    pub repository_path: Option<String>,
    pub input_mode: InputMode,
}

#[derive(Debug, PartialEq)]
pub enum CliAction {
    Run(CliOptions),
    ShowHelp,
}

pub fn parse_args<I>(args: I) -> Result<CliAction, String>
where
    I: Iterator<Item = String>,
{
    let mut before_separator = Vec::new();
    let mut after_separator = Vec::new();
    let mut seen_separator = false;

    for arg in args {
        if seen_separator {
            after_separator.push(arg);
        } else if arg == "--" {
            seen_separator = true;
        } else {
            before_separator.push(arg);
        }
    }

    let mut config_path = None;
    let mut repository_path = None;
    let mut file_path = None;
    let mut stdin_mode = false;
    let mut show_help = false;
    let mut args = before_separator.into_iter();

    while let Some(arg) = args.next() {
        match arg.as_str() {
            "--help" | "-h" => show_help = true,
            "--stdin" => {
                if stdin_mode {
                    return Err(format!("--stdin may be specified only once. {}", HELP_HINT));
                }
                stdin_mode = true;
            }
            "--config" | "-c" => {
                let Some(path) = args.next() else {
                    return Err(format!("missing value for {}. {}", arg, HELP_HINT));
                };
                if config_path.is_some() {
                    return Err(format!(
                        "--config/-c may be specified only once. {}",
                        HELP_HINT
                    ));
                }
                config_path = Some(path);
            }
            "--repository" | "-r" => {
                let Some(path) = args.next() else {
                    return Err(format!("missing value for {}. {}", arg, HELP_HINT));
                };
                if repository_path.is_some() {
                    return Err(format!(
                        "--repository/-r may be specified only once. {}",
                        HELP_HINT
                    ));
                }
                repository_path = Some(path);
            }
            "--file" | "-f" => {
                let Some(path) = args.next() else {
                    return Err(format!("missing value for {}. {}", arg, HELP_HINT));
                };
                if file_path.is_some() {
                    return Err(format!(
                        "--file/-f may be specified only once. {}",
                        HELP_HINT
                    ));
                }
                file_path = Some(path);
            }
            _ => return Err(format!("unknown argument '{}'. {}", arg, HELP_HINT)),
        }
    }

    if show_help {
        if config_path.is_some()
            || repository_path.is_some()
            || file_path.is_some()
            || stdin_mode
            || seen_separator
        {
            return Err(format!(
                "--help/-h must be used without other arguments. {}",
                HELP_HINT
            ));
        }
        return Ok(CliAction::ShowHelp);
    }

    if stdin_mode && file_path.is_some() {
        return Err(format!(
            "--stdin cannot be combined with --file/-f. {}",
            HELP_HINT
        ));
    }

    if stdin_mode && repository_path.is_some() {
        return Err(format!(
            "--repository/-r cannot be used with --stdin. {}",
            HELP_HINT
        ));
    }

    if file_path.is_some() && repository_path.is_some() {
        return Err(format!(
            "--repository/-r cannot be used with --file/-f. {}",
            HELP_HINT
        ));
    }

    if stdin_mode && seen_separator {
        return Err(format!(
            "--stdin cannot be combined with git arguments after --. {}",
            HELP_HINT
        ));
    }

    if file_path.is_some() && seen_separator {
        return Err(format!(
            "--file/-f cannot be combined with git arguments after --. {}",
            HELP_HINT
        ));
    }

    let input_mode = if seen_separator {
        if after_separator.is_empty() {
            return Err(format!(
                "expected at least one git argument after --. {}",
                HELP_HINT
            ));
        }
        InputMode::Git {
            git_args: after_separator,
        }
    } else if let Some(path) = file_path {
        InputMode::File { path }
    } else if stdin_mode {
        InputMode::Stdin
    } else {
        InputMode::Git {
            git_args: vec!["-1".to_string()],
        }
    };

    Ok(CliAction::Run(CliOptions {
        config_path,
        repository_path,
        input_mode,
    }))
}

#[cfg(test)]
mod tests {
    use super::{CliAction, CliOptions, InputMode, parse_args};

    fn parse_from(args: &[&str]) -> Result<CliAction, String> {
        parse_args(args.iter().map(|arg| (*arg).to_string()))
    }

    #[test]
    fn parse_config_path_long_flag() {
        let action = parse_from(&["--config", "custom.yaml"]).unwrap();
        assert_eq!(
            action,
            CliAction::Run(CliOptions {
                config_path: Some("custom.yaml".to_string()),
                repository_path: None,
                input_mode: InputMode::Git {
                    git_args: vec!["-1".to_string()],
                },
            })
        );
    }

    #[test]
    fn parse_config_path_short_flag() {
        let action = parse_from(&["-c", "custom.yaml"]).unwrap();
        assert_eq!(
            action,
            CliAction::Run(CliOptions {
                config_path: Some("custom.yaml".to_string()),
                repository_path: None,
                input_mode: InputMode::Git {
                    git_args: vec!["-1".to_string()],
                },
            })
        );
    }

    #[test]
    fn parse_config_path_missing_value() {
        assert_eq!(
            parse_from(&["--config"]).unwrap_err(),
            "missing value for --config. Run with --help or -h for usage information."
        );
    }

    #[test]
    fn parse_unknown_arg() {
        assert_eq!(
            parse_from(&["--unknown"]).unwrap_err(),
            "unknown argument '--unknown'. Run with --help or -h for usage information."
        );
    }

    #[test]
    fn parse_default_git_mode() {
        let action = parse_from(&[]).unwrap();
        assert_eq!(
            action,
            CliAction::Run(CliOptions {
                config_path: None,
                repository_path: None,
                input_mode: InputMode::Git {
                    git_args: vec!["-1".to_string()],
                },
            })
        );
    }

    #[test]
    fn parse_stdin_mode() {
        let action = parse_from(&["--stdin"]).unwrap();
        assert_eq!(
            action,
            CliAction::Run(CliOptions {
                config_path: None,
                repository_path: None,
                input_mode: InputMode::Stdin,
            })
        );
    }

    #[test]
    fn parse_stdin_with_config() {
        let action = parse_from(&["-c", "custom.yaml", "--stdin"]).unwrap();
        assert_eq!(
            action,
            CliAction::Run(CliOptions {
                config_path: Some("custom.yaml".to_string()),
                repository_path: None,
                input_mode: InputMode::Stdin,
            })
        );
    }

    #[test]
    fn parse_stdin_repeated_is_rejected() {
        assert_eq!(
            parse_from(&["--stdin", "--stdin"]).unwrap_err(),
            "--stdin may be specified only once. Run with --help or -h for usage information.",
        );
    }

    #[test]
    fn parse_stdin_with_file_is_rejected() {
        assert_eq!(
            parse_from(&["--stdin", "--file", "msg.txt"]).unwrap_err(),
            "--stdin cannot be combined with --file/-f. Run with --help or -h for usage information.",
        );
    }

    #[test]
    fn parse_stdin_with_git_args_is_rejected() {
        assert_eq!(
            parse_from(&["--stdin", "--", "HEAD"]).unwrap_err(),
            "--stdin cannot be combined with git arguments after --. Run with --help or -h for usage information.",
        );
    }

    #[test]
    fn parse_help_with_stdin_is_rejected() {
        assert_eq!(
            parse_from(&["--help", "--stdin"]).unwrap_err(),
            "--help/-h must be used without other arguments. Run with --help or -h for usage information.",
        );
    }

    #[test]
    fn parse_help_short_flag() {
        assert_eq!(parse_from(&["-h"]).unwrap(), CliAction::ShowHelp);
    }

    #[test]
    fn parse_help_with_config_is_rejected() {
        assert_eq!(
            parse_from(&["--help", "--config", "custom.yaml"]).unwrap_err(),
            "--help/-h must be used without other arguments. Run with --help or -h for usage information.",
        );
    }

    #[test]
    fn parse_file_mode_long_flag() {
        let action = parse_from(&["--file", "COMMIT_EDITMSG"]).unwrap();
        assert_eq!(
            action,
            CliAction::Run(CliOptions {
                config_path: None,
                repository_path: None,
                input_mode: InputMode::File {
                    path: "COMMIT_EDITMSG".to_string(),
                },
            })
        );
    }

    #[test]
    fn parse_file_mode_short_flag() {
        let action = parse_from(&["-f", "COMMIT_EDITMSG"]).unwrap();
        assert_eq!(
            action,
            CliAction::Run(CliOptions {
                config_path: None,
                repository_path: None,
                input_mode: InputMode::File {
                    path: "COMMIT_EDITMSG".to_string(),
                },
            })
        );
    }

    #[test]
    fn parse_file_missing_value() {
        assert_eq!(
            parse_from(&["--file"]).unwrap_err(),
            "missing value for --file. Run with --help or -h for usage information."
        );
    }

    #[test]
    fn parse_repeated_config_is_rejected() {
        assert_eq!(
            parse_from(&["--config", "a.yaml", "-c", "b.yaml"]).unwrap_err(),
            "--config/-c may be specified only once. Run with --help or -h for usage information.",
        );
    }

    #[test]
    fn parse_repeated_file_is_rejected() {
        assert_eq!(
            parse_from(&["--file", "a.txt", "-f", "b.txt"]).unwrap_err(),
            "--file/-f may be specified only once. Run with --help or -h for usage information.",
        );
    }

    #[test]
    fn parse_git_mode() {
        let action = parse_from(&["--", "HEAD"]).unwrap();
        assert_eq!(
            action,
            CliAction::Run(CliOptions {
                config_path: None,
                repository_path: None,
                input_mode: InputMode::Git {
                    git_args: vec!["HEAD".to_string()],
                },
            })
        );
    }

    #[test]
    fn parse_git_mode_with_multiple_args() {
        let action =
            parse_from(&["-c", "custom.yaml", "--", "master..HEAD", "--no-merges"]).unwrap();
        assert_eq!(
            action,
            CliAction::Run(CliOptions {
                config_path: Some("custom.yaml".to_string()),
                repository_path: None,
                input_mode: InputMode::Git {
                    git_args: vec!["master..HEAD".to_string(), "--no-merges".to_string()],
                },
            })
        );
    }

    #[test]
    fn parse_separator_without_git_args_is_rejected() {
        assert_eq!(
            parse_from(&["--"]).unwrap_err(),
            "expected at least one git argument after --. Run with --help or -h for usage information.",
        );
    }

    #[test]
    fn parse_help_with_git_args_is_rejected() {
        assert_eq!(
            parse_from(&["--help", "--", "HEAD"]).unwrap_err(),
            "--help/-h must be used without other arguments. Run with --help or -h for usage information.",
        );
    }

    #[test]
    fn parse_file_with_git_args_is_rejected() {
        assert_eq!(
            parse_from(&["--file", "COMMIT_EDITMSG", "--", "HEAD"]).unwrap_err(),
            "--file/-f cannot be combined with git arguments after --. Run with --help or -h for usage information.",
        );
    }

    #[test]
    fn parse_repository_long_flag() {
        let action = parse_from(&["--repository", "/path/to/repo"]).unwrap();
        assert_eq!(
            action,
            CliAction::Run(CliOptions {
                config_path: None,
                repository_path: Some("/path/to/repo".to_string()),
                input_mode: InputMode::Git {
                    git_args: vec!["-1".to_string()],
                },
            })
        );
    }

    #[test]
    fn parse_repository_short_flag() {
        let action = parse_from(&["-r", "/path/to/repo"]).unwrap();
        assert_eq!(
            action,
            CliAction::Run(CliOptions {
                config_path: None,
                repository_path: Some("/path/to/repo".to_string()),
                input_mode: InputMode::Git {
                    git_args: vec!["-1".to_string()],
                },
            })
        );
    }

    #[test]
    fn parse_repository_with_git_args() {
        let action = parse_from(&["-r", "/path/to/repo", "--", "HEAD~5..HEAD"]).unwrap();
        assert_eq!(
            action,
            CliAction::Run(CliOptions {
                config_path: None,
                repository_path: Some("/path/to/repo".to_string()),
                input_mode: InputMode::Git {
                    git_args: vec!["HEAD~5..HEAD".to_string()],
                },
            })
        );
    }

    #[test]
    fn parse_repository_with_config() {
        let action = parse_from(&["-c", "config.yaml", "-r", "/repo"]).unwrap();
        assert_eq!(
            action,
            CliAction::Run(CliOptions {
                config_path: Some("config.yaml".to_string()),
                repository_path: Some("/repo".to_string()),
                input_mode: InputMode::Git {
                    git_args: vec!["-1".to_string()],
                },
            })
        );
    }

    #[test]
    fn parse_repository_missing_value() {
        assert_eq!(
            parse_from(&["--repository"]).unwrap_err(),
            "missing value for --repository. Run with --help or -h for usage information."
        );
    }

    #[test]
    fn parse_repository_repeated_is_rejected() {
        assert_eq!(
            parse_from(&["-r", "a", "-r", "b"]).unwrap_err(),
            "--repository/-r may be specified only once. Run with --help or -h for usage information.",
        );
    }

    #[test]
    fn parse_repository_with_stdin_is_rejected() {
        assert_eq!(
            parse_from(&["-r", "/repo", "--stdin"]).unwrap_err(),
            "--repository/-r cannot be used with --stdin. Run with --help or -h for usage information.",
        );
    }

    #[test]
    fn parse_repository_with_file_is_rejected() {
        assert_eq!(
            parse_from(&["-r", "/repo", "--file", "msg.txt"]).unwrap_err(),
            "--repository/-r cannot be used with --file/-f. Run with --help or -h for usage information.",
        );
    }
}
