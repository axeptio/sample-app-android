// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.2.1" apply false
    id("org.jetbrains.kotlin.android") version "2.1.0" apply false
    id("org.jetbrains.kotlin.plugin.compose") version "2.1.0" apply false
    id("com.android.library") version "8.2.1" apply false
    id("com.google.gms.google-services") version "4.4.1" apply false
    // OWASP dependency check removed - using Dependabot for vulnerability scanning
    // id("org.owasp.dependencycheck") version "8.4.3" apply false
}