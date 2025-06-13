plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.example.coffeeshop"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.coffeeshop"
        minSdk = 29
        targetSdk = 35
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

}



dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)



    // Use the latest version of Firebase BOM to manage versions
    implementation(platform("com.google.firebase:firebase-bom:31.0.1"))

    // Firebase Authentication (no version needed, BOM will manage the version)
    implementation("com.google.firebase:firebase-auth")

    // Firebase Firestore (no version needed, BOM will manage the version)
    implementation("com.google.firebase:firebase-firestore")

    // Firebase Storage (no version needed, BOM will manage the version)
    implementation("com.google.firebase:firebase-storage")

    // RecyclerView dependency
    implementation("androidx.recyclerview:recyclerview:1.2.1")

    // Glide dependency
    implementation("com.github.bumptech.glide:glide:4.13.0")

    // Glide annotation processor
    annotationProcessor("com.github.bumptech.glide:compiler:4.13.0")


    implementation ("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.16.0")



}
