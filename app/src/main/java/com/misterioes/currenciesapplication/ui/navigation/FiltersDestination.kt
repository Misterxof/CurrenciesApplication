package com.misterioes.currenciesapplication.ui.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.misterioes.currenciesapplication.ui.feature.filters.FiltersScreen
import com.misterioes.currenciesapplication.ui.feature.filters.FiltersViewModel
import com.misterioes.currenciesapplication.ui.main.MainContract

@Composable
fun FiltersDestination(onNavigationRequested: (navigationEffect: MainContract.Effect.Navigation) -> Unit) {
    val viewModel: FiltersViewModel = hiltViewModel()
    FiltersScreen(
        state = viewModel.state,
        onIntentSend = { event -> viewModel.setIntent(event) },
        effect = viewModel.effect,
        onNavigationRequested = { navigationEffect ->
            onNavigationRequested(navigationEffect)
        }
    )
}