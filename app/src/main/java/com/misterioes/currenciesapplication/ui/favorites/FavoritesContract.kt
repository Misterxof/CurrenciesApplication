package com.misterioes.currenciesapplication.ui.favorites

import com.misterioes.currenciesapplication.domain.model.Rate
import com.misterioes.currenciesapplication.ui.base.ViewIntent
import com.misterioes.currenciesapplication.ui.base.ViewSideEffect
import com.misterioes.currenciesapplication.ui.base.ViewState

class FavoritesContract {
    data class State(
        val isLoading: Boolean,
        val favorites: List<Rate>,
        val error: String
    ) : ViewState

    sealed class Intent() : ViewIntent {
        data object GetFavorites : Intent()
    }

    sealed class Effect() : ViewSideEffect {}
}