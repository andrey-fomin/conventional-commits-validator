#!/usr/bin/env bash
set -euo pipefail

preflight_check() {
  local missing=()
 
  if ! command -v gh &>/dev/null; then
    missing+=("gh (GitHub CLI)")
  elif ! gh auth status &>/dev/null 2>&1; then
    echo "Error: 'gh' is installed but not authenticated."
    echo "Run: gh auth login"
    exit 1
  fi

  if ! command -v python3 &>/dev/null; then
    missing+=("python3")
  fi

  if [[ ${#missing[@]} -gt 0 ]]; then
    echo "Error: Missing required dependencies:"
    for dep in "${missing[@]}"; do
      echo "  - $dep"
    done
    exit 1
  fi
}

preflight_check

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"
MANIFEST_FILE="$REPO_ROOT/.github/app-manifest.json"

REPO_OWNER="${REPO_OWNER:-$(gh repo view --json owner --jq '.owner.login')}"
REPO_NAME="${REPO_NAME:-$(gh repo view --json name --jq '.name')}"

if [[ ! -f "$MANIFEST_FILE" ]]; then
  echo "Error: Manifest file not found at $MANIFEST_FILE"
  exit 1
fi

ENCODED_MANIFEST=$(python3 -c "import json, urllib.parse; print(urllib.parse.quote(json.dumps(json.load(open('$MANIFEST_FILE')))))")

APP_URL="https://github.com/settings/apps/new?manifest=${ENCODED_MANIFEST}"

echo "GitHub App Setup"
echo "================"
echo ""
echo "Repository: ${REPO_OWNER}/${REPO_NAME}"
echo ""
echo "Step 1: Create the GitHub App"
echo "  Opening browser to create the app..."
echo ""
echo "  Permissions configured:"
echo "    - Contents: Write"
echo "    - Pull requests: Write"
echo ""
echo "  After clicking 'Create GitHub App', you will see:"
echo "    - App ID (save this)"
echo "    - Private key (download the PEM file)"
echo ""
read -p "Press Enter to open the browser..." </dev/tty

if command -v open &>/dev/null; then
  open "$APP_URL" 2>/dev/null || true
elif command -v xdg-open &>/dev/null; then
  xdg-open "$APP_URL" 2>/dev/null || true
else
  echo "Please open manually: $APP_URL"
fi

echo ""
echo "Step 2: Enter the App details"
read -p "App ID: " APP_ID </dev/tty
read -p "Path to downloaded PEM file: " PEM_PATH </dev/tty

if [[ ! -f "$PEM_PATH" ]]; then
  echo "Error: PEM file not found at $PEM_PATH"
  exit 1
fi

echo ""
echo "Step 3: Installing the App"
APP_NAME=$(python3 -c "import json; print(json.load(open('$MANIFEST_FILE'))['name'])")
INSTALL_URL="https://github.com/apps/${APP_NAME}/installations/new"
echo "  Please install the app on repository ${REPO_OWNER}/${REPO_NAME}"
echo "  Opening: $INSTALL_URL"

if command -v open &>/dev/null; then
  open "$INSTALL_URL" 2>/dev/null || true
elif command -v xdg-open &>/dev/null; then
  xdg-open "$INSTALL_URL" 2>/dev/null || true
else
  echo "Please open manually: $INSTALL_URL"
fi

read -p "Press Enter after installing the app..." </dev/tty

echo ""
echo "Step 4: Configuring repository secrets and variables"

gh variable set APP_ID --repo "${REPO_OWNER}/${REPO_NAME}" --body "$APP_ID"
echo "  Set variable: APP_ID"

gh secret set APP_PRIVATE_KEY --repo "${REPO_OWNER}/${REPO_NAME}" < "$PEM_PATH"
echo "  Set secret: APP_PRIVATE_KEY"

echo ""
echo "Setup complete!"
echo ""
echo "Configuration saved:"
echo "  - Repository variable: APP_ID = ${APP_ID}"
echo "  - Repository secret: APP_PRIVATE_KEY (set from ${PEM_PATH})"
echo ""
echo "Next steps:"
echo "  1. Ensure this PR has been merged to main"
echo "  2. Trigger a new release by merging a feature PR or pushing to main"
echo "  3. Verify the Release workflow runs automatically after release PR merge"