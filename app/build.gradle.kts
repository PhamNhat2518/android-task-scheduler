plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")

}

android {
    namespace = "vn.edu.tlu.appquanlylichtrinh"
    compileSdk = 35

    defaultConfig {
        applicationId = "vn.edu.tlu.appquanlylichtrinh"
        minSdk = 25
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
    // Firebase BoM (Cách làm tốt nhất)
    implementation(platform("com.google.firebase:firebase-bom:33.1.1"))

    // Thư viện Firebase Authentication
    implementation("com.google.firebase:firebase-auth")
    // Thêm thư viện Firebase Realtime Database
    implementation("com.google.firebase:firebase-database")
}