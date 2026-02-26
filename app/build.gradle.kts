plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.secrets.gradle)
    alias(libs.plugins.jetbrainsKotlinSerialization)
    alias(libs.plugins.devtools.ksp)
    alias(libs.plugins.hilt.android)
}

android {
    namespace = "com.uriel.musicjam"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.uriel.musicjam"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        manifestPlaceholders["redirectSchemeName"] = "musicjam"
        manifestPlaceholders["redirectHostName"] = "callback"

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
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    buildFeatures {
        compose = true
        buildConfig = true
        resValues = true
    }

    flavorDimensions.add("environment")
    productFlavors {
        create("dev") {
//            dimension = "environment"
//            buildConfigField("String", "BASE_URL_RICK", "\"https://rickandmortyapi.com/api/\"")
//            buildConfigField("String", "BASE_URL_JSON", "\"https://jsonplaceholder.typicode.com/\"")
//            resValue("string", "app_name", "Demo (DEV)")
        }

        create("prod") {
//            dimension = "environment"
//            buildConfigField("String", "BASE_URL_RICK", "\"https://rickandmortyapi.com/api/\"")
//            buildConfigField("String", "BASE_URL_JSON", "\"https://jsonplaceholder.typicode.com/\"")
//            resValue("string", "app_name", "Demo")
        }
    }

}

secrets {
    propertiesFileName = "local.properties"
    ignoreList.add("sdk.dir")
}

ksp {
    arg("hilt.disableModulesHaveInstallInCheck", "true")
}


kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.palette)

    implementation(libs.androidx.compose.ui.text.google.fonts)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.com.squareup.retrofit2.retrofit)
    implementation(libs.com.squareup.retrofit2.converter.json)
    implementation(libs.io.coil.kt.coil.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.hilt.android)
    implementation(libs.hilt.navigation.compose)
    implementation(libs.androidx.compose.ui.text)
    ksp(libs.hilt.compiler)

    implementation(libs.spotify.auth)
    implementation(files("libs/spotify-app-remote-release-0.8.0.aar"))
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("com.google.guava:guava:31.1-android")
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    dependencies {
        implementation("org.hildan.krossbow:krossbow-stomp-core:7.1.0")
        implementation("org.hildan.krossbow:krossbow-websocket-okhttp:7.1.0")
        implementation("org.hildan.krossbow:krossbow-stomp-kxserialization-json:7.1.0")
    }
}