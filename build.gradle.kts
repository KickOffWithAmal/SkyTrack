plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.ksp) apply false
}

// Redirect build artifacts to local APFS disk to prevent macOS ExFAT AppleDouble (._*) metadata file issues
val userHome = System.getProperty("user.home")
val localBuildRoot = file("$userHome/.gradle-builds/SkyTrack_01")
allprojects {
    layout.buildDirectory.set(file("$localBuildRoot/${if (project == rootProject) "root" else project.name}"))
}

