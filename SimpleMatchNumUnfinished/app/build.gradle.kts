
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.org.jetbrains.kotlin.android)
}


android {
    namespace="com.example.mjjohnso.simplematch"
    compileSdk=35
    defaultConfig {
        applicationId="com.example.mjjohnso.simplematch"
        minSdk=21
        targetSdk=33
        versionCode=1
        versionName="1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildTypes {
        release {
            isMinifyEnabled=false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }


    buildFeatures {
        compose=true // Enable Jetpack Compose support
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

}

dependencies {
    val composeBom = platform(libs.androidx.compose.bom)
    implementation(composeBom)
    androidTestImplementation(composeBom)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.material3)
    implementation(libs.ui.tooling.preview)
    debugImplementation(libs.ui.tooling)
    implementation(libs.activity.compose)
}
