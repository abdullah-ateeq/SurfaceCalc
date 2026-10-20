package com.example.surfacecalc

import com.example.surfacecalc.domain.BigDecimalMath
import com.example.surfacecalc.domain.MathConstants
import com.example.surfacecalc.domain.SurfaceAreaCalculator
import java.math.BigDecimal
import java.math.RoundingMode
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SurfaceAreaCalculatorTest {

    private val mc = MathConstants.DEFAULT_MATH_CONTEXT

    // 1. Cube
    @Test
    fun testCubeSurfaceArea() {
        // Side = 5 -> A = 6 * 5^2 = 150
        val side = BigDecimal("5")
        val area = SurfaceAreaCalculator.calculateCube(side, mc)
        assertEquals(0, BigDecimal("150").compareTo(area))
    }

    // 2. Rectangular Prism
    @Test
    fun testRectangularPrismSurfaceArea() {
        // Length = 10, Width = 5, Height = 4 -> A = 2(50 + 40 + 20) = 220
        val l = BigDecimal("10")
        val w = BigDecimal("5")
        val h = BigDecimal("4")
        val area = SurfaceAreaCalculator.calculateRectangularPrism(l, w, h, mc)
        assertEquals(0, BigDecimal("220").compareTo(area))
    }

    // 3. Cylinder
    @Test
    fun testCylinderSurfaceArea() {
        // Radius = 3, Height = 10 -> A = 2*pi*3^2 + 2*pi*3*10 = 18*pi + 60*pi = 78*pi
        // 78 * 3.14159265358979323846... ≈ 245.04422698...
        // At 6 decimals: 245.044227
        val r = BigDecimal("3")
        val h = BigDecimal("10")
        val area = SurfaceAreaCalculator.calculateCylinder(r, h, mc)
        val rounded = area.setScale(6, RoundingMode.HALF_UP)
        assertEquals("245.044227", rounded.toPlainString())
    }

    // 4. Sphere
    @Test
    fun testSphereSurfaceArea() {
        // Radius = 3 -> A = 4*pi*3^2 = 36*pi ≈ 113.0973355...
        // At 6 decimals: 113.097336
        val r = BigDecimal("3")
        val area = SurfaceAreaCalculator.calculateSphere(r, mc)
        val rounded = area.setScale(6, RoundingMode.HALF_UP)
        assertEquals("113.097336", rounded.toPlainString())
    }

    // 5. Cone
    @Test
    fun testConeSurfaceArea() {
        // Radius = 3, Slant Height = 5 -> A = pi*3^2 + pi*3*5 = 9*pi + 15*pi = 24*pi ≈ 75.39822368...
        // At 6 decimals: 75.398224
        val r = BigDecimal("3")
        val l = BigDecimal("5")
        val area = SurfaceAreaCalculator.calculateCone(r, l, mc)
        val rounded = area.setScale(6, RoundingMode.HALF_UP)
        assertEquals("75.398224", rounded.toPlainString())
    }

    // 6. Square Pyramid
    @Test
    fun testSquarePyramidSurfaceArea() {
        // Base Side = 5, Slant Height = 6 -> A = 5^2 + 2(5)(6) = 25 + 60 = 85
        val s = BigDecimal("5")
        val l = BigDecimal("6")
        val area = SurfaceAreaCalculator.calculateSquarePyramid(s, l, mc)
        assertEquals(0, BigDecimal("85").compareTo(area))
    }

    // 7. Triangular Prism
    @Test
    fun testTriangularPrismSurfaceArea() {
        // Side A = 3, Side B = 4, Side C = 5, Length = 10
        // s = (3+4+5)/2 = 6, Area = sqrt(6*3*2*1) = 6
        // A = 2*6 + (3+4+5)*10 = 12 + 120 = 132
        val a = BigDecimal("3")
        val b = BigDecimal("4")
        val c = BigDecimal("5")
        val len = BigDecimal("10")
        val area = SurfaceAreaCalculator.calculateTriangularPrism(a, b, c, len, mc)
        assertEquals(0, BigDecimal("132").compareTo(area))
    }

    // 8. Hemisphere
    @Test
    fun testHemisphereSurfaceArea() {
        // Radius = 3 -> A = 3*pi*3^2 = 27*pi ≈ 84.82300165...
        // At 6 decimals: 84.823002
        val r = BigDecimal("3")
        val area = SurfaceAreaCalculator.calculateHemisphere(r, mc)
        val rounded = area.setScale(6, RoundingMode.HALF_UP)
        assertEquals("84.823002", rounded.toPlainString())
    }

    // 27. BigDecimal precision
    @Test
    fun testBigDecimalPrecisionNoBinaryFloatArtifacts() {
        // 0.1 multiplied or squared in Double produces 0.010000000000000002.
        // In BigDecimal it must be exact: 0.01
        val side = BigDecimal("0.1")
        val area = SurfaceAreaCalculator.calculateCube(side, mc)
        // 6 * 0.01 = 0.06
        assertEquals(0, BigDecimal("0.06").compareTo(area))
    }

    // 28. High-precision pi
    @Test
    fun testHighPrecisionPi() {
        assertTrue(MathConstants.PI.toString().startsWith("3.1415926535897932384626433832795028841971693993751"))
        assertTrue(MathConstants.PI.precision() >= 50)
    }

    // 29. Radius squared
    @Test
    fun testRadiusSquaredOperation() {
        val r = BigDecimal("7")
        val rSquared = r.multiply(r, mc)
        assertEquals(0, BigDecimal("49").compareTo(rSquared))
    }

    // 30. Side squared
    @Test
    fun testSideSquaredOperation() {
        val s = BigDecimal("12.5")
        val sSquared = s.multiply(s, mc)
        assertEquals(0, BigDecimal("156.25").compareTo(sSquared))
    }

    // 31. Heron's formula & BigDecimalMath sqrt
    @Test
    fun testHeronsFormulaAndSquareRoot() {
        val v = BigDecimal("144")
        val sqrtV = BigDecimalMath.sqrt(v, mc)
        assertEquals(0, BigDecimal("12").compareTo(sqrtV))

        val vSmall = BigDecimal("0.000001")
        val sqrtVSmall = BigDecimalMath.sqrt(vSmall, mc)
        assertEquals(0, BigDecimal("0.001").compareTo(sqrtVSmall))
    }
}
