package com.example.surfacecalc.domain

import java.math.BigDecimal
import java.math.BigInteger
import java.math.MathContext
import java.math.RoundingMode

object BigDecimalMath {

    fun sqrt(
        value: BigDecimal,
        mathContext: MathContext = MathConstants.DEFAULT_MATH_CONTEXT
    ): BigDecimal {
        val signum = value.signum()
        if (signum < 0) {
            throw IllegalArgumentException("Cannot compute square root of negative number: $value")
        }
        if (signum == 0) {
            return BigDecimal.ZERO
        }
        if (value.compareTo(BigDecimal.ONE) == 0) {
            return BigDecimal.ONE
        }

        val targetPrecision = mathContext.precision
        val workingPrecision = targetPrecision + 12
        val workContext = MathContext(workingPrecision, RoundingMode.HALF_UP)
        val two = BigDecimal("2")

        var unscaled = value.unscaledValue()
        var scale = value.scale()
        if (scale % 2 != 0) {
            unscaled = unscaled.multiply(BigInteger.TEN)
            scale += 1
        }
        val halfScale = scale / 2

        val bitLength = unscaled.bitLength()
        val seedInt = BigInteger.ONE.shiftLeft((bitLength + 1) / 2)
        var x = BigDecimal(seedInt.max(BigInteger.ONE))
        val v = BigDecimal(unscaled)

        val tolerance = BigDecimal.ONE.scaleByPowerOfTen(-workingPrecision)
        for (i in 0 until 60) {
            val nextX = x.add(v.divide(x, workContext), workContext).divide(two, workContext)
            val diff = (nextX.subtract(x)).abs()
            if (diff <= tolerance || nextX == x) {
                x = nextX
                break
            }
            x = nextX
        }

        val result = x.scaleByPowerOfTen(-halfScale)
        return result.round(mathContext)
    }
}
