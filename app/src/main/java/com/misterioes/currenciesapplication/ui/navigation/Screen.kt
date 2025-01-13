package com.misterioes.currenciesapplication.ui.navigation

sealed class Screen(val route: String) {
    object CurrenciesScreen : Screen("Currencies")
    object FavoritesScreen : Screen("Favorites")
    object FiltersScreen : Screen("Filters")
}