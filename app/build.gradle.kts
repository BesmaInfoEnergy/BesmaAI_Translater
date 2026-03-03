plugins {
    id("com.android.application")
    id("kotlin-android")
}

android {
    namespace = "com.besmainfoenergy.besmaai_translater"
    compileSdk = 34 // Mis à jour pour 2026

    defaultConfig {
        applicationId = "com.besmainfoenergy.besmaai_translater"
        minSdk = 21
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        vectorDrawables { 
            useSupportLibrary = true
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17 // Passage à Java 17 (standard actuel)
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true // Optimise encore plus la taille de l'APK
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    buildFeatures {
        viewBinding = true
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions.jvmTarget = "17"
}

dependencies {
    // UI de base
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.android.material:material:1.11.0")

    // RESEAU : OkHttp pour communiquer avec n8n
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0") // Pour débugger tes appels n8n

    // DATA : GSON pour manipuler le JSON de n8n
    implementation("com.google.code.gson:gson:2.10.1")

    // ASYNC : Coroutines pour ne pas geler l'écran pendant l'appel API
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
}
