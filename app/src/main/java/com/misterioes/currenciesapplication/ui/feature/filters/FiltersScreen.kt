package com.misterioes.currenciesapplication.ui.feature.filters

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.RadioButton
import androidx.compose.material.RadioButtonDefaults
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.misterioes.currenciesapplication.R
import com.misterioes.currenciesapplication.domain.model.Filter
import com.misterioes.currenciesapplication.domain.usecase.CurrenciesUseCase
import com.misterioes.currenciesapplication.ui.main.MainContract
import com.misterioes.currenciesapplication.ui.navigation.Screen
import com.misterioes.currenciesapplication.ui.theme.HeaderBackground
import com.misterioes.currenciesapplication.ui.theme.Secondary
import com.misterioes.currenciesapplication.ui.theme.TextDefault
import com.misterioes.currenciesapplication.ui.theme.TextSecondary
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.receiveAsFlow

@Composable
fun FiltersScreen(
    state: StateFlow<FiltersContract.State>,
    onIntentSend: (event: FiltersContract.Intent) -> Unit,
    effect: ReceiveChannel<FiltersContract.Effect>?,
    onNavigationRequested: (navigationEffect: MainContract.Effect.Navigation) -> Unit
) {
    val uiState by state.collectAsState()

    Scaffold(
        topBar = { FiltersHeader() { onNavigationRequested(MainContract.Effect.Navigation.NavigateCurrencies) } }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            val context = LocalContext.current

            LaunchedEffect(key1 = Unit) {
                effect?.consumeAsFlow()?.collect() { effect ->
                    when (effect) {
                        is FiltersContract.Effect.ShowFilter -> {
                            Toast.makeText(context, "${effect.text} is selected", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }

            FiltersList(uiState.filters, uiState.selectedFilter) {
                onIntentSend(FiltersContract.Intent.SelectFilter(it))
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { onIntentSend(FiltersContract.Intent.ApplyFilter) },
                shape = RoundedCornerShape(64.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("Apply")
            }
        }
    }

}

@Composable
fun FiltersHeader(
    onBackClick: () -> Unit
) {
    TopAppBar(
        title = {
            Text(text = stringResource(id = R.string.header_filters))
        },
        navigationIcon = {
            IconButton(onClick = { onBackClick() }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colors.primary
                )
            }
        },
        backgroundColor = HeaderBackground
    )
}

@Composable
fun FiltersList(
    list: List<Filter>,
    selectedFilter: Filter?,
    onItemClick: (Filter) -> Unit
) {
    Column(modifier = Modifier) {
        Text(
            text = "Sort by",
            color = TextSecondary,
            style = MaterialTheme.typography.subtitle2
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn {
            items(list) { filter ->
                FilterListItem(filter, selectedFilter) {
                    onItemClick(filter)
                }
            }
        }
    }
}

@Composable
fun FilterListItem(
    filter: Filter,
    selectedFilter: Filter?,
    onItemClick: () -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onItemClick() }
            .padding(vertical = 8.dp)) {

        Text(text = filter.text, color = TextDefault)

        Spacer(modifier = Modifier.weight(1f))

        RadioButton(
            selected = filter == selectedFilter,
            colors = RadioButtonDefaults.colors(
                selectedColor = MaterialTheme.colors.primary,
                unselectedColor = Secondary
            ),
            onClick = onItemClick
        )
    }
}

@Preview(showBackground = true)
@Composable
fun FilterListPreview() {
    val filters = listOf(
        Filter("Code A-Z", true) { it.sortedBy { it.symbol } },
        Filter("Code Z-A", false) { it.sortedByDescending { it.symbol } },
        Filter("Quote Asc.", false) { it.sortedBy { it.rate } },
        Filter("Quote Desc.", false) { it.sortedByDescending { it.rate } },
    )

    Scaffold(
        topBar = { FiltersHeader() {} }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {

            FiltersList(filters, filters[2]) {}

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { },
                shape = RoundedCornerShape(64.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("Apply")
            }
        }
    }
}