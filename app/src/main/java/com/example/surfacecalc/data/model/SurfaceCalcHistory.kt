package com.example.surfacecalc.data.model

data class SurfaceCalcHistory(
    val id: String,
    val shape: SurfaceShape,
    val inputs: Map<String, String>,
    val inputUnits: Map<String, LengthUnit>,
    val resultValue: String,
    val resultUnit: AreaUnit,
    val calculatedAt: Long
)
