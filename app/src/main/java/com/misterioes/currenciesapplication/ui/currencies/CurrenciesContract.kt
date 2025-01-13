package com.misterioes.currenciesapplication.ui.currencies

import androidx.compose.runtime.snapshots.SnapshotStateList
import com.misterioes.currenciesapplication.domain.model.Currencies
import com.misterioes.currenciesapplication.domain.model.Currency
import com.misterioes.currenciesapplication.domain.model.Filter
import com.misterioes.currenciesapplication.domain.model.Rate
import com.misterioes.currenciesapplication.ui.base.ViewIntent
import com.misterioes.currenciesapplication.ui.base.ViewSideEffect
import com.misterioes.currenciesapplication.ui.base.ViewState

class CurrenciesContract {
    data class State(
        val isLoading: Boolean,
        val allCurrencies: List<String>,
        val selectedItem: String,
        val currency: Currency?,
        val rates: SnapshotStateList<Rate>,
        val filter: Filter?,
        val error: Exception?
    ) : ViewState

    sealed class Intent() : ViewIntent {
        data class GetCurrency(val currency: Currencies) : Intent()
        data object GetAllCurrencies : Intent()
        data object RetryConnection : Intent()
        data class AddCurrencyToFavorites(val rate: Rate) : Intent()
    }

    sealed class Effect() : ViewSideEffect {
        sealed class Navigation : Effect() {
            data object NavigateFilters : Navigation()
            data object NavigateCurrencies : Navigation()
            data object NavigateFavorite : Navigation()
        }
    }
}