package com.misterioes.currenciesapplication.domain.model

sealed class Result {
    data class Success(val currency: Currency) : Result()
    data class Error(val error: kotlin.Exception) : Result()
}