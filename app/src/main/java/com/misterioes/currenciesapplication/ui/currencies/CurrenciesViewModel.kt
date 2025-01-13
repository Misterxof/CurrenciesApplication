package com.misterioes.currenciesapplication.ui.currencies

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.viewModelScope
import com.misterioes.currenciesapplication.domain.combineAndAwait
import com.misterioes.currenciesapplication.domain.model.Currencies
import com.misterioes.currenciesapplication.domain.model.Rate
import com.misterioes.currenciesapplication.domain.model.Result
import com.misterioes.currenciesapplication.domain.usecase.CurrenciesUseCase
import com.misterioes.currenciesapplication.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CurrenciesViewModel @Inject constructor(private val currenciesUseCase: CurrenciesUseCase) :
    BaseViewModel<CurrenciesContract.Intent, CurrenciesContract.State, CurrenciesContract.Effect>() {
    init {
        getCurrency()
        setIntent(CurrenciesContract.Intent.GetAllCurrencies)
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
            getCurrency()
        }
    }

    private fun getAllCurrencies() {
        val currencies = currenciesUseCase.allCurrencies
        setState { copy(allCurrencies = currencies) }
    }

    private fun getCurrency() {
        if (currenciesUseCase.currentCurrency == null) {
            setIntent(CurrenciesContract.Intent.GetCurrency(Currencies.USD))
        } else {
            viewModelScope.launch(Dispatchers.IO) {
                currenciesUseCase.getFavorites().collect { favorites ->
                    val srList =
                        currenciesUseCase.selectedFilter.sortFunction(currenciesUseCase.currentCurrency!!.rates.toList())

                    if (favorites.isNotEmpty()) {
                        for (i in srList.indices) {
                            if (favorites.find { fav -> fav.base == srList[i].base && fav.symbol == srList[i].symbol } == null) {
                                srList[i].selected = false
                            } else {
                                srList[i].selected = true
                            }
                        }
                    } else
                        srList.map { it.selected = false }

                    val list = mutableStateListOf<Rate>().apply { addAll(srList) }

                    setState {
                        copy(
                            isLoading = false,
                            currency = currenciesUseCase.currentCurrency,
                            selectedItem = currenciesUseCase.currentCurrency!!.base,
                            rates = list,
                            filter = currenciesUseCase.selectedFilter,
                            error = null
                        )
                    }
                }
            }
        }
    }

    private fun getCurrency(currency: Currencies) {
        getListWithFavs(currency)
    }

    private fun setStateCurrency(result: Result.Success, list: List<Rate>) {
        val list = mutableStateListOf<Rate>().apply { addAll(list) }
        setState {
            copy(
                isLoading = false,
                currency = result.currency,
                selectedItem = result.currency.base,
                rates = list,
                filter = currenciesUseCase.selectedFilter,
                error = null
            )
        }
        currenciesUseCase.currentCurrency = result.currency
    }

    private fun getListWithFavs(currency: Currencies) {
        viewModelScope.launch(Dispatchers.IO) {
            combineAndAwait(
                currenciesUseCase.getCurrency(currency),
                currenciesUseCase.getFavorites()
            ).collect { (result, favorites) ->
                when (result) {
                    is Result.Success -> {
                        val srList =
                            currenciesUseCase.selectedFilter.sortFunction(result.currency.rates.toList())
                        if (favorites.isNotEmpty()) {
                            for (i in srList.indices) {
                                if (favorites.find { fav -> fav.base == srList[i].base && fav.symbol == srList[i].symbol } == null)
                                    srList[i].selected = false
                                else srList[i].selected = true
                            }
                        } else
                            srList.map { it.selected = false }

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