package com.example.surfacecalc.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.surfacecalc.data.model.AreaUnit
import com.example.surfacecalc.data.model.LengthUnit
import com.example.surfacecalc.data.model.SurfaceAreaResult
import com.example.surfacecalc.data.model.SurfaceCalcHistory
import com.example.surfacecalc.data.model.SurfaceShape
import com.example.surfacecalc.data.model.ThemeMode
import com.example.surfacecalc.data.repository.SettingsRepository
import com.example.surfacecalc.data.repository.SurfaceCalcHistoryRepository
import com.example.surfacecalc.domain.AreaUnitRegistry
import com.example.surfacecalc.domain.SurfaceCalcFormatter
import com.example.surfacecalc.domain.SurfaceCalcValidator
import com.example.surfacecalc.domain.SurfaceCalculatorCoordinator
import java.math.RoundingMode
import java.util.UUID
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

class SurfaceCalcViewModel(
    private val settingsRepository: SettingsRepository,
    private val historyRepository: SurfaceCalcHistoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SurfaceCalcUiState())
    val uiState: StateFlow<SurfaceCalcUiState> = _uiState.asStateFlow()

    init {
        // Observe settings
        settingsRepository.settings.onEach { settings ->
            _uiState.update { current ->
                current.copy(
                    themeMode = settings.themeMode,
                    dynamicColor = settings.dynamicColor,
                    hapticFeedback = settings.hapticFeedback,
                    animationsEnabled = settings.animationsEnabled,
                    decimalPlaces = settings.decimalPlaces,
                    roundingMode = settings.roundingMode,
                    showFormula = settings.showFormula,
                    showCalculationSteps = settings.showCalculationSteps,
                    showShapeDiagram = settings.showShapeDiagram,
                    confirmHistoryDelete = settings.confirmHistoryDelete,
                    preserveLastCalculation = settings.preserveLastCalculation
                )
            }
            recalculate()
        }.launchIn(viewModelScope)

        // Observe history
        historyRepository.history.onEach { list ->
            _uiState.update { it.copy(history = list) }
        }.launchIn(viewModelScope)

        // Restore last calculation if setting enabled
        val settings = settingsRepository.settings.value
        if (settings.preserveLastCalculation) {
            val last = historyRepository.getLastCalculation()
            if (last != null) {
                _uiState.update { current ->
                    current.copy(
                        shape = last.shape,
                        inputs = last.inputs,
                        inputUnits = last.inputUnits,
                        resultUnit = last.resultUnit
                    )
                }
            }
        }

        // Run initial calculation
        recalculate()
    }

    fun onShapeSelected(shape: SurfaceShape) {
        val defaultInputs = getDefaultInputsForShape(shape)
        val defaultUnits = getDefaultUnitsForShape(shape)

        _uiState.update { current ->
            current.copy(
                shape = shape,
                inputs = defaultInputs,
                inputUnits = defaultUnits,
                errorMessage = null
            )
        }
        recalculate()
        persistCurrentState()
    }

    fun onInputChange(key: String, value: String) {
        val updatedInputs = _uiState.value.inputs.toMutableMap()
        updatedInputs[key] = value
        _uiState.update { it.copy(inputs = updatedInputs) }
        recalculate()
        persistCurrentState()
    }

    fun onInputUnitChange(key: String, unit: LengthUnit) {
        val updatedUnits = _uiState.value.inputUnits.toMutableMap()
        updatedUnits[key] = unit
        _uiState.update { it.copy(inputUnits = updatedUnits) }
        recalculate()
        persistCurrentState()
    }

    fun onResultUnitChange(unit: AreaUnit) {
        _uiState.update { it.copy(resultUnit = unit) }
        recalculate()
        persistCurrentState()
    }

    fun recalculate() {
        val state = _uiState.value
        val calcResult = SurfaceCalculatorCoordinator.calculate(
            shape = state.shape,
            inputs = state.inputs,
            inputUnits = state.inputUnits,
            outputUnit = state.resultUnit,
            decimalPlaces = state.decimalPlaces,
            roundingMode = state.roundingMode
        )

        if (calcResult.isSuccess) {
            val res = calcResult.getOrThrow()
            _uiState.update {
                it.copy(
                    result = res,
                    errorMessage = null,
                    isCalculating = false
                )
            }
        } else {
            val err = calcResult.exceptionOrNull()?.message ?: "Calculation error"
            _uiState.update {
                it.copy(
                    result = null,
                    errorMessage = err,
                    isCalculating = false
                )
            }
        }
    }

    fun onSave(): Boolean {
        val state = _uiState.value
        val result = state.result ?: return false

        val formattedVal = SurfaceCalcFormatter.formatNumber(
            result.rawValue,
            state.decimalPlaces,
            state.roundingMode
        )

        val historyItem = SurfaceCalcHistory(
            id = UUID.randomUUID().toString(),
            shape = state.shape,
            inputs = state.inputs,
            inputUnits = state.inputUnits,
            resultValue = formattedVal,
            resultUnit = state.resultUnit,
            calculatedAt = System.currentTimeMillis()
        )

        val saved = historyRepository.saveCalculation(historyItem)
        if (saved) {
            _uiState.update { it.copy(saveNotification = "Calculation saved to history") }
        } else {
            _uiState.update { it.copy(saveNotification = "Calculation is already saved") }
        }
        return saved
    }

    fun clearSaveNotification() {
        _uiState.update { it.copy(saveNotification = null) }
    }

    fun onDeleteHistoryItem(id: String) {
        historyRepository.deleteCalculation(id)
    }

    fun onClearHistory() {
        historyRepository.clearHistory()
    }

    fun onRestoreHistory(item: SurfaceCalcHistory) {
        _uiState.update { current ->
            current.copy(
                shape = item.shape,
                inputs = item.inputs,
                inputUnits = item.inputUnits,
                resultUnit = item.resultUnit,
                errorMessage = null
            )
        }
        recalculate()
        persistCurrentState()
    }

    fun onReset() {
        val defaultShape = SurfaceShape.CUBE
        val defaultInputs = mapOf("side" to "5")
        val defaultUnits = mapOf("side" to LengthUnit.CENTIMETER)
        val defaultResultUnit = AreaUnit.SQUARE_CENTIMETER

        _uiState.update { current ->
            current.copy(
                shape = defaultShape,
                inputs = defaultInputs,
                inputUnits = defaultUnits,
                resultUnit = defaultResultUnit,
                errorMessage = null
            )
        }
        recalculate()
        persistCurrentState()
    }

    private fun persistCurrentState() {
        val state = _uiState.value
        if (state.preserveLastCalculation) {
            historyRepository.saveLastCalculation(
                shape = state.shape,
                inputs = state.inputs,
                inputUnits = state.inputUnits,
                resultUnit = state.resultUnit
            )
        }
    }

    fun getCopyText(): String {
        val state = _uiState.value
        val res = state.result ?: return ""
        return SurfaceCalcFormatter.formatCopyText(res, state.decimalPlaces, state.roundingMode)
    }

    fun getValueOnlyText(): String {
        val state = _uiState.value
        val res = state.result ?: return ""
        return SurfaceCalcFormatter.formatArea(res.rawValue, res.outputUnit, state.decimalPlaces, state.roundingMode)
    }

    fun getShareText(): String {
        return getCopyText()
    }

    // Settings modifiers
    fun onSetThemeMode(mode: ThemeMode) = settingsRepository.setThemeMode(mode)
    fun onSetDynamicColor(enabled: Boolean) = settingsRepository.setDynamicColor(enabled)
    fun onSetHapticFeedback(enabled: Boolean) = settingsRepository.setHapticFeedback(enabled)
    fun onSetAnimationsEnabled(enabled: Boolean) = settingsRepository.setAnimationsEnabled(enabled)
    fun onSetDecimalPlaces(places: Int) = settingsRepository.setDecimalPlaces(places)
    fun onSetRoundingMode(mode: RoundingMode) = settingsRepository.setRoundingMode(mode)
    fun onSetShowFormula(show: Boolean) = settingsRepository.setShowFormula(show)
    fun onSetShowCalculationSteps(show: Boolean) = settingsRepository.setShowCalculationSteps(show)
    fun onSetShowShapeDiagram(show: Boolean) = settingsRepository.setShowShapeDiagram(show)
    fun onSetConfirmHistoryDelete(confirm: Boolean) = settingsRepository.setConfirmHistoryDelete(confirm)
    fun onSetPreserveLastCalculation(preserve: Boolean) = settingsRepository.setPreserveLastCalculation(preserve)
    fun onResetSettings() = settingsRepository.resetSettings()

    companion object {
        fun getDefaultInputsForShape(shape: SurfaceShape): Map<String, String> {
            return when (shape) {
                SurfaceShape.CUBE -> mapOf("side" to "5")
                SurfaceShape.RECTANGULAR_PRISM -> mapOf("length" to "10", "width" to "5", "height" to "4")
                SurfaceShape.CYLINDER -> mapOf("radius" to "3", "height" to "10")
                SurfaceShape.SPHERE -> mapOf("radius" to "3")
                SurfaceShape.CONE -> mapOf("radius" to "3", "slantHeight" to "5")
                SurfaceShape.SQUARE_PYRAMID -> mapOf("baseSide" to "5", "slantHeight" to "6")
                SurfaceShape.TRIANGULAR_PRISM -> mapOf("sideA" to "3", "sideB" to "4", "sideC" to "5", "prismLength" to "10")
                SurfaceShape.HEMISPHERE -> mapOf("radius" to "3")
            }
        }

        fun getDefaultUnitsForShape(shape: SurfaceShape): Map<String, LengthUnit> {
            val fields = SurfaceCalcValidator.getFieldsForShape(shape)
            return fields.associate { it.key to LengthUnit.CENTIMETER }
        }
    }
}

class SurfaceCalcViewModelFactory(
    private val settingsRepository: SettingsRepository,
    private val historyRepository: SurfaceCalcHistoryRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SurfaceCalcViewModel::class.java)) {
            return SurfaceCalcViewModel(settingsRepository, historyRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
