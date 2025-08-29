# Semantic Versioning & Automated Releases

This repository uses automated semantic versioning with [semantic-release](https://semantic-release.gitbook.io/) based on conventional commits.

## How It Works

### Conventional Commits
All commits must follow the [Conventional Commits](https://www.conventionalcommits.org/) specification:

```
<type>[optional scope]: <description>

[optional body]

[optional footer(s)]
```

### Commit Types & Version Bumps

| Commit Type | Version Bump | Example |
|-------------|--------------|---------|
| `feat` | **MINOR** | `feat(api): add getVendorConsents method` |
| `fix` | **PATCH** | `fix(ui): resolve crash on configuration screen` |
| `feat!` or `BREAKING CHANGE` | **MAJOR** | `feat!: remove deprecated consent methods` |
| `docs`, `style`, `refactor`, `test`, `chore` | **No release** | `docs: update API examples` |

### Scopes
Use these scopes to categorize changes:
- `api` - SDK API changes
- `ui` - User interface changes
- `config` - Configuration management
- `build` - Build system changes
- `docs` - Documentation updates
- `kotlin` - Kotlin-specific changes
- `java` - Java-specific changes
- `android` - Android platform changes

## Release Process

### Automatic Releases
Releases are triggered automatically when code is pushed to:
- **`master`** → Production releases (1.0.0, 1.1.0, 1.1.1)
- **`develop`** → Pre-releases (1.1.0-beta.1, 1.1.0-beta.2)

### What Happens During Release
1. **Analyze commits** since last release
2. **Calculate version** based on commit types
3. **Update Android app versions** in `build.gradle.kts` files
4. **Generate CHANGELOG.md** from commit messages
5. **Create git tag** and GitHub release
6. **Build Android APKs** and attach to release

### Manual Version Updates
Android app versions are automatically synchronized:
- `versionName` = semantic version (e.g., "2.1.0")
- `versionCode` = calculated integer (e.g., 20100)

## Development Workflow

### Making Changes
1. Create feature branch from `develop`
2. Make commits using conventional commit format
3. Open PR to `develop` (commits validated automatically)
4. Merge PR → triggers beta release
5. When ready, merge `develop` to `master` → triggers production release

### Commit Validation
Commits are validated on:
- **Local commits** (via husky git hook)
- **Pull requests** (via GitHub Actions)

Invalid commits will be rejected with helpful error messages.

### Examples of Good Commits
```bash
# New feature (minor version bump)
git commit -m "feat(api): add isVendorConsented validation method"

# Bug fix (patch version bump)  
git commit -m "fix(config): prevent crash when switching services"

# Breaking change (major version bump)
git commit -m "feat(api)!: remove deprecated consent methods

BREAKING CHANGE: getCookie() method has been removed, use getConsentData() instead"

# Documentation (no version bump)
git commit -m "docs(api): add usage examples for vendor consent APIs"

# Multiple changes
git commit -m "feat(ui): add debug consent screen

- Add comprehensive TCF analysis
- Include vendor consent breakdown  
- Implement real-time updates"
```

### Examples of Bad Commits
```bash
# Missing type
git commit -m "add new feature"

# Incorrect format
git commit -m "Added new API method"

# Too vague
git commit -m "fix: bug fix"
```

## Configuration Files

- **`.releaserc.json`** - Semantic release configuration
- **`.commitlintrc.json`** - Commit message validation rules
- **`package.json`** - Node.js dependencies for tooling
- **`.github/workflows/release.yml`** - GitHub Actions workflow
- **`scripts/update-version.js`** - Android version update script

## Troubleshooting

### Commit Rejected Locally
```bash
# Error: commit message doesn't follow conventional format
git commit --amend -m "feat(api): add vendor consent method"
```

### Release Failed
Check GitHub Actions logs:
1. Go to repository → Actions tab
2. Click on failed release workflow
3. Review error logs in "Release" job

### Manual Release
If automated release fails, you can trigger manually:
```bash
npx semantic-release --dry-run  # Preview what would happen
npx semantic-release           # Actually perform release
```

## Migration Notes

- **Existing tags**: Current `2.0.6` tag is preserved
- **Version continuity**: Next release will be calculated from `2.0.6`
- **Backward compatibility**: All existing functionality remains unchanged