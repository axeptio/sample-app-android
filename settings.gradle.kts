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
                username = "" // TODO: GITHUB USERNAME
                password = "" // TODO: GITHUB TOKEN
            }
        }
    }
}

rootProject.name = "sample-app-android"
include(":samplekotlin")
include(":samplejava")