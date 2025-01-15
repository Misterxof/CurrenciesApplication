package com.misterioes.currenciesapplication.domain.usecase

import com.misterioes.currenciesapplication.domain.model.Currencies
import com.misterioes.currenciesapplication.domain.model.Currency
import com.misterioes.currenciesapplication.domain.model.Filter
import com.misterioes.currenciesapplication.domain.model.Rate
import com.misterioes.currenciesapplication.domain.model.Result
import com.misterioes.currenciesapplication.domain.repository.CurrenciesRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CurrenciesUseCase @Inject constructor(private val repository: CurrenciesRepository) {
    val allCurrencies = Currencies.entries.map { it.name }

    private val filters = listOf(
        Filter("Code A-Z", true) { it.sortedBy { it.symbol } },
        Filter("Code Z-A", false) { it.sortedByDescending { it.symbol } },
        Filter("Quote Asc.", false) { it.sortedBy { it.rate } },
        Filter("Quote Desc.", false) { it.sortedByDescending { it.rate } },
    )

    private val _filter = MutableStateFlow<Filter>(filters[0])
    val filter = _filter.asStateFlow()

    fun setFilter(filter: Filter) {
        CoroutineScope(Job() + Dispatchers.IO).launch {
            _filter.emit(filter)
        }
    }

    fun getFilters(): List<Filter> {
        return filters
    }

    suspend fun getCurrency(currency: Currencies): Flow<Result> {
        return repository.getCurrency(getSymbols(currency), currency.name)
    }

    suspend fun getFavorites(): Flow<List<Rate>> {
        return repository.getAllRates()
    }

    suspend fun upsert(rate: Rate) {
        repository.upsert(rate)
    }

    suspend fun deleteRate(rate: Rate) {
        repository.delete(rate)
    }

    fun getSymbols(currency: Currencies): String {
        when (currency) {
            Currencies.USD -> return "EUR,GBP,BYN,JPY"
            Currencies.EUR -> return "USD,GBP,BYN,JPY"
            Currencies.BYN -> return "EUR,GBP,USD,JPY"
            Currencies.GBP -> return "EUR,USD,BYN,JPY"
            Currencies.JPY -> return "EUR,GBP,BYN,USD"
        }
    }
}