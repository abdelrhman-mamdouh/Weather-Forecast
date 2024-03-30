plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
}

android {
    namespace = "com.example.weatherguide"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.weatherguide"
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
        dataBinding = true
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.navigation:navigation-runtime-ktx:2.7.7")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.7")
    implementation("androidx.navigation:navigation-fragment:2.7.7")
    implementation("androidx.gridlayout:gridlayout:1.0.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")


    // Glide library
    implementation("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")

    // WorkManager library for background processing tasks
    implementation("androidx.work:work-runtime-ktx:2.7.1")

    // Retrofit library for making HTTP requests and handling API responses
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // Lifecycle components for managing Android lifecycle-aware components
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.4.0")

    // Kotlin coroutines for asynchronous programming
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.5.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.2")

    // Room persistence library for database management
    implementation("androidx.room:room-ktx:2.6.1")
    implementation("androidx.room:room-runtime:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")

    // Lifecycle extensions for additional Android lifecycle support
    implementation("androidx.lifecycle:lifecycle-extensions:2.2.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.4.1")

    // Picasso library for image loading and caching
    implementation("com.squareup.picasso:picasso:2.8")

    // CircleImageView library for displaying circular images
    implementation("de.hdodenhof:circleimageview:3.1.0")

    // SDP (ScalableDP) and SSP (ScalableSP) libraries for handling scalable dimensions and spacing
    implementation("com.intuit.sdp:sdp-android:1.0.6")
    implementation("com.intuit.ssp:ssp-android:1.0.6")

    // OpenStreetMap library for displaying maps in Android applications
    implementation("org.osmdroid:osmdroid-android:6.1.11")
    // Additional library for using maps based on MapsForge format with OSMDroid
    implementation("org.osmdroid:osmdroid-mapsforge:6.1.11")

    // Google Play services dependency for accessing location services
    implementation("com.google.android.gms:play-services-location:21.0.0")

    implementation("com.google.android.libraries.places:places:3.3.0")

    implementation ("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")

    implementation ("com.facebook.shimmer:shimmer:0.5.0")

    implementation ("com.airbnb.android:lottie:6.3.0")


}