// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.app.versioning) apply false
    alias(libs.plugins.aboutlibraries.plugin) apply false
    alias(libs.plugins.serialization.plugin)
    alias(libs.plugins.compose.compiler) apply false
}