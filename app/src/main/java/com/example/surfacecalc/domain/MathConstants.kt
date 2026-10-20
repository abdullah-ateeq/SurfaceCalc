package com.example.surfacecalc.domain

import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode

object MathConstants {
    val PI = BigDecimal("3.1415926535897932384626433832795028841971693993751")
    val TWO = BigDecimal("2")
    val THREE = BigDecimal("3")
    val FOUR = BigDecimal("4")
    val SIX = BigDecimal("6")
    val HALF = BigDecimal("0.5")

    val DEFAULT_MATH_CONTEXT = MathContext(50, RoundingMode.HALF_UP)
}
