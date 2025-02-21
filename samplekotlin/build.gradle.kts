plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
}

android {
    namespace = "io.axept.samplekotlin"
    compileSdk = 34

    defaultConfig {
        applicationId = "io.axept.samplekotlin"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {

        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
//            signingConfig = signingConfigs.getByName("debug")
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
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation("io.axept.android:android-sdk:2.0.4")

    implementation(platform("com.google.firebase:firebase-bom:32.7.2"))
    implementation("com.google.firebase:firebase-analytics")
    implementation ("com.google.android.gms:play-services-ads:22.6.0")

    implementation("androidx.preference:preference-ktx:1.2.1")

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.activity:activity-compose:1.8.2")

    implementation(platform("androidx.compose:compose-bom:2024.02.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    implementation("androidx.navigation:navigation-compose:2.7.7")
    implementation("com.google.accompanist:accompanist-webview:0.35.0-alpha")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}