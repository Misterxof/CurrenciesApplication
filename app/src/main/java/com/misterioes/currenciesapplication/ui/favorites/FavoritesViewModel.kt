package com.misterioes.currenciesapplication.ui.favorites

import androidx.lifecycle.viewModelScope
import com.misterioes.currenciesapplication.domain.usecase.CurrenciesUseCase
import com.misterioes.currenciesapplication.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(private val currenciesUseCase: CurrenciesUseCase) :
    BaseViewModel<FavoritesContract.Intent, FavoritesContract.State, FavoritesContract.Effect>() {

    init {
        setIntent(FavoritesContract.Intent.GetFavorites)
    }

    override fun initialState(): FavoritesContract.State {
        return FavoritesContract.State(true, emptyList(), "")
    }

    override fun handleIntent(intent: FavoritesContract.Intent) {
        when (intent) {
            is FavoritesContract.Intent.GetFavorites -> getFavorites()
        }
    }

    private fun getFavorites() {
        viewModelScope.launch(Dispatchers.IO) {
            currenciesUseCase.getFavorites().collect {
                if (it.isNotEmpty())
                    setState { copy(isLoading = false, favorites = it) }
            }
        }
    }
}