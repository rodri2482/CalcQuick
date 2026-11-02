package com.rodri2482.calcquick.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ScientificPad(
    onKeyInput: (String) -> Unit,
    onFunctionInput: (String) -> Unit,
    onMemoryClear: () -> Unit,
    onMemoryRecall: () -> Unit,
    onMemoryAdd: () -> Unit,
    onMemorySubtract: () -> Unit,
    hapticEnabled: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // Row 1: sin, cos, tan, log, ln, sqrt
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            CalcButton(text = "sin", fontSize = 15.sp, height = 48.dp, type = CalcButtonType.FUNCTION, onClick = { onFunctionInput("sin") }, hapticEnabled = hapticEnabled, modifier = Modifier.weight(1f))
            CalcButton(text = "cos", fontSize = 15.sp, height = 48.dp, type = CalcButtonType.FUNCTION, onClick = { onFunctionInput("cos") }, hapticEnabled = hapticEnabled, modifier = Modifier.weight(1f))
            CalcButton(text = "tan", fontSize = 15.sp, height = 48.dp, type = CalcButtonType.FUNCTION, onClick = { onFunctionInput("tan") }, hapticEnabled = hapticEnabled, modifier = Modifier.weight(1f))
            CalcButton(text = "log", fontSize = 15.sp, height = 48.dp, type = CalcButtonType.FUNCTION, onClick = { onFunctionInput("log") }, hapticEnabled = hapticEnabled, modifier = Modifier.weight(1f))
            CalcButton(text = "ln", fontSize = 15.sp, height = 48.dp, type = CalcButtonType.FUNCTION, onClick = { onFunctionInput("ln") }, hapticEnabled = hapticEnabled, modifier = Modifier.weight(1f))
            CalcButton(text = "√", fontSize = 16.sp, height = 48.dp, type = CalcButtonType.FUNCTION, onClick = { onFunctionInput("sqrt") }, hapticEnabled = hapticEnabled, modifier = Modifier.weight(1f))
        }

        // Row 2: asin, acos, atan, x², x^y, x!
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            CalcButton(text = "asin", fontSize = 14.sp, height = 48.dp, type = CalcButtonType.FUNCTION, onClick = { onFunctionInput("asin") }, hapticEnabled = hapticEnabled, modifier = Modifier.weight(1f))
            CalcButton(text = "acos", fontSize = 14.sp, height = 48.dp, type = CalcButtonType.FUNCTION, onClick = { onFunctionInput("acos") }, hapticEnabled = hapticEnabled, modifier = Modifier.weight(1f))
            CalcButton(text = "atan", fontSize = 14.sp, height = 48.dp, type = CalcButtonType.FUNCTION, onClick = { onFunctionInput("atan") }, hapticEnabled = hapticEnabled, modifier = Modifier.weight(1f))
            CalcButton(text = "x²", fontSize = 15.sp, height = 48.dp, type = CalcButtonType.FUNCTION, onClick = { onKeyInput("²") }, hapticEnabled = hapticEnabled, modifier = Modifier.weight(1f))
            CalcButton(text = "x^y", fontSize = 15.sp, height = 48.dp, type = CalcButtonType.FUNCTION, onClick = { onKeyInput("^") }, hapticEnabled = hapticEnabled, modifier = Modifier.weight(1f))
            CalcButton(text = "x!", fontSize = 15.sp, height = 48.dp, type = CalcButtonType.FUNCTION, onClick = { onKeyInput("!") }, hapticEnabled = hapticEnabled, modifier = Modifier.weight(1f))
        }

        // Row 3: π, e, abs, MC, MR, M+, M-
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            CalcButton(text = "π", fontSize = 16.sp, height = 48.dp, type = CalcButtonType.FUNCTION, onClick = { onKeyInput("π") }, hapticEnabled = hapticEnabled, modifier = Modifier.weight(1f))
            CalcButton(text = "e", fontSize = 16.sp, height = 48.dp, type = CalcButtonType.FUNCTION, onClick = { onKeyInput("e") }, hapticEnabled = hapticEnabled, modifier = Modifier.weight(1f))
            CalcButton(text = "abs", fontSize = 14.sp, height = 48.dp, type = CalcButtonType.FUNCTION, onClick = { onFunctionInput("abs") }, hapticEnabled = hapticEnabled, modifier = Modifier.weight(1f))
            CalcButton(text = "MC", fontSize = 13.sp, height = 48.dp, type = CalcButtonType.OPERATOR, onClick = onMemoryClear, hapticEnabled = hapticEnabled, modifier = Modifier.weight(1f))
            CalcButton(text = "MR", fontSize = 13.sp, height = 48.dp, type = CalcButtonType.OPERATOR, onClick = onMemoryRecall, hapticEnabled = hapticEnabled, modifier = Modifier.weight(1f))
            CalcButton(text = "M+", fontSize = 13.sp, height = 48.dp, type = CalcButtonType.OPERATOR, onClick = onMemoryAdd, hapticEnabled = hapticEnabled, modifier = Modifier.weight(1f))
            CalcButton(text = "M-", fontSize = 13.sp, height = 48.dp, type = CalcButtonType.OPERATOR, onClick = onMemorySubtract, hapticEnabled = hapticEnabled, modifier = Modifier.weight(1f))
        }
    }
}
