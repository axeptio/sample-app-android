# CLAUDE.md

> A `CLAUDE.md` from `~/code/axeptio/.claude/` may also be loaded, because this checkout is a sibling
> of that repo. That file describes the **`axeptio-shared` plugin marketplace** — a Markdown-only repo
> with no build. Its repo-specific claims do not apply here. **This file wins for this repository.**
> In particular: this repo has a real Gradle build, cuts tagged releases, and has **no Beads database**
> (`bd` will error — use your harness's own task tracking).

## What this repo is

The **public reference integration** for the Axeptio Android SDK. It is a real Android app —
`samplekotlin` (Kotlin + Jetpack Compose), built with Gradle — but its actual product is the
**README**: external customers read it to learn how to integrate the SDK. A wrong statement in the
README misleads paying integrators, so treat documentation accuracy as production correctness, not
as a nicety.

`samplejava/` was removed when the SDK dropped Java support in `2.2.0`. `samplekotlin` is the only
module.

## The repo's central invariant

**The app's version mirrors the Axeptio SDK version it targets.** `versionName 2.5.0` means "this
sample demonstrates SDK 2.5.0". Three consequences:

1. `samplekotlin/build.gradle.kts` is the source of truth. `versionName` must equal `package.json`'s
   version **and** the `axeptioSdkVersion` constant. `scripts/declared-version.sh` enforces both and
   fails the build otherwise; it runs in CI.
2. `node scripts/update-version.js <version>` sets `versionCode`/`versionName` + `package.json` —
   and **nothing else**. `axeptioSdkVersion` and the README's dependency snippets are hand-edited.
3. **Bumping the version on `master` is what publishes a release.** There is no separate release
   trigger. Do not bump casually.

`axeptioSdkVersion` feeds the dependency coordinate and both flavors' `AXEPTIO_SDK_VERSION`
BuildConfig fields. `versionName` deliberately stays a **string literal**: `update-version.js`
rewrites it by regex and `declared-version.sh` parses it with `awk`, and neither can see through a
Gradle variable. Do not "tidy" it into the constant — you will silently break both.

## Gotchas that will waste your time

**A local SDK checkout silently replaces the published artifact.** `settings.gradle.kts` includes
`../axeptio-android-sdk-sources/android` as module `:android` *if that directory exists*, and
`build.gradle.kts` then prefers `project(":android")` over the published coordinate. On a machine
with that checkout, a local build does **not** exercise the SDK version you just pinned. To verify
against the real artifact, move the directory aside and confirm what resolved:

```bash
mv ../axeptio-android-sdk-sources ../axeptio-android-sdk-sources.off
./gradlew :samplekotlin:dependencies --configuration publishersDebugRuntimeClasspath | grep axept
# expect: +--- io.axept.android:android-sdk:<the version you pinned>
mv ../axeptio-android-sdk-sources.off ../axeptio-android-sdk-sources   # always restore
```

**The SDK is not on Maven Central.** It resolves from GitHub Packages and needs credentials, or
every build fails with 401:

```bash
export GITHUB_USERNAME=$(gh api user --jq .login)
export GITHUB_TOKEN=$(gh auth token)   # needs read:packages
```

**Verify SDK API claims against the artifact, not the release notes.** Release notes omit things —
`2.5.0`'s notes list `cmpRestoredEventCount` but not the `consentSavedEventCount` that also shipped.
Download the AAR and diff the ABI:

```bash
curl -sSL -u "x:$(gh auth token)" -o sdk.aar \
  "https://maven.pkg.github.com/axeptio/axeptio-android-sdk/io/axept/android/android-sdk/<v>/android-sdk-<v>.aar"
unzip -oq sdk.aar && unzip -oq classes.jar -d cls
javap -classpath cls io.axept.android.library.Axeptio        # etc.
```

**There is effectively no test coverage.** `ExampleUnitTest` asserts `2+2==4`. `./gradlew test`
passing means almost nothing; behaviour must be checked on a device or emulator.

## Commands

```bash
./scripts/declared-version.sh    # prints the declared version; fails on any drift
./gradlew :samplekotlin:test :samplekotlin:lint
./gradlew :samplekotlin:assembleDebug :samplekotlin:assembleRelease
./scripts/build-app.sh           # also: emulator.sh, install-app.sh, logcat.sh
```

Flavors are `publishers` (TCF) and `brands`. Several SDK features are Publishers/TCF-only (the
vendor APIs) and some bugs have been brands-only, so **check both** when verifying SDK behaviour.

## Branch & PR flow

- Branch from `master`, open a PR. Branch names follow the Linear ticket: `msk-258-<slug>`.
- **Conventional commits are enforced** by commitlint via a husky `commit-msg` hook, with a
  restricted scope list in `.commitlintrc.json` (`sdk`, `build`, `kotlin`, `config`, `ui`, `docs`, …).
  A subject like "Address Copilot review round 1" is rejected — write a real conventional subject.
- PR titles are validated by `validate-pull-request-title.yml`.
- `master` is governed by **rulesets** (`PR`, `Compliance`, `Copilot`), not classic branch
  protection. `Compliance` requires signed commits with no bypass; `PR` requires changes to arrive
  via pull request, bypassed by the `Admins` and `bot` teams.

## Releasing

`release.yml` on push to `master` reads `./scripts/declared-version.sh`, and if no `v<version>` tag
exists yet it generates the changelog, commits it to `master`, creates a **signed** tag and publishes
a GitHub Release.

Two things to know before trusting it:

- The job **pushes directly to `master`**, which the rulesets otherwise forbid. This worked for the
  first time at `v2.5.0`, only after `axeptio-bot` was added to a `bot` team with a bypass on the
  `PR` ruleset. Every earlier attempt failed at "Create and Push Tag", and `v2.4.0` was tagged by
  hand. If a release fails, read that step's log first.
- Do **not** hand-write `CHANGELOG.md`; the workflow generates it from the conventional commits on
  the PR. Write good commit subjects instead.

## Documentation conventions

The README documents SDK behaviour per version and is read by customers. When a version claim
changes, keep the historical statement rather than deleting it — "added in 2.4.0, fixed in 2.5.0" is
more useful to someone pinned to an old version than silent removal. State which SDK version
introduced a behaviour, and verify the claim against the SDK's KDoc or the AAR before writing it.
