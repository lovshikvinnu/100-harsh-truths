package com.harshtruths.app.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.favoritesDataStore by preferencesDataStore(name = "favorites_datastore")
private val FAVORITES_KEY = stringSetPreferencesKey("favorite_quote_ids")

class FavoritesDataStore(private val context: Context) {

    val favorites: Flow<Set<Int>> = context.favoritesDataStore.data
        .map { prefs ->
            prefs[FAVORITES_KEY]?.mapNotNull { it.toIntOrNull() }?.toSet() ?: emptySet()
        }

    suspend fun addFavorite(quoteId: Int) {
        context.favoritesDataStore.edit { prefs ->
            val current = prefs[FAVORITES_KEY] ?: emptySet()
            prefs[FAVORITES_KEY] = current + quoteId.toString()
        }
    }

    suspend fun removeFavorite(quoteId: Int) {
        context.favoritesDataStore.edit { prefs ->
            val current = prefs[FAVORITES_KEY] ?: emptySet()
            prefs[FAVORITES_KEY] = current - quoteId.toString()
        }
    }

    suspend fun isFavorite(quoteId: Int): Boolean {
        val prefs = context.favoritesDataStore.data.first()
        return prefs[FAVORITES_KEY]?.contains(quoteId.toString()) ?: false
    }
}
