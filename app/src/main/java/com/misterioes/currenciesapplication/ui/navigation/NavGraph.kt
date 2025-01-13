package com.misterioes.currenciesapplication.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.misterioes.currenciesapplication.ui.main.MainContract

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String = Screen.CurrenciesScreen.route,
    modifier: Modifier,
    onNavigationRequested: (navigationEffect: MainContract.Effect.Navigation) -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.CurrenciesScreen.route) {
            CurrenciesDestination() { navigationEffect -> onNavigationRequested(navigationEffect) }
        }
        composable(Screen.FavoritesScreen.route) {
            FavoritesDestination() { navigationEffect -> onNavigationRequested(navigationEffect) }
        }
        composable(Screen.FiltersScreen.route) {
            FiltersDestination() { navigationEffect -> onNavigationRequested(navigationEffect) }
        }
    }
}