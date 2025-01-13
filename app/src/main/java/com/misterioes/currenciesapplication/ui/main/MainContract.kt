package com.misterioes.currenciesapplication.ui.main

import com.misterioes.currenciesapplication.ui.base.ViewSideEffect
import com.misterioes.currenciesapplication.ui.base.ViewState

class MainContract {
    data class State(val route: String? = null) : ViewState

    sealed class Effect() : ViewSideEffect {
        sealed class Navigation : Effect() {
            data object NavigateBack : Navigation()
            data object NavigateFilters : Navigation()
            data object NavigateCurrencies : Navigation()
            data object NavigateFavorite : Navigation()
        }
    }
}