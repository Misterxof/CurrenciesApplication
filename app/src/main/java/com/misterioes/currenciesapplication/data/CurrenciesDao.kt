package com.misterioes.currenciesapplication.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CurrenciesDao  {
    @Query("SELECT * FROM rateEntity")
    fun getAllRates(): Flow<List<RateEntity>>

    @Upsert
    fun upsertRate(rateEntity: RateEntity)

    @Delete
    fun deleteRate(rateEntity: RateEntity)
}