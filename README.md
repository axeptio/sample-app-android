<h1>
  <img src="https://axeptio.imgix.net/2024/07/e444a7b2-ea3d-4471-a91c-6be23e0c3cbb.png" alt="Descrizione immagine" width="80" style="vertical-align: middle; margin-right: 10px;" />
  Axeptio Android SDK Documentation
</h1>

[![License](https://img.shields.io/badge/license-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0) [![PRs Welcome](https://img.shields.io/badge/PRs-welcome-brightgreen.svg)](https://github.com/axeptio/sample-app-android/pulls)  [![Axeptio SDK Version](https://img.shields.io/github/v/release/axeptio/axeptio-android-sdk)](https://github.com/axeptio/axeptio-android-sdk/releases) [![Java Integration](https://img.shields.io/badge/Integration-Java%20%26%20XML-blue)](https://github.com/axeptio/sample-app-android/tree/main/samplejava) [![Kotlin Integration](https://img.shields.io/badge/Integration-Kotlin%20%26%20Compose-blue)](https://github.com/axeptio/sample-app-android/tree/main/samplekotlin) [![Android SDK Compatibility](https://img.shields.io/badge/Android%20SDK-%3E%3D%2026-blue)](https://developer.android.com/studio)






Welcome to the Axeptio Mobile SDK Samples project! This repository demonstrates how to implement the **Axeptio Android SDK** in your mobile applications.
## 📑 Table of Contents
1. [Overview](#overview)
2. [Getting Started](#getting-started)
3. [Axeptio SDK Implementation](#axeptio-sdk-implementation)
4. [Initialize the SDK](#initialize-the-sdk)
5. [Responsibilities: Mobile App vs SDK](#responsibilities-mobile-app-vs-sdk)
6. [Get Stored Consents](#get-stored-consents)
7. [Show Consent Popup on Demand](#show-consent-popup-on-demand)
8. [Popup Events](#popup-events)
9. [Sharing Consents with Other Web Views](#sharing-consents-with-other-web-views)
10. [Clear User's Consent Choices](#clear-users-consent-choices)
11. [Google Consent v2](#google-consent-v2)


<br><br>

## 👨‍💻Overview
This project includes two modules:
- `samplejava`: Demonstrates how to use the Axeptio SDK with Java and XML.
- `samplekotlin`: Shows the integration of the Axeptio SDK with Kotlin and Compose.

Both modules can be built using either the **brands** or **publishers** variants, depending on your specific needs.

<br><br><br>

## Getting Started
To begin testing the Axeptio SDK sample applications, follow these steps:

##### Clone the repository

First, clone the repository to your local development environment:
```bash
git clone https://github.com/axeptio/sample-app-android
```
##### Configure your Github access token
To properly configure access to the Axeptio SDK, you need to add your GitHub token in the `settings.gradle.kts` file to fetch the SDK from the private repository. The library is not available on a public Maven repository, so it is crucial to configure the private repository correctly to avoid errors. You can also consider publishing the Axeptio SDK to a public repository to simplify integration, reducing the process complexity. Here’s how to configure the private repository in the `settings.gradle.kts` file:
```kotin
maven {
    url = uri("https://maven.pkg.github.com/axeptio/axeptio-android-sdk")
    credentials {
        username = "[GITHUB_USERNAME]"  // Enter your GitHub username
        password = "[GITHUB_TOKEN]"    // Enter your GitHub token
    }
}
```
##### Ensure Proper Configuration in Axeptio Backoffice
Before proceeding with the integration, ensure that your project is correctly configured in the Axeptio backoffice. Specifically, verify that your clientId and configurationId are set up correctly. This is critical for the SDK to function as expected. If these values are not correctly configured, the SDK will not initialize properly, leading to errors during integration.

##### Select the appropriate sample module
Choose the module corresponding to your preferred programming language and UI framework:

- **samplejava**: Java and XML integration
- **samplekotlin**: Kotlin and Compose integration

##### Choose your build variant:
Depending on your use case, select the appropriate build variant:

- **publishers**
- **brands**

<br><br><br>

## 💻Axeptio SDK Implementation
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
                username = "[GITHUB_USERNAME]"
                
                // Provide your GitHub token here, ensuring the 'read:packages' scope is enabled
                password = "[GITHUB_TOKEN]"
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
    implementation("io.axept.android:android-sdk:2.0.3")
}
```
 - **Groovy**
```groovy
dependencies {
    implementation 'io.axept.android:android-sdk:2.0.3'
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
    
    // Check if the SDK is already initialized to prevent multiple initializations
    if (!AxeptioSDK.instance().isInitialized()) {
        // Initialize the Axeptio SDK with the required configuration
        AxeptioSDK.instance().initialize(
            activity = this@MainActivity,  // Context of the current activity
            targetService = AxeptioService.PUBLISHERS_TCF,  // Choose the target service: Publishers or Brands
            clientId = "your_client_id",  // Replace with your actual client ID
            cookiesVersion = "your_cookies_version",  // Specify the version of cookies management
            token = "optional_consent_token"  // Optional: Provide an existing consent token if available
        )
    }
}
```
##### Java Implementation
```java
@Override
protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    
    // Check if the SDK is already initialized to prevent multiple initializations
    if (!AxeptioSDK.instance().isInitialized()) {
        // Initialize the Axeptio SDK with the required configuration
        Axeptio axeptio = AxeptioSDK.instance();
        axeptio.initialize(
            MainActivity.this,  // Context of the current activity
            AxeptioService.PUBLISHERS_TCF,  // Choose the target service: Publishers or Brands
            "your_project_id",  // Replace with your actual project ID
            "your_configuration_id",  // Provide your configuration ID
            "optional_consent_token"  // Optional: Provide an existing consent token if available
        );
    }
}
```
##### Consent Popup Behavior
Once the SDK is initialized, the consent popup will automatically display if the user's consent is either expired or has not yet been registered. The SDK takes care of managing the consent state automatically.

##### Transferring User Consents (Publishers)
For publishers, you can transfer a user's consent information by providing their Axeptio token. This token allows the SDK to automatically update the user's consent preferences in the SharedPreferences, following the TCFv2 (Transparency and Consent Framework) IAB (Interactive Advertising Bureau) specifications.

##### Preventing Multiple Initializations of the SDK
Calling `initialize()` multiple times during the same session can cause crashes. To avoid this, always check if the SDK has already been initialized before making the call:
- **Kotlin**
```kotlin
if (!AxeptioSDK.instance().isInitialized()) {
    AxeptioSDK.instance().initialize(
        activity = this@MainActivity,
        targetService = AxeptioService.PUBLISHERS_TCF,
        clientId = "your_client_id",
        cookiesVersion = "your_cookies_version",
        token = "optional_consent_token"
    )
}
```
- **Java**
```java
if (!AxeptioSDK.instance().isInitialized()) {
    AxeptioSDK.instance().initialize(
        MainActivity.this,
        AxeptioService.PUBLISHERS_TCF,
        "your_project_id",
        "your_configuration_id",
        "optional_consent_token"
    );
}
```
By checking the `isInitialized()` method, you ensure that the SDK is initialized only once, preventing potential issues caused by multiple initializations.

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

1. **Managing App Tracking Transparency (ATT) Flow:**
   - The mobile app is responsible for initiating and managing the ATT authorization process on iOS 14 and later. This includes presenting the ATT request prompt at an appropriate time in the app's lifecycle.

2. **Controlling the Display Sequence of ATT and CMP:**
   - The app must determine the appropriate sequence for displaying the ATT prompt and the Axeptio consent management platform (CMP). Specifically, the app should request ATT consent before invoking the Axeptio CMP.

3. **Compliance with App Store Privacy Labels:**
   - The app must ensure accurate and up-to-date declarations of data collection practices according to Apple’s privacy label requirements, ensuring full transparency to users about data usage.

4. **Event Handling and User Consent Updates:**
   - The app is responsible for handling SDK events such as user consent actions. Based on these events, the app must adjust its behavior accordingly, ensuring that user consent is respected across sessions.

#### **Axeptio SDK Responsibilities:**

1. **Displaying the Consent Management Interface:**
   - The Axeptio SDK is responsible for rendering the user interface for the consent management platform (CMP) once triggered. It provides a customizable interface for users to give or revoke consent.

2. **Storing and Managing User Consent Choices:**
   - The SDK securely stores and manages user consent choices, maintaining a persistent record that can be referenced throughout the app's lifecycle.

3. **Sending Consent Status via APIs:**
   - The SDK facilitates communication of the user's consent status through APIs, allowing the app to be updated with the user’s preferences.

4. **No Implicit Handling of ATT Permissions:**
   - The Axeptio SDK does **not** manage the App Tracking Transparency (ATT) permission flow. It is the host app's responsibility to request and handle ATT permissions explicitly before displaying the consent management interface. The SDK functions only once the ATT permission is granted (or bypassed due to platform restrictions).
<br><br><br>

## 🔑Get Stored Consents
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

- **Kotlin**:
```kotlin
// Show the consent popup on demand
AxeptioSDK.instance().showConsentScreen(
    activity = activity,  // Pass the activity context
    managePreferencesUseCase = true  // Optional: Manages user preferences when the popup is shown
)
```
-**Java**
```java
// Show the consent popup on demand
AxeptioSDK.instance().showConsentScreen(
    activity,  // Pass the activity context
    true  // Optional: Manages user preferences when the popup is shown
);
```
<br><br><br>
## Popup Events
When the consent popup is closed, an event is triggered. You can listen for this event by setting an `AxeptioEventListener`.
- **Kotlin**:
```kotlin
// Set an event listener for when the consent popup is closed
AxeptioSDK.instance().setEventListener(object : AxeptioEventListener {
    override fun onPopupClosedEvent() {
        // Handle the event when the popup is closed
    }
})
```
-**Java**
```java
// Set an event listener for when the consent popup is closed
AxeptioSDK.instance().setEventListener(new AxeptioEventListener() {
    @Override
    public void onPopupClosedEvent() {
        super.onPopupClosedEvent();
        // Handle the event when the popup is closed
    }
});
```
<br><br><br>
## Sharing Consents with Other Web Views
This feature is available exclusively for **Publishers** service.

The SDK provides a helper function to append the `axeptio_token` query parameter to any URL. You can either specify a custom user token or use the token currently stored in the SDK.
- **Kotlin**:
```kotlin
// Append the Axeptio token to a URL
AxeptioSDK.instance().appendAxeptioToken(
    uri = Uri.parse("https://myurl.com"),  // The URL to which you want to append the token
    token = AxeptioSDK.instance().token ?: ""  // Use the current token, or provide a custom one
)
```
This will return: `https://myurl.com?axeptio_token=[token]`

<br><br><br>
## 🧹Clear User's Consent Choices
To clear the user’s consent choices, you can use the following method. Please note that this operation is asynchronous, so you should use the `AxeptioEventListener.onConsentCleared()` method to be notified when the user’s consent choices have been cleared from SharedPreferences.
- **Kotlin**
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
This section describes how to integrate **Google Consent Mode** with the Axeptio SDK in your Android application.
###### Prerequisites:
Before proceeding, ensure that **Firebase Analytics** is integrated into your Android project.
###### How It Works:
When user consent is collected through your **Consent Management Platform (CMP)**, the SDK will automatically set the `IABTCF_EnableAdvertiserConsentMode` key in the **SharedPreferences_** to `true`.
##### Register to Google Consent Updates
The **Axeptio SDK** provides a callback method to listen for updates on Google Consent. These updates need to be mapped to the corresponding Firebase models. Once the consent statuses are mapped, you can update Firebase Analytics consent settings using the `setConsent()` method from **Firebase Analytics**.

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

###### Java Example:
```java
// Set an event listener to listen for Google Consent Mode updates
AxeptioSDK.instance().setEventListener(new AxeptioEventListener() {
    @Override
    public void onGoogleConsentModeUpdate(@NonNull Map<GoogleConsentType, ? extends GoogleConsentStatus> consentMap) {
        super.onGoogleConsentModeUpdate(consentMap);

        // Prepare the Firebase consent map
        Map<FirebaseAnalytics.ConsentType, FirebaseAnalytics.ConsentStatus> firebaseConsentMap = new HashMap<>();

        // Map the Google consent types and statuses to Firebase consent types
        for (Map.Entry<GoogleConsentType, ? extends GoogleConsentStatus> entry : consentMap.entrySet()) {
            FirebaseAnalytics.ConsentType firebaseConsentType = null;
            switch (entry.getKey()) {
                case ANALYTICS_STORAGE:
                    firebaseConsentType = FirebaseAnalytics.ConsentType.ANALYTICS_STORAGE;
                    break;
                case AD_STORAGE:
                    firebaseConsentType = FirebaseAnalytics.ConsentType.AD_STORAGE;
                    break;
                case AD_USER_DATA:
                    firebaseConsentType = FirebaseAnalytics.ConsentType.AD_USER_DATA;
                    break;
                case AD_PERSONALIZATION:
                    firebaseConsentType = FirebaseAnalytics.ConsentType.AD_PERSONALIZATION;
                    break;
            }

            FirebaseAnalytics.ConsentStatus firebaseConsentStatus = null;
            switch ((GoogleConsentStatus) entry.getValue()) {
                case GRANTED:
                    firebaseConsentStatus = FirebaseAnalytics.ConsentStatus.GRANTED;
                    break;
                case DENIED:
                    firebaseConsentStatus = FirebaseAnalytics.ConsentStatus.DENIED;
                    break;
            }

            // Add the consent status mapping to the Firebase consent map
            if (firebaseConsentType != null && firebaseConsentStatus != null) {
                firebaseConsentMap.put(firebaseConsentType, firebaseConsentStatus);
            }
        }

        // Update Firebase Analytics with the mapped consent statuses
        FirebaseAnalytics.getInstance(MainActivity.this).setConsent(firebaseConsentMap);
    }
});
```
##### Summary of Steps:
1. Integrate **Firebase Analytics** into your Android project.
2. Use the provided listener `onGoogleConsentModeUpdate()` to capture consent updates.
3. Map the **Google Consent Types** and **Google Consent Statuses** to **Firebase Consent Types-**.
4. Use Firebase’s `setConsent()` method to update the user’s consent status in Firebase Analytics.

By following these steps, you can ensure that Google Consent Mode is correctly integrated with your application, and Firebase Analytics receives the consent status updates accordingly.
<br><br><br>
For more detailed information, you can visit the [Axeptio documentation](https://support.axeptio.eu/hc/en-gb).
We hope this guide helps you get started with the Axeptio Android SDK. Good luck with your integration, and thank you for choosing Axeptio!