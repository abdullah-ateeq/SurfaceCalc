package com.example.surfacecalc.domain

import com.example.surfacecalc.data.model.SurfaceCalcError
import java.math.BigDecimal

class SurfaceCalcException(val error: SurfaceCalcError, override val message: String = error.userMessage()) : Exception(message)

object SurfaceCalcParser {

    private const val MAX_DIGITS = 500

    fun parseDimension(input: String): Result<BigDecimal> {
        val trimmed = input.trim()
        if (trimmed.isEmpty()) {
            return Result.failure(SurfaceCalcException(SurfaceCalcError.EmptyInput))
        }

        // Count digits
        val digitCount = trimmed.count { it.isDigit() }
        if (digitCount > MAX_DIGITS || trimmed.length > MAX_DIGITS + 10) {
            return Result.failure(SurfaceCalcException(SurfaceCalcError.InputTooLong))
        }

        // Handle leading/trailing dot or pure non-digits
        var sanitized = trimmed
        if (sanitized.startsWith(".")) {
            sanitized = "0$sanitized"
        } else if (sanitized.startsWith("-.")) {
            sanitized = "-0${sanitized.substring(2)}"
        } else if (sanitized.startsWith("+.")) {
            sanitized = "0${sanitized.substring(2)}"
        }

        if (sanitized.endsWith(".")) {
            sanitized = "${sanitized}0"
        }

        val parsed = try {
            BigDecimal(sanitized)
        } catch (_: NumberFormatException) {
            return Result.failure(SurfaceCalcException(SurfaceCalcError.InvalidNumber))
        }

        if (parsed.signum() <= 0) {
            return Result.failure(SurfaceCalcException(SurfaceCalcError.NonPositiveValue))
        }

        return Result.success(parsed)
    }
}
