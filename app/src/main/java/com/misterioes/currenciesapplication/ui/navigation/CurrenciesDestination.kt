package com.misterioes.currenciesapplication.ui.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.misterioes.currenciesapplication.ui.currencies.CurrenciesScreen
import com.misterioes.currenciesapplication.ui.currencies.CurrenciesViewModel
import com.misterioes.currenciesapplication.ui.main.MainContract

@Composable
fun CurrenciesDestination(onNavigationRequested: (navigationEffect: MainContract.Effect.Navigation) -> Unit) {
    val viewModel: CurrenciesViewModel = hiltViewModel<CurrenciesViewModel>()
    CurrenciesScreen(
        state = viewModel.state,
        onIntentSend = { event -> viewModel.setIntent(event) },
        onNavigationRequested = { navigationEffect ->
            onNavigationRequested(navigationEffect)
        }
    )
}