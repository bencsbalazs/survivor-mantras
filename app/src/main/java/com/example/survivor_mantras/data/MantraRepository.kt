package com.example.survivor_mantras.data

import android.content.Context
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

class MantraRepository(private val context: Context) {
    private var staticMantras: List<Mantra> = emptyList()
    private var categories: List<MantraCategory> = emptyList()
    private var customMantras: List<Mantra> = emptyList()
    
    private val customMantrasFile = File(context.filesDir, "custom_mantras.json")

    init {
        loadStaticData()
        loadCustomMantras()
    }

    private fun loadStaticData() {
        try {
            val mantrasJson = context.assets.open("mantras.json").bufferedReader().use { it.readText() }
            staticMantras = Json.decodeFromString(mantrasJson)

            val categoriesJson = context.assets.open("categories.json").bufferedReader().use { it.readText() }
            categories = Json.decodeFromString(categoriesJson)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun loadCustomMantras() {
        if (customMantrasFile.exists()) {
            try {
                val json = customMantrasFile.readText()
                customMantras = Json.decodeFromString(json)
            } catch (e: Exception) {
                e.printStackTrace()
                customMantras = emptyList()
            }
        }
    }

    private fun saveCustomMantras() {
        try {
            val json = Json.encodeToString(customMantras)
            customMantrasFile.writeText(json)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getCategories(): List<MantraCategory> = categories

    fun getTitleResId(category: MantraCategory): Int {
        return context.resources.getIdentifier(category.titleRes, "string", context.packageName)
    }

    fun getCustomMantras(): List<Mantra> = customMantras

    fun getAllMantrasByCategory(categoryId: Int): List<Mantra> {
        return (staticMantras + customMantras).filter { it.categoryId == categoryId }
    }

    fun getRandomMantraIncludingCustom(categoryId: Int): Mantra? {
        val filtered = getAllMantrasByCategory(categoryId)
        return if (filtered.isNotEmpty()) filtered.random() else null
    }

    fun addCustomMantra(textEn: String, textHu: String, categoryId: Int) {
        val nextId = (staticMantras.maxByOrNull { it.id }?.id ?: 0) + (customMantras.maxByOrNull { it.id }?.id ?: 0) + 1
        val newMantra = Mantra(nextId, textEn, textHu, categoryId)
        customMantras = customMantras + newMantra
        saveCustomMantras()
    }
}
