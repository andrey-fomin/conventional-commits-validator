# Releasing

## One-command release

Use the local release helper:

```bash
scripts/release 0.2.0
```

The script will:

1. verify the git working tree is clean
2. update `Cargo.toml` to the requested version
3. create a release commit
4. create a matching tag such as `v0.2.0`
5. push the current branch and the tag to `origin`

## What happens next

Pushing the tag triggers `.github/workflows/release.yml`, which:

1. verifies the tag version matches `Cargo.toml`
2. runs tests
3. builds release binaries for Linux, macOS, and Windows
4. packages archives and SHA256 checksum files
5. uploads them to the GitHub Release for the tag

## Notes

- Pass the version without a leading `v`
- The script fails if the worktree is dirty
- The script fails if the target tag already exists
