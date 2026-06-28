package com.example.survivor_mantras.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.survivor_mantras.R
import com.example.survivor_mantras.ui.components.CategorySelector

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: MantraViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var textEn by remember { mutableStateOf("") }
    var textHu by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(uiState.categories.firstOrNull()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = stringResource(R.string.add_mantra),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                CategorySelector(
                    categories = uiState.categories,
                    selectedCategory = selectedCategory,
                    onCategorySelected = { selectedCategory = it },
                    getCategoryTitleRes = { viewModel.getCategoryTitleRes(it) }
                )
            }

            item {
                OutlinedTextField(
                    value = textEn,
                    onValueChange = { textEn = it },
                    label = { Text(stringResource(R.string.enter_mantra_en)) },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                OutlinedTextField(
                    value = textHu,
                    onValueChange = { textHu = it },
                    label = { Text(stringResource(R.string.enter_mantra_hu)) },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Button(
                        onClick = {
                            val cat = selectedCategory
                            if (cat != null && textEn.isNotBlank() && textHu.isNotBlank()) {
                                viewModel.addCustomMantra(textEn, textHu, cat.id)
                                textEn = ""
                                textHu = ""
                            }
                        },
                        modifier = Modifier.align(Alignment.CenterEnd),
                        enabled = textEn.isNotBlank() && textHu.isNotBlank() && selectedCategory != null
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text(stringResource(R.string.add_mantra))
                    }
                }
            }

            item {
                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
                Text(
                    text = stringResource(R.string.custom_mantras),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            if (uiState.customMantras.isEmpty()) {
                item {
                    Text(
                        text = stringResource(R.string.no_custom_mantras),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            } else {
                items(uiState.customMantras) { mantra ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "EN: ${mantra.textEn}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = "HU: ${mantra.textHu}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            uiState.categories.find { it.id == mantra.categoryId }?.let { cat ->
                                Text(
                                    text = stringResource(viewModel.getCategoryTitleRes(cat)),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
