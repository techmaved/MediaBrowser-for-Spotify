import org.gradle.kotlin.dsl.support.kotlinCompilerOptions
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin)
    alias(libs.plugins.ksp)
    alias(libs.plugins.app.versioning)
    alias(libs.plugins.aboutlibraries.plugin)
    alias(libs.plugins.serialization.plugin)
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "de.techmaved.mediabrowserforspotify"
    compileSdk = 36

    buildFeatures {
        buildConfig = true
    }

    signingConfigs {
        create("release") {
            storeFile = file("keystore.jks")
            storePassword = System.getenv("SIGNING_STORE_PASSWORD")
            keyAlias = System.getenv("SIGNING_KEY_ALIAS")
            keyPassword = System.getenv("SIGNING_KEY_PASSWORD")
        }
    }

    defaultConfig {

        manifestPlaceholders += mapOf(
            "redirectSchemeName" to "spotify-sdk",
            "redirectHostName" to "auth"
        )
        applicationId = "de.techmaved.mediabrowserforspotify"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "0.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        buildConfigField(
            "String",
            "SPOTIFY_CLIENT_ID",
            "\"${"713b5da48aaa48309005f5448d1842f3"}\""
        )

        buildConfigField(
            "String",
            "SPOTIFY_REDIRECT_URI",
            "\"${"mediabrowserforspotify://auth"}\""
        )
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )

            signingConfig = signingConfigs["release"]
        }

        debug {
            buildConfigField(
                "String",
                "SPOTIFY_REDIRECT_URI",
                "\"${"mediabrowserforspotifydebug://auth"}\""
            )

            applicationIdSuffix = ".debug"
            versionNameSuffix = ".debug"
        }
    }

    appVersioning {
        overrideVersionName { gitTag, _, _ ->
            gitTag.rawTagName.replaceFirst("v", "")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        compose = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
    }
}

dependencies {
    implementation(libs.core.ktx)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.activity.compose)
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.graphics)
    implementation(libs.compose.tooling)
    implementation(libs.compose.m3)
    implementation(files("../libs/spotify-app-remote-release-0.8.0.aar"))
    //implementation(files("../libs/spotify-auth-release-2.1.0.aar"))´
    implementation(libs.gson)
    implementation(libs.media3.exoplayer)
    implementation(libs.media3.ui)
    implementation(libs.media3.common)
    implementation(libs.media3.session)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(platform(libs.compose.bom))
    androidTestImplementation(libs.ui.test.junit4)
    debugImplementation(libs.ui.tooling)
    debugImplementation(libs.ui.test.manifest)
    implementation(libs.spotify.api.kotlin.core)
    implementation(libs.appcompat)
    implementation(libs.room.runtime)
    annotationProcessor(libs.room.compiler)
    ksp(libs.room.compiler)
    implementation(libs.room.ktx)
    implementation(libs.room.rxjava2)
    implementation(libs.room.rxjava3)
    implementation(libs.room.guava)
    testImplementation(libs.test.room)
    implementation(libs.room.paging)
    implementation(libs.acra.core)
    implementation(libs.acra.dialog)
    compileOnly(libs.auto.service.annotations)
    ksp(libs.auto.service.ksp)
    ksp(libs.auto.service)
    implementation(libs.documentfile)
    implementation(libs.iconics.core)
    implementation(libs.iconics.compose)
    implementation(libs.fontawesome.typeface)
    implementation(libs.datastore.preferences)
    implementation(libs.aboutlibraries.core)
    implementation(libs.aboutlibraries.compose.m3)
    implementation(libs.navigation.fragment.compose)
    implementation(libs.navigation.compose)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.compose.material.icons)
}