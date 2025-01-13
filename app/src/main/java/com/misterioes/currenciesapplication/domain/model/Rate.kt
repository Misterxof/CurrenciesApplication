package com.misterioes.currenciesapplication.domain.model

data class Rate(
    val base: String,
    val symbol: String,
    val rate: Double,
    var selected: Boolean
)
