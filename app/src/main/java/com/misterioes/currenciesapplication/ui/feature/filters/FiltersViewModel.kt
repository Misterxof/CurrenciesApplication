package com.misterioes.currenciesapplication.ui.feature.filters

import androidx.lifecycle.viewModelScope
import com.misterioes.currenciesapplication.domain.model.Filter
import com.misterioes.currenciesapplication.domain.usecase.CurrenciesUseCase
import com.misterioes.currenciesapplication.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FiltersViewModel @Inject constructor(private val currenciesUseCase: CurrenciesUseCase) :
    BaseViewModel<FiltersContract.Intent, FiltersContract.State, FiltersContract.Effect>() {

    init {
        setIntent(FiltersContract.Intent.GetFilters)
    }

    override fun initialState(): FiltersContract.State {
        return FiltersContract.State(true, emptyList(), null, "")
    }

    override fun handleIntent(intent: FiltersContract.Intent) {
        when (intent) {
            FiltersContract.Intent.GetFilters -> getFilters()
            FiltersContract.Intent.ApplyFilter -> applyFilter()
            is FiltersContract.Intent.SelectFilter -> setFilter(intent.filter)
        }
    }

    private fun getFilters() {
        val filters = currenciesUseCase.getFilters()
        viewModelScope.launch {
            currenciesUseCase.filter.collect {
                setState { copy(isLoading = false, filters = filters, selectedFilter = it) }
            }
        }
    }

    private fun setFilter(filter: Filter) {
        setState { copy(selectedFilter = filter) }
    }

    private fun applyFilter() {
        state.value.selectedFilter?.let { filter ->
            currenciesUseCase.setFilter(filter)
            sendEffect { FiltersContract.Effect.ShowFilter(filter.text) }
        }
    }
}