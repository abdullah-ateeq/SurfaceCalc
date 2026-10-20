package com.example.surfacecalc

import com.example.surfacecalc.data.model.AreaUnit
import com.example.surfacecalc.data.model.LengthUnit
import com.example.surfacecalc.data.model.SurfaceCalcHistory
import com.example.surfacecalc.data.model.SurfaceShape
import com.example.surfacecalc.data.model.ThemeMode
import com.example.surfacecalc.data.repository.SettingsRepository
import com.example.surfacecalc.data.repository.SurfaceCalcHistoryRepository
import java.math.RoundingMode
import java.util.UUID
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class SurfaceCalcRepositoryTest {

    private lateinit var fakePrefsHistory: FakeSharedPreferences
    private lateinit var fakePrefsSettings: FakeSharedPreferences
    private lateinit var historyRepo: SurfaceCalcHistoryRepository
    private lateinit var settingsRepo: SettingsRepository

    @Before
    fun setUp() {
        fakePrefsHistory = FakeSharedPreferences()
        fakePrefsSettings = FakeSharedPreferences()
        historyRepo = SurfaceCalcHistoryRepository(fakePrefsHistory)
        settingsRepo = SettingsRepository(fakePrefsSettings)
    }

    // 51. History save
    @Test
    fun testHistorySave() {
        val item = SurfaceCalcHistory(
            id = UUID.randomUUID().toString(),
            shape = SurfaceShape.CUBE,
            inputs = mapOf("side" to "5"),
            inputUnits = mapOf("side" to LengthUnit.CENTIMETER),
            resultValue = "150",
            resultUnit = AreaUnit.SQUARE_CENTIMETER,
            calculatedAt = System.currentTimeMillis()
        )
        val saved = historyRepo.saveCalculation(item)
        assertTrue(saved)
        assertEquals(1, historyRepo.history.value.size)
    }

    // 52. History load
    @Test
    fun testHistoryLoad() {
        val item = SurfaceCalcHistory(
            id = UUID.randomUUID().toString(),
            shape = SurfaceShape.CYLINDER,
            inputs = mapOf("radius" to "3", "height" to "10"),
            inputUnits = mapOf("radius" to LengthUnit.CENTIMETER, "height" to LengthUnit.CENTIMETER),
            resultValue = "245.044227",
            resultUnit = AreaUnit.SQUARE_CENTIMETER,
            calculatedAt = System.currentTimeMillis()
        )
        historyRepo.saveCalculation(item)

        // Reload repository from same preferences
        val reloadedRepo = SurfaceCalcHistoryRepository(fakePrefsHistory)
        val list = reloadedRepo.history.value
        assertEquals(1, list.size)
        assertEquals(SurfaceShape.CYLINDER, list[0].shape)
        assertEquals("245.044227", list[0].resultValue)
    }

    // 53. History limit (max 10 entries; oldest removed when 11th added)
    @Test
    fun testHistoryLimitMaxTen() {
        for (i in 1..11) {
            val item = SurfaceCalcHistory(
                id = UUID.randomUUID().toString(),
                shape = SurfaceShape.CUBE,
                inputs = mapOf("side" to "$i"),
                inputUnits = mapOf("side" to LengthUnit.CENTIMETER),
                resultValue = "${6 * i * i}",
                resultUnit = AreaUnit.SQUARE_CENTIMETER,
                calculatedAt = System.currentTimeMillis() + i
            )
            historyRepo.saveCalculation(item)
        }
        val list = historyRepo.history.value
        assertEquals(10, list.size)
        // Most recent should be side 11
        assertEquals("11", list.first().inputs["side"])
        // Oldest (side 1) should have been removed
        assertFalse(list.any { it.inputs["side"] == "1" })
    }

    // 54. History deletion
    @Test
    fun testHistoryDeletion() {
        val id1 = UUID.randomUUID().toString()
        val item1 = SurfaceCalcHistory(
            id = id1,
            shape = SurfaceShape.CUBE,
            inputs = mapOf("side" to "5"),
            inputUnits = mapOf("side" to LengthUnit.CENTIMETER),
            resultValue = "150",
            resultUnit = AreaUnit.SQUARE_CENTIMETER,
            calculatedAt = System.currentTimeMillis()
        )
        historyRepo.saveCalculation(item1)
        assertEquals(1, historyRepo.history.value.size)

        historyRepo.deleteCalculation(id1)
        assertEquals(0, historyRepo.history.value.size)
    }

    // 55. History clear
    @Test
    fun testHistoryClear() {
        val item1 = SurfaceCalcHistory(
            id = UUID.randomUUID().toString(),
            shape = SurfaceShape.CUBE,
            inputs = mapOf("side" to "5"),
            inputUnits = mapOf("side" to LengthUnit.CENTIMETER),
            resultValue = "150",
            resultUnit = AreaUnit.SQUARE_CENTIMETER,
            calculatedAt = System.currentTimeMillis()
        )
        historyRepo.saveCalculation(item1)
        historyRepo.clearHistory()
        assertEquals(0, historyRepo.history.value.size)
    }

    // 56. History restore
    @Test
    fun testHistoryRestore() {
        val item = SurfaceCalcHistory(
            id = UUID.randomUUID().toString(),
            shape = SurfaceShape.RECTANGULAR_PRISM,
            inputs = mapOf("length" to "10", "width" to "5", "height" to "4"),
            inputUnits = mapOf(
                "length" to LengthUnit.CENTIMETER,
                "width" to LengthUnit.CENTIMETER,
                "height" to LengthUnit.CENTIMETER
            ),
            resultValue = "220",
            resultUnit = AreaUnit.SQUARE_CENTIMETER,
            calculatedAt = System.currentTimeMillis()
        )
        historyRepo.saveCalculation(item)

        val retrieved = historyRepo.history.value.first()
        assertEquals(SurfaceShape.RECTANGULAR_PRISM, retrieved.shape)
        assertEquals("10", retrieved.inputs["length"])
        assertEquals("5", retrieved.inputs["width"])
        assertEquals("4", retrieved.inputs["height"])
        assertEquals(AreaUnit.SQUARE_CENTIMETER, retrieved.resultUnit)
    }

    // 57. Settings persistence
    @Test
    fun testSettingsPersistence() {
        settingsRepo.setThemeMode(ThemeMode.DARK)
        settingsRepo.setDecimalPlaces(4)
        settingsRepo.setRoundingMode(RoundingMode.HALF_DOWN)
        settingsRepo.setShowFormula(false)

        val reloadedSettingsRepo = SettingsRepository(fakePrefsSettings)
        val s = reloadedSettingsRepo.settings.value
        assertEquals(ThemeMode.DARK, s.themeMode)
        assertEquals(4, s.decimalPlaces)
        assertEquals(RoundingMode.HALF_DOWN, s.roundingMode)
        assertFalse(s.showFormula)
    }

    // 58. Last calculation persistence
    @Test
    fun testLastCalculationPersistence() {
        historyRepo.saveLastCalculation(
            shape = SurfaceShape.CONE,
            inputs = mapOf("radius" to "3", "slantHeight" to "5"),
            inputUnits = mapOf("radius" to LengthUnit.CENTIMETER, "slantHeight" to LengthUnit.CENTIMETER),
            resultUnit = AreaUnit.SQUARE_CENTIMETER
        )

        val lastState = historyRepo.getLastCalculation()
        assertNotNull(lastState)
        assertEquals(SurfaceShape.CONE, lastState!!.shape)
        assertEquals("3", lastState.inputs["radius"])
        assertEquals("5", lastState.inputs["slantHeight"])
    }

    // 59. Reset
    @Test
    fun testSettingsReset() {
        settingsRepo.setDecimalPlaces(12)
        settingsRepo.setThemeMode(ThemeMode.DARK)
        settingsRepo.resetSettings()

        val defaultState = settingsRepo.settings.value
        assertEquals(6, defaultState.decimalPlaces)
        assertEquals(ThemeMode.SYSTEM, defaultState.themeMode)
    }
}
