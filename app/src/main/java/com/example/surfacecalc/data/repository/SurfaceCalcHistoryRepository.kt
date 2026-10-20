package com.example.surfacecalc.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.surfacecalc.data.model.AreaUnit
import com.example.surfacecalc.data.model.LengthUnit
import com.example.surfacecalc.data.model.SurfaceCalcHistory
import com.example.surfacecalc.data.model.SurfaceShape
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject

data class LastCalculationState(
    val shape: SurfaceShape,
    val inputs: Map<String, String>,
    val inputUnits: Map<String, LengthUnit>,
    val resultUnit: AreaUnit
)

class SurfaceCalcHistoryRepository(private val prefs: SharedPreferences) {

    constructor(context: Context) : this(context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE))

    private val _history = MutableStateFlow(loadHistory())
    val history: StateFlow<List<SurfaceCalcHistory>> = _history.asStateFlow()

    private fun loadHistory(): List<SurfaceCalcHistory> {
        val jsonStr = prefs.getString(KEY_HISTORY_LIST, null) ?: return emptyList()
        val list = mutableListOf<SurfaceCalcHistory>()
        try {
            val jsonArray = JSONArray(jsonStr)
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                val id = obj.getString("id")
                val shape = SurfaceShape.valueOf(obj.getString("shape"))
                val resultValue = obj.getString("resultValue")
                val resultUnit = AreaUnit.valueOf(obj.getString("resultUnit"))
                val calculatedAt = obj.getLong("calculatedAt")

                val inputsObj = obj.getJSONObject("inputs")
                val inputs = mutableMapOf<String, String>()
                val inputKeys = inputsObj.keys()
                while (inputKeys.hasNext()) {
                    val k = inputKeys.next()
                    inputs[k] = inputsObj.getString(k)
                }

                val unitsObj = obj.getJSONObject("inputUnits")
                val inputUnits = mutableMapOf<String, LengthUnit>()
                val unitKeys = unitsObj.keys()
                while (unitKeys.hasNext()) {
                    val k = unitKeys.next()
                    inputUnits[k] = LengthUnit.valueOf(unitsObj.getString(k))
                }

                list.add(
                    SurfaceCalcHistory(
                        id = id,
                        shape = shape,
                        inputs = inputs,
                        inputUnits = inputUnits,
                        resultValue = resultValue,
                        resultUnit = resultUnit,
                        calculatedAt = calculatedAt
                    )
                )
            }
        } catch (_: Exception) {
            return emptyList()
        }
        return list
    }

    private fun saveHistoryToPrefs(list: List<SurfaceCalcHistory>) {
        val jsonArray = JSONArray()
        for (item in list) {
            val obj = JSONObject()
            obj.put("id", item.id)
            obj.put("shape", item.shape.name)
            obj.put("resultValue", item.resultValue)
            obj.put("resultUnit", item.resultUnit.name)
            obj.put("calculatedAt", item.calculatedAt)

            val inputsObj = JSONObject()
            for ((k, v) in item.inputs) {
                inputsObj.put(k, v)
            }
            obj.put("inputs", inputsObj)

            val unitsObj = JSONObject()
            for ((k, v) in item.inputUnits) {
                unitsObj.put(k, v.name)
            }
            obj.put("inputUnits", unitsObj)

            jsonArray.put(obj)
        }
        prefs.edit().putString(KEY_HISTORY_LIST, jsonArray.toString()).apply()
        _history.value = list
    }

    fun saveCalculation(item: SurfaceCalcHistory): Boolean {
        val currentList = _history.value.toMutableList()

        // Consecutive duplicate check: compare shape, inputs, input units, and result unit with newest item
        if (currentList.isNotEmpty()) {
            val newest = currentList.first()
            if (newest.shape == item.shape &&
                newest.inputs == item.inputs &&
                newest.inputUnits == item.inputUnits &&
                newest.resultUnit == item.resultUnit
            ) {
                return false // Consecutive duplicate rejected
            }
        }

        currentList.add(0, item)
        // Keep maximum 10 items (drop oldest when 11th added)
        while (currentList.size > MAX_HISTORY_ENTRIES) {
            currentList.removeAt(currentList.lastIndex)
        }

        saveHistoryToPrefs(currentList)
        return true
    }

    fun deleteCalculation(id: String) {
        val currentList = _history.value.filter { it.id != id }
        saveHistoryToPrefs(currentList)
    }

    fun clearHistory() {
        saveHistoryToPrefs(emptyList())
    }

    fun saveLastCalculation(
        shape: SurfaceShape,
        inputs: Map<String, String>,
        inputUnits: Map<String, LengthUnit>,
        resultUnit: AreaUnit
    ) {
        val obj = JSONObject()
        obj.put("shape", shape.name)
        obj.put("resultUnit", resultUnit.name)

        val inputsObj = JSONObject()
        for ((k, v) in inputs) {
            inputsObj.put(k, v)
        }
        obj.put("inputs", inputsObj)

        val unitsObj = JSONObject()
        for ((k, v) in inputUnits) {
            unitsObj.put(k, v.name)
        }
        obj.put("inputUnits", unitsObj)

        prefs.edit().putString(KEY_LAST_CALCULATION, obj.toString()).apply()
    }

    fun getLastCalculation(): LastCalculationState? {
        val jsonStr = prefs.getString(KEY_LAST_CALCULATION, null) ?: return null
        return try {
            val obj = JSONObject(jsonStr)
            val shape = SurfaceShape.valueOf(obj.getString("shape"))
            val resultUnit = AreaUnit.valueOf(obj.getString("resultUnit"))

            val inputsObj = obj.getJSONObject("inputs")
            val inputs = mutableMapOf<String, String>()
            val inputKeys = inputsObj.keys()
            while (inputKeys.hasNext()) {
                val k = inputKeys.next()
                inputs[k] = inputsObj.getString(k)
            }

            val unitsObj = obj.getJSONObject("inputUnits")
            val inputUnits = mutableMapOf<String, LengthUnit>()
            val unitKeys = unitsObj.keys()
            while (unitKeys.hasNext()) {
                val k = unitKeys.next()
                inputUnits[k] = LengthUnit.valueOf(unitsObj.getString(k))
            }

            LastCalculationState(
                shape = shape,
                inputs = inputs,
                inputUnits = inputUnits,
                resultUnit = resultUnit
            )
        } catch (_: Exception) {
            null
        }
    }

    companion object {
        private const val PREFS_NAME = "surface_calc_history"
        private const val KEY_HISTORY_LIST = "history_list"
        private const val KEY_LAST_CALCULATION = "last_calculation"
        const val MAX_HISTORY_ENTRIES = 10
    }
}
