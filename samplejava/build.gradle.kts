plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.davinciapp.samplejava"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.davinciapp.samplejava"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }

        android.buildFeatures.buildConfig = true
        flavorDimensions += listOf("service")
        productFlavors {
            create("publishers") {
                dimension = "service"
                buildConfigField("String", "AXEPTIO_CLIENT_ID", "\"5fbfa806a0787d3985c6ee5f\"")
                buildConfigField("String", "AXEPTIO_COOKIES_VERSION", "\"google cmp partner program sandbox-en-EU\"")
                buildConfigField("String", "AXEPTIO_TARGET_SERVICE", "\"publishers\"")
            }
            create("brands") {
                dimension = "service"
                buildConfigField("String", "AXEPTIO_CLIENT_ID", "\"5fbfa806a0787d3985c6ee5f\"")
                buildConfigField("String", "AXEPTIO_COOKIES_VERSION", "\"insideapp-brands\"")
                buildConfigField("String", "AXEPTIO_TARGET_SERVICE", "\"brands\"")
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation("io.axept.android:android-sdk:2.0.3")

    implementation(platform("com.google.firebase:firebase-bom:32.7.2"))
    implementation("com.google.firebase:firebase-analytics")
    implementation ("com.google.android.gms:play-services-ads:22.6.0")

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
}