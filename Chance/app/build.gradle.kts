import org.gradle.internal.declarativedsl.parsing.main

plugins {
    //id("com.android.application")
    id("com.google.gms.google-services")
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.chance"
    compileSdk = 36



    defaultConfig {
        applicationId = "com.example.chance"
        minSdk = 24
        targetSdk = 36
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
    buildFeatures {
        viewBinding = true
        dataBinding = true
    }

}

dependencies {


    testImplementation("org.mockito:mockito-core:5.11.0")

    testImplementation("org.mockito:mockito-inline:5.2.0")

    testImplementation("junit:junit:4.13.2")

    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    implementation(platform("com.google.firebase:firebase-bom:34.5.0"))
    implementation("com.google.firebase:firebase-analytics")
    //implementation("com.journeyapps:zxing-android-embedded:4.3.0")
    implementation("com.google.zxing:core:3.5.3")
    implementation("com.github.bumptech.glide:glide:5.0.5")
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.storage)
    implementation("com.google.android.flexbox:flexbox:3.0.0")
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}

tasks.withType<JavaCompile> {
    exclude("**/com/example/chance/_legacy_view/**")
    exclude("**/com/example/chance/_legacy_adapter/**")
    exclude("**/com/example/chance/_legacy_ui/**")
}