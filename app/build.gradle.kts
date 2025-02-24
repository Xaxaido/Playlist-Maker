plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    alias(libs.plugins.jetbrainsKsp)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.practicum.playlistmaker"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.practicum.playlistmaker"
        minSdk = 29
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
        renderscriptTargetApi = 31
        renderscriptSupportModeEnabled = true

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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation (libs.androidx.ui)
    implementation (libs.androidx.material)
    implementation (libs.androidx.activity.compose)
    implementation (libs.androidx.constraintlayout.compose)
    implementation(libs.androidx.navigation.compose)
    implementation (libs.insert.koin.koin.androidx.compose)
    implementation(libs.coil.compose)
    debugImplementation(libs.androidx.ui.tooling)

    implementation(libs.adapterdelegates4.kotlin.dsl)
    implementation (libs.hannesdorfmann.adapterdelegates4.kotlin.dsl.viewbinding)
    implementation (libs.koinAndroid)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.material3.android)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation(libs.retrofit2)
    implementation(libs.converter.gson)
    implementation(libs.glide)
    ksp(libs.compiler)
    implementation(libs.paging)
    implementation(libs.activityKtx)
    implementation (libs.kotlinxCoroutinesCore)
    implementation (libs.viewpager2)
    implementation (libs.roomRuntime)
    implementation (libs.roomKtx)
    ksp(libs.roomCompiler)
    implementation (libs.androidx.palette.ktx)
    implementation (libs.androidx.media)
    implementation (libs.facebook.shimmer)
    implementation(libs.fragmentKtx)
    implementation (libs.navigation.fragment.ktx)
    implementation (libs.androidx.navigation.ui.ktx)
    implementation (libs.jsoup)
}