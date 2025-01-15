package com.misterioes.currenciesapplication.ui.currencies

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.viewModelScope
import com.misterioes.currenciesapplication.domain.combineAndAwait
import com.misterioes.currenciesapplication.domain.model.Currencies
import com.misterioes.currenciesapplication.domain.model.Filter
import com.misterioes.currenciesapplication.domain.model.Rate
import com.misterioes.currenciesapplication.domain.model.Result
import com.misterioes.currenciesapplication.domain.usecase.CurrenciesUseCase
import com.misterioes.currenciesapplication.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CurrenciesViewModel @Inject constructor(private val currenciesUseCase: CurrenciesUseCase) : BaseViewModel<CurrenciesContract.Intent, CurrenciesContract.State, CurrenciesContract.Effect>() {

    init {
        setIntent(CurrenciesContract.Intent.GetCurrency(Currencies.USD))
        setIntent(CurrenciesContract.Intent.GetAllCurrencies)
        getFilter()
    }

    override fun initialState(): CurrenciesContract.State {
        return CurrenciesContract.State(
            isLoading = true,
            emptyList(),
            "",
            null,
            mutableStateListOf<Rate>(),
            null,
            null
        )
    }

    override fun handleIntent(intent: CurrenciesContract.Intent) {
        when (intent) {
            is CurrenciesContract.Intent.AddCurrencyToFavorites -> addCurrencyToFavorites(intent.rate)
            is CurrenciesContract.Intent.GetCurrency -> getCurrency(intent.currency)
            is CurrenciesContract.Intent.GetAllCurrencies -> getAllCurrencies()
            is CurrenciesContract.Intent.RetryConnection -> retryConnection()
        }
    }

    private fun retryConnection() {
        viewModelScope.launch {
            delay(5000)
            setIntent(CurrenciesContract.Intent.GetCurrency(Currencies.USD))
        }
    }

    private fun getFilter() {
        viewModelScope.launch {
            currenciesUseCase.filter.collect {
                setState { copy(filter = it) }
                if(state.value.rates.isNotEmpty()) {
                    val rates = state.value.filter!!.sortFunction(state.value.rates)
                    val list = mutableStateListOf<Rate>().apply { addAll(rates) }
                    setState { copy(rates = list) }
                }
            }
        }
    }

    private fun getAllCurrencies() {
        val currencies = currenciesUseCase.allCurrencies
        setState { copy(allCurrencies = currencies) }
    }

    private fun updateCurrencyWitFavorites(favorites: List<Rate>, rates: List<Rate>) {
        if (favorites.isNotEmpty()) {
            for (i in rates.indices) {
                if (favorites.find { fav -> fav.base == rates[i].base && fav.symbol == rates[i].symbol } == null) {
                    rates[i].selected = false
                } else {
                    rates[i].selected = true
                }
            }
        } else
            rates.map { it.selected = false }
    }

    private fun setStateCurrency(result: Result.Success, list: List<Rate>) {
        val list = mutableStateListOf<Rate>().apply { addAll(list) }
        setState {
            copy(
                isLoading = false,
                currency = result.currency,
                selectedItem = result.currency.base,
                rates = list,
                error = null
            )
        }
    }

    private fun getCurrency(currency: Currencies) {
        viewModelScope.launch(Dispatchers.IO) {
            combineAndAwait(
                currenciesUseCase.getCurrency(currency),
                currenciesUseCase.getFavorites()
            ).collect { (result, favorites) ->
                when (result) {
                    is Result.Success -> {
                        val srList =
                            state.value.filter!!.sortFunction(result.currency.rates.toList())

                        updateCurrencyWitFavorites(favorites, srList)
                        setStateCurrency(result, srList)
                    }

                    is Result.Error -> {
                        setState { copy(error = result.error) }
                    }
                }
            }
        }
    }

    private fun addCurrencyToFavorites(rate: Rate) {
        viewModelScope.launch(Dispatchers.IO) {
            val i = state.value.rates.indexOf(rate)

            if (!rate.selected) {
                state.value.rates[i] = state.value.rates[i].copy(selected = true)
                currenciesUseCase.upsert(state.value.rates[i])
            } else {
                currenciesUseCase.deleteRate(rate)
                state.value.rates[i] = state.value.rates[i].copy(selected = false)
            }
        }
    }
}