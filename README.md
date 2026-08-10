<img src="https://github.com/user-attachments/assets/c4c2d3a6-52a1-4515-b27f-4041af19fcf6" width="600" height="300"/>

# Axeptio Android SDK Documentation

> **Aligned with Axeptio Android SDK `2.4.0`.** This repository ships a single module — `samplekotlin` (Kotlin + Jetpack Compose). The `samplejava/` module was removed when the SDK dropped Java language support in `2.2.0`; see [Migrating from Java](#migrating-from-java).

[![License](https://img.shields.io/badge/license-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0) [![PRs Welcome](https://img.shields.io/badge/PRs-welcome-brightgreen.svg)](https://github.com/axeptio/sample-app-android/pulls)  [![Axeptio SDK Version](https://img.shields.io/github/v/release/axeptio/axeptio-android-sdk)](https://github.com/axeptio/axeptio-android-sdk/releases) [![Kotlin Integration](https://img.shields.io/badge/Integration-Kotlin%20%26%20Compose-blue)](https://github.com/axeptio/sample-app-android/tree/master/samplekotlin) [![Android SDK Compatibility](https://img.shields.io/badge/Android%20SDK-%3E%3D%2026-blue)](https://developer.android.com/studio)
 





Welcome to the Axeptio Mobile SDK Samples project! This repository demonstrates how to implement the **Axeptio Android SDK** in your mobile applications.
## Table of Contents
1. [Overview](#overview)
2. [Getting Started](#getting-started)
3. [Local Testing with Production Widget Configuration](#local-testing-with-production-widget-configuration)
4. [Switching Between Publisher and Brand Flavors](#switching-between-publisher-and-brand-flavors)
5. [Axeptio SDK Implementation](#axeptio-sdk-implementation)
6. [Initialize the SDK](#initialize-the-sdk)
7. [Responsibilities: Mobile App vs SDK](#responsibilities-mobile-app-vs-sdk)
8. [Get Stored Consents](#get-stored-consents)
9. [Show Consent Popup on Demand](#show-consent-popup-on-demand)
10. [Popup Events](#popup-events)
11. [Event source for KPI tracking](#event-source-for-kpi-tracking)
12. [Sharing Consents with Other Web Views](#sharing-consents-with-other-web-views)
13. [Clear User's Consent Choices](#clear-users-consent-choices)
14. [Google Consent v2](#google-consent-v2)
15. [Codeless Consent Forwarding to Attribution Partners](#codeless-consent-forwarding-to-attribution-partners)
16. [Silent CMP Restoration](#silent-cmp-restoration)
17. [Consent Expiry and Popup Control](#consent-expiry-and-popup-control)
18. [TCF Vendor Management APIs](#tcf-vendor-management-apis)
19. [Migrating from Java](#migrating-from-java)


<br><br>

## Overview
The repository ships a single module — `samplekotlin` — which demonstrates the Axeptio Android SDK `2.4.0` with Kotlin and Jetpack Compose. It can be built using either the **brands** or **publishers** variants.

### Sample App Features
The `samplekotlin` module includes additional debugging and testing capabilities:
- **Configuration Management**: Dynamic switching between Brands and Publishers TCF services, plus the `setForceShowConsentDebug()` and `setDisplayPopUpOnEnterForeground()` toggles
- **Debug Consent Info**: Detailed analysis of TCF consent data, vendor information, and the remaining days before consent expires
- **Vendor Consent Testing**: Live testing interface for individual vendor consent validation
- **AxeptioStore Demo**: Reactive `StateFlow` consent state in Compose, including the SDK error channel and the `onCMPRestored()` silent-restoration callback
- **Automation Scripts**: Complete build, deploy, and testing automation

> **⚠️ Note**: The `ConfigurationManager` class is part of the sample application and is not included in the Axeptio SDK. It demonstrates how to implement dynamic configuration switching in your own applications.

<br><br><br>

## Getting Started
To begin testing the Axeptio SDK sample applications, follow these steps:

##### Clone the repository

First, clone the repository to your local development environment:
```bash
git clone https://github.com/axeptio/sample-app-android
```
##### Configure your Github access token
> 🛡️ Maven requires authentication to access private repositories such as GitHub Packages.
> Without valid credentials (GitHub username and token), Gradle will not be able to download dependencies and will return a 401 Unauthorized error.
> The following steps explain how to create a Personal Access Token and configure Gradle to use it securely via environment variables.

To properly configure access to the Axeptio SDK, you need to add your GitHub token in the `settings.gradle.kts` file to fetch the SDK from the private repository. The library is not available on a public Maven repository, so it is crucial to configure the private repository correctly to avoid errors. You can also consider publishing the Axeptio SDK to a public repository to simplify integration, reducing the process complexity. Here’s how to configure the private repository in the `settings.gradle.kts` file:
```kotlin
maven {
    url = uri("https://maven.pkg.github.com/axeptio/axeptio-android-sdk")
    credentials {
        username = "[GITHUB_USERNAME]"  // Enter your GitHub username
        password = "[GITHUB_TOKEN]"    // Enter your GitHub token
    }
}
```

You can avoid hardcoding credentials by using environment variables instead of directly writing your GitHub username and token in the file. This is more secure and avoids leaking sensitive information.
To do this, replace the static strings with calls to environment variables using `System.getenv()` as follows:
```kotlin
credentials {
    username = System.getenv("GITHUB_USERNAME")
    password = System.getenv("GITHUB_TOKEN")
}
```
If you haven't already created a GitHub Personal Access Token (PAT), you can do so by:
1. Going [here](https://github.com/settings/tokens)
2. Clicking on "Generate new token (classic)"
3. Giving it a name and expiration
4. Selecting the `read:packages` scope
5. Generating and copying the token (you will not be able to see it again)

Once you have the token, export it as environment variables
- On macOS/Linux (e.g., in `.bashrc`, `.zshrc`, or shell session):
  ```bash
  export GITHUB_USERNAME="your-github-username"
  export GITHUB_TOKEN="your-personal-access-token"
  ```
- On Windows (CMD):
  ```cmd
  setx GITHUB_USERNAME "your-github-username"
  setx GITHUB_TOKEN "your-personal-access-token"
  ```
After doing this, Gradle will automatically pick them up when resolving dependencies.

##### Configure Fake Google App information

For local testing without real Google services

```bash
export GOOGLE_PROJECT="demo-project-12345"
export GOOGLE_API_KEY="AIzaSyDemoKey1234567890abcdefghijklmnop"
```

and then run 

```bash
./generate-config.sh
```


##### Ensure Proper Configuration in Axeptio Backoffice
Before proceeding with the integration, ensure that your project is correctly configured in the Axeptio backoffice. Specifically, verify that your clientId and configurationId are set up correctly. This is critical for the SDK to function as expected. If these values are not correctly configured, the SDK will not initialize properly, leading to errors during integration.

##### Select the appropriate sample module
Choose the module corresponding to your preferred programming language and UI framework:

- **samplekotlin**: Kotlin and Compose integration (the only module — Java support was dropped in SDK `2.2.0`)

##### Choose your build variant:
Depending on your use case, select the appropriate build variant:

- **publishers**
- **brands**

<br><br><br>
## Local Testing with Production Widget Configuration
To test SDK changes using a cookie configuration from the production backoffice, follow these steps:
1. Checkout the `axeptio-android-sdk-sources` repository and switch to the branch you need to test.
2. Configure the widget as described in the configuration section.

To test the version currently in production, instead checkout the `sample-app-android repository`, configure the widget, and in `build.gradle.kts` set the desired SDK version, for example: 
```gradle
implementation("io.axept.android:android-sdk:2.4.0")
```
To configure the widget, update the productFlavors in `build.gradle.kts` with the appropriate `AXEPTIO_CLIENT_ID`, `AXEPTIO_COOKIES_VERSION`, and `AXEPTIO_TARGET_SERVICE`. Example:
```kotlin
productFlavors {
    create("publishers") {
        dimension = "service"
        buildConfigField("String", "AXEPTIO_CLIENT_ID", "\"67b63ac7d81d22bf09c09e52\"")
        buildConfigField("String", "AXEPTIO_COOKIES_VERSION", "\"tcf-consent-mode\"")
        buildConfigField("String", "AXEPTIO_TARGET_SERVICE", "\"publishers\"")
    }
    create("brands") {
        dimension = "service"
        buildConfigField("String", "AXEPTIO_CLIENT_ID", "\"67f3f816b336596c4a7c741c\"")
        buildConfigField("String", "AXEPTIO_COOKIES_VERSION", "\"demo-en-EU\"")
        buildConfigField("String", "AXEPTIO_TARGET_SERVICE", "\"brands\"")
    }
}
```
Use the *Build Variants* tab to switch between brands and publishers as needed. Finally, make sure your `settings.gradle.kts` includes your GitHub credentials for accessing the SDK:
```kotlin
maven {
    url = uri("https://maven.pkg.github.com/axeptio/axeptio-android-sdk")
    credentials {
        username = "USER" // TODO: GITHUB USERNAME
        password = "TOKEN" // TODO: GITHUB TOKEN
    }
}
```

<br><br><br>
## Switching Between Publisher and Brand Flavors
The Axeptio SDK provides two build flavors: `publishers` and `brands`. You can switch between them depending on your project needs. Each flavor activates specific behavior in the SDK.
#### In Android Studio:
1. Locate the *"Build Variants"* tab (usually in the lower-left corner of the IDE).
2. If it's not visible, go to *View > Tool Windows > Build Variants* to enable it.
3. In the Module column, select either `publishersDebug` or `brandsDebug` from the dropdown.

Use Gradle commands to build a specific variant:
```gradle
./gradlew assemblePublishersDebug
```
or
```gradle
./gradlew assembleBrandsDebug
```
Make sure to clean the project if you switch flavors often:
```bash
./gradlew clean
```


<br><br><br>
## Axeptio SDK Implementation
The Axeptio SDK provides consent management functionality for Android applications, enabling seamless integration for handling user consent.

##### Gradle Implementation
The SDK is hosted on GitHub Packages and is compatible with Android SDK versions **>= 26**.

Follow these steps to integrate the Axeptio SDK into your Android project:
- **Add the Maven repository to your `settings.gradle` file**

   Ensure the provided GitHub token has the `read:packages` scope enabled. Add the following configuration to your `settings.gradle` file.
 - **Kotlin DSL**
```kotlin
// Start dependency resolution management block
dependencyResolutionManagement {
    repositories {
        // Add Google's Maven repository to the project
        google()
        
        // Add Maven Central repository
        mavenCentral()
        
        // Add the GitHub Packages repository for the Axeptio SDK
        maven {
            // Set the URL of the GitHub repository hosting the Axeptio SDK
            url = uri("https://maven.pkg.github.com/axeptio/axeptio-android-sdk")
            
            // Configure credentials for accessing the GitHub Packages repository
            credentials {
                // Provide your GitHub username here
                username = System.getenv("GITHUB_ACTOR")
                
                // Provide your GitHub token here, ensuring the 'read:packages' scope is enabled
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }
}
```
 - **Groovy**
```groovy
repositories {
    maven {
        url = uri("https://maven.pkg.github.com/axeptio/axeptio-android-sdk")
        credentials {
            username = "[GITHUB_USERNAME]"
            password = "[GITHUB_TOKEN]"
        }
    }
}
```
- **Add the SDK dependency to your `build.gradle` file**
After adding the repository, include the Axeptio SDK as a dependency in your project.
 - **Kotlin DSL**
```kotlin
dependencies {  
    implementation("io.axept.android:android-sdk:2.4.0")
}
```
 - **Groovy**
```groovy
dependencies {
    implementation 'io.axept.android:android-sdk:2.4.0'
}
```
For more detailed instructions, refer to the [GitHub Documentation](https://docs.github.com/en/packages/working-with-a-github-packages-registry/working-with-the-gradle-registry#using-a-published-package)

<br><br><br>

## Initialize the SDK
To initialize the Axeptio SDK, you must call the initialization method inside the `onCreate()` method of your main activity. This call should be made before invoking any other Axeptio SDK functions. The SDK can be configured for either **Publishers** or **Brands** using the `AxeptioService` enum during initialization.
##### Kotlin Implementation
```kotlin
override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    // Minimal initialization — every other parameter has a default.
    AxeptioSDK.instance().initialize(
        activity = this@MainActivity,  // Context of the current activity
        targetService = AxeptioService.PUBLISHERS_TCF,  // Choose the target service: Publishers or Brands
        clientId = "your_client_id",  // Replace with your actual client ID
        cookiesVersion = "your_cookies_version",  // Specify the version of cookies management
        token = "optional_consent_token"  // Optional: Provide an existing consent token if available
    )
}
```

##### Full signature
```kotlin
AxeptioSDK.instance().initialize(
    activity = this@MainActivity,
    targetService = AxeptioService.PUBLISHERS_TCF,
    clientId = "your_client_id",
    cookiesVersion = "your_cookies_version",
    token = null,                            // Optional existing consent token
    widgetType = WidgetType.PRODUCTION,      // Widget environment to load
    prId = null,                             // PR/testing ID for non-production widget configurations
    consentExpirationDays = 190,             // Days before the stored consent expires
    shouldUpdateConsentExpiration = false,   // Apply a changed expiry to already-stored consent
)
```

| Parameter | Default | Notes |
|---|---|---|
| `token` | `null` | Transfers an existing user consent (Publishers). |
| `widgetType` | `WidgetType.PRODUCTION` | Selects the widget environment. |
| `prId` | `null` | Used with non-production `widgetType` values to load a specific widget build. |
| `consentExpirationDays` | `190` | Called `cookiesDurationDays` on iOS. |
| `shouldUpdateConsentExpiration` | `false` | Called `shouldUpdateCookiesDuration` on iOS. |

> **`targetService` is fixed for the process lifetime.** Calling `initialize()` again with a *different* `targetService` is ignored — the SDK logs a warning and fires `onError(...)`, because its internal repositories are bound to the first service. Restart the app to switch services (this is what the sample's Configuration screen expects).

##### Consent Popup Behavior
Once the SDK is initialized, the consent popup will automatically display if the user's consent is either expired or has not yet been registered. The SDK takes care of managing the consent state automatically.

##### Transferring User Consents (Publishers)
For publishers, you can transfer a user's consent information by providing their Axeptio token. This token allows the SDK to automatically update the user's consent preferences in the SharedPreferences, following the TCFv2 (Transparency and Consent Framework) IAB (Interactive Advertising Bureau) specifications.


##### Handling the "INSTALL_FAILED_INVALID_APK" Error
This error can occur during installation, typically due to issues with the APK or dependencies. The best solution is to perform a **clean build** to ensure that all libraries are properly integrated. To do so, execute the following command in your terminal:
```bash
./gradlew clean build
```
This will clean the project and rebuild it, resolving any issues related to corrupted or improperly linked files. After completing the build, try reinstalling the app.

###### Key Consideration 
- **Client ID** and **Configuration ID** should be properly configured according to your specific project setup.
- The **Axeptio token** is optional, but it allows for better management of user consent states across different sessions.
- Always ensure that you check for SDK initialization before calling `initialize()` to prevent multiple initializations that could cause crashes.

The integration of the Axeptio SDK into your mobile application involves clear delineation of responsibilities between the mobile app and the SDK itself. Below are the distinct roles for each in handling user consent and tracking.
<br><br><br>
## Responsibilities: Mobile App vs SDK
#### **Mobile Application Responsibilities:**

- **Event Handling and User Consent Updates:**
   - The app is responsible for handling SDK events such as user consent actions. Based on these events, the app must adjust its behavior accordingly, ensuring that user consent is respected across sessions.

#### **Axeptio SDK Responsibilities:**

1. **Displaying the Consent Management Interface:**
   - The Axeptio SDK is responsible for rendering the user interface for the consent management platform (CMP) once triggered. It provides a customizable interface for users to give or revoke consent.

2. **Storing and Managing User Consent Choices:**
   - The SDK securely stores and manages user consent choices, maintaining a persistent record that can be referenced throughout the app's lifecycle.

3. **Sending Consent Status via APIs:**
   - The SDK facilitates communication of the user's consent status through APIs, allowing the app to be updated with the user’s preferences.

<br><br><br>

## Get Stored Consents
You can retrieve the consents stored by the Axeptio SDK in **SharedPreferences**. The following example demonstrates how to access these values within your app:
- **Kotlin Examples**
```kotlin
// Access SharedPreferences to retrieve stored consent values
val sharedPref = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

// Retrieve a specific consent value by key (replace "key" with the actual key you're using)
val consentValue = sharedPref.getString("key", "default_value")
```
In this example, replace `key` with the actual key used to store consent information, and `default_value` with the value you want to return if no consent is found.
For more detailed information about the stored values, cookies, and how to handle them according to the Axeptio SDK, please refer to the [Axeptio Documentation](https://support.axeptio.eu/hc/en-gb/articles/8558526367249-Does-Axeptio-deposit-cookies)
<br><br><br>
## Show Consent Popup on Demand
You can trigger the consent popup to open on demand at any point in your application by using the following methods.

```kotlin
// Show the consent popup on demand
AxeptioSDK.instance().showConsentScreen(
    activity = activity,  // Pass the activity context
    managePreferencesUseCase = true  // Optional: Manages user preferences when the popup is shown
)
```
<br><br><br>
## Popup Events
Register an `AxeptioEventListener` to receive SDK events. Every callback has an empty default implementation, so override only the ones you need. Callbacks are delivered on the main thread.

```kotlin
AxeptioSDK.instance().setEventListener(object : AxeptioEventListener {
    override fun onPopupClosedEvent() {
        // The consent popup has been closed by the user.
    }

    override fun onGoogleConsentModeUpdate(consentMap: Map<GoogleConsentType, GoogleConsentStatus>) {
        // Google Consent Mode v2 signals changed. See "Google Consent v2".
    }

    override fun onCMPRestored() {
        // Existing, still-valid consent was restored without showing a popup (SDK 2.4.0+).
        // See "Silent CMP Restoration".
    }

    override fun onConsentCleared() {
        // clearConsents() finished removing consent from SharedPreferences.
    }

    override fun onError(message: String) {
        // SDK-level error notification (SDK 2.2.0+).
    }
})
```

Use `removeEventListener(listener)` to unregister. More than one listener can be attached at a time — the sample's `AxeptioStore` demo relies on that.

##### Reactive consumption with `AxeptioStore` (Compose)
`AxeptioStore` implements `AxeptioEventListener` and re-exposes the events as `StateFlow`s for Jetpack Compose. It does not register itself, which keeps the lifecycle explicit:

```kotlin
val store = remember { AxeptioStore() }

DisposableEffect(store) {
    AxeptioSDK.instance().setEventListener(store)
    onDispose { AxeptioSDK.instance().removeEventListener(store) }
}

val googleConsent by store.googleConsent.collectAsState()
val error by store.error.collectAsState()
```
It exposes `googleConsent`, `popupClosedEventCount`, `consentClearedEventCount`, `error` and `clearError()`. See [`AxeptioStoreDemoScreen.kt`](samplekotlin/src/main/java/io/axept/samplekotlin/screen/AxeptioStoreDemoScreen.kt).
<br><br><br>
## Event source for KPI tracking
To ensure proper KPI attribution in the back office, the App SDK now adds a specific `event_source` value when emitting TCF events from the WebView.

- `sdk-app-tcf` → Used when TCF is loaded in a mobile app (via WebView)
- `sdk-web-tcf` → Used when TCF is loaded on a website
- `sdk-app-brands` → Used when the brands widget is loaded in an app
- `sdk-web` → Used for regular brands on the web

> ⚠️ This change ensures that events triggered from the App SDK are not incorrectly counted under Web KPIs.

No additional configuration is needed on your side if you are using the official SDK integration.



<br><br><br>
## Sharing Consents with Other Web Views
This feature is available exclusively for **Publishers** service.

The SDK provides a helper function to append the `axeptio_token` query parameter to any URL. You can either specify a custom user token or use the token currently stored in the SDK.
```kotlin
// Append the Axeptio token to a URL
AxeptioSDK.instance().appendAxeptioToken(
    uri = Uri.parse("https://myurl.com"),  // The URL to which you want to append the token
    token = AxeptioSDK.instance().token ?: ""  // Use the current token, or provide a custom one
)
```
This will return: `https://myurl.com?axeptio_token=[token]`

<br><br><br>
## Clear User's Consent Choices
To clear the user’s consent choices, you can use the following method. Please note that this operation is asynchronous, so you should use the `AxeptioEventListener.onConsentCleared()` method to be notified when the user’s consent choices have been cleared from SharedPreferences.
```kotlin
// Clear the user's consent choices
AxeptioSDK.instance().clearConsents()
```
You can listen for the consent clearance event with the following code:
```kotlin
// Set an event listener for when the consents are cleared
AxeptioSDK.instance().setEventListener(object : AxeptioEventListener {
    override fun onConsentCleared() {
        // Handle the event when consents are cleared
    }
})
```
<br><br><br>
## Google Consent v2
This section describes how **Google Consent Mode** works with the Axeptio SDK in your Android application.

###### Prerequisites:
Ensure that **Firebase Analytics** is integrated into your Android project.

###### How It Works:
When user consent is collected through your **Consent Management Platform (CMP)**, the SDK sets the `IABTCF_EnableAdvertiserConsentMode` key in **SharedPreferences** to `true` and derives the four Google Consent Mode v2 signals (`ANALYTICS_STORAGE`, `AD_STORAGE`, `AD_USER_DATA`, `AD_PERSONALIZATION`).

##### Firebase is updated automatically (SDK 2.3.0+)
From SDK `2.3.0`, **you no longer need to write any mapping code for Firebase Analytics**. The SDK detects Firebase at runtime and applies the consent signals itself — see [Codeless Consent Forwarding to Attribution Partners](#codeless-consent-forwarding-to-attribution-partners). The last known consent is also replayed at initialization, so the signal reaches Firebase before your app starts it on a warm launch.

##### Register to Google Consent Updates (manual / other destinations)
The SDK still exposes `onGoogleConsentModeUpdate()` so you can react to consent changes yourself — to feed a destination the SDK doesn't know about, or to keep an explicit mapping under your own control. The example below is the reference Firebase mapping; it is **optional** from `2.3.0` and applying it alongside the codeless path is harmless, since `setConsent()` is idempotent.

###### Kotlin Example:
```kotlin
// Set an event listener to listen for Google Consent Mode updates
AxeptioSDK.instance().setEventListener(object : AxeptioEventListener {
    override fun onGoogleConsentModeUpdate(consentMap: Map<GoogleConsentType, GoogleConsentStatus>) {
        // Map the Google consent types and statuses to Firebase consent types
        val firebaseConsentMap = consentMap.entries.associate { (type, status) ->
            val firebaseConsentType = when (type) {
                GoogleConsentType.ANALYTICS_STORAGE -> ConsentType.ANALYTICS_STORAGE
                GoogleConsentType.AD_STORAGE -> ConsentType.AD_STORAGE
                GoogleConsentType.AD_USER_DATA -> ConsentType.AD_USER_DATA
                GoogleConsentType.AD_PERSONALIZATION -> ConsentType.AD_PERSONALIZATION
            }

            val firebaseConsentStatus = when (status) {
                GoogleConsentStatus.GRANTED -> ConsentStatus.GRANTED
                GoogleConsentStatus.DENIED -> ConsentStatus.DENIED
            }

            firebaseConsentType to firebaseConsentStatus
        }

        // Update Firebase Analytics consent with the mapped consent statuses
        Firebase.analytics.setConsent(firebaseConsentMap)
    }
})
```

##### Summary of Steps:
1. Integrate **Firebase Analytics** into your Android project.
2. Initialize the Axeptio SDK — Firebase consent is then kept in sync automatically.
3. *(Optional)* Register `onGoogleConsentModeUpdate()` if you need to mirror the signals to another destination or keep an explicit mapping.

<br><br><br>

## Codeless Consent Forwarding to Attribution Partners
*Introduced in SDK `2.3.0`.*

The SDK forwards Google Consent Mode v2 / DMA signals to your analytics and **App Attribution Partner (AAP)** SDKs without any integration code on your side:

| Partner | What it receives |
|---|---|
| **Firebase Analytics** | `setConsent()` with the four Consent Mode v2 types |
| **AppsFlyer** | `consent_data` — `gdpr_applies`, `ad_user_data_enabled`, `ad_personalization_enabled` |
| **Adjust** | `google_dma` third-party-sharing options — `eea`, `ad_personalization`, `ad_user_data` |
| **Singular** | `limitDataSharing(...)` — `false` only when `ad_user_data` **and** `ad_personalization` are both granted |

##### How it works
- Forwarding is set up **inside `initialize()`** — there is no method to call and nothing to configure.
- Each partner is detected **at runtime by reflection**. If a partner SDK isn't on your classpath, forwarding to it is a **silent no-op**; you only pay for the partners you actually ship.
- Your only responsibility is to **initialize the partner SDK itself** (e.g. `AppsFlyerLib.getInstance().start(...)`). Do not write consent-forwarding code — the SDK does it.
- The last persisted consent is **replayed at initialization**, so the signal lands even on a warm launch where the user never sees a popup.
- `clearConsents()` also clears the persisted Google consent.

> **⚠️ Note on this sample**: `samplekotlin` bundles **Firebase Analytics only**, so Firebase forwarding is the path you can observe here. AppsFlyer, Adjust and Singular are supported by the SDK exactly as described above — add the partner SDK to your own app and forwarding starts working with no further code.

##### Verifying the forwarding
For Firebase, enable verbose logging and watch logcat:
```bash
adb shell setprop log.tag.FA VERBOSE
adb shell setprop log.tag.FA-SVC VERBOSE
adb logcat -s FA FA-SVC
```
Accept or refuse consent in the Axeptio popup and look for `Setting consent, package, consent: … ad_storage=granted/denied, analytics_storage=…` followed by `Setting DMA consent … ad_user_data=…`. These `setprop` flags reset on device reboot.

<br><br><br>

## Silent CMP Restoration
*Introduced in SDK `2.4.0`.*

`onCMPRestored()` fires when the SDK silently restores existing, still-valid consent — that is, consent was already stored and **no popup was shown**.

```kotlin
AxeptioSDK.instance().setEventListener(object : AxeptioEventListener {
    override fun onCMPRestored() {
        // Stored consent has been restored; getVendorConsents() is populated by now.
    }
})
```

Behaviour worth knowing before you rely on it:

- It fires **once per restoration**, and the SDK restores **more than once per session** — on `initialize()`, when the app returns to the foreground, and after network connectivity is restored. Expect it repeatedly over an app's lifetime, not just at startup. Make your handler idempotent.
- When restoration succeeds, the consent is readable via `getVendorConsents()` by the time the callback fires, and the matching Google Consent Mode signals are delivered through `onGoogleConsentModeUpdate()` during the same restoration.
- It is **not** called when the consent popup is displayed — use `onPopupClosedEvent()` for that case — nor after `clearConsents()` invalidates an in-flight restoration.

See [`AxeptioStoreDemoScreen.kt`](samplekotlin/src/main/java/io/axept/samplekotlin/screen/AxeptioStoreDemoScreen.kt) for a live counter. Note that `AxeptioStore` does not currently expose a `StateFlow` for this callback, so the sample observes it with a plain `AxeptioEventListener` registered alongside the store — several listeners can be attached at once.

<br><br><br>

## Consent Expiry and Popup Control

##### How long is the current consent valid?
```kotlin
val daysLeft = AxeptioSDK.instance().getRemainingDaysForConsent()
// 0 when no consent is stored, or when it has already expired.
```
The expiry window itself is set at initialization via `consentExpirationDays` (default `190`), with `shouldUpdateConsentExpiration` controlling whether an existing consent adopts a changed value.

##### Force the popup during development
```kotlin
// Always display the consent screen on showConsentScreen(), ignoring cached consent.
AxeptioSDK.instance().setForceShowConsentDebug(true)
```
Intended for development and QA builds only.

##### Control the foreground popup
```kotlin
// SDK default is true. Set to false to suppress auto-display when the app
// returns to the foreground and drive the presentation timing yourself.
AxeptioSDK.instance().setDisplayPopUpOnEnterForeground(false)
```

Both toggles are wired to switches on the sample's **Configuration** screen.

> **Deprecated**: `clearConsent()` is a cross-platform-naming alias kept for iOS parity. Use `clearConsents()`.

<br><br><br>

## TCF Vendor Management APIs
The Axeptio SDK provides comprehensive APIs for managing vendor consent in TCF (Transparency and Consent Framework) mode. These APIs are **exclusively available for Publishers using the TCF service** and allow you to query individual vendor consent states, implement vendor-specific functionality, and maintain compliance with IAB TCF requirements.

#### When to Use TCF Vendor APIs
Use these APIs when your app needs to:
- Query consent status for specific advertising vendors
- Implement vendor-specific data processing logic
- Debug consent collection issues in TCF mode
- Ensure compliance with specific vendor requirements

### Available TCF Vendor APIs
These APIs allow you to retrieve and analyze vendor consent information in TCF mode:

##### Get All Vendor Consents
Returns a map of all vendor IDs with their consent status:
```kotlin
try {
    val vendorConsents: Map<Int, Boolean> = AxeptioSDK.instance().getVendorConsents()
    // vendorConsents contains: {755: true, 756: false, 757: true, ...}
    
    vendorConsents.forEach { (vendorId, isConsented) ->
        println("Vendor $vendorId: ${if (isConsented) "CONSENTED" else "REFUSED"}")
    }
} catch (e: Exception) {
    // Handle potential errors (e.g., SDK not initialized, no consent data)
    Log.e("VendorConsents", "Error retrieving vendor consents: ${e.message}")
}
```

##### Get Consented Vendors List
Returns only the vendor IDs that have been consented to:
```kotlin
try {
    val consentedVendors: List<Int> = AxeptioSDK.instance().getConsentedVendors()
    // consentedVendors contains: [755, 757, 760, ...]
    
    println("${consentedVendors.size} vendors have been consented to")
    consentedVendors.forEach { vendorId ->
        println("✅ Vendor $vendorId is consented")
    }
} catch (e: Exception) {
    Log.e("ConsentedVendors", "Error retrieving consented vendors: ${e.message}")
}
```

##### Get Refused Vendors List
Returns only the vendor IDs that have been refused:
```kotlin
try {
    val refusedVendors: List<Int> = AxeptioSDK.instance().getRefusedVendors()
    // refusedVendors contains: [756, 758, 759, ...]
    
    println("${refusedVendors.size} vendors have been refused")
    refusedVendors.forEach { vendorId ->
        println("❌ Vendor $vendorId is refused")
    }
} catch (e: Exception) {
    Log.e("RefusedVendors", "Error retrieving refused vendors: ${e.message}")
}
```

##### Check Individual Vendor Consent
Check if a specific vendor has consent:
```kotlin
try {
    val vendorId = 755 // Example vendor ID
    val isConsented: Boolean = AxeptioSDK.instance().isVendorConsented(vendorId)
    
    if (isConsented) {
        println("✅ Vendor $vendorId has consent - proceed with data processing")
        // Safe to process data with this vendor
    } else {
        println("❌ Vendor $vendorId does not have consent - skip data processing")
        // Do not process data with this vendor
    }
} catch (e: Exception) {
    Log.e("VendorCheck", "Error checking vendor $vendorId: ${e.message}")
    // Assume no consent on error for safety
}
```

### Debug and Analysis APIs
For debugging and detailed consent analysis:

##### Get Consent Debug Information
Returns detailed consent information including TCF strings and raw data:
```kotlin
try {
    val debugInfo: Map<String, Any?> = AxeptioSDK.instance().getConsentDebugInfo()
    
    // Access common TCF fields
    val tcfString = debugInfo["IABTCF_TCString"] as? String
    val vendorConsents = debugInfo["IABTCF_VendorConsents"] as? String
    val vendorLegitimateInterests = debugInfo["IABTCF_VendorLegitimateInterests"] as? String
    
    println("TCF String: $tcfString")
    println("Vendor Consents Bitstring: $vendorConsents")
    println("Vendor Legitimate Interests: $vendorLegitimateInterests")
    
    // Log all available debug fields
    debugInfo.forEach { (key, value) ->
        println("$key: $value")
    }
} catch (e: Exception) {
    Log.e("DebugInfo", "Error retrieving debug information: ${e.message}")
}
```

### Best Practices for Vendor API Usage

1. **Error Handling**: Always wrap API calls in try-catch blocks as these APIs may throw exceptions if the SDK is not properly initialized or if no consent data is available.

2. **TCF Service Requirement**: These vendor APIs are **exclusively for Publishers using the TCF service**. When using the **Brands** service, these APIs will return empty results as vendor consent management is specific to the TCF framework.

3. **Performance Considerations**: Cache vendor consent results when possible, as parsing TCF data can be computationally intensive for large vendor lists.

4. **Data Processing Logic**: Use `isVendorConsented()` in your data processing pipeline to ensure compliance:
```kotlin
fun processUserData(vendorId: Int, userData: UserData) {
    if (AxeptioSDK.instance().isVendorConsented(vendorId)) {
        // Proceed with data processing
        sendDataToVendor(vendorId, userData)
    } else {
        // Skip processing for this vendor
        Log.d("Compliance", "Skipping vendor $vendorId - no consent")
    }
}
```

5. **Initialization Check**: Ensure the SDK is initialized before calling these APIs by checking that you have called the `initialize()` method successfully before attempting to retrieve vendor consent data.

> **⚠️ Important Note**: The TCF vendor consent APIs are available starting from SDK version 2.0.8 and are **exclusively for Publishers using the TCF service**. These APIs will return empty results when used with the Brands service, as vendor consent management is specific to the TCF framework.

<br><br><br>
For more detailed information, you can visit the [Axeptio documentation](https://support.axeptio.eu/hc/en-gb).
We hope this guide helps you get started with the Axeptio Android SDK. Good luck with your integration, and thank you for choosing Axeptio!

<br><br><br>

## Migrating from Java
SDK `2.2.0` made the Axeptio Android SDK **Kotlin-only**. The `samplejava/` module was removed from this repository at the same time.

What changed:
- `AxeptioSDK.instance()` and `AxeptioAPIRepository.instance()` no longer expose `@JvmStatic` companions.
- Java call sites compiled against `2.0.x` / `2.1.x` may hit `NoSuchMethodError` at runtime against a newer SDK — **recompile against the release you are using**.

Your options:
1. **Migrate the integration to Kotlin** (recommended). The Kotlin snippets throughout this README, and the `samplekotlin` module, are the reference integration.
2. **Keep calling from Java** by going through the singleton instance explicitly:
   ```java
   Axeptio axeptio = AxeptioSDK.INSTANCE.instance();
   ```
   Note that Kotlin default arguments are not available from Java, so every parameter of `initialize(...)` must be passed explicitly.
3. **Stay on SDK `2.1.x`**, which retains Java support. Note that it does not receive the fixes and features described in this document.
