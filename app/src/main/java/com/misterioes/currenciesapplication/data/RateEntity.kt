package com.misterioes.currenciesapplication.data

import androidx.room.Entity

@Entity(primaryKeys = ["base", "symbol"])
data class RateEntity(
    val base: String,
    val symbol: String,
    val rate: Double,
    var selected: Boolean
)