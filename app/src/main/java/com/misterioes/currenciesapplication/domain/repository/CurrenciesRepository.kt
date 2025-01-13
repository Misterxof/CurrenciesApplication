package com.misterioes.currenciesapplication.domain.repository

import com.misterioes.currenciesapplication.domain.model.Rate
import com.misterioes.currenciesapplication.domain.model.Result
import kotlinx.coroutines.flow.Flow

interface CurrenciesRepository {
    suspend fun getCurrency(
        symbols: String, base: String,
    ): Flow<Result>

    suspend fun getAllRates(): Flow<List<Rate>>

    suspend fun upsert(rate: Rate)

    suspend fun delete(rate: Rate)
}