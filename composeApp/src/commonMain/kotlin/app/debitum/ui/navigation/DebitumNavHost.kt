package app.debitum.ui.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import app.debitum.ui.bills.BillsScreen
import app.debitum.ui.splash.SplashScreen

@Composable
fun DebitumNavHost() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = SplashRoute,
    ) {
        composable<SplashRoute>(
            exitTransition = { fadeOut(animationSpec = tween(380)) },
        ) {
            SplashScreen(
                onFinished = {
                    navController.navigate(BillsRoute) {
                        popUpTo(SplashRoute) { inclusive = true }
                    }
                },
            )
        }
        composable<BillsRoute>(
            enterTransition = { fadeIn(animationSpec = tween(420)) },
        ) {
            BillsScreen()
        }
    }
}
