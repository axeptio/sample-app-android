# [2.5.0](https://github.com/axeptio/sample-app-android/compare/v2.4.0...v2.5.0) (2026-09-03)


### Bug Fixes

* add explicit permissions to workflow jobs ([6121c90](https://github.com/axeptio/sample-app-android/commit/6121c901b156584f683155ffd0b45dd537f3e00d))
* address actionlint issues in release workflow ([0f4c147](https://github.com/axeptio/sample-app-android/commit/0f4c147c9f20576f01674b6aabc46bd0ac80be02))
* **build:** tolerate whitespace when parsing axeptioSdkVersion ([0a5788b](https://github.com/axeptio/sample-app-android/commit/0a5788b34246ba659b9853b853583f39043ee30a))
* **build:** track package-lock.json so the release job can run ([770b432](https://github.com/axeptio/sample-app-android/commit/770b432554b9080d13b2a8a2d6c904def587707a))
* **config:** pair cookiesVersion with targetService and apply popup toggles live ([7f738a2](https://github.com/axeptio/sample-app-android/commit/7f738a2004c2934b8af5842d4417bed75e46a356)), closes [#43](https://github.com/axeptio/sample-app-android/issues/43)
* **release:** drive releases from the declared version, not tag reachability ([535690c](https://github.com/axeptio/sample-app-android/commit/535690cf63205938061487a848909fa410e14615)), closes [#45](https://github.com/axeptio/sample-app-android/issues/45)
* **release:** fail the release gate on a tag-query error instead of releasing ([c3f25d5](https://github.com/axeptio/sample-app-android/commit/c3f25d58834b9b3152888ea9c4c46120ecee8235))
* replace semantic-release with GPG-signed manual release workflow ([5229552](https://github.com/axeptio/sample-app-android/commit/522955275720d9c2218b3b6346a38491a0986b87))
* resolve CI build failure due to invalid versionCode = NaN ([ba5f490](https://github.com/axeptio/sample-app-android/commit/ba5f4904d580ad5534d0a49ca64c62317fbfb6f9))
* tcf fields api alignment ([8396418](https://github.com/axeptio/sample-app-android/commit/8396418a64063674df93f71200296085fd6f89f9))


* feat!: align sample app with Axeptio Android SDK 2.4.0 ([ee90040](https://github.com/axeptio/sample-app-android/commit/ee900405dba2305f284952390d68225ad10df12d))
* feat!: align sample app with Axeptio Android SDK 2.2.0-beta.1 ([472ed53](https://github.com/axeptio/sample-app-android/commit/472ed534d584b8ca9c3dec2cb99fba2743b974aa))


### Features

* **kotlin:** demonstrate onConsentSaved() and the new AxeptioStore counters ([8bbface](https://github.com/axeptio/sample-app-android/commit/8bbface39a27410c66de21234a442230ac086dba))
* **sdk:** bump io.axept.android:android-sdk to 2.5.0 ([96b5004](https://github.com/axeptio/sample-app-android/commit/96b500498ae89eb7b038d54b8996825fea65e021))


### BREAKING CHANGES

* the samplejava module is removed. The SDK dropped Java
language support in 2.2.0 and removed the @JvmStatic companions.

Co-Authored-By: Claude Opus 5 (1M context) <noreply@anthropic.com>
* the Java sample (samplejava/) is removed. The Android SDK
dropped Java language support in 2.2.0-beta.1 (MSK-160), so the Java module
can no longer compile against the SDK. Consumers needing a Java reference
should stay on master (SDK 2.0.x / 2.1.x).

- Bump axeptio-android-sdk dependency to 2.2.0-beta.1 in samplekotlin.
- Delete the samplejava/ module and scrub its references from
  settings.gradle.kts, scripts/update-version.js, .github/workflows/release.yml,
  .releaserc.json, .gitignore, generate-config.sh, and .idea/gradle.xml.
- Expand samplekotlin init to the full signature (widgetType, prId,
  consentExpirationDays, shouldUpdateConsentExpiration) and register
  setForceShowConsentDebug() and the new onError listener callback.
- Extend ConfigurationManager / CustomerConfiguration with the new config
  fields and SharedPreferences persistence; align the stored default
  cookiesVersion with "google cmp partner program sandbox-en-EU".
- Add AxeptioStoreDemoScreen demonstrating the StateFlow-based AxeptioStore
  in Jetpack Compose, reachable from a new entry on MainScreen — this is the
  Android analogue of iOS 2.2.0-beta.1's SwiftUISampleView.
- Bump samplekotlin versionName/versionCode and package.json to 2.2.0-beta.1.
- Add a prominent beta banner, rewrite the README Overview section, and add
  a 2.2.0-beta.1 CHANGELOG entry.

Co-Authored-By: Claude Opus 4.6 (1M context) <noreply@anthropic.com>



# [1.0.0-beta.2](https://github.com/axeptio/sample-app-android/compare/v1.0.0-beta.1...v1.0.0-beta.2) (2025-09-02)


### Bug Fixes

* apply feedback from PR ([27b0954](https://github.com/axeptio/sample-app-android/commit/27b0954f69db40c4eb0e43d3df5c35b4673c1019))
* deprecated-api-warnings-in-android-sample-app ([681d812](https://github.com/axeptio/sample-app-android/commit/681d8123fa0fe5f8977697abf870db4dd9dff4a8))



# [1.0.0-beta.1](https://github.com/axeptio/sample-app-android/compare/ba95cddf24881241b841582e6d2aaa0cf0135f31...v1.0.0-beta.1) (2025-08-29)


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


### Features

* add comprehensive automation scripts for Android sample app testing ([1544738](https://github.com/axeptio/sample-app-android/commit/15447382323ddc8e4dcb0cbc518176dd5c2b8885))
* add SDK version display to main screen header ([57b2b9f](https://github.com/axeptio/sample-app-android/commit/57b2b9f0791252e8229a13624f30cd0a9f831c13))
* **build:** add comprehensive quality gates and security scanning to ci pipeline ([d6f2361](https://github.com/axeptio/sample-app-android/commit/d6f23616213f82ae4a80af40358303c8626dbd6e))
* complete sdk implementation instructions ([a99cfe9](https://github.com/axeptio/sample-app-android/commit/a99cfe98a00783b5f990535acfeebee0520bc949))
* enhance debug capabilities and fix configuration management ([5896441](https://github.com/axeptio/sample-app-android/commit/589644129c0247a315744d1159f9cfb8962af652))
* implement comprehensive TCF vendor consent testing capabilities ([03846b8](https://github.com/axeptio/sample-app-android/commit/03846b84571ef951674fe8acd8daebf8bf58cd9a))
* readme instructions ([ba95cdd](https://github.com/axeptio/sample-app-android/commit/ba95cddf24881241b841582e6d2aaa0cf0135f31))
* **release:** implement comprehensive semantic versioning and automated releases ([09be413](https://github.com/axeptio/sample-app-android/commit/09be4136bafe19f8c406bef6c559c368d46c657c))



