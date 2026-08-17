#!/usr/bin/env node

/**
 * Sets the version this repo declares, in both places that declare it.
 *
 * Run this by hand in the PR that points the sample app at a new SDK release — the version is a
 * deliberate choice that mirrors the SDK version being targeted, not something CI infers. The release
 * workflow reads the result and cuts vX.Y.Z on merge (MSK-241), so bumping here *is* releasing.
 *
 * Usage: node scripts/update-version.js <version>
 */

const fs = require('fs');
const path = require('path');
const { execFileSync } = require('child_process');

const version = process.argv[2];
if (!version) {
  console.error('Usage: node scripts/update-version.js <version>');
  process.exit(1);
}

// Extract version code from semantic version (major.minor.patch)
// Handle prerelease versions like "1.0.0-beta.1" by taking only the base version
const versionWithoutPrerelease = version.split('-')[0]; // "1.0.0-beta.1" -> "1.0.0"
const [major, minor, patch] = versionWithoutPrerelease.split('.').map(Number);
const versionCode = major * 10000 + minor * 100 + patch;

console.log(`Updating to version: ${version} (versionCode: ${versionCode})`);

// Update Kotlin sample app
const kotlinBuildFile = path.join(__dirname, '../samplekotlin/build.gradle.kts');
updateBuildFile(kotlinBuildFile, version, versionCode);

// Keep package.json in step: scripts/declared-version.sh fails the build if these two disagree.
updatePackageVersion(version);

console.log('✅ Version updated successfully. Commit both files in your PR.');

function updateBuildFile(filePath, version, versionCode) {
  try {
    let content = fs.readFileSync(filePath, 'utf8');
    
    // Update versionCode
    content = content.replace(
      /versionCode\s*=\s*\d+/,
      `versionCode = ${versionCode}`
    );
    
    // Update versionName
    content = content.replace(
      /versionName\s*=\s*"[^"]*"/,
      `versionName = "${version}"`
    );
    
    fs.writeFileSync(filePath, content, 'utf8');
    console.log(`✅ Updated ${path.relative(process.cwd(), filePath)}`);

  } catch (error) {
    console.error(`❌ Failed to update ${filePath}:`, error.message);
    process.exit(1);
  }
}

// Delegated to npm rather than rewritten by hand: the root version lives in package-lock.json as well
// as package.json, and all three CI jobs run `npm ci`, which wants the two in sync.
function updatePackageVersion(version) {
  try {
    execFileSync(
      'npm',
      ['version', version, '--no-git-tag-version', '--allow-same-version'],
      { cwd: path.join(__dirname, '..'), stdio: 'pipe' }
    );
    console.log('✅ Updated package.json and package-lock.json');
  } catch (error) {
    console.error('❌ Failed to update package.json:', error.message);
    process.exit(1);
  }
}