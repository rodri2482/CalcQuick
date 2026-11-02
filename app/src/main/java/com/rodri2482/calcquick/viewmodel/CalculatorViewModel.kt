package com.rodri2482.calcquick.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.rodri2482.calcquick.data.HistoryDatabase
import com.rodri2482.calcquick.evaluator.MathExpressionEvaluator
import com.rodri2482.calcquick.model.CalcMode
import com.rodri2482.calcquick.model.CalculationHistoryItem
import com.rodri2482.calcquick.model.UnitCategory
import com.rodri2482.calcquick.model.UnitConverterData
import com.rodri2482.calcquick.model.UnitItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.Locale
import kotlin.math.pow

enum class ThemeStyle(val displayName: String) {
    NEON_CYBER("Neon Cyber (Dark)"),
    OLED_DARK("OLED Black"),
    CLEAN_LIGHT("Clean Light"),
    MIDNIGHT_BLUE("Midnight Blue")
}

data class CalculatorUiState(
    val activeMode: CalcMode = CalcMode.STANDARD,
    val expression: String = "",
    val cursorPosition: Int = 0,
    val livePreview: String? = null,
    val isDegreeMode: Boolean = true,
    val memoryValue: Double = 0.0,
    val hasMemory: Boolean = false,
    val decimalPrecision: Int = 6,
    val errorMessage: String? = null,
    
    // Converter state
    val converterCategory: UnitCategory = UnitCategory.LENGTH,
    val converterInput: String = "1",
    val fromUnit: UnitItem = UnitConverterData.getUnitsForCategory(UnitCategory.LENGTH)[0],
    val toUnit: UnitItem = UnitConverterData.getUnitsForCategory(UnitCategory.LENGTH)[2], // cm
    val converterResult: String = "100",

    // Loan state
    val loanPrincipal: String = "250000",
    val loanInterestRate: String = "6.5",
    val loanTermYears: String = "30",
    val loanMonthlyPayment: String = "1,580.17",
    val loanTotalPayment: String = "568,861.20",
    val loanTotalInterest: String = "318,861.20",

    // Tip & Split state
    val tipBillAmount: String = "100.00",
    val tipPercentage: Int = 15,
    val tipSplitCount: Int = 2,
    val tipAmount: String = "15.00",
    val tipTotalAmount: String = "115.00",
    val tipPerPersonAmount: String = "57.50",

    // Discount state
    val discountOriginalPrice: String = "120.00",
    val discountPercent: String = "20",
    val discountTaxPercent: String = "8",
    val discountFinalPrice: String = "103.68",
    val discountSavedAmount: String = "24.00",

    // BMI state
    val bmiIsMetric: Boolean = true,
    val bmiHeight: String = "175",
    val bmiWeight: String = "70",
    val bmiScore: Double = 22.86,
    val bmiCategory: String = "Normal Weight",

    // History state
    val history: List<CalculationHistoryItem> = emptyList(),

    // Preferences
    val themeStyle: ThemeStyle = ThemeStyle.NEON_CYBER,
    val soundEnabled: Boolean = true,
    val hapticEnabled: Boolean = true
)

class CalculatorViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = application.getSharedPreferences("calcquick_prefs", Context.MODE_PRIVATE)

    private val _uiState = MutableStateFlow(CalculatorUiState(
        themeStyle = restoreTheme(),
        soundEnabled = prefs.getBoolean(PREF_SOUND, true),
        hapticEnabled = prefs.getBoolean(PREF_HAPTIC, true),
        isDegreeMode = prefs.getBoolean(PREF_DEGREE_MODE, true),
        memoryValue = Double.fromBits(prefs.getLong(PREF_MEMORY_BITS, 0L)),
        hasMemory = prefs.getBoolean(PREF_HAS_MEMORY, false),
        decimalPrecision = prefs.getInt(PREF_DECIMAL_PRECISION, 6).coerceIn(2, 10)
    ))
    val uiState: StateFlow<CalculatorUiState> = _uiState.asStateFlow()

    init {
        observeHistory()
        updateLivePreview()
        recalculateConverter()
        recalculateLoan()
        recalculateTip()
        recalculateDiscount()
        recalculateBmi()
    }

    /**
     * Calculation history is the source of truth in Room; the UI state just
     * mirrors it. Settings/theme/memory live in SharedPreferences so they
     * survive process death.
     */
    private fun observeHistory() {
        HistoryDatabase.get(getApplication<Application>())
            .historyDao()
            .observeAll()
            .onEach { items -> _uiState.update { it.copy(history = items) } }
            .launchIn(viewModelScope)
    }

    private fun restoreTheme(): ThemeStyle =
        ThemeStyle.entries.firstOrNull { it.name == prefs.getString(PREF_THEME, null) }
            ?: ThemeStyle.NEON_CYBER

    fun setMode(mode: CalcMode) {
        _uiState.update { it.copy(activeMode = mode, errorMessage = null) }
    }

    fun setThemeStyle(theme: ThemeStyle) {
        prefs.edit().putString(PREF_THEME, theme.name).apply()
        _uiState.update { it.copy(themeStyle = theme) }
    }

    fun setDecimalPrecision(precision: Int) {
        val clamped = precision.coerceIn(2, 10)
        prefs.edit().putInt(PREF_DECIMAL_PRECISION, clamped).apply()
        _uiState.update {
            it.copy(decimalPrecision = clamped, livePreview = null)
        }
        updateLivePreview()
    }

    fun toggleSound() {
        val newValue = !_uiState.value.soundEnabled
        prefs.edit().putBoolean(PREF_SOUND, newValue).apply()
        _uiState.update { it.copy(soundEnabled = newValue) }
    }

    fun toggleHaptic() {
        val newValue = !_uiState.value.hapticEnabled
        prefs.edit().putBoolean(PREF_HAPTIC, newValue).apply()
        _uiState.update { it.copy(hapticEnabled = newValue) }
    }

    // --- Calculator Engine Actions ---

    fun onKeyInput(key: String) {
        _uiState.update { state ->
            val expr = state.expression
            val pos = state.cursorPosition.coerceIn(0, expr.length)
            val newExpr = StringBuilder(expr).insert(pos, key).toString()
            state.copy(
                expression = newExpr,
                cursorPosition = pos + key.length,
                errorMessage = null
            )
        }
        updateLivePreview()
    }

    fun onFunctionInput(funcName: String) {
        val snippet = "$funcName("
        onKeyInput(snippet)
    }

    fun onBackspace() {
        _uiState.update { state ->
            val expr = state.expression
            if (expr.isNotEmpty() && state.cursorPosition > 0) {
                val pos = state.cursorPosition
                val newExpr = StringBuilder(expr).deleteAt(pos - 1).toString()
                state.copy(
                    expression = newExpr,
                    cursorPosition = (pos - 1).coerceAtLeast(0),
                    errorMessage = null
                )
            } else state
        }
        updateLivePreview()
    }

    fun onClearAll() {
        _uiState.update {
            it.copy(
                expression = "",
                cursorPosition = 0,
                livePreview = null,
                errorMessage = null
            )
        }
    }

    fun toggleDegreeRadian() {
        val newValue = !_uiState.value.isDegreeMode
        prefs.edit().putBoolean(PREF_DEGREE_MODE, newValue).apply()
        _uiState.update { it.copy(isDegreeMode = newValue) }
        updateLivePreview()
    }

    fun togglePlusMinus() {
        _uiState.update { state ->
            val expr = state.expression
            if (expr.startsWith("-")) {
                state.copy(expression = expr.substring(1), cursorPosition = (state.cursorPosition - 1).coerceAtLeast(0))
            } else if (expr.isNotEmpty()) {
                state.copy(expression = "-$expr", cursorPosition = state.cursorPosition + 1)
            } else {
                state.copy(expression = "-", cursorPosition = 1)
            }
        }
        updateLivePreview()
    }

    fun onEqualsPressed() {
        val currentState = _uiState.value
        val expr = currentState.expression.trim()
        if (expr.isEmpty()) return

        try {
            val result = MathExpressionEvaluator.evaluate(
                expression = expr,
                isDegreeMode = currentState.isDegreeMode,
                decimalPrecision = currentState.decimalPrecision
            )

            // Save to history (persisted in Room; ViewModel cleanup is free)
            val newHistoryItem = CalculationHistoryItem(
                modeName = currentState.activeMode.title,
                expression = expr,
                result = result
            )
            viewModelScope.launch {
                HistoryDatabase.get(getApplication()).historyDao().insert(newHistoryItem)
            }

            _uiState.update { state ->
                state.copy(
                    expression = result,
                    cursorPosition = result.length,
                    livePreview = null,
                    errorMessage = null
                )
            }
        } catch (e: Exception) {
            _uiState.update { it.copy(errorMessage = e.message ?: "Evaluation Error") }
        }
    }

    private fun updateLivePreview() {
        val currentState = _uiState.value
        val preview = MathExpressionEvaluator.tryEvaluatePreview(
            expression = currentState.expression,
            isDegreeMode = currentState.isDegreeMode,
            decimalPrecision = currentState.decimalPrecision
        )
        _uiState.update { it.copy(livePreview = preview) }
    }

    // --- Memory Functions ---

    fun memoryClear() {
        prefs.edit().putLong(PREF_MEMORY_BITS, 0L).putBoolean(PREF_HAS_MEMORY, false).apply()
        _uiState.update { it.copy(memoryValue = 0.0, hasMemory = false) }
    }

    fun memoryRecall() {
        val mem = _uiState.value.memoryValue
        // Locale.US: this string feeds the expression parser; the parser
        // (and Double parser) always expects '.' as the decimal separator.
        val formattedMem = if (mem % 1.0 == 0.0) String.format(Locale.US, "%.0f", mem) else mem.toString()
        onKeyInput(formattedMem)
    }

    fun memoryAdd() {
        val currentState = _uiState.value
        val valueToAdd = currentState.livePreview?.toDoubleOrNull()
            ?: currentState.expression.toDoubleOrNull()
            ?: return
        val newMem = currentState.memoryValue + valueToAdd
        prefs.edit().putLong(PREF_MEMORY_BITS, newMem.toRawBits()).putBoolean(PREF_HAS_MEMORY, true).apply()
        _uiState.update { it.copy(memoryValue = newMem, hasMemory = true) }
    }

    fun memorySubtract() {
        val currentState = _uiState.value
        val valueToSub = currentState.livePreview?.toDoubleOrNull()
            ?: currentState.expression.toDoubleOrNull()
            ?: return
        val newMem = currentState.memoryValue - valueToSub
        prefs.edit().putLong(PREF_MEMORY_BITS, newMem.toRawBits()).putBoolean(PREF_HAS_MEMORY, true).apply()
        _uiState.update { it.copy(memoryValue = newMem, hasMemory = true) }
    }

    // --- Converter Actions ---

    fun onConverterCategorySelected(category: UnitCategory) {
        val units = UnitConverterData.getUnitsForCategory(category)
        _uiState.update {
            it.copy(
                converterCategory = category,
                fromUnit = units[0],
                toUnit = if (units.size > 1) units[1] else units[0]
            )
        }
        recalculateConverter()
    }

    fun onConverterInputChanged(input: String) {
        _uiState.update { it.copy(converterInput = input) }
        recalculateConverter()
    }

    fun onConverterFromUnitChanged(unit: UnitItem) {
        _uiState.update { it.copy(fromUnit = unit) }
        recalculateConverter()
    }

    fun onConverterToUnitChanged(unit: UnitItem) {
        _uiState.update { it.copy(toUnit = unit) }
        recalculateConverter()
    }

    fun swapConverterUnits() {
        _uiState.update {
            it.copy(fromUnit = it.toUnit, toUnit = it.fromUnit)
        }
        recalculateConverter()
    }

    private fun recalculateConverter() {
        val state = _uiState.value
        val valDouble = state.converterInput.toDoubleOrNull() ?: 0.0
        val res = UnitConverterData.convert(valDouble, state.converterCategory, state.fromUnit, state.toUnit)
        
        val formattedRes = if (res % 1.0 == 0.0 && Math.abs(res) < 1e9) {
            String.format(Locale.US, "%.0f", res)
        } else {
            BigDecimal(res).setScale(state.decimalPrecision, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString()
        }

        _uiState.update { it.copy(converterResult = formattedRes) }
    }

    // --- Loan Calculator Actions ---

    fun onLoanInputChanged(principal: String? = null, rate: String? = null, years: String? = null) {
        _uiState.update {
            it.copy(
                loanPrincipal = principal ?: it.loanPrincipal,
                loanInterestRate = rate ?: it.loanInterestRate,
                loanTermYears = years ?: it.loanTermYears
            )
        }
        recalculateLoan()
    }

    private fun recalculateLoan() {
        val state = _uiState.value
        val p = state.loanPrincipal.toDoubleOrNull() ?: 0.0
        val annualRate = state.loanInterestRate.toDoubleOrNull() ?: 0.0
        val years = state.loanTermYears.toDoubleOrNull() ?: 0.0

        if (p <= 0 || annualRate <= 0 || years <= 0) {
            _uiState.update {
                it.copy(
                    loanMonthlyPayment = "0.00",
                    loanTotalPayment = "0.00",
                    loanTotalInterest = "0.00"
                )
            }
            return
        }

        val monthlyRate = annualRate / 100.0 / 12.0
        // Round (not truncate) so fractional years behave reasonably, and
        // guard against 0 months, which previously produced (1+r)^0 - 1 = 0
        // and an Infinity monthly payment.
        val totalMonths = Math.round(years * 12.0)
        if (totalMonths < 1L) {
            _uiState.update {
                it.copy(
                    loanMonthlyPayment = "0.00",
                    loanTotalPayment = "0.00",
                    loanTotalInterest = "0.00"
                )
            }
            return
        }

        val monthlyPayment = (p * monthlyRate * (1 + monthlyRate).pow(totalMonths.toInt())) /
                ((1 + monthlyRate).pow(totalMonths.toInt()) - 1)

        val totalPayment = monthlyPayment * totalMonths
        val totalInterest = totalPayment - p

        _uiState.update {
            it.copy(
                loanMonthlyPayment = String.format(Locale.US, "%,.2f", monthlyPayment),
                loanTotalPayment = String.format(Locale.US, "%,.2f", totalPayment),
                loanTotalInterest = String.format(Locale.US, "%,.2f", totalInterest)
            )
        }
    }

    // --- Tip & Split Actions ---

    fun onTipInputChanged(bill: String? = null, tipPct: Int? = null, split: Int? = null) {
        _uiState.update {
            it.copy(
                tipBillAmount = bill ?: it.tipBillAmount,
                tipPercentage = tipPct ?: it.tipPercentage,
                tipSplitCount = (split ?: it.tipSplitCount).coerceAtLeast(1)
            )
        }
        recalculateTip()
    }

    private fun recalculateTip() {
        val state = _uiState.value
        val bill = state.tipBillAmount.toDoubleOrNull() ?: 0.0
        val tipPct = state.tipPercentage
        val split = state.tipSplitCount

        val tipAmt = bill * (tipPct / 100.0)
        val total = bill + tipAmt
        val perPerson = total / split

        _uiState.update {
            it.copy(
                tipAmount = String.format(Locale.US, "%.2f", tipAmt),
                tipTotalAmount = String.format(Locale.US, "%.2f", total),
                tipPerPersonAmount = String.format(Locale.US, "%.2f", perPerson)
            )
        }
    }

    // --- Discount & Tax Actions ---

    fun onDiscountInputChanged(price: String? = null, discount: String? = null, tax: String? = null) {
        _uiState.update {
            it.copy(
                discountOriginalPrice = price ?: it.discountOriginalPrice,
                discountPercent = discount ?: it.discountPercent,
                discountTaxPercent = tax ?: it.discountTaxPercent
            )
        }
        recalculateDiscount()
    }

    private fun recalculateDiscount() {
        val state = _uiState.value
        val orig = state.discountOriginalPrice.toDoubleOrNull() ?: 0.0
        val discPct = state.discountPercent.toDoubleOrNull() ?: 0.0
        val taxPct = state.discountTaxPercent.toDoubleOrNull() ?: 0.0

        val saved = orig * (discPct / 100.0)
        val discountedPrice = orig - saved
        val taxAmt = discountedPrice * (taxPct / 100.0)
        val finalPrice = discountedPrice + taxAmt

        _uiState.update {
            it.copy(
                discountFinalPrice = String.format(Locale.US, "%.2f", finalPrice),
                discountSavedAmount = String.format(Locale.US, "%.2f", saved)
            )
        }
    }

    // --- BMI Calculator Actions ---

    fun onBmiInputChanged(height: String? = null, weight: String? = null, isMetric: Boolean? = null) {
        _uiState.update { state ->
            val newIsMetric = isMetric ?: state.bmiIsMetric
            var newHeight = height ?: state.bmiHeight
            var newWeight = weight ?: state.bmiWeight

            // When toggling the unit system, convert the already-entered
            // values so they aren't reinterpreted in the wrong unit
            // (175 cm became 175 inches before this fix).
            if (isMetric != null && isMetric != state.bmiIsMetric) {
                val h = state.bmiHeight.toDoubleOrNull()
                val w = state.bmiWeight.toDoubleOrNull()
                newHeight = if (isMetric) {
                    convertUnit(h) { it * 2.54 }              // in -> cm
                } else {
                    convertUnit(h) { it / 2.54 }              // cm -> in
                }
                newWeight = if (isMetric) {
                    convertUnit(w) { it * 0.45359237 }        // lb -> kg
                } else {
                    convertUnit(w) { it / 0.45359237 }        // kg -> lb
                }
            }

            it.copy(
                bmiHeight = newHeight,
                bmiWeight = newWeight,
                bmiIsMetric = newIsMetric
            )
        }
        recalculateBmi()
    }

    private fun convertUnit(value: Double?, converter: (Double) -> Double): String {
        if (value == null) return ""
        return BigDecimal.valueOf(converter(value))
            .setScale(1, RoundingMode.HALF_UP)
            .stripTrailingZeros()
            .toPlainString()
    }

    private fun recalculateBmi() {
        val state = _uiState.value
        val h = state.bmiHeight.toDoubleOrNull() ?: 0.0
        val w = state.bmiWeight.toDoubleOrNull() ?: 0.0

        if (h <= 0 || w <= 0) return

        val bmi = if (state.bmiIsMetric) {
            val meters = h / 100.0
            w / (meters * meters)
        } else {
            703.0 * w / (h * h)
        }

        val category = when {
            bmi < 18.5 -> "Underweight"
            bmi < 25.0 -> "Normal Weight"
            bmi < 30.0 -> "Overweight"
            else -> "Obese"
        }

        _uiState.update {
            it.copy(
                bmiScore = BigDecimal(bmi).setScale(2, RoundingMode.HALF_UP).toDouble(),
                bmiCategory = category
            )
        }
    }

    // --- History Actions ---

    fun recallHistoryItem(item: CalculationHistoryItem) {
        _uiState.update { state ->
            state.copy(
                activeMode = CalcMode.STANDARD,
                expression = item.expression,
                cursorPosition = item.expression.length,
                errorMessage = null
            )
        }
        updateLivePreview()
    }

    fun toggleFavoriteHistory(itemId: String) {
        viewModelScope.launch {
            HistoryDatabase.get(getApplication()).historyDao().toggleFavorite(itemId)
        }
    }

    fun clearHistory() {
        _uiState.update { it.copy(history = emptyList()) }
        viewModelScope.launch {
            HistoryDatabase.get(getApplication()).historyDao().clearAll()
        }
    }

    private companion object {
        const val PREF_THEME = "theme_style"
        const val PREF_SOUND = "sound_enabled"
        const val PREF_HAPTIC = "haptic_enabled"
        const val PREF_DEGREE_MODE = "degree_mode"
        const val PREF_MEMORY_BITS = "memory_value_bits"
        const val PREF_HAS_MEMORY = "has_memory"
        const val PREF_DECIMAL_PRECISION = "decimal_precision"
    }
}
