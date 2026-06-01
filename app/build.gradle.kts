import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.firebase.crashlytics)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.google.services)
    alias(libs.plugins.ksp)

    id("vkid.manifest.placeholders")
}

android {
    namespace = "io.github.mamedovilkin.todoapp"
    compileSdk = 37

    defaultConfig {
        applicationId = "io.github.mamedovilkin.todoapp"
        minSdk = 24
        targetSdk = 37
        versionCode = 14
        versionName = "3.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        val localPropertiesFile = rootProject.file("local.properties")
        val localProperties = Properties()
        localProperties.load(FileInputStream(localPropertiesFile))

        addManifestPlaceholders(mapOf(
            "VKIDRedirectHost" to localProperties["VK_ID_REDIRECT_HOST"].toString(),
            "VKIDRedirectScheme" to localProperties["VK_ID_REDIRECT_SCHEME"].toString(),
            "VKIDClientID" to localProperties["VK_ID_CLIENT_ID"].toString(),
            "VKIDClientSecret" to localProperties["VK_ID_CLIENT_SECRET"].toString()
        ))
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("debug")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
        isCoreLibraryDesugaringEnabled = true
    }

    buildFeatures {
        compose = true
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
}

dependencies {
    // Modules
    implementation(project(":database"))

    // VK ID
    implementation(libs.vkid)

    // Desugaring
    coreLibraryDesugaring(libs.desugar.jdk.libs)

    // Android Jetpack
    implementation(libs.androidx.material3.window.size.class1)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.glance.appwidget)
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.glance.material)
    implementation(libs.androidx.security.crypto)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.ui)

    // ViewModel
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)

    // Coroutines
    implementation(libs.kotlinx.coroutines.play.services)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.coroutines.core)

    // Room
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // Koin
    implementation(project.dependencies.platform(libs.koin.bom))
    implementation(libs.koin.core)
    implementation(libs.koin.android)
    implementation(libs.koin.compose)
    implementation(libs.koin.compose.navigation)

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.firestore)

    // OkHttp
    implementation(libs.okhttp)

    // Lottie
    implementation(libs.lottie.compose)

    // Glide
    implementation(libs.glide)
}