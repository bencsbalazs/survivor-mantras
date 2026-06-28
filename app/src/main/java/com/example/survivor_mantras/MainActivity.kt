package com.example.survivor_mantras

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.survivor_mantras.ui.MantraScreen
import com.example.survivor_mantras.ui.MantraViewModel
import com.example.survivor_mantras.ui.SettingsScreen
import com.example.survivor_mantras.ui.theme.Survivor_mantrasTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Survivor_mantrasTheme {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    val viewModel: MantraViewModel = viewModel()
                    var showSettings by remember { mutableStateOf(false) }

                    if (showSettings) {
                        SettingsScreen(
                            viewModel = viewModel,
                            onBack = { showSettings = false }
                        )
                    } else {
                        MantraScreen(
                            viewModel = viewModel,
                            onSettingsClick = { showSettings = true }
                        )
                    }
                }
            }
        }
    }
}
