package com.example.surfacecalc.ui.viewmodel

import com.example.surfacecalc.data.model.AreaUnit
import com.example.surfacecalc.data.model.LengthUnit
import com.example.surfacecalc.data.model.SurfaceAreaResult
import com.example.surfacecalc.data.model.SurfaceCalcHistory
import com.example.surfacecalc.data.model.SurfaceShape
import com.example.surfacecalc.data.model.ThemeMode
import java.math.RoundingMode

data class SurfaceCalcUiState(
    val shape: SurfaceShape = SurfaceShape.CUBE,
    val inputs: Map<String, String> = mapOf("side" to "5"),
    val inputUnits: Map<String, LengthUnit> = mapOf("side" to LengthUnit.CENTIMETER),
    val resultUnit: AreaUnit = AreaUnit.SQUARE_CENTIMETER,
    val result: SurfaceAreaResult? = null,
    val history: List<SurfaceCalcHistory> = emptyList(),
    val decimalPlaces: Int = 6,
    val roundingMode: RoundingMode = RoundingMode.HALF_UP,
    val showFormula: Boolean = true,
    val showCalculationSteps: Boolean = true,
    val showShapeDiagram: Boolean = true,
    val errorMessage: String? = null,
    val isCalculating: Boolean = false,
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val dynamicColor: Boolean = true,
    val hapticFeedback: Boolean = true,
    val animationsEnabled: Boolean = true,
    val confirmHistoryDelete: Boolean = true,
    val preserveLastCalculation: Boolean = true,
    val saveNotification: String? = null
)
