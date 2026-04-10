pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven {
            url = uri("https://maven.pkg.github.com/axeptio/tcf-android-sdk")
            credentials {
                username = System.getenv("GITHUB_USERNAME") ?: "" // TODO: GITHUB USERNAME
                password = System.getenv("GITHUB_TOKEN") ?: "" // TODO: GITHUB TOKEN
            }
        }
    }
}

rootProject.name = "sample-app-android"
include(":samplekotlin")

// Include local SDK sources for development (if available)
val localAndroidSdkDir = File("../axeptio-android-sdk-sources/android")
if (localAndroidSdkDir.exists()) {
    include(":android")
    project(":android").projectDir = localAndroidSdkDir
}