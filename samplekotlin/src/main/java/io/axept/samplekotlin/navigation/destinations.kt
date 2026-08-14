package io.axept.samplekotlin.navigation

interface Destination {
    val route: String
}

object HomeDestination : Destination {
    override val route = "home_screen"
}

object WebViewDestination : Destination {
    override val route = "webview_screen"
}

object VendorConsentTestDestination : Destination {
    override val route = "vendor_consent_test_screen"
}

object ConfigurationDestination : Destination {
    override val route = "configuration_screen"
}

object DebugConsentInfoDestination : Destination {
    override val route = "debug_consent_info_screen"
}

object AxeptioStoreDemoDestination : Destination {
    override val route = "axeptio_store_demo_screen"
}

internal enum class ScreenArguments(val slug: String) {
    TOKEN("token")
}