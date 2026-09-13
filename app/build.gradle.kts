plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.skypass.skytrack"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.skypass.skytrack"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        // Replace this with your Apps Script /exec Web App URL.
        buildConfigField(
            type = "String",
            name = "SKYTRACK_API_URL",
            value = "\"https://script.google.com/macros/s/AKfycbwcJ7zu4gJQ2ezOOVbLhIl29jhxdV6gtE5epP2gu_SBL9eNvt-OnA9fQspyvJj2ckkDtg/exec\""
        )

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
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation("androidx.compose.ui:ui-text-google-fonts:1.6.3")

    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation("androidx.compose.material:material-icons-extended")
    dependencies {
        implementation("androidx.compose.material:material-icons-extended:1.7.0") // match your Compose BOM/version
        // ...your other dependencies
    }

    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.okhttp.logging)
    implementation(libs.data.store.preferences)
    implementation(libs.navigation.compose)

    implementation(libs.hilt.android)
    implementation(libs.androidx.core.splashscreen)
    ksp(libs.hilt.compiler)
    implementation(libs.hilt.navigation.compose)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}
