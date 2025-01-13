package com.misterioes.currenciesapplication.domain.model

data class Filter(
    val text: String,
    var isSelected: Boolean,
    val sortFunction: (List<Rate>) -> List<Rate>
)

