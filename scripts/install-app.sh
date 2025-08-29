#!/bin/bash

# =============================================================================
# App Installation Script
# =============================================================================
# Install and launch the Axeptio sample app on connected device/emulator
# Usage: ./scripts/install-app.sh [publishers|brands] [debug|release] [--launch]
# =============================================================================

set -e

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

print_status() {
    echo -e "${GREEN}[INSTALL]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARN]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Configuration
SERVICE=${1:-publishers}
BUILD_TYPE=${2:-debug}
LAUNCH_AFTER_INSTALL=false
APP_PACKAGE="io.axept.samplekotlin"
MAIN_ACTIVITY="io.axept.samplekotlin.MainActivity"

# Check for --launch flag
for arg in "$@"; do
    if [ "$arg" = "--launch" ]; then
        LAUNCH_AFTER_INSTALL=true
    fi
done

print_status "📱 Installing Axeptio Sample App"
print_status "   Service: $SERVICE"
print_status "   Build Type: $BUILD_TYPE"
print_status "   Launch after install: $LAUNCH_AFTER_INSTALL"
echo

# Check if device is connected
check_device() {
    local device_count=$(adb devices | grep -c "device$" || true)
    
    if [ "$device_count" -eq 0 ]; then
        print_error "❌ No Android device or emulator connected!"
        print_status "💡 Solutions:"
        print_status "   1. Start an emulator: ./scripts/emulator.sh start"
        print_status "   2. Connect a physical device with USB debugging enabled"
        print_status "   3. Check device connection: adb devices"
        exit 1
    elif [ "$device_count" -gt 1 ]; then
        print_warning "⚠️ Multiple devices connected:"
        adb devices | grep "device$" | while read -r line; do
            echo "    • $line"
        done
        print_status "   Using first available device..."
    else
        print_status "✅ Device connected:"
        adb devices | grep "device$" | head -n1 | while read -r line; do
            echo "    • $line"
        done
    fi
}

# Find APK file
find_apk() {
    local apk_path="samplekotlin/build/outputs/apk/$SERVICE/$BUILD_TYPE/samplekotlin-$SERVICE-$BUILD_TYPE.apk"
    
    if [ ! -f "$apk_path" ]; then
        print_error "❌ APK not found: $apk_path"
        print_status "💡 Build the app first:"
        print_status "   ./scripts/build-app.sh $SERVICE $BUILD_TYPE"
        print_status "   or run: ./gradlew :samplekotlin:assemble${SERVICE^}${BUILD_TYPE^}"
        exit 1
    fi
    
    echo "$apk_path"
}

# Install APK
install_apk() {
    local apk_path=$1
    
    print_status "📦 Installing APK: $(basename "$apk_path")"
    print_status "   Size: $(ls -lh "$apk_path" | awk '{print $5}')"
    
    # Install with -r flag to replace existing installation
    if adb install -r "$apk_path"; then
        print_status "✅ Installation successful!"
    else
        print_error "❌ Installation failed!"
        print_status "💡 Troubleshooting:"
        print_status "   1. Check if device has enough storage"
        print_status "   2. Try uninstalling first: adb uninstall $APP_PACKAGE"
        print_status "   3. Restart adb: adb kill-server && adb start-server"
        exit 1
    fi
}

# Launch app
launch_app() {
    print_status "🚀 Launching app..."
    
    if adb shell am start -n "$APP_PACKAGE/$MAIN_ACTIVITY"; then
        print_status "✅ App launched successfully!"
        print_status "📱 App package: $APP_PACKAGE"
    else
        print_error "❌ Failed to launch app!"
        print_status "💡 Try launching manually from device/emulator"
        exit 1
    fi
}

# Show app info
show_app_info() {
    print_status "📋 App Information:"
    
    # Check if app is installed
    if adb shell pm list packages | grep -q "$APP_PACKAGE"; then
        echo -e "${GREEN}  ✅ Package installed: $APP_PACKAGE${NC}"
        
        # Get version info if possible
        local version_info=$(adb shell dumpsys package "$APP_PACKAGE" | grep "versionName" | head -1 || true)
        if [ -n "$version_info" ]; then
            echo "  📊 $version_info"
        fi
        
        # Check if app is running
        if adb shell pgrep -f "$APP_PACKAGE" > /dev/null 2>&1; then
            echo -e "${GREEN}  🏃 Status: Running${NC}"
        else
            echo -e "${YELLOW}  💤 Status: Not running${NC}"
        fi
    else
        echo -e "${RED}  ❌ Package not installed${NC}"
    fi
}

# Main execution
main() {
    # Check device connection
    check_device
    
    # Find APK
    APK_PATH=$(find_apk)
    
    # Install APK
    install_apk "$APK_PATH"
    
    # Launch if requested
    if [ "$LAUNCH_AFTER_INSTALL" = true ]; then
        echo
        launch_app
    fi
    
    echo
    show_app_info
    
    echo
    print_status "🎉 Installation complete!"
    
    if [ "$LAUNCH_AFTER_INSTALL" = false ]; then
        print_status "💡 To launch the app:"
        print_status "   ./scripts/install-app.sh --launch"
        print_status "   or manually from device/emulator"
    fi
}

# Help text
if [ "$1" = "--help" ] || [ "$1" = "-h" ]; then
    print_status "📱 App Installation Script"
    echo
    echo "Usage: $0 [service] [build_type] [--launch]"
    echo
    echo "Parameters:"
    echo "  service     - publishers (default) or brands"
    echo "  build_type  - debug (default) or release"
    echo "  --launch    - Launch app after installation"
    echo
    echo "Examples:"
    echo "  $0                        # Install publishers debug"
    echo "  $0 publishers debug       # Install publishers debug"
    echo "  $0 brands release         # Install brands release"
    echo "  $0 publishers debug --launch # Install and launch"
    echo
    exit 0
fi

# Run main function
main