#!/bin/bash

# =============================================================================
# Axeptio Android Sample App - Complete Test Automation Script  
# =============================================================================
# This script automates the complete workflow:
# 1. Build the sample app (publishersDebug variant)
# 2. Start Android emulator if not running
# 3. Install the app to emulator  
# 4. Launch the app
# 5. Open logcat for debugging
# =============================================================================

set -e  # Exit on any error

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Configuration
APP_PACKAGE="io.axept.samplekotlin"
MAIN_ACTIVITY="io.axept.samplekotlin.MainActivity"
EMULATOR_NAME="${EMULATOR_NAME:-Pixel_9}"
BUILD_VARIANT="publishersDebug"

echo -e "${BLUE}🚀 Axeptio Android Sample App - Test Automation${NC}"
echo -e "${BLUE}=================================================${NC}"

# Function to print status messages
print_status() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARN]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Function to check if emulator is running
is_emulator_running() {
    adb devices | grep -q "emulator"
}

# Function to wait for emulator to be ready
wait_for_emulator() {
    print_status "Waiting for emulator to be ready..."
    adb wait-for-device
    
    # Wait for boot to complete
    while [ "$(adb shell getprop sys.boot_completed 2>/dev/null | tr -d '\r')" != "1" ]; do
        echo -n "."
        sleep 2
    done
    echo
    print_status "Emulator is ready!"
}

# Function to build the app
build_app() {
    print_status "Building app (${BUILD_VARIANT} variant)..."
    if ./gradlew :samplekotlin:assemblePublishersDebug; then
        print_status "✅ Build successful!"
    else
        print_error "❌ Build failed!"
        exit 1
    fi
}

# Function to start emulator
start_emulator() {
    if is_emulator_running; then
        print_status "✅ Emulator already running"
    else
        print_status "🚀 Starting emulator: $EMULATOR_NAME"
        emulator -avd "$EMULATOR_NAME" -no-snapshot-save &
        
        # Store emulator PID for cleanup
        EMULATOR_PID=$!
        print_status "Emulator started with PID: $EMULATOR_PID"
        
        wait_for_emulator
    fi
}

# Function to install app
install_app() {
    local apk_path="samplekotlin/build/outputs/apk/publishers/debug/samplekotlin-publishers-debug.apk"
    
    if [ ! -f "$apk_path" ]; then
        print_error "APK not found at: $apk_path"
        print_error "Make sure the build completed successfully."
        exit 1
    fi
    
    print_status "📱 Installing app to emulator..."
    if adb install -r "$apk_path"; then
        print_status "✅ App installed successfully!"
    else
        print_error "❌ App installation failed!"
        exit 1
    fi
}

# Function to launch app
launch_app() {
    print_status "🎬 Launching app..."
    if adb shell am start -n "$APP_PACKAGE/$MAIN_ACTIVITY"; then
        print_status "✅ App launched successfully!"
    else
        print_error "❌ App launch failed!"
        exit 1
    fi
}

# Function to open logcat
open_logcat() {
    print_status "📋 Opening logcat for debugging..."
    print_status "   Filter: $APP_PACKAGE and Axeptio tags"
    print_warning "   Press Ctrl+C to stop logcat"
    echo
    
    # Open logcat with app-specific and Axeptio filters
    adb logcat | grep -E "($APP_PACKAGE|Axeptio|TCF|Vendor)"
}

# Cleanup function
cleanup() {
    if [ ! -z "$EMULATOR_PID" ]; then
        print_status "🧹 Cleaning up..."
        # Note: Don't kill emulator automatically - user might want to keep it running
        # kill $EMULATOR_PID 2>/dev/null || true
    fi
}

# Set up cleanup on script exit
trap cleanup EXIT

# Main execution flow
main() {
    echo -e "${BLUE}Configuration:${NC}"
    echo "  📱 Emulator: $EMULATOR_NAME"
    echo "  📦 Package: $APP_PACKAGE"  
    echo "  🏗️ Build Variant: $BUILD_VARIANT"
    echo
    
    # Step 1: Build the app
    build_app
    
    # Step 2: Start emulator
    start_emulator
    
    # Step 3: Install app
    install_app
    
    # Step 4: Launch app
    launch_app
    
    echo -e "${GREEN}🎉 SUCCESS! App is now running on emulator${NC}"
    echo -e "${BLUE}📋 Next steps:${NC}"
    echo "  1. Test the TCF Vendor API features"
    echo "  2. Try the Configuration management"
    echo "  3. Check the logcat output below for debugging"
    echo
    
    # Step 5: Open logcat for debugging
    read -p "Open logcat for debugging? [y/N]: " -n 1 -r
    echo
    if [[ $REPLY =~ ^[Yy]$ ]]; then
        open_logcat
    else
        print_status "✅ Script completed. Emulator and app are ready!"
        print_status "   Run './scripts/logcat.sh' to monitor logs later"
    fi
}

# Check if we're in the right directory
if [ ! -f "settings.gradle.kts" ]; then
    print_error "❌ Please run this script from the sample-app-android root directory"
    exit 1
fi

# Run main function
main "$@"