package com.harshtruths.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.harshtruths.app.data.FavoritesDataStore
import com.harshtruths.app.data.QuoteRepository
import com.harshtruths.app.data.SettingsDataStore
import com.harshtruths.app.ui.FavoritesScreen
import com.harshtruths.app.ui.MainScreen
import com.harshtruths.app.ui.SettingsScreen
import com.harshtruths.app.ui.theme.HarshTruthsTheme

class MainViewModel(
    val quoteRepository: QuoteRepository,
    val favoritesDataStore: FavoritesDataStore,
    val settingsDataStore: SettingsDataStore
) : ViewModel()

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels {
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return MainViewModel(
                    quoteRepository = QuoteRepository(this@MainActivity),
                    favoritesDataStore = FavoritesDataStore(this@MainActivity),
                    settingsDataStore = SettingsDataStore(this@MainActivity)
                ) as T
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var currentScreen by remember { mutableStateOf(Screen.Main) }
            HarshTruthsTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavHost(
                        viewModel = viewModel,
                        currentScreen = currentScreen,
                        onScreenChange = { currentScreen = it }
                    )
                }
            }
        }
    }
}

enum class Screen {
    Main, Favorites, Settings
}

@Composable
fun AppNavHost(
    viewModel: MainViewModel,
    currentScreen: Screen,
    onScreenChange: (Screen) -> Unit
) {
    when (currentScreen) {
        Screen.Main -> MainScreen(
            quoteRepository = viewModel.quoteRepository,
            favoritesDataStore = viewModel.favoritesDataStore,
            onNavigateToFavorites = { onScreenChange(Screen.Favorites) },
            onNavigateToSettings = { onScreenChange(Screen.Settings) }
        )
        Screen.Favorites -> FavoritesScreen(
            quoteRepository = viewModel.quoteRepository,
            favoritesDataStore = viewModel.favoritesDataStore,
            onNavigateBack = { onScreenChange(Screen.Main) }
        )
        Screen.Settings -> SettingsScreen(
            quoteRepository = viewModel.quoteRepository,
            settingsDataStore = viewModel.settingsDataStore,
            onNavigateBack = { onScreenChange(Screen.Main) }
        )
    }
}
