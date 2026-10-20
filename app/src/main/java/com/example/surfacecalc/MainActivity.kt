package com.example.surfacecalc

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.surfacecalc.data.repository.SettingsRepository
import com.example.surfacecalc.data.repository.SurfaceCalcHistoryRepository
import com.example.surfacecalc.ui.navigation.SurfaceCalcApp
import com.example.surfacecalc.ui.theme.SurfaceCalcTheme
import com.example.surfacecalc.ui.viewmodel.SurfaceCalcViewModel
import com.example.surfacecalc.ui.viewmodel.SurfaceCalcViewModelFactory

class MainActivity : ComponentActivity() {

    private val settingsRepository by lazy { SettingsRepository(applicationContext) }
    private val historyRepository by lazy { SurfaceCalcHistoryRepository(applicationContext) }

    private val viewModel: SurfaceCalcViewModel by viewModels {
        SurfaceCalcViewModelFactory(settingsRepository, historyRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val state by viewModel.uiState.collectAsState()
            SurfaceCalcTheme(
                themeMode = state.themeMode,
                dynamicColor = state.dynamicColor
            ) {
                SurfaceCalcApp(viewModel = viewModel)
            }
        }
    }
}
