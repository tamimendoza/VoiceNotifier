plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.room)
    alias(libs.plugins.firebase)
    alias(libs.plugins.firebase.crashlytics)
}

android {
    namespace = "com.emprendecoders.voicenotifier"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.emprendecoders.voicenotifier"
        minSdk = 23
        //noinspection EditedTargetSdkVersion
        targetSdk = 35
        versionCode = 8
        versionName = "1.0.8"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        debug {
            isDebuggable = true
            isMinifyEnabled = false
            isShrinkResources = false
        }
        release {
            isDebuggable = false
            isMinifyEnabled = true
            isShrinkResources = true
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
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.15"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    tasks.register<Copy>("copyGoogleServicesJson") {
        val buildType = if (gradle.startParameter.taskNames.any { it.contains("Release") }) "release" else "debug"
        println("Copying google-services.json for $buildType build")
        from("src/$buildType/google-services.json")
        into(projectDir)
        rename { "google-services.json" }
    }
    tasks.whenTaskAdded {
        if (name.contains("Debug") || name.contains("Release")) {
            dependsOn("copyGoogleServicesJson")
        }
    }
}

dependencies {
    // Core Android dependencies
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    // Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.ktx)
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.analytics)

    // Gson
    implementation(libs.gson)

    // Room Database
    implementation(libs.room.runtime)
    ksp(libs.room.compiler)
    implementation(libs.room.ktx)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)

    // Debug
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

room {
    schemaDirectory("$projectDir/schemas")
}