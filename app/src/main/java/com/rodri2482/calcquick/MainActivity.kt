package com.rodri2482.calcquick

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rodri2482.calcquick.model.CalcMode
import com.rodri2482.calcquick.ui.components.*
import com.rodri2482.calcquick.ui.theme.ApexCalcTheme
import com.rodri2482.calcquick.viewmodel.CalculatorViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: CalculatorViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            var showHistorySheet by remember { mutableStateOf(false) }
            var showSettingsDialog by remember { mutableStateOf(false) }

            ApexCalcTheme(themeStyle = uiState.themeStyle) {
                CompositionLocalProvider(LocalFeedbackSound provides uiState.soundEnabled) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Scaffold(
                        contentWindowInsets = WindowInsets.safeDrawing
                    ) { innerPadding ->
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            // Mode Selection Bar at top
                            ModeSelectorRow(
                                selectedMode = uiState.activeMode,
                                onModeSelected = { mode ->
                                    if (mode == CalcMode.HISTORY) {
                                        showHistorySheet = true
                                    } else {
                                        viewModel.setMode(mode)
                                    }
                                }
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Main Content view according to mode
                            when (uiState.activeMode) {
                                CalcMode.STANDARD, CalcMode.SCIENTIFIC -> {
                                    // Display Area
                                    CalcDisplay(
                                        expression = uiState.expression,
                                        livePreview = uiState.livePreview,
                                        isDegreeMode = uiState.isDegreeMode,
                                        hasMemory = uiState.hasMemory,
                                        errorMessage = uiState.errorMessage,
                                        onBackspace = { viewModel.onBackspace() },
                                        onClearAll = { viewModel.onClearAll() },
                                        onToggleDegRad = { viewModel.toggleDegreeRadian() },
                                        onOpenHistory = { showHistorySheet = true },
                                        onOpenSettings = { showSettingsDialog = true },
                                        modifier = Modifier.weight(1f)
                                    )

                                    Spacer(modifier = Modifier.height(12.dp))

                                    // Scientific expansion pad if in Scientific mode
                                    if (uiState.activeMode == CalcMode.SCIENTIFIC) {
                                        ScientificPad(
                                            onKeyInput = { viewModel.onKeyInput(it) },
                                            onFunctionInput = { viewModel.onFunctionInput(it) },
                                            onMemoryClear = { viewModel.memoryClear() },
                                            onMemoryRecall = { viewModel.memoryRecall() },
                                            onMemoryAdd = { viewModel.memoryAdd() },
                                            onMemorySubtract = { viewModel.memorySubtract() },
                                            hapticEnabled = uiState.hapticEnabled
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                    }

                                    // Standard Numeric Keypad
                                    StandardPad(
                                        expression = uiState.expression,
                                        onKeyInput = { viewModel.onKeyInput(it) },
                                        onBackspace = { viewModel.onBackspace() },
                                        onClearAll = { viewModel.onClearAll() },
                                        onEqualsPressed = { viewModel.onEqualsPressed() },
                                        onTogglePlusMinus = { viewModel.togglePlusMinus() },
                                        hapticEnabled = uiState.hapticEnabled
                                    )
                                }

                                CalcMode.CONVERTER -> {
                                    ConverterView(
                                        category = uiState.converterCategory,
                                        inputValue = uiState.converterInput,
                                        resultValue = uiState.converterResult,
                                        fromUnit = uiState.fromUnit,
                                        toUnit = uiState.toUnit,
                                        onCategorySelected = { viewModel.onConverterCategorySelected(it) },
                                        onInputChanged = { viewModel.onConverterInputChanged(it) },
                                        onFromUnitChanged = { viewModel.onConverterFromUnitChanged(it) },
                                        onToUnitChanged = { viewModel.onConverterToUnitChanged(it) },
                                        onSwapUnits = { viewModel.swapConverterUnits() },
                                        hapticEnabled = uiState.hapticEnabled,
                                        modifier = Modifier.weight(1f)
                                    )
                                }

                                CalcMode.LOAN -> {
                                    LoanView(
                                        principal = uiState.loanPrincipal,
                                        interestRate = uiState.loanInterestRate,
                                        termYears = uiState.loanTermYears,
                                        monthlyPayment = uiState.loanMonthlyPayment,
                                        totalPayment = uiState.loanTotalPayment,
                                        totalInterest = uiState.loanTotalInterest,
                                        onInputChanged = { p, r, y -> viewModel.onLoanInputChanged(p, r, y) },
                                        modifier = Modifier.weight(1f)
                                    )
                                }

                                CalcMode.TIP_SPLIT -> {
                                    TipView(
                                        billAmount = uiState.tipBillAmount,
                                        tipPercentage = uiState.tipPercentage,
                                        splitCount = uiState.tipSplitCount,
                                        tipAmount = uiState.tipAmount,
                                        totalAmount = uiState.tipTotalAmount,
                                        perPersonAmount = uiState.tipPerPersonAmount,
                                        onInputChanged = { b, t, s -> viewModel.onTipInputChanged(b, t, s) },
                                        modifier = Modifier.weight(1f)
                                    )
                                }

                                CalcMode.DISCOUNT -> {
                                    DiscountView(
                                        originalPrice = uiState.discountOriginalPrice,
                                        discountPercent = uiState.discountPercent,
                                        taxPercent = uiState.discountTaxPercent,
                                        finalPrice = uiState.discountFinalPrice,
                                        savedAmount = uiState.discountSavedAmount,
                                        onInputChanged = { p, d, t -> viewModel.onDiscountInputChanged(p, d, t) },
                                        modifier = Modifier.weight(1f)
                                    )
                                }

                                CalcMode.BMI -> {
                                    BmiView(
                                        isMetric = uiState.bmiIsMetric,
                                        height = uiState.bmiHeight,
                                        weight = uiState.bmiWeight,
                                        bmiScore = uiState.bmiScore,
                                        category = uiState.bmiCategory,
                                        onInputChanged = { h, w, m -> viewModel.onBmiInputChanged(h, w, m) },
                                        modifier = Modifier.weight(1f)
                                    )
                                }

                                CalcMode.HISTORY -> {
                                    // Managed via modal sheet
                                }
                            }
                        }
                    }

                    // Modals
                    if (showHistorySheet) {
                        HistorySheet(
                            history = uiState.history,
                            onItemClick = { item ->
                                viewModel.recallHistoryItem(item)
                                showHistorySheet = false
                            },
                            onFavoriteToggle = { viewModel.toggleFavoriteHistory(it) },
                            onClearHistory = { viewModel.clearHistory() },
                            onDismiss = { showHistorySheet = false }
                        )
                    }

                    if (showSettingsDialog) {
                        SettingsDialog(
                            currentTheme = uiState.themeStyle,
                            soundEnabled = uiState.soundEnabled,
                            hapticEnabled = uiState.hapticEnabled,
                            decimalPrecision = uiState.decimalPrecision,
                            onThemeSelected = { viewModel.setThemeStyle(it) },
                            onToggleSound = { viewModel.toggleSound() },
                            onToggleHaptic = { viewModel.toggleHaptic() },
                            onPrecisionSelected = { viewModel.setDecimalPrecision(it) },
                            onDismiss = { showSettingsDialog = false }
                        )
                    }
                    }
                }
            }
        }
    }
}
