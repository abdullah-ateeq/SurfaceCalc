package com.example.surfacecalc.domain

import java.math.BigDecimal
import java.math.MathContext

object SurfaceAreaCalculator {

    fun calculateCube(
        side: BigDecimal,
        mathContext: MathContext = MathConstants.DEFAULT_MATH_CONTEXT
    ): BigDecimal {
        val sSquared = side.multiply(side, mathContext)
        return MathConstants.SIX.multiply(sSquared, mathContext)
    }

    fun calculateRectangularPrism(
        length: BigDecimal,
        width: BigDecimal,
        height: BigDecimal,
        mathContext: MathContext = MathConstants.DEFAULT_MATH_CONTEXT
    ): BigDecimal {
        val lw = length.multiply(width, mathContext)
        val lh = length.multiply(height, mathContext)
        val wh = width.multiply(height, mathContext)
        val sum = lw.add(lh, mathContext).add(wh, mathContext)
        return MathConstants.TWO.multiply(sum, mathContext)
    }

    fun calculateCylinder(
        radius: BigDecimal,
        height: BigDecimal,
        mathContext: MathContext = MathConstants.DEFAULT_MATH_CONTEXT
    ): BigDecimal {
        val rSquared = radius.multiply(radius, mathContext)
        val basesArea = MathConstants.TWO.multiply(MathConstants.PI, mathContext).multiply(rSquared, mathContext)
        val curvedArea = MathConstants.TWO.multiply(MathConstants.PI, mathContext)
            .multiply(radius, mathContext)
            .multiply(height, mathContext)
        return basesArea.add(curvedArea, mathContext)
    }

    fun calculateSphere(
        radius: BigDecimal,
        mathContext: MathContext = MathConstants.DEFAULT_MATH_CONTEXT
    ): BigDecimal {
        val rSquared = radius.multiply(radius, mathContext)
        return MathConstants.FOUR.multiply(MathConstants.PI, mathContext).multiply(rSquared, mathContext)
    }

    fun calculateCone(
        radius: BigDecimal,
        slantHeight: BigDecimal,
        mathContext: MathContext = MathConstants.DEFAULT_MATH_CONTEXT
    ): BigDecimal {
        val rSquared = radius.multiply(radius, mathContext)
        val baseArea = MathConstants.PI.multiply(rSquared, mathContext)
        val curvedArea = MathConstants.PI.multiply(radius, mathContext).multiply(slantHeight, mathContext)
        return baseArea.add(curvedArea, mathContext)
    }

    fun calculateSquarePyramid(
        baseSide: BigDecimal,
        slantHeight: BigDecimal,
        mathContext: MathContext = MathConstants.DEFAULT_MATH_CONTEXT
    ): BigDecimal {
        val baseArea = baseSide.multiply(baseSide, mathContext)
        val facesArea = MathConstants.TWO.multiply(baseSide, mathContext).multiply(slantHeight, mathContext)
        return baseArea.add(facesArea, mathContext)
    }

    fun calculateTriangularPrism(
        sideA: BigDecimal,
        sideB: BigDecimal,
        sideC: BigDecimal,
        prismLength: BigDecimal,
        mathContext: MathContext = MathConstants.DEFAULT_MATH_CONTEXT
    ): BigDecimal {
        // Semi-perimeter s = (a + b + c) / 2
        val perimeter = sideA.add(sideB, mathContext).add(sideC, mathContext)
        val s = perimeter.divide(MathConstants.TWO, mathContext)

        val sMinusA = s.subtract(sideA, mathContext)
        val sMinusB = s.subtract(sideB, mathContext)
        val sMinusC = s.subtract(sideC, mathContext)

        val radicand = s.multiply(sMinusA, mathContext)
            .multiply(sMinusB, mathContext)
            .multiply(sMinusC, mathContext)

        if (radicand.signum() <= 0) {
            throw IllegalArgumentException("These side lengths cannot form a valid triangle.")
        }

        val triangleArea = BigDecimalMath.sqrt(radicand, mathContext)
        val twoTriangles = MathConstants.TWO.multiply(triangleArea, mathContext)
        val lateralArea = perimeter.multiply(prismLength, mathContext)

        return twoTriangles.add(lateralArea, mathContext)
    }

    fun calculateHemisphere(
        radius: BigDecimal,
        mathContext: MathContext = MathConstants.DEFAULT_MATH_CONTEXT
    ): BigDecimal {
        val rSquared = radius.multiply(radius, mathContext)
        return MathConstants.THREE.multiply(MathConstants.PI, mathContext).multiply(rSquared, mathContext)
    }
}
