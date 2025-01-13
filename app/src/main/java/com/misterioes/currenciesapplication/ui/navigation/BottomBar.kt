package com.misterioes.currenciesapplication.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import com.misterioes.currenciesapplication.R
import com.misterioes.currenciesapplication.ui.main.MainContract
import com.misterioes.currenciesapplication.ui.theme.Secondary

@Composable
fun BottomBar(
    navController: NavController,
    navBackStackEntry: NavBackStackEntry?,
    onNavigationRequested: (navigationEffect: MainContract.Effect) -> Unit
) {
    BottomNavigation(backgroundColor = MaterialTheme.colors.background) {
        val currentRoute = navBackStackEntry?.destination?.route

        BottomNavigationItem(
            selected = currentRoute == Screen.CurrenciesScreen.route,
            onClick = {
                onNavigationRequested(MainContract.Effect.Navigation.NavigateCurrencies)
            },
            icon = {
                BottomIcon(
                    painter = painterResource(R.drawable.currencies),
                    contentDescription = "Currencies",
                    currentRoute == Screen.CurrenciesScreen.route
                )
            },
            unselectedContentColor = Secondary,
            label = { Text("Currencies") }
        )
        BottomNavigationItem(
            selected = currentRoute == Screen.FavoritesScreen.route,
            onClick = {
                onNavigationRequested(MainContract.Effect.Navigation.NavigateFavorite)
            },
            icon = {
                BottomIcon(
                    painter = painterResource(R.drawable.baseline_star_24),
                    contentDescription = "Favorites",
                    currentRoute == Screen.FavoritesScreen.route
                )
            },
            unselectedContentColor = Secondary,
            label = { Text("Favorites") }
        )
    }
}

@Composable
fun BottomIcon(painter: Painter, contentDescription: String, isSelected: Boolean) {
    if (isSelected) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .background(
                    Secondary.copy(alpha = ContentAlpha.medium),
                    shape = RoundedCornerShape(32.dp)
                )
                .width(64.dp)
        ) {
            Icon(
                painter = painter,
                contentDescription = contentDescription,
                tint = MaterialTheme.colors.primary,
            )
        }
    } else {
        Icon(
            painter = painter,
            contentDescription = contentDescription,
        )
    }
}