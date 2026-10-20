package com.example.surfacecalc.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.surfacecalc.data.model.ThemeMode
import java.math.RoundingMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class SettingsState(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val dynamicColor: Boolean = true,
    val hapticFeedback: Boolean = true,
    val animationsEnabled: Boolean = true,
    val decimalPlaces: Int = 6,
    val roundingMode: RoundingMode = RoundingMode.HALF_UP,
    val showFormula: Boolean = true,
    val showCalculationSteps: Boolean = true,
    val showShapeDiagram: Boolean = true,
    val confirmHistoryDelete: Boolean = true,
    val preserveLastCalculation: Boolean = true
)

class SettingsRepository(private val prefs: SharedPreferences) {

    constructor(context: Context) : this(context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE))

    private val _settings = MutableStateFlow(loadSettings())
    val settings: StateFlow<SettingsState> = _settings.asStateFlow()

    private fun loadSettings(): SettingsState {
        val themeStr = prefs.getString(KEY_THEME_MODE, ThemeMode.SYSTEM.name) ?: ThemeMode.SYSTEM.name
        val themeMode = try { ThemeMode.valueOf(themeStr) } catch (_: Exception) { ThemeMode.SYSTEM }

        val roundingStr = prefs.getString(KEY_ROUNDING_MODE, RoundingMode.HALF_UP.name) ?: RoundingMode.HALF_UP.name
        val roundingMode = try { RoundingMode.valueOf(roundingStr) } catch (_: Exception) { RoundingMode.HALF_UP }

        return SettingsState(
            themeMode = themeMode,
            dynamicColor = prefs.getBoolean(KEY_DYNAMIC_COLOR, true),
            hapticFeedback = prefs.getBoolean(KEY_HAPTIC_FEEDBACK, true),
            animationsEnabled = prefs.getBoolean(KEY_ANIMATIONS, true),
            decimalPlaces = prefs.getInt(KEY_DECIMAL_PLACES, 6),
            roundingMode = roundingMode,
            showFormula = prefs.getBoolean(KEY_SHOW_FORMULA, true),
            showCalculationSteps = prefs.getBoolean(KEY_SHOW_CALCULATION_STEPS, true),
            showShapeDiagram = prefs.getBoolean(KEY_SHOW_SHAPE_DIAGRAM, true),
            confirmHistoryDelete = prefs.getBoolean(KEY_CONFIRM_HISTORY_DELETE, true),
            preserveLastCalculation = prefs.getBoolean(KEY_PRESERVE_LAST_CALCULATION, true)
        )
    }

    fun setThemeMode(mode: ThemeMode) {
        prefs.edit().putString(KEY_THEME_MODE, mode.name).apply()
        _settings.value = _settings.value.copy(themeMode = mode)
    }

    fun setDynamicColor(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_DYNAMIC_COLOR, enabled).apply()
        _settings.value = _settings.value.copy(dynamicColor = enabled)
    }

    fun setHapticFeedback(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_HAPTIC_FEEDBACK, enabled).apply()
        _settings.value = _settings.value.copy(hapticFeedback = enabled)
    }

    fun setAnimationsEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_ANIMATIONS, enabled).apply()
        _settings.value = _settings.value.copy(animationsEnabled = enabled)
    }

    fun setDecimalPlaces(places: Int) {
        prefs.edit().putInt(KEY_DECIMAL_PLACES, places).apply()
        _settings.value = _settings.value.copy(decimalPlaces = places)
    }

    fun setRoundingMode(mode: RoundingMode) {
        prefs.edit().putString(KEY_ROUNDING_MODE, mode.name).apply()
        _settings.value = _settings.value.copy(roundingMode = mode)
    }

    fun setShowFormula(show: Boolean) {
        prefs.edit().putBoolean(KEY_SHOW_FORMULA, show).apply()
        _settings.value = _settings.value.copy(showFormula = show)
    }

    fun setShowCalculationSteps(show: Boolean) {
        prefs.edit().putBoolean(KEY_SHOW_CALCULATION_STEPS, show).apply()
        _settings.value = _settings.value.copy(showCalculationSteps = show)
    }

    fun setShowShapeDiagram(show: Boolean) {
        prefs.edit().putBoolean(KEY_SHOW_SHAPE_DIAGRAM, show).apply()
        _settings.value = _settings.value.copy(showShapeDiagram = show)
    }

    fun setConfirmHistoryDelete(confirm: Boolean) {
        prefs.edit().putBoolean(KEY_CONFIRM_HISTORY_DELETE, confirm).apply()
        _settings.value = _settings.value.copy(confirmHistoryDelete = confirm)
    }

    fun setPreserveLastCalculation(preserve: Boolean) {
        prefs.edit().putBoolean(KEY_PRESERVE_LAST_CALCULATION, preserve).apply()
        _settings.value = _settings.value.copy(preserveLastCalculation = preserve)
    }

    fun resetSettings() {
        prefs.edit().clear().apply()
        _settings.value = SettingsState()
    }

    companion object {
        private const val PREFS_NAME = "surface_calc_settings"
        private const val KEY_THEME_MODE = "theme_mode"
        private const val KEY_DYNAMIC_COLOR = "dynamic_color"
        private const val KEY_HAPTIC_FEEDBACK = "haptic_feedback"
        private const val KEY_ANIMATIONS = "animations"
        private const val KEY_DECIMAL_PLACES = "decimal_places"
        private const val KEY_ROUNDING_MODE = "rounding_mode"
        private const val KEY_SHOW_FORMULA = "show_formula"
        private const val KEY_SHOW_CALCULATION_STEPS = "show_calculation_steps"
        private const val KEY_SHOW_SHAPE_DIAGRAM = "show_shape_diagram"
        private const val KEY_CONFIRM_HISTORY_DELETE = "confirm_history_delete"
        private const val KEY_PRESERVE_LAST_CALCULATION = "preserve_last_calculation"
    }
}
