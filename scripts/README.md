# 🚀 Axeptio Android Sample App - Automation Scripts

This directory contains automation scripts to streamline the development and testing workflow for the Axeptio Android Sample App.

## 📋 Quick Start

### Complete Automation (Recommended)
```bash
# From sample-app-android root directory
./start-test-app.sh
```
This single command will:
1. ✅ Build the app (publishers debug variant)
2. 🚀 Start Android emulator (if not running)
3. 📱 Install app to emulator
4. 🎬 Launch the app
5. 📋 Optionally open logcat for debugging

## 🛠️ Individual Scripts

### Build App
```bash
# Build publishers debug (default)
./scripts/build-app.sh

# Build specific variants
./scripts/build-app.sh publishers debug
./scripts/build-app.sh brands release
./scripts/build-app.sh publishers debug --clean
```

### Emulator Management
```bash
# Start emulator
./scripts/emulator.sh start

# Check status
./scripts/emulator.sh status

# Stop emulator
./scripts/emulator.sh stop

# List available emulators
./scripts/emulator.sh list
```

### App Installation & Launch
```bash
# Install only
./scripts/install-app.sh

# Install and launch
./scripts/install-app.sh --launch

# Install specific variants
./scripts/install-app.sh brands release --launch
```

### Logcat Monitoring
```bash
# Monitor Axeptio-related logs (default)
./scripts/logcat.sh

# Different filter modes
./scripts/logcat.sh --app        # App-specific logs only
./scripts/logcat.sh --tcf        # TCF and vendor consent logs
./scripts/logcat.sh --all        # All logs
./scripts/logcat.sh --clear      # Clear logcat buffer first
```

## 🎯 Common Workflows

### First Time Setup
```bash
# Check what emulators are available
./scripts/emulator.sh list

# Complete setup and launch
./start-test-app.sh
```

### Development Cycle
```bash
# After making code changes
./scripts/build-app.sh
./scripts/install-app.sh --launch

# Monitor logs while testing
./scripts/logcat.sh --tcf
```

### Testing Different Variants
```bash
# Test Publishers (TCF) variant
./scripts/build-app.sh publishers debug
./scripts/install-app.sh publishers debug --launch

# Test Brands variant  
./scripts/build-app.sh brands debug
./scripts/install-app.sh brands debug --launch
```

### Debugging
```bash
# Start logging before launching app
./scripts/logcat.sh --axeptio

# In another terminal, install and launch
./scripts/install-app.sh --launch
```

## 📱 Configuration

### Environment Variables
- `EMULATOR_NAME`: Override default emulator (default: "Pixel_9")
  ```bash
  EMULATOR_NAME="Pixel_7_Pro" ./start-test-app.sh
  ```

### Default Settings
- **Service**: Publishers (enables TCF Vendor API testing)
- **Build Type**: Debug
- **Emulator**: Pixel_9
- **Package**: io.axept.samplekotlin

## 🔧 Troubleshooting

### Common Issues

**"No emulator found"**
```bash
# List available emulators
./scripts/emulator.sh list

# Create one in Android Studio if none exist
# AVD Manager → Create Virtual Device
```

**"Build failed"**
```bash
# Clean build
./scripts/build-app.sh publishers debug --clean

# Check Android Studio for detailed errors
```

**"Installation failed"**
```bash
# Uninstall existing app
adb uninstall io.axept.samplekotlin

# Restart ADB
adb kill-server && adb start-server

# Try again
./scripts/install-app.sh --launch
```

**"Emulator won't start"**
```bash
# Check emulator status
./scripts/emulator.sh status

# Stop any running emulators
./scripts/emulator.sh stop

# Start fresh
./scripts/emulator.sh start
```

### Log Analysis
- **Red logs**: Errors that need attention
- **Yellow logs**: Warnings
- **Green logs**: Info messages
- **Cyan logs**: Debug messages

Look for these key log patterns:
- `Axeptio`: SDK initialization and operations
- `TCF`: TCF-related parsing and processing
- `Vendor`: Vendor consent operations
- `Parser`: Consent string parsing

## 🎉 Features Tested

With these scripts, you can efficiently test:
- ✅ **TCF Vendor API**: getVendorConsents(), getConsentedVendors(), etc.
- ✅ **Configuration Management**: Service switching, presets
- ✅ **Service-aware UI**: Brands vs Publishers differences
- ✅ **Auto-refresh**: Live consent data updates
- ✅ **Error handling**: Parsing errors, validation

## 📚 Script Details

| Script | Purpose | Key Features |
|--------|---------|--------------|
| `start-test-app.sh` | Complete automation | Build → Emulator → Install → Launch |
| `build-app.sh` | Build management | Multiple variants, clean builds |
| `emulator.sh` | Emulator control | Start/stop/status/list |
| `install-app.sh` | App deployment | Install + optional launch |
| `logcat.sh` | Log monitoring | Filtered, colorized output |

All scripts include comprehensive error handling, status feedback, and help documentation.