package com.misterioes.currenciesapplication.domain.model

data class Currency(
    val base: String,
    val rates: HashSet<Rate>,
)