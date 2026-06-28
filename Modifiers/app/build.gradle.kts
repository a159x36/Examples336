
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.compose.compiler)
}


android {
    namespace="com.example.mjjohnso.modifiers"
    compileSdk=37
    defaultConfig {
        applicationId="com.example.mjjohnso.modifiers"
        minSdk=23
        targetSdk=36
        versionCode=1
        versionName="1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildTypes {
        release {
            isMinifyEnabled=true
            isShrinkResources=true
        }
    }


    buildFeatures {
        compose=true // Enable Jetpack Compose support
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlin {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
        }
    }

}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.ui.tooling)
}
