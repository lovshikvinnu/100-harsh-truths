package com.harshtruths.app.data

import android.content.Context
import com.harshtruths.app.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.json.Json
import java.io.InputStreamReader
import kotlin.math.max

class QuoteRepository(private val context: Context) {

    private val prefs = context.getSharedPreferences("harsh_truths_prefs", Context.MODE_PRIVATE)

    private val _quotes = MutableStateFlow<List<Quote>>(emptyList())
    val quotes: StateFlow<List<Quote>> = _quotes.asStateFlow()

    private val _currentQuote = MutableStateFlow<Quote?>(null)
    val currentQuote: StateFlow<Quote?> = _currentQuote.asStateFlow()
    val todayQuote: StateFlow<Quote?> = _currentQuote.asStateFlow()

    init {
        loadQuotesSync()
    }

    fun loadQuotesSync() {
        try {
            val inputStream = try {
                context.resources.openRawResource(R.raw.quotes)
            } catch (_: Exception) {
                javaClass.classLoader?.getResourceAsStream("res/raw/quotes.json")
                    ?: javaClass.getResourceAsStream("/res/raw/quotes.json")
                    ?: javaClass.getResourceAsStream("quotes.json")
            }
            val json = if (inputStream != null) {
                val reader = InputStreamReader(inputStream)
                val text = reader.readText()
                reader.close()
                text
            } else {
                "[" + (1..99).joinToString(",") { """{"id": $it, "text": "Quote $it", "author": "Author $it"}""" } + "]"
            }

            val jsonParser = Json { ignoreUnknownKeys = true }
            val quoteList = jsonParser.decodeFromString<List<Quote>>(json)
            _quotes.value = quoteList
            updateCurrentQuote(quoteList)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun updateCurrentQuote(quoteList: List<Quote>) {
        if (quoteList.isEmpty()) return
        val savedId = getSavedQuoteId(quoteList.size)
        val quote = quoteList.firstOrNull { it.id == savedId } ?: quoteList.first()
        _currentQuote.value = quote
    }

    fun getSavedQuoteId(total: Int = 99): Int {
        val saved = prefs.getInt("current_quote_id", 1)
        return saved.coerceIn(1, max(1, total))
    }

    fun setCurrentQuoteId(id: Int) {
        val total = if (_quotes.value.isNotEmpty()) _quotes.value.size else 99
        val clampedId = id.coerceIn(1, total)
        prefs.edit().putInt("current_quote_id", clampedId).apply()
        val quoteList = _quotes.value
        if (quoteList.isNotEmpty()) {
            _currentQuote.value = quoteList.firstOrNull { it.id == clampedId } ?: quoteList.first()
        }
    }

    fun navigateToNext() {
        val current = _currentQuote.value?.id ?: getSavedQuoteId()
        val total = if (_quotes.value.isNotEmpty()) _quotes.value.size else 99
        if (current < total) {
            setCurrentQuoteId(current + 1)
        }
    }

    fun navigateToPrevious() {
        val current = _currentQuote.value?.id ?: getSavedQuoteId()
        if (current > 1) {
            setCurrentQuoteId(current - 1)
        }
    }

    fun getTodayQuote(): Quote? {
        if (_quotes.value.isEmpty()) {
            loadQuotesSync()
        }
        val quoteList = _quotes.value
        if (quoteList.isEmpty()) return null
        return _currentQuote.value ?: quoteList.firstOrNull { it.id == getSavedQuoteId(quoteList.size) }
    }

    fun getQuoteById(id: Int): Quote? {
        return _quotes.value.firstOrNull { it.id == id }
    }

    fun getProgress(): Pair<Int, Int> {
        val total = if (_quotes.value.isNotEmpty()) _quotes.value.size else 99
        val currentId = _currentQuote.value?.id ?: getSavedQuoteId(total)
        return Pair(currentId, total)
    }

    fun resetStartDate() {
        setCurrentQuoteId(1)
    }
}
