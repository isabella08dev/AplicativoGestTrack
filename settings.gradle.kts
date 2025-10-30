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
        maven { url = uri("https://jitpack.io") }
        maven { url = uri("https://maven.google.com") }
    }
}

// Bloco ESSENCIAL que estava faltando para habilitar o Version Catalog (`libs`)
// versionCatalogs {
    //create("libs") {
        //from(files("gradle/libs.versions.toml"))
  //  }
// }

rootProject.name = "GestTrack"
include(":app")
