plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
    id("io.github.reactivecircus.app-versioning")
}

android {
    namespace = "de.techmaved.mediabrowserforspotify"
    compileSdk = 34

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
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.2"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    val roomVersion = "2.6.1"
    val acraVersion = "5.11.3"

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    implementation("androidx.activity:activity-compose:1.8.1")
    implementation(platform("androidx.compose:compose-bom:2023.10.01"))
    implementation("androidx.compose.ui:ui:1.5.4")
    implementation("androidx.compose.ui:ui-graphics:1.5.4")
    implementation("androidx.compose.ui:ui-tooling-preview:1.5.4")
    implementation("androidx.compose.material3:material3:1.1.2")
    implementation(files("../libs/spotify-app-remote-release-0.8.0.aar"))
    //implementation(files("../libs/spotify-auth-release-2.1.0.aar"))
    implementation("com.github.pghazal.spotify-web-api-android:api-retrofit2:1.0.2")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("androidx.media3:media3-exoplayer:1.2.0")
    implementation("androidx.media3:media3-ui:1.2.0")
    implementation("androidx.media3:media3-common:1.2.0")
    implementation("androidx.media3:media3-session:1.2.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.10.01"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.5.4")
    debugImplementation("androidx.compose.ui:ui-tooling:1.5.4")
    debugImplementation("androidx.compose.ui:ui-test-manifest:1.5.4")
    implementation("com.adamratzman:spotify-api-kotlin-core:4.0.3")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.room:room-runtime:$roomVersion")
    annotationProcessor("androidx.room:room-compiler:$roomVersion")
    ksp("androidx.room:room-compiler:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    implementation("androidx.room:room-rxjava2:$roomVersion")
    implementation("androidx.room:room-rxjava3:$roomVersion")
    implementation("androidx.room:room-guava:$roomVersion")
    testImplementation("androidx.room:room-testing:$roomVersion")
    implementation("androidx.room:room-paging:$roomVersion")
    implementation("ch.acra:acra-core:$acraVersion")
    implementation("ch.acra:acra-dialog:$acraVersion")
    compileOnly("com.google.auto.service:auto-service-annotations:1.1.1")
    ksp("dev.zacsweers.autoservice:auto-service-ksp:1.1.0")
    ksp("com.google.auto.service:auto-service:1.1.1")
    implementation("androidx.documentfile:documentfile:1.0.1")
    implementation("com.mikepenz:iconics-core:5.4.0")
    implementation("com.mikepenz:iconics-compose:5.4.0")
    implementation("com.mikepenz:fontawesome-typeface:5.9.0.2-kotlin@aar")
}