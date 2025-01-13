package com.misterioes.currenciesapplication.ui.favorites

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.misterioes.currenciesapplication.R
import com.misterioes.currenciesapplication.domain.model.Rate
import com.misterioes.currenciesapplication.ui.main.MainContract
import com.misterioes.currenciesapplication.ui.theme.CardBackground
import com.misterioes.currenciesapplication.ui.theme.HeaderBackground
import com.misterioes.currenciesapplication.ui.theme.TextDefault
import com.misterioes.currenciesapplication.ui.theme.Yellow
import kotlinx.coroutines.flow.StateFlow

@Composable
fun FavoritesScreen(
    state: StateFlow<FavoritesContract.State>,
    onIntentSend: (event: FavoritesContract.Intent) -> Unit,
    onNavigationRequested: (navigationEffect: MainContract.Effect.Navigation) -> Unit
) {
    val uiState by state.collectAsState()

    Scaffold(
        topBar = { FavoritesHeader() }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            if (uiState.favorites.isNotEmpty())
                FavoritesList(uiState.favorites)
            else Text(
                text = stringResource(R.string.no_fav),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}

@Composable
fun FavoritesHeader() {
    TopAppBar(
        title = {
            Text(text = stringResource(id = R.string.header_favorite))
        },
        backgroundColor = HeaderBackground
    )
}

@Composable
fun FavoritesList(
    list: List<Rate>
) {
    Column(modifier = Modifier) {
        LazyColumn {
            items(list) { favorite ->
                FavoriteListItem(favorite)
            }
        }
    }
}

@Composable
fun FavoriteListItem(
    favorite: Rate,
) {
    Card(
        modifier = Modifier.padding(bottom = 8.dp),
        backgroundColor = CardBackground,
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = 8.dp)
        ) {

            Text(text = "${favorite.base}/${favorite.symbol}", color = TextDefault)

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "${favorite.rate}",
                color = TextDefault,
                style = TextStyle(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(end = 8.dp)
            )

            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = "Favorite",
                tint = Yellow
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FilterListPreview() {
    Scaffold(
        topBar = { FavoritesHeader() }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            val favorite = listOf<Rate>(
                Rate("USD", "BYN", 3.400003, false),
                Rate("USD", "EUR", 0.900323, false),
                Rate("BYN", "RUB", 100.2331, false),
            )

            FavoritesList(favorite)
        }
    }
}