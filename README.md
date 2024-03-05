# Axeptio Mobile SDK Samples

Welcome to the Axeptio Mobile SDK Samples project! This repository demonstrates how to implement the Axeptio Android SDK in your mobile applications.

## Overview

The project consists of two modules:
- `samplejava`: Illustrates the usage of the Axeptio SDK with Java and XML.
- `samplekotlin`: Demonstrates the integration of the Axeptio SDK with Kotlin and Compose.

## Getting Started

To get started with implementing the Axeptio SDK in your Android app, follow these steps:

1. Clone this repository to your local machine:

   ```bash
   git clone https://github.com/yourusername/axeptio-mobile-sdk-samples.git

2. Provide your Github access token for the Axeptio SDK in the `settings.gradle.kts` project's file.
```
maven {  
	url = uri("https://maven.pkg.github.com/axeptio/tcf-android-sdk")  
	credentials {  
		username = "" // TODO: Username  
		password = "" // TODO: Read package token  
	}  
}
```

3.  Choose the appropriate sample module (samplejava or samplekotlin) based on your preferred language and UI framework.

## Additional Resources

For additional instructions and information about the Axeptio Mobile SDK implementation, please refer to the official documentation.