#!/bin/bash

# =============================================================================
# Emulator Management Script
# =============================================================================
# Manage Android emulators (start/stop/status/list)
# Usage: ./scripts/emulator.sh [start|stop|status|list] [emulator_name]
# =============================================================================

set -e

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

print_status() {
    echo -e "${GREEN}[EMULATOR]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARN]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Default emulator name
DEFAULT_EMULATOR="Pixel_9"
EMULATOR_NAME="${2:-$DEFAULT_EMULATOR}"

# Functions
list_emulators() {
    print_status "📱 Available emulators:"
    emulator -list-avds | while read -r line; do
        echo "  • $line"
    done
}

is_emulator_running() {
    adb devices | grep -q "emulator"
}

get_running_emulators() {
    adb devices | grep "emulator" | awk '{print $1}'
}

start_emulator() {
    if is_emulator_running; then
        print_warning "⚠️ An emulator is already running:"
        get_running_emulators | while read -r emu; do
            echo "  • $emu"
        done
        return 0
    fi
    
    # Check if emulator exists
    if ! emulator -list-avds | grep -q "^$EMULATOR_NAME$"; then
        print_error "❌ Emulator '$EMULATOR_NAME' not found!"
        print_status "Available emulators:"
        list_emulators
        exit 1
    fi
    
    print_status "🚀 Starting emulator: $EMULATOR_NAME"
    print_status "   This may take a minute..."
    
    # Start emulator in background
    emulator -avd "$EMULATOR_NAME" -no-snapshot-save > /dev/null 2>&1 &
    EMULATOR_PID=$!
    
    print_status "⏳ Waiting for emulator to be ready..."
    adb wait-for-device
    
    # Wait for boot completion
    while [ "$(adb shell getprop sys.boot_completed 2>/dev/null | tr -d '\r')" != "1" ]; do
        echo -n "."
        sleep 2
    done
    echo
    
    print_status "✅ Emulator is ready! (PID: $EMULATOR_PID)"
}

stop_emulator() {
    if ! is_emulator_running; then
        print_warning "⚠️ No emulator is currently running"
        return 0
    fi
    
    print_status "🛑 Stopping emulator(s)..."
    get_running_emulators | while read -r emu; do
        print_status "   Stopping: $emu"
        adb -s "$emu" emu kill
    done
    
    # Wait a moment for cleanup
    sleep 2
    
    if ! is_emulator_running; then
        print_status "✅ Emulator(s) stopped successfully"
    else
        print_warning "⚠️ Some emulators may still be running"
    fi
}

show_status() {
    print_status "📊 Emulator Status:"
    
    if is_emulator_running; then
        echo -e "${GREEN}  🟢 Running emulators:${NC}"
        adb devices | grep "emulator" | while read -r line; do
            echo "    • $line"
        done
    else
        echo -e "${YELLOW}  🔴 No emulators running${NC}"
    fi
    
    echo
    print_status "📋 All available emulators:"
    emulator -list-avds | while read -r line; do
        echo "  • $line"
    done
}

# Main command processing
case "$1" in
    "start")
        start_emulator
        ;;
    "stop")
        stop_emulator
        ;;
    "status")
        show_status
        ;;
    "list")
        list_emulators
        ;;
    "")
        print_status "🤖 Emulator Management Script"
        echo
        echo "Usage: $0 [command] [emulator_name]"
        echo
        echo "Commands:"
        echo "  start   - Start emulator (default: $DEFAULT_EMULATOR)"
        echo "  stop    - Stop running emulator(s)"
        echo "  status  - Show emulator status"
        echo "  list    - List available emulators"
        echo
        echo "Examples:"
        echo "  $0 start"
        echo "  $0 start Pixel_7"
        echo "  $0 status"
        echo "  $0 stop"
        ;;
    *)
        print_error "❌ Unknown command: $1"
        print_status "Run '$0' without arguments for help"
        exit 1
        ;;
esac