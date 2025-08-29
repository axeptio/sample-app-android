# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added
- Advanced SDK APIs for vendor consent management
  - `getVendorConsents()`: Full vendor consent mapping
  - `getConsentedVendors()`: List of consented vendor IDs
  - `getRefusedVendors()`: List of refused vendor IDs
  - `isVendorConsented()`: Individual vendor consent validation
  - `getConsentDebugInfo()`: Comprehensive debug information access
- Debug Consent Info Screen with detailed TCF consent data analysis
- Vendor Consent Test Screen with live testing interface for vendor validation
- Configuration Management system with dynamic service switching (Brands ↔ Publishers TCF)
- SDK Version Display in app header for clear build identification
- Comprehensive automation scripts for build/test/deploy workflow
- Complete API documentation with production-ready integration examples
- Best practice guidelines with comprehensive error handling patterns
- Material Design 3 implementation throughout sample app
- Semantic versioning and automated release management

### Fixed
- Configuration management bug where app was stuck on "Brands" service
- Clear consent flow now shows proper loading states and visual feedback
- MainActivity integration now uses ConfigurationManager instead of hardcoded BuildConfig

### Changed
- Auto-refresh performance optimized from 3s to 10s intervals for better battery usage
- Enhanced error handling throughout ViewModels with production-ready patterns
- Thread-safe operations using proper coroutine usage

### Documentation
- Added extensive README documentation for new vendor consent APIs
- Enhanced code comments with third-party developer best practices
- Clarified ConfigurationManager scope as sample-app specific (not SDK API)

## [2.0.6] - 2024-08-26

### Added
- Initial sample applications for Axeptio Android SDK
- Java sample app (`samplejava`) with XML layouts
- Kotlin sample app (`samplekotlin`) with Jetpack Compose
- Google Consent Mode v2 integration examples
- Firebase Analytics integration
- Publishers and Brands build variants
- Basic consent management functionality
- SharedPreferences monitoring capabilities

### Documentation
- Comprehensive README with integration guides
- SDK initialization examples for both Java and Kotlin
- Google Consent Mode implementation examples
- Build variant configuration instructions

[Unreleased]: https://github.com/axeptio/sample-app-android/compare/2.0.6...HEAD
[2.0.6]: https://github.com/axeptio/sample-app-android/releases/tag/2.0.6