plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    kotlin("plugin.serialization") version "1.9.0"
    kotlin("kapt")
    id("kotlin-parcelize")
    id("androidx.navigation.safeargs.kotlin")
}

android {
    namespace = "com.example.playlistmaker"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.playlistmaker"
        minSdk = 29
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }

        debug {
            isMinifyEnabled = false
        }
    }

    buildFeatures {
        viewBinding = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

    signingConfigs {
        create("release") {
            val storeFileProp = providers.gradleProperty("STORE_FILE").orNull?.trim()
            val storePassProp = providers.gradleProperty("STORE_PASSWORD").orNull?.trim()
            val keyAliasProp  = providers.gradleProperty("KEY_ALIAS").orNull?.trim()
            val keyPassProp   = providers.gradleProperty("KEY_PASSWORD").orNull?.trim()

            require(!storeFileProp.isNullOrBlank()) { "STORE_FILE missing" }
            require(!storePassProp.isNullOrBlank()) { "STORE_PASSWORD missing" }
            require(!keyAliasProp.isNullOrBlank())  { "KEY_ALIAS missing" }
            require(!keyPassProp.isNullOrBlank())   { "KEY_PASSWORD missing" }

            storeFile = file(storeFileProp!!)
            storePassword = storePassProp
            keyAlias = keyAliasProp
            keyPassword = keyPassProp
            storeType = "JKS"
        }
    }
    buildTypes {
        getByName("release") {
            signingConfig = signingConfigs.getByName("release")
        }
    }

    lint {
        disable.add("NullSafeMutableLiveData")
        checkReleaseBuilds = false
        abortOnError = false
    }

    defaultConfig {
        versionCode = 1
        versionName = "1.0.0"
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation("com.github.bumptech.glide:glide:4.16.0")
    kapt("com.github.bumptech.glide:compiler:4.16.0")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.0.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    implementation ("com.google.android.material:material:1.11.0")
    implementation ("jp.wasabeef:glide-transformations:4.3.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")
    implementation("androidx.activity:activity-ktx:1.8.2")
    implementation ("androidx.lifecycle:lifecycle-livedata-ktx:2.6.1")
    implementation ("io.insert-koin:koin-android:3.3.0")
    implementation ("androidx.navigation:navigation-fragment-ktx:2.5.3")
    implementation ("androidx.navigation:navigation-ui-ktx:2.5.3")
    implementation ("androidx.fragment:fragment-ktx:1.5.6")
    implementation("androidx.room:room-runtime:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
}