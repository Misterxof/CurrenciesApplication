package com.misterioes.currenciesapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.misterioes.currenciesapplication.ui.main.MainContract
import com.misterioes.currenciesapplication.ui.main.MainViewModel
import com.misterioes.currenciesapplication.ui.navigation.BottomBar
import com.misterioes.currenciesapplication.ui.navigation.NavGraph
import com.misterioes.currenciesapplication.ui.navigation.Screen
import com.misterioes.currenciesapplication.ui.theme.CurrenciesApplicationTheme
import com.misterioes.currenciesapplication.ui.theme.HeaderBackground
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.consumeAsFlow

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CurrenciesApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    baseContext.deleteDatabase("RateEntity")
                    val viewModel: MainViewModel = hiltViewModel()
                    val navController = rememberNavController()
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val view = LocalView.current
                    val isLight = MaterialTheme.colors.isLight

                    SideEffect {
                        this.window.statusBarColor = HeaderBackground.toArgb()
                        WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars =
                            isLight
                    }

                    Scaffold(
                        bottomBar = {
                            if (navBackStackEntry?.destination?.route != Screen.FiltersScreen.route) {
                                BottomBar(navController, navBackStackEntry) { effect ->
                                    viewModel.setEffect(effect)
                                }
                            }
                        }
                    ) { innerPadding ->
                        NavGraph(
                            navController,
                            Screen.CurrenciesScreen.route,
                            modifier = Modifier.padding(innerPadding)
                        ) { effect ->
                            viewModel.setEffect(effect)
                        }
                    }

                    LaunchedEffect(key1 = Unit) {
                        viewModel.effect.consumeAsFlow().collect() { effect ->
                            when (effect) {
                                MainContract.Effect.Navigation.NavigateBack -> navController.popBackStack()
                                MainContract.Effect.Navigation.NavigateCurrencies -> navController.navigate(
                                    Screen.CurrenciesScreen.route
                                )

                                MainContract.Effect.Navigation.NavigateFavorite -> navController.navigate(
                                    Screen.FavoritesScreen.route
                                )

                                MainContract.Effect.Navigation.NavigateFilters -> navController.navigate(
                                    Screen.FiltersScreen.route
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    CurrenciesApplicationTheme {
        Greeting("Android")
    }
}