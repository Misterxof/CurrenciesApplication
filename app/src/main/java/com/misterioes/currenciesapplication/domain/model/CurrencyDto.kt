package com.misterioes.currenciesapplication.domain.model

data class CurrencyDto(
    val base: String,
    val rates: HashMap<String, Double>
)
