package com.misterioes.currenciesapplication.ui.currencies

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedCard
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.misterioes.currenciesapplication.R
import com.misterioes.currenciesapplication.domain.model.Currencies
import com.misterioes.currenciesapplication.domain.model.Filter
import com.misterioes.currenciesapplication.domain.model.Rate
import com.misterioes.currenciesapplication.ui.main.MainContract
import com.misterioes.currenciesapplication.ui.theme.CardBackground
import com.misterioes.currenciesapplication.ui.theme.HeaderBackground
import com.misterioes.currenciesapplication.ui.theme.Secondary
import com.misterioes.currenciesapplication.ui.theme.TextDefault
import com.misterioes.currenciesapplication.ui.theme.Yellow
import kotlinx.coroutines.flow.StateFlow
import java.net.ConnectException
import java.net.SocketTimeoutException

@Composable
fun CurrenciesScreen(
    state: StateFlow<CurrenciesContract.State>,
    onIntentSend: (event: CurrenciesContract.Intent) -> Unit,
    onNavigationRequested: (navigationEffect: MainContract.Effect.Navigation) -> Unit
) {
    val uiState by state.collectAsState()

    Scaffold(
        topBar = {
            CurrenciesHeader()
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (uiState.error == null && !uiState.isLoading) {
                Row(
                    modifier = Modifier
                        .background(HeaderBackground)
                ) {
                    Spinner(uiState.allCurrencies, uiState.selectedItem,
                        {
                            onIntentSend(
                                CurrenciesContract.Intent.GetCurrency(
                                    Currencies.valueOf(
                                        it
                                    )
                                )
                            )
                        }) {
                        onNavigationRequested(MainContract.Effect.Navigation.NavigateFilters)
                    }
                }
                Column(
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .padding(horizontal = 16.dp)
                ) {
                    if (uiState.currency != null) {
                        CurrenciesList(uiState.rates, uiState.filter!!) {
                            onIntentSend(CurrenciesContract.Intent.AddCurrencyToFavorites(it))
                        }
                    }
                }
            } else if (uiState.error != null) {
                when (uiState.error) {
                    is SocketTimeoutException -> {
                        ErrorText(stringResource(R.string.no_connection))
                        onIntentSend(CurrenciesContract.Intent.RetryConnection)
                    }

                    is ConnectException -> {
                        ErrorText(stringResource(R.string.no_connection))
                        onIntentSend(CurrenciesContract.Intent.RetryConnection)
                    }

                    else -> {
                        ErrorText(stringResource(R.string.error))
                    }
                }
            } else {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .padding(paddingValues)
                        .padding(8.dp)
                        .fillMaxWidth()
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.width(64.dp),
                        color = Secondary,
                        trackColor = HeaderBackground,
                    )
                }
            }
        }
    }
}

@Composable
fun ErrorText(text: String) {
    Column(modifier = Modifier.padding(8.dp)) {
        Text(
            text = text,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}

@Composable
fun CurrenciesHeader() {
    TopAppBar(
        title = {
            Column {
                Text(
                    text = stringResource(id = R.string.header_currencies)
                )
            }
        },
        backgroundColor = HeaderBackground,
        elevation = 0.dp
    )
}

@Composable
fun CurrenciesList(
    list: SnapshotStateList<Rate>,
    filter: Filter,
    onItemClick: (Rate) -> Unit
) {
    Column(modifier = Modifier) {
        LazyColumn {
            items(list) { rate ->
                CurrenciesListItem(rate) {
                    onItemClick(rate)
                }
            }
        }
    }
}

@Composable
fun CurrenciesListItem(
    rate: Rate,
    onItemClick: () -> Unit
) {
    Card(
        backgroundColor = CardBackground,
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .padding(bottom = 8.dp)
            .clickable {
                onItemClick()
            }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = 8.dp)
        ) {

            Text(text = rate.symbol, color = TextDefault)

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "${rate.rate}",
                color = TextDefault,
                style = TextStyle(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(end = 8.dp)
            )

            if (rate.selected) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Favorite",
                    tint = Yellow
                )
            } else {
                Icon(
                    painter = painterResource(R.drawable.baseline_star_outline_24),
                    contentDescription = "Favorite",
                    tint = Secondary
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Spinner(
    items: List<String>,
    selectedItem: String,
    onItemSelected: (String) -> Unit,
    onFilterButtonClick: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .padding(bottom = 8.dp)
            .height(IntrinsicSize.Min)
            .fillMaxWidth()
    ) {
        OutlinedCard(
            border = BorderStroke(width = 1.dp, color = Secondary),
            colors = CardDefaults.outlinedCardColors(
                containerColor = MaterialTheme.colors.background
            ),
            modifier = Modifier
                .padding(end = 8.dp)
                .width(IntrinsicSize.Min)
                .weight(9f)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clickable { expanded = true }
                    .fillMaxWidth()
                    .fillMaxHeight()

            ) {
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = {
                        expanded = !expanded
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(9f)

                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clickable { expanded = true }
                            .fillMaxWidth()
                    ) {
                        Text(
                            text = selectedItem,
                            modifier = Modifier
                                .padding(start = 8.dp)
                                .weight(3f)
                                .menuAnchor(type = MenuAnchorType.PrimaryEditable)
                        )
                        Icon(
                            imageVector =
                            if (expanded) Icons.Default.KeyboardArrowUp
                            else Icons.Default.KeyboardArrowDown,
                            contentDescription = "Arrow down",
                            tint = MaterialTheme.colors.primary,
                            modifier = Modifier
                                .weight(0.8f)
                        )
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            containerColor = MaterialTheme.colors.background,
                            border = BorderStroke(width = 1.dp, color = Secondary)
                        ) {
                            items.forEach { item ->
                                DropdownMenuItem(
                                    modifier =
                                    if (item == selectedItem)
                                        Modifier.background(Secondary)
                                    else Modifier,
                                    text = { Text(text = item) },
                                    onClick = {
                                        //  selectedItem = item
                                        onItemSelected(item)
                                        expanded = false
                                    })
                            }
                        }
                    }
                }
            }
        }

        OutlinedCard(
            border = BorderStroke(width = 1.dp, color = Secondary),
            colors = CardDefaults.outlinedCardColors(
                containerColor = MaterialTheme.colors.background
            ),
            modifier = Modifier
                .width(IntrinsicSize.Min)
                .weight(1.5f)
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(
                    modifier = Modifier
                        .fillMaxHeight(),
                    onClick = { onFilterButtonClick() }
                ) {
                    Icon(
                        painter = painterResource(R.drawable.filter),
                        contentDescription = "Back",
                        tint = MaterialTheme.colors.primary
                    )
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun CurrenciesListPreview() {

    val filters = listOf(
        Filter("Code A-Z", true) { it.sortedBy { it.symbol } },
        Filter("Code Z-A", false) { it.sortedByDescending { it.symbol } },
        Filter("Quote Asc.", false) { it.sortedBy { it.rate } },
        Filter("Quote Desc.", false) { it.sortedByDescending { it.rate } },
    )
    val allCurrencies = Currencies.entries.map { it.name }
    var selectedFilter: Filter = filters[0]

    Scaffold(
        topBar = {
            CurrenciesHeader()
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(paddingValues)
        ) {

            val rate = hashSetOf(
                Rate("USD", "GBP", 0.805145, false),
                Rate("USD", "JPY", 157.161003, false),
                Rate("USD", "EUR", 0.970375, true),
                Rate("USD", "BYN", 3.267245, false)
            )

            var selectedItem by remember { mutableStateOf(Currencies.USD.name) }

            Row(
                modifier = Modifier
                    .background(HeaderBackground)
            ) {
                Spinner(allCurrencies, selectedItem, {}, {})
            }
            Column(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .padding(horizontal = 16.dp)
            ) {
                // CurrenciesList(rate.toList(), selectedFilter) {}
            }
        }
    }
}