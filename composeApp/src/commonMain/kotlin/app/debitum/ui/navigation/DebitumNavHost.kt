package app.debitum.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import app.debitum.ui.bills.BillsScreen

@Composable
fun DebitumNavHost() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = BillsRoute) {
        composable<BillsRoute> {
            BillsScreen()
        }
    }
}
