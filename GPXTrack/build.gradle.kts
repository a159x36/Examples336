// Top-level build file where you can add configuration options common to all sub-projects/modules.

plugins {
    alias(libs.plugins.com.android.application) apply (false)
    alias(libs.plugins.org.jetbrains.kotlin.android) apply (false)
    alias(libs.plugins.compose.compiler) apply false
}

buildscript {
    dependencies {
        classpath("com.google.android.libraries.mapsplatform.secrets-gradle-plugin:secrets-gradle-plugin:2.0.1")
    }
}