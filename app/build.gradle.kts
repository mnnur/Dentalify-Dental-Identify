plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
    id ("com.google.devtools.ksp")
    id("kotlin-parcelize")
}

android {
    namespace = "com.ps108.dentify"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.ps108.dentify"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

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
        buildConfig = true
        mlModelBinding = true
    }
}

dependencies {

    implementation("com.google.firebase:firebase-firestore:24.10.0")
    implementation("com.google.firebase:firebase-storage:20.3.0")
    ext {

        set("room_version", "2.6.0")
        set("camerax_version", "1.3.0")
        set("paging_version", "2.1.2")
    }

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.5")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.5")
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.5")

    implementation("androidx.preference:preference-ktx:1.2.1")

    implementation ("com.github.bumptech.glide:glide:4.16.0")

    implementation("com.google.firebase:firebase-auth:22.3.0")
    implementation("com.google.android.gms:play-services-auth:20.7.0")
    implementation("com.google.android.gms:play-services-base:18.2.0")
    implementation("org.tensorflow:tensorflow-lite-support:0.1.0")
    implementation("org.tensorflow:tensorflow-lite-metadata:0.1.0")

//    implementation("androidx.navigation:navigation-fragment:2.7.5")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")


    implementation("androidx.camera:camera-camera2:${extra["camerax_version"]}")
    implementation("androidx.camera:camera-lifecycle:${extra["camerax_version"]}")
    implementation("androidx.camera:camera-view:${extra["camerax_version"]}")
    implementation("androidx.exifinterface:exifinterface:1.3.6")

    implementation("androidx.room:room-runtime:${extra["room_version"]}")
    ksp("androidx.room:room-compiler:${extra["room_version"]}")
    implementation("androidx.room:room-ktx:${extra["room_version"]}")

    implementation("androidx.paging:paging-runtime-ktx:${extra["paging_version"]}")

}