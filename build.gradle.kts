import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.firebase.crashlytics) apply false
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.google.services) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.ksp) apply false

    id("vkid.manifest.placeholders") version "1.1.0" apply true
}

vkidManifestPlaceholders {
    val localPropertiesFile = rootProject.file("local.properties")
    val localProperties = Properties()
    localProperties.load(FileInputStream(localPropertiesFile))

    init(
        clientId = localProperties["VK_ID_CLIENT_ID"].toString(),
        clientSecret = localProperties["VK_ID_CLIENT_SECRET"].toString(),
    )

    vkidRedirectHost = localProperties["VK_ID_REDIRECT_HOST"].toString()
    vkidRedirectScheme = localProperties["VK_ID_REDIRECT_SCHEME"].toString()
    vkidClientId = localProperties["VK_ID_CLIENT_ID"].toString()
    vkidClientSecret = localProperties["VK_ID_CLIENT_SECRET"].toString()
}