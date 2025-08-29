#!/bin/bash

# =============================================================================
# Logcat Monitoring Script for Axeptio Sample App
# =============================================================================
# Monitor Android logcat with filters for Axeptio-related logs
# Usage: ./scripts/logcat.sh [--app|--axeptio|--tcf|--all] [--clear]
# =============================================================================

set -e

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
NC='\033[0m'

print_status() {
    echo -e "${GREEN}[LOGCAT]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARN]${NC} $1"
}

# Configuration
APP_PACKAGE="io.axept.samplekotlin"
FILTER_MODE="axeptio"  # Default filter

# Parse arguments
for arg in "$@"; do
    case "$arg" in
        "--app")
            FILTER_MODE="app"
            ;;
        "--axeptio")
            FILTER_MODE="axeptio"
            ;;
        "--tcf")
            FILTER_MODE="tcf"
            ;;
        "--all")
            FILTER_MODE="all"
            ;;
        "--clear")
            print_status "🧹 Clearing logcat buffer..."
            adb logcat -c
            print_status "✅ Logcat cleared"
            ;;
    esac
done

# Check if device is connected
if ! adb devices | grep -q "device"; then
    print_warning "❌ No device connected!"
    print_status "💡 Start emulator: ./scripts/emulator.sh start"
    exit 1
fi

print_status "📋 Starting logcat monitoring"
print_status "   Filter mode: $FILTER_MODE"
print_status "   Press Ctrl+C to stop"
echo

# Color coding function for log levels
colorize_logs() {
    while IFS= read -r line; do
        case "$line" in
            *" E "*)
                echo -e "${RED}$line${NC}"
                ;;
            *" W "*)
                echo -e "${YELLOW}$line${NC}"
                ;;
            *" I "*)
                echo -e "${GREEN}$line${NC}"
                ;;
            *" D "*)
                echo -e "${CYAN}$line${NC}"
                ;;
            *" V "*)
                echo -e "${NC}$line${NC}"
                ;;
            *)
                echo "$line"
                ;;
        esac
    done
}

# Filter functions
case "$FILTER_MODE" in
    "app")
        print_status "🔍 Filtering: App-specific logs ($APP_PACKAGE)"
        adb logcat | grep "$APP_PACKAGE" | colorize_logs
        ;;
    "axeptio")
        print_status "🔍 Filtering: Axeptio SDK logs"
        adb logcat | grep -i -E "(axeptio|vendor|consent|tcf)" | colorize_logs
        ;;
    "tcf")
        print_status "🔍 Filtering: TCF and Vendor consent logs"
        adb logcat | grep -i -E "(tcf|vendor|consent|parser)" | colorize_logs
        ;;
    "all")
        print_status "🔍 Showing: All logcat output"
        adb logcat | colorize_logs
        ;;
    *)
        print_status "🔍 Default: Axeptio-related logs"
        adb logcat | grep -i -E "(axeptio|vendor|consent|tcf|$APP_PACKAGE)" | colorize_logs
        ;;
esac