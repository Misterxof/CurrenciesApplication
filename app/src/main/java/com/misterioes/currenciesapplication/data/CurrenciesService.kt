package com.misterioes.currenciesapplication.data

import com.misterioes.currenciesapplication.domain.model.CurrencyDto
import retrofit2.http.GET
import retrofit2.http.Query

interface CurrenciesService {
    @GET("/exchangerates_data/latest")
    suspend fun getCurrency(
        @Query("symbols") symbols: String,
        @Query("base") base: String,
        ) : CurrencyDto
}