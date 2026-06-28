package com.example.survivor_mantras.data

import kotlinx.serialization.Serializable

@Serializable
data class MantraCategory(
    val id: Int,
    val titleRes: String
)

@Serializable
data class Mantra(
    val id: Int,
    val textEn: String,
    val textHu: String,
    val categoryId: Int
)
