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