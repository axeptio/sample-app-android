# Axeptio Mobile SDK Samples

Welcome to the Axeptio Mobile SDK Samples project! This repository demonstrates how to implement the Axeptio Android SDK in your mobile applications.

## Overview

The project consists of two modules:
- `samplejava`: Illustrates the usage of the Axeptio SDK with Java and XML.
- `samplekotlin`: Demonstrates the integration of the Axeptio SDK with Kotlin and Compose.

## Getting Started

To get started with implementing the Axeptio SDK in your Android app, follow these steps:

1. Clone this repository to your local machine:

   ```shell
   git clone https://github.com/axeptio/sample-app-android

2. Provide your Github access token for the Axeptio SDK in the `settings.gradle.kts` project's file.
```kts
maven {  
	url = uri("https://maven.pkg.github.com/axeptio/tcf-android-sdk")  
	credentials {  
		username = "" // TODO: Username  
		password = "" // TODO: Read package token  
	}  
}
```

3.  Choose the appropriate sample module (samplejava or samplekotlin) based on your preferred language and UI framework.

# Axeptio SDK implementation

**Axeptio** CMP android sdk.

## Gradle implementation
The SDK is hosted by Github Packages.
It supports Android SDK versions >= 26.

First, add the maven repository to your *settings.gradle* file.

**Kotlin DSL**
```kotlin
dependencyResolutionManagement {
    ...
    repositories {
        google()
        mavenCentral()
        maven {
            url = uri("https://maven.pkg.github.com/axeptio/tcf-android-sdk")
            credentials {
               username = "[GITHUB_USERNAME]"
               password = "[GITHUB_TOKEN]"
            }
        }
    }
}
```

**Groovy**
```groovy
repositories {
    maven {
        url = uri("https://maven.pkg.github.com/axeptio/tcf-android-sdk")
        credentials {
           username = "[GITHUB_USERNAME]"
           password = "[GITHUB_TOKEN]"
        }
   }
}
```
Then add the package dependency to your *build.gradle* file.

**Kotlin DSL**
```kotlin
dependencies {  
	implementation("io.axept.android:sdk:[latest_version]")
}
```
**Groovy**
```groovy
dependencies {
    implementation 'io.axept.android:sdk:[latest_version]'
}
```

For more details, you can refer to the [Github documentation](https://docs.github.com/en/packages/working-with-a-github-packages-registry/working-with-the-gradle-registry#using-a-published-package).

## Initialize the SDK
Initialize the SDK inside the onCreate() of your main activity

**Kotlin**
```kotlin
override fun onCreate(savedInstanceState: Bundle?) {  
	super.onCreate(savedInstanceState)  
  
	AxeptioSDK.instance().initialize(  
		activity = this@MainActivity,  
		projectId = [your_project_id],  
		configurationId = [your_configuration_id]  
	)  
  
}
```
**Java**
```java
@Override  
protected void onCreate(@Nullable Bundle savedInstanceState) {  
	super.onCreate(savedInstanceState);
  
	Axeptio axeptio = AxeptioSDK.instance();  
	axeptio.initialize(
		MainActivity.this,
		[your_project_id],  
		[your_configuration_id]
	);
  
}
```

The consent pop up will automatically open if the user's consents are expired or haven't been registered yet.

>By default, the user's consent choices expire after 6 months

The SDK will automatically update the user's SharedPreferences according to the TCFv2 [IAB documentation](https://github.com/InteractiveAdvertisingBureau/GDPR-Transparency-and-Consent-Framework/blob/master/TCFv2/IAB%20Tech%20Lab%20-%20CMP%20API%20v2.md#in-app-details).

## Show consent popup on demand

Additionally, you can request the consent popup to open on demand.

**Kotlin**
```kotlin
AxeptioSDK.instance().showConsentScreen(activity)
```
**Java**
```java
AxeptioSDK.instance().showConsentScreen(activity);
```

### Popup events

When closing, the consent popup will trigger an event which you can listen by setting an AxeptioEventListener.

**Kotlin**
```kotlin
AxeptioSDK.instance().setEventListener(object : AxeptioEventListener {  
	override fun onPopupClosedEvent() {  
		// Do something
	}  
})
```
**Java**
```java
AxeptioSDK.instance().setEventListener(new AxeptioEventListener() {
	@Override
	public void onPopupClosedEvent() {
		AxeptioEventListener.super.onPopupClosedEvent();
	}
});
```

## Google Consent v2

Instructions on how to integrate **Google Consent Mode** with the Axeptio SDK in your Android application.

> If you haven't already, add [Firebase Analytics](https://developers.google.com/tag-platform/security/guides/app-consent?hl=en&platform=android&consentmode=advanced#kotlin)  to your Android project.


### Register to Google Consent updates

Axeptio SDK provides a callback to listen to Google Consent updates.
You'll have to map the consent types and status to the corresponding Firebase models.
You can then update Firebase analytics consents by calling Firebase analytics' *setConsent()*.

**Kotlin**
```kotlin
AxeptioSDK.instance().setEventListener(object : AxeptioEventListener {  
	override fun onGoogleConsentModeUpdate(consentMap:Map<GoogleConsentType,GoogleConsentStatus>) {  
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
		Firebase.analytics.setConsent(firebaseConsentMap)  
	}  
})
```
**Java**
```java
        AxeptioSDK.instance().setEventListener(new AxeptioEventListener() {
            @Override
            public void onGoogleConsentModeUpdate(@NonNull Map<GoogleConsentType, ? extends GoogleConsentStatus> consentMap) {
                AxeptioEventListener.super.onGoogleConsentModeUpdate(consentMap);

                Map<FirebaseAnalytics.ConsentType, FirebaseAnalytics.ConsentStatus> firebaseConsentMap = new HashMap<>();

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

                    if (firebaseConsentType != null && firebaseConsentStatus != null) {
                        firebaseConsentMap.put(firebaseConsentType, firebaseConsentStatus);
                    }

                }

                FirebaseAnalytics.getInstance(MainActivity.this).setConsent(firebaseConsentMap);
            }
        });
```
