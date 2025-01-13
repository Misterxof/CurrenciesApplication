package com.misterioes.currenciesapplication.ui.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.misterioes.currenciesapplication.ui.favorites.FavoritesScreen
import com.misterioes.currenciesapplication.ui.favorites.FavoritesViewModel
import com.misterioes.currenciesapplication.ui.main.MainContract

@Composable
fun FavoritesDestination(onNavigationRequested: (navigationEffect: MainContract.Effect.Navigation) -> Unit) {
    val viewModel: FavoritesViewModel = hiltViewModel()
    FavoritesScreen(
        state = viewModel.state,
        onIntentSend = { event ->  viewModel.setIntent(event) },
        onNavigationRequested = { navigationEffect ->
            onNavigationRequested(navigationEffect)
        }
    )
}