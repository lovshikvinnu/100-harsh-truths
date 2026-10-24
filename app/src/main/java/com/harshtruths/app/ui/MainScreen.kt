package com.harshtruths.app.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.harshtruths.app.R
import com.harshtruths.app.data.FavoritesDataStore
import com.harshtruths.app.data.QuoteRepository
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    quoteRepository: QuoteRepository,
    favoritesDataStore: FavoritesDataStore,
    onNavigateToFavorites: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val quotes by quoteRepository.quotes.collectAsState()
    val todayQuote by quoteRepository.todayQuote.collectAsState()
    val favorites by favoritesDataStore.favorites.collectAsState(initial = emptySet())
    val (unlocked, total) = quoteRepository.getProgress()
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.app_name), fontWeight = FontWeight.Medium) },
                actions = {
                    IconButton(onClick = onNavigateToFavorites) {
                        Icon(
                            painter = painterResource(R.drawable.ic_bookmark),
                            contentDescription = "Favorites"
                        )
                    }
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(
                            painter = painterResource(R.drawable.ic_settings),
                            contentDescription = "Settings"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        }
    ) { padding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (quotes.isEmpty()) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Loading...",
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else if (todayQuote != null) {
                    val currentQuote = todayQuote!!
                    val isFavorite = favorites.contains(currentQuote.id)

                    ProgressIndicator(unlocked, total)

                    QuoteCard(
                        quote = currentQuote,
                        isFavorite = isFavorite,
                        onFavoriteClick = {
                            coroutineScope.launch {
                                if (isFavorite) {
                                    favoritesDataStore.removeFavorite(currentQuote.id)
                                } else {
                                    favoritesDataStore.addFavorite(currentQuote.id)
                                }
                            }
                        },
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )

                    QuoteNavigationControls(
                        currentId = currentQuote.id,
                        total = total,
                        onPreviousClick = {
                            quoteRepository.navigateToPrevious()
                        },
                        onNextClick = {
                            quoteRepository.navigateToNext()
                        }
                    )
                } else {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "No quote available",
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
