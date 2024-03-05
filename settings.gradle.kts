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
                username = "" // TODO: Username
                password = "" // TODO: Read package token
            }
        }
    }
}

rootProject.name = "sample-app-android"
include(":samplekotlin")