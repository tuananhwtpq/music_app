import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.text.SimpleDateFormat

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("kotlin-parcelize")
}

android {
    namespace = "com.example.baseproject"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.baseproject"
        minSdk = 29
        targetSdk = 35
//        versionCode = 100
//        versionName = "1.0.0"

        versionCode = 1
        versionName = "test"

        val dateTime = SimpleDateFormat("dd-MM-yyyy").format(System.currentTimeMillis())
        setProperty("archivesBaseName", "Base - Project ($versionCode)_$dateTime")

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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
//    kotlinOptions {
//        jvmTarget = "17"
//    }

    kotlin.compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
    }

    buildFeatures {
        viewBinding = true
    }

    bundle {
        language {
            enableSplit = false
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    //ads library
    implementation(libs.ssquadadslibrary)

    //other library
    implementation(libs.lottie)
    implementation(libs.dotsindicator)
    implementation(libs.glide)
    implementation(libs.user.messaging.platform)

    implementation(libs.gson)

    //navigation
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)

    //room
    implementation(libs.androidx.room.runtime)
    //ksp
    implementation(libs.androidx.room.ktx)

    //exo player
    implementation(libs.androidx.media3.exoplayer)
    implementation(libs.androidx.media3.exoplayer.dash)
    implementation(libs.androidx.media3.ui)
    implementation(libs.androidx.media3.ui.compose)

    //media session service
    implementation(libs.androidx.media3.session)

}