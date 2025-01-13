package com.misterioes.currenciesapplication.ui.feature.filters

import com.misterioes.currenciesapplication.domain.model.Filter
import com.misterioes.currenciesapplication.ui.base.ViewIntent
import com.misterioes.currenciesapplication.ui.base.ViewSideEffect
import com.misterioes.currenciesapplication.ui.base.ViewState

class FiltersContract {
    data class State(
        val isLoading: Boolean,
        val filters: List<Filter>,
        val selectedFilter: Filter?,
        val error: String
    ) : ViewState

    sealed class Intent() : ViewIntent {
        data object GetFilters : Intent()
        data class SelectFilter(val filter: Filter) : Intent()
        data object ApplyFilter : Intent()
    }

    sealed class Effect() : ViewSideEffect {
        data class ShowFilter(val text: String) : Effect()
    }
}