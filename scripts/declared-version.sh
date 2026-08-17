#!/usr/bin/env bash
#
# Prints the version this repo declares, and fails if the two places that declare it disagree.
#
# The sample app's version deliberately mirrors the Axeptio SDK version it targets, so the version is
# a human decision recorded in the repo — not something inferred from commit history or from which
# tags happen to be reachable. `samplekotlin/build.gradle.kts` is the source of truth; `package.json`
# must agree with it. Bump both at once with `node scripts/update-version.js <version>`.
#
# Only the version reaches stdout, so callers can capture it:
#   DECLARED=$(./scripts/declared-version.sh)
#
# Diagnostics go to stderr as GitHub Actions error annotations. Used by both the release gate and PR
# Quality Checks in .github/workflows/release.yml (MSK-241).

set -euo pipefail

repo_root=$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)
gradle_file="$repo_root/samplekotlin/build.gradle.kts"
package_file="$repo_root/package.json"

# awk rather than `sed | head`: under `set -o pipefail`, head closing the pipe early can fail the
# whole pipeline on SIGPIPE. `exit` after the first match keeps it single-process.
declared=$(awk -F'"' '/versionName = /{ print $2; exit }' "$gradle_file")
if [ -z "$declared" ]; then
    echo "::error file=samplekotlin/build.gradle.kts::could not parse versionName" >&2
    exit 1
fi

pkg=$(node -p "require('$package_file').version")
if [ "$declared" != "$pkg" ]; then
    echo "::error::versionName is $declared but package.json is $pkg - run 'node scripts/update-version.js <version>' to set both" >&2
    exit 1
fi

echo "$declared"
