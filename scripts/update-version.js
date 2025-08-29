#!/usr/bin/env node

/**
 * Script to update Android app versions from semantic-release
 * Usage: node scripts/update-version.js <version>
 */

const fs = require('fs');
const path = require('path');

const version = process.argv[2];
if (!version) {
  console.error('Usage: node scripts/update-version.js <version>');
  process.exit(1);
}

// Extract version code from semantic version (major.minor.patch)
const [major, minor, patch] = version.split('.').map(Number);
const versionCode = major * 10000 + minor * 100 + patch;

console.log(`Updating to version: ${version} (versionCode: ${versionCode})`);

// Update Kotlin sample app
const kotlinBuildFile = path.join(__dirname, '../samplekotlin/build.gradle.kts');
updateBuildFile(kotlinBuildFile, version, versionCode);

// Update Java sample app  
const javaBuildFile = path.join(__dirname, '../samplejava/build.gradle.kts');
updateBuildFile(javaBuildFile, version, versionCode);

console.log('✅ Version updated successfully in both sample apps');

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