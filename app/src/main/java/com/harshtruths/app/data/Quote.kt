package com.harshtruths.app.data

import kotlinx.serialization.Serializable

@Serializable
data class Quote(
    val id: Int,
    val text: String,
    val author: String
)