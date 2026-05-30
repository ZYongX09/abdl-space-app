plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
    id("com.google.devtools.ksp")
    id("kotlin-parcelize")
}

android {
    namespace = "top.abdl.space"
    compileSdk = 37

    defaultConfig {
        applicationId = "top.abdl.space"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0.0"

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
    }

    flavorDimensions += "environment"
    productFlavors {
        create("dev") {
            dimension = "environment"
            buildConfigField("String", "API_BASE", "\"https://api.abdl-space.top\"")
        }
        create("staging") {
            dimension = "environment"
            buildConfigField("String", "API_BASE", "\"https://api.abdl-space.top\"")
        }
        create("prod") {
            dimension = "environment"
            buildConfigField("String", "API_BASE", "\"https://api.abdl-space.top\"")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlin {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
        }
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    // Compose BOM
    val composeBom = platform("androidx.compose:compose-bom:2026.02.00")
    implementation(composeBom)

    // Material 3
    implementation("androidx.compose.material3:material3")

    // Compose UI
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.animation:animation")
    implementation("androidx.compose.material:material-icons-extended")
    debugImplementation("androidx.compose.ui:ui-tooling")

    // Navigation
    implementation("androidx.navigation:navigation-compose:2.9.0")

    // Lifecycle + ViewModel
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.9.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.9.0")

    // Retrofit + OkHttp
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // Room (本地缓存)
    implementation("androidx.room:room-runtime:2.7.1")
    implementation("androidx.room:room-ktx:2.7.1")
    ksp("androidx.room:room-compiler:2.7.1")

    // Coil 2.x (图片加载)
    implementation("io.coil-kt:coil-compose:2.7.0")

    // Koin (轻量 DI)
    implementation("io.insert-koin:koin-android:4.1.1")
    implementation("io.insert-koin:koin-androidx-compose:4.1.1")

    // Paging 3 (列表分页)
    implementation("androidx.paging:paging-runtime-ktx:3.3.6")
    implementation("androidx.paging:paging-compose:3.3.6")

    // DataStore (Token 存储)
    implementation("androidx.datastore:datastore-preferences:1.1.4")

    // Security Crypto (EncryptedSharedPreferences)
    implementation("androidx.security:security-crypto:1.1.0-alpha06")

    // Kotlin Parcelize
    implementation("org.jetbrains.kotlin:kotlin-parcelize-runtime")

    // Cloudflare Turnstile Android SDK
    // implementation("com.cloudflare:turnstile-android:1.3.0")

    // Haze — 毛玻璃效果
    implementation("dev.chrisbanes.haze:haze:1.7.2")
    implementation("dev.chrisbanes.haze:haze-materials:1.7.2")

    // Miuix — 小米风格组件（底栏、设置项、Switch 等）
    implementation("top.yukonga.miuix.kmp:miuix-ui-android:0.9.1")
    implementation("top.yukonga.miuix.kmp:miuix-preference-android:0.9.1")

    // AndroidLiquidGlass — 液态玻璃效果
    // TODO: 等 backdrop API 对齐后启用（需要更深入研究）
    // implementation("io.github.kyant0:backdrop:2.0.0-alpha03")

    // Shimmer — 骨架屏加载
    implementation("com.valentinilk.shimmer:compose-shimmer:1.4.0")

    // Core KTX
    implementation("androidx.core:core-ktx:1.16.0")
    implementation("androidx.activity:activity-compose:1.10.1")
}
