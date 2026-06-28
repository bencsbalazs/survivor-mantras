package com.example.survivor_mantras.ui

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.survivor_mantras.data.Mantra
import com.example.survivor_mantras.data.MantraCategory
import com.example.survivor_mantras.data.MantraRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MantraUiState(
    val currentMantra: Mantra? = null,
    val currentCategory: MantraCategory? = null,
    val categories: List<MantraCategory> = emptyList(),
    val currentLanguage: String = "en",
    val showWelcome: Boolean = true,
    val customMantras: List<Mantra> = emptyList(),
    val isAutoplayEnabled: Boolean = false,
    val autoplayIntervalSeconds: Int = 5
)

class MantraViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = MantraRepository(application)
    private var autoplayJob: Job? = null

    private val _uiState = MutableStateFlow(MantraUiState())
    val uiState: StateFlow<MantraUiState> = _uiState.asStateFlow()

    init {
        val cats = repository.getCategories()
        val currentLocale = AppCompatDelegate.getApplicationLocales().get(0)?.language ?: "en"
        _uiState.update { 
            it.copy(
                categories = cats,
                currentCategory = cats.firstOrNull(),
                currentLanguage = if (currentLocale == "hu") "hu" else "en",
                customMantras = repository.getCustomMantras()
            ) 
        }
    }

    fun getCategoryTitleRes(category: MantraCategory): Int {
        return repository.getTitleResId(category)
    }

    fun dismissWelcome() {
        _uiState.update { it.copy(showWelcome = false) }
        refreshMantra()
    }

    fun selectCategory(category: MantraCategory) {
        _uiState.update { it.copy(currentCategory = category) }
        refreshMantra()
    }

    fun refreshMantra() {
        val state = _uiState.value
        val categoryId = state.currentCategory?.id ?: return
        val mantras = repository.getAllMantrasByCategory(categoryId)
        if (mantras.isNotEmpty()) {
            _uiState.update { it.copy(currentMantra = mantras.random()) }
        }
    }

    fun setLanguage(languageCode: String) {
        val appLocale: LocaleListCompat = LocaleListCompat.forLanguageTags(languageCode)
        AppCompatDelegate.setApplicationLocales(appLocale)
        _uiState.update { it.copy(currentLanguage = languageCode) }
    }

    fun addCustomMantra(textEn: String, textHu: String, categoryId: Int) {
        repository.addCustomMantra(textEn, textHu, categoryId)
        _uiState.update { it.copy(customMantras = repository.getCustomMantras()) }
    }

    fun toggleAutoplay() {
        _uiState.update { it.copy(isAutoplayEnabled = !it.isAutoplayEnabled) }
        if (_uiState.value.isAutoplayEnabled) {
            startAutoplay()
        } else {
            stopAutoplay()
        }
    }

    fun setAutoplayInterval(seconds: Int) {
        _uiState.update { it.copy(autoplayIntervalSeconds = seconds) }
        if (_uiState.value.isAutoplayEnabled) {
            stopAutoplay()
            startAutoplay()
        }
    }

    private fun startAutoplay() {
        autoplayJob?.cancel()
        autoplayJob = viewModelScope.launch {
            while (true) {
                delay(_uiState.value.autoplayIntervalSeconds * 1000L)
                refreshMantra()
            }
        }
    }

    private fun stopAutoplay() {
        autoplayJob?.cancel()
        autoplayJob = null
    }

    override fun onCleared() {
        super.onCleared()
        stopAutoplay()
    }
}
