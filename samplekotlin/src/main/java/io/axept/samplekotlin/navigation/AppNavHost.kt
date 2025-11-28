package io.axept.samplekotlin.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import io.axept.android.library.AxeptioService
import io.axept.samplekotlin.screen.MainScreen
import io.axept.samplekotlin.screen.WebViewScreen
import io.axept.samplekotlin.screen.VendorConsentTestScreen
import io.axept.samplekotlin.screen.DebugConsentInfoScreen
import io.axept.samplekotlin.screen.configuration.ConfigurationScreen

@Composable
internal fun AppNavHost(navController: NavHostController, targetService: AxeptioService) {
    NavHost(navController = navController, startDestination = HomeDestination.route) {

        composable(route = HomeDestination.route) {
            MainScreen(
                targetService = targetService,
                onOpenWebView = { token ->
                    val tokenArg = token.ifBlank { null }
                    navController.navigate(WebViewDestination.route + "/$tokenArg")
                },
                onNavigateToVendorTest = {
                    navController.navigate(VendorConsentTestDestination.route)
                },
                onNavigateToConfiguration = {
                    navController.navigate(ConfigurationDestination.route)
                },
                onNavigateToDebugInfo = {
                    navController.navigate(DebugConsentInfoDestination.route)
                }
            )
        }

        composable(
            route = WebViewDestination.route + "/{${ScreenArguments.TOKEN.slug}}",
            arguments = listOf(navArgument(ScreenArguments.TOKEN.slug) {
                type = NavType.StringType
                defaultValue = null
                nullable = true
            })
        ) {backStackEntry ->
            WebViewScreen(
                onBack = { navController.popBackStack() },
                customToken = backStackEntry.arguments?.getString(ScreenArguments.TOKEN.slug)
            )
        }

        composable(route = VendorConsentTestDestination.route) {
            VendorConsentTestScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(route = ConfigurationDestination.route) {
            ConfigurationScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(route = DebugConsentInfoDestination.route) {
            DebugConsentInfoScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}