#!/bin/bash

# =============================================================================
# Build Script for Axeptio Android Sample App
# =============================================================================
# Builds the sample app with specified variant
# Usage: ./scripts/build-app.sh [publishers|brands] [debug|release]
# =============================================================================

set -e

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
BLUE='\033[0;34m'
NC='\033[0m'

print_status() {
    echo -e "${GREEN}[BUILD]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Default configuration
SERVICE=${1:-publishers}  # publishers or brands
BUILD_TYPE=${2:-debug}    # debug or release

# Capitalize first letter for Gradle task
SERVICE_CAP="$(tr '[:lower:]' '[:upper:]' <<< ${SERVICE:0:1})${SERVICE:1}"
BUILD_TYPE_CAP="$(tr '[:lower:]' '[:upper:]' <<< ${BUILD_TYPE:0:1})${BUILD_TYPE:1}"

GRADLE_TASK=":samplekotlin:assemble${SERVICE_CAP}${BUILD_TYPE_CAP}"

print_status "🏗️ Building Axeptio Sample App"
print_status "   Service: $SERVICE"
print_status "   Build Type: $BUILD_TYPE"
print_status "   Gradle Task: $GRADLE_TASK"
echo

# Check if we're in the right directory
if [ ! -f "settings.gradle.kts" ]; then
    print_error "Please run this script from the sample-app-android root directory"
    exit 1
fi

# Clean build option
if [ "$3" == "--clean" ]; then
    print_status "🧹 Cleaning previous build..."
    ./gradlew clean
fi

# Build the app
print_status "🚀 Starting build..."
if ./gradlew $GRADLE_TASK; then
    print_status "✅ Build completed successfully!"
    
    # Show APK location
    APK_PATH="samplekotlin/build/outputs/apk/$SERVICE/$BUILD_TYPE/samplekotlin-$SERVICE-$BUILD_TYPE.apk"
    if [ -f "$APK_PATH" ]; then
        print_status "📦 APK location: $APK_PATH"
        print_status "📊 APK size: $(ls -lh "$APK_PATH" | awk '{print $5}')"
    fi
else
    print_error "❌ Build failed!"
    exit 1
fi