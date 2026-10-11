plugins { id("com.android.application") }
android {
    namespace = "com.paytondev.launchpad"
    compileSdk = 37
    defaultConfig {
        applicationId = "com.paytondev.launchpad"
        minSdk = 19
        targetSdk = 19
        versionCode = 3
        versionName = "1.2.0"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }
    lint {
        // This is a locally installed app for an Android 4.4 vehicle, not Play.
        disable += setOf("ExpiredTargetSdkVersion", "OldTargetApi")
        abortOnError = true
    }
}
dependencies { testImplementation("junit:junit:4.13.2") }
