package com.example.survivor_mantras.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.survivor_mantras.R
import com.example.survivor_mantras.ui.components.CategorySelector
import com.example.survivor_mantras.ui.components.LanguageSwitcher
import androidx.compose.ui.tooling.preview.Preview
import com.example.survivor_mantras.ui.theme.Survivor_mantrasTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MantraScreen(
    viewModel: MantraViewModel = viewModel(),
    onSettingsClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    if (uiState.showWelcome) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = stringResource(R.string.welcome_message),
                    style = MaterialTheme.typography.displayMedium,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(32.dp))
                Button(onClick = { viewModel.dismissWelcome() }) {
                    Text("OK")
                }
            }
        }
    } else {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(R.string.app_name)) },
                    actions = {
                        IconButton(onClick = onSettingsClick) {
                            Icon(Icons.Default.Settings, contentDescription = stringResource(R.string.settings))
                        }
                        LanguageSwitcher(
                            currentLanguage = uiState.currentLanguage,
                            onLanguageSelected = { viewModel.setLanguage(it) }
                        )
                    }
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                CategorySelector(
                    categories = uiState.categories,
                    selectedCategory = uiState.currentCategory,
                    onCategorySelected = { viewModel.selectCategory(it) },
                    getCategoryTitleRes = { viewModel.getCategoryTitleRes(it) }
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    uiState.currentMantra?.let { mantra ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = if (uiState.currentLanguage == "hu") mantra.textHu else mantra.textEn,
                                style = MaterialTheme.typography.headlineMedium,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            uiState.currentCategory?.let { category ->
                                Text(
                                    text = "- ${stringResource(viewModel.getCategoryTitleRes(category))}",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontStyle = FontStyle.Italic,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            }
                        }
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 48.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Autoplay Controls
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(bottom = 16.dp)
                    ) {
                        var showIntervalDialog by remember { mutableStateOf(false) }

                        Button(
                            onClick = { viewModel.toggleAutoplay() },
                            colors = if (uiState.isAutoplayEnabled) 
                                ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                            else ButtonDefaults.buttonColors()
                        ) {
                            Icon(
                                if (uiState.isAutoplayEnabled) Icons.Default.Stop else Icons.Default.PlayArrow,
                                contentDescription = null
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(if (uiState.isAutoplayEnabled) stringResource(R.string.autoplay_off) else stringResource(R.string.autoplay))
                        }

                        Spacer(Modifier.width(16.dp))

                        TextButton(onClick = { showIntervalDialog = true }) {
                            Text(stringResource(R.string.autoplay_interval, uiState.autoplayIntervalSeconds))
                        }

                        if (showIntervalDialog) {
                            AlertDialog(
                                onDismissRequest = { showIntervalDialog = false },
                                title = { Text(stringResource(R.string.autoplay)) },
                                text = {
                                    Column {
                                        listOf(3, 5, 10, 30).forEach { seconds ->
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(vertical = 8.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                RadioButton(
                                                    selected = uiState.autoplayIntervalSeconds == seconds,
                                                    onClick = { 
                                                        viewModel.setAutoplayInterval(seconds)
                                                        showIntervalDialog = false
                                                    }
                                                )
                                                Text(
                                                    text = stringResource(R.string.autoplay_interval, seconds),
                                                    modifier = Modifier.padding(start = 8.dp)
                                                )
                                            }
                                        }
                                    }
                                },
                                confirmButton = {
                                    TextButton(onClick = { showIntervalDialog = false }) {
                                        Text("Close")
                                    }
                                }
                            )
                        }
                    }

                    Button(
                        onClick = { viewModel.refreshMantra() },
                        enabled = !uiState.isAutoplayEnabled
                    ) {
                        Text(stringResource(R.string.refresh_mantra))
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MantraScreenPreview() {
    Survivor_mantrasTheme {
        MantraScreen(onSettingsClick = {})
    }
}
