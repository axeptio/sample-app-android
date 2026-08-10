# 2.4.0 (2026-08-10)

Aligns the public sample app with Axeptio Android SDK `2.4.0`, consolidating the `2.2.0`, `2.3.0` and `2.4.0` SDK releases. Supersedes the `release/2.2.0-beta.1` branch, which is now retired.

### ⚠ BREAKING CHANGES

* **sample-java:** the Java sample module has been removed. The SDK dropped Java language support in `2.2.0` (MSK-160) and removed the `@JvmStatic` companions; Java consumers must recompile and either migrate to Kotlin or call `AxeptioSDK.INSTANCE.instance()`. See the "Migrating from Java" section of the README.

### Features

* **sdk:** bump `io.axept.android:android-sdk` to `2.4.0`.
* **sample-kotlin:** register the `onCMPRestored()` listener callback (new in SDK `2.4.0`) in `MainActivity` and surface a live counter on the `AxeptioStore` demo screen.
* **sample-kotlin:** wire `setDisplayPopUpOnEnterForeground()` and expose it, alongside `setForceShowConsentDebug()`, as switches on the Configuration screen.
* **sample-kotlin:** show `getRemainingDaysForConsent()` on the Debug Consent Info screen.
* **sample-kotlin:** pass the full `initialize(...)` signature including `widgetType`, `prId`, `consentExpirationDays` and `shouldUpdateConsentExpiration`.
* **sample-kotlin:** add `AxeptioStoreDemoScreen` showcasing the StateFlow-based `AxeptioStore` in Jetpack Compose, reachable from the main screen.
* **sample-kotlin:** report the real SDK version in the app header instead of "Local Development Build".

### Bug Fixes

* **gradle:** point the GitHub Packages Maven repository at `axeptio/axeptio-android-sdk` instead of the stale `axeptio/tcf-android-sdk` URL.
* **sample-kotlin:** stop `saveCustomConfiguration()` silently resetting `widgetType`, `prId` and the consent-expiration fields, which the Configuration screen does not expose.

### Documentation

* **readme:** document codeless consent forwarding to Firebase, AppsFlyer, Adjust and Singular (SDK `2.3.0`) — including that it needs no integration code and is a no-op for absent partners.
* **readme:** document `onCMPRestored()` and its repeat-fire semantics, `getRemainingDaysForConsent()`, `setForceShowConsentDebug()`, `setDisplayPopUpOnEnterForeground()`, and the deprecated `clearConsent()` alias.
* **readme:** document the full `initialize(...)` signature and that `targetService` is fixed for the process lifetime.
* **readme:** rewrite the Google Consent v2 section to lead with automatic Firebase forwarding, keeping the manual mapping as the optional explicit path.
* **readme:** remove the Java code samples, which no longer compile against the SDK, and replace them with a migration section.

---

# 2.2.0-beta.1 (2026-04-10)

Beta release aligning the public sample app with Axeptio Android SDK `2.2.0-beta.1`.

### ⚠ BREAKING CHANGES

* **sample-java:** the Java sample module has been removed. The SDK dropped Java language support (MSK-160); consumers needing a Java reference should stay on the `master` branch (SDK 2.0.x / 2.1.x).

### Features

* **sdk:** bump `io.axept.android:android-sdk` to `2.2.0-beta.1`.
* **sample-kotlin:** pass the full `initialize(...)` signature including `widgetType`, `prId`, `consentExpirationDays`, and `shouldUpdateConsentExpiration`.
* **sample-kotlin:** register `setForceShowConsentDebug()` and the new `onError` listener callback (new in 2.2.0-beta.1).
* **sample-kotlin:** add `AxeptioStoreDemoScreen` showcasing the new StateFlow-based `AxeptioStore` in Jetpack Compose, reachable from a new entry on the main screen.

---

## [1.0.0-beta.2](https://github.com/axeptio/sample-app-android/compare/v1.0.0-beta.1...v1.0.0-beta.2) (2025-09-02)


### Bug Fixes

* apply feedback from PR ([27b0954](https://github.com/axeptio/sample-app-android/commit/27b0954f69db40c4eb0e43d3df5c35b4673c1019))
* deprecated-api-warnings-in-android-sample-app ([681d812](https://github.com/axeptio/sample-app-android/commit/681d8123fa0fe5f8977697abf870db4dd9dff4a8))

## 1.0.0-beta.1 (2025-08-29)


### Features

* add comprehensive automation scripts for Android sample app testing ([1544738](https://github.com/axeptio/sample-app-android/commit/15447382323ddc8e4dcb0cbc518176dd5c2b8885))
* add SDK version display to main screen header ([57b2b9f](https://github.com/axeptio/sample-app-android/commit/57b2b9f0791252e8229a13624f30cd0a9f831c13))
* **build:** add comprehensive quality gates and security scanning to ci pipeline ([d6f2361](https://github.com/axeptio/sample-app-android/commit/d6f23616213f82ae4a80af40358303c8626dbd6e))
* complete sdk implementation instructions ([a99cfe9](https://github.com/axeptio/sample-app-android/commit/a99cfe98a00783b5f990535acfeebee0520bc949))
* enhance debug capabilities and fix configuration management ([5896441](https://github.com/axeptio/sample-app-android/commit/589644129c0247a315744d1159f9cfb8962af652))
* implement comprehensive TCF vendor consent testing capabilities ([03846b8](https://github.com/axeptio/sample-app-android/commit/03846b84571ef951674fe8acd8daebf8bf58cd9a))
* readme instructions ([ba95cdd](https://github.com/axeptio/sample-app-android/commit/ba95cddf24881241b841582e6d2aaa0cf0135f31))
* **release:** implement comprehensive semantic versioning and automated releases ([09be413](https://github.com/axeptio/sample-app-android/commit/09be4136bafe19f8c406bef6c559c368d46c657c))


### Bug Fixes

* Add more detail on documentation ([36adaa9](https://github.com/axeptio/sample-app-android/commit/36adaa9a9313df5f9ec9716dec0edbb6eb38b87e))
* Add more detail on documentation ([e118e5b](https://github.com/axeptio/sample-app-android/commit/e118e5b35ff702f02dbe8f63153de6776ea969c5))
* **build:** disable subject case validation in commitlint ([5145cea](https://github.com/axeptio/sample-app-android/commit/5145cea4020f31beabfc5655bd75bef010da900e))
* **build:** remove failing OWASP security scan and create MSK-85 for gradle upgrade ([e0d9171](https://github.com/axeptio/sample-app-android/commit/e0d917109e86664955a36774dd00d2e4e2792d9d))
* **build:** resolve compose preview compilation and commitlint validation issues ([eab0d15](https://github.com/axeptio/sample-app-android/commit/eab0d15bb0aa39b7a20d28243d631b204c220cdc))
* **build:** resolve github actions workflow failures and pin node.js version ([4ea7b2a](https://github.com/axeptio/sample-app-android/commit/4ea7b2ac06274cf42dac30e907efed52e278d786))
* **build:** use published sdk 2.0.8 for ci builds and make local sdk optional ([795e903](https://github.com/axeptio/sample-app-android/commit/795e9037a1e57e952bd892b71676bf7ad03e7f82))
* update README ([bade8a4](https://github.com/axeptio/sample-app-android/commit/bade8a4414e30ce97c616003b1f21696e2ace959))
* update README ([19efdd3](https://github.com/axeptio/sample-app-android/commit/19efdd3327e0e854e119eb5a8908d81568640d13))
* use secrets ([96322dd](https://github.com/axeptio/sample-app-android/commit/96322ddb636ac19c0e4455f6c90b1571ead83fde))

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
