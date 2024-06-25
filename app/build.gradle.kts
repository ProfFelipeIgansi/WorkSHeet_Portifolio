plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("com.google.gms.google-services")

    kotlin("kapt")
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.worksheetportiflio"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.worksheetportiflio"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}
/*configurations.all {
    resolutionStrategy {
        force("org.jetbrains:annotations:23.0.0")
    }
}*/
dependencies {
implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.navigation.common.ktx)
    implementation(libs.androidx.runtime.android)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.firebase.auth.ktx)
    implementation(libs.firebase.firestore.ktx)
    implementation(libs.androidx.material3.android)
    implementation(libs.androidx.runtime.livedata)
    implementation(libs.androidx.junit.ktx)
    testImplementation(libs.junit.junit)
    testImplementation(libs.testng)
    androidTestImplementation(libs.junit.junit)

    kapt(libs.androidx.room.compiler)
}