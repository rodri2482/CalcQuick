package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun StandardPad(
    expression: String,
    onKeyInput: (String) -> Unit,
    onBackspace: () -> Unit,
    onClearAll: () -> Unit,
    onEqualsPressed: () -> Unit,
    onTogglePlusMinus: () -> Unit,
    hapticEnabled: Boolean,
    modifier: Modifier = Modifier
) {
    val openCount = expression.count { it == '(' }
    val closeCount = expression.count { it == ')' }
    val nextParen = if (openCount > closeCount && expression.lastOrNull() !in listOf('(', '+', '−', '×', '÷')) ")" else "("

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Row 1: AC, (), %, ÷
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CalcButton(
                text = "AC",
                type = CalcButtonType.CLEAR,
                onClick = onClearAll,
                hapticEnabled = hapticEnabled,
                modifier = Modifier.weight(1f),
                testTag = "btn_clear"
            )
            CalcButton(
                text = "()",
                type = CalcButtonType.OPERATOR,
                onClick = { onKeyInput(nextParen) },
                hapticEnabled = hapticEnabled,
                modifier = Modifier.weight(1f),
                testTag = "btn_paren"
            )
            CalcButton(
                text = "%",
                type = CalcButtonType.OPERATOR,
                onClick = { onKeyInput("%") },
                hapticEnabled = hapticEnabled,
                modifier = Modifier.weight(1f),
                testTag = "btn_percent"
            )
            CalcButton(
                text = "÷",
                type = CalcButtonType.OPERATOR,
                onClick = { onKeyInput("÷") },
                hapticEnabled = hapticEnabled,
                modifier = Modifier.weight(1f),
                testTag = "btn_divide"
            )
        }

        // Row 2: 7, 8, 9, ×
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CalcButton(text = "7", onClick = { onKeyInput("7") }, hapticEnabled = hapticEnabled, modifier = Modifier.weight(1f))
            CalcButton(text = "8", onClick = { onKeyInput("8") }, hapticEnabled = hapticEnabled, modifier = Modifier.weight(1f))
            CalcButton(text = "9", onClick = { onKeyInput("9") }, hapticEnabled = hapticEnabled, modifier = Modifier.weight(1f))
            CalcButton(
                text = "×",
                type = CalcButtonType.OPERATOR,
                onClick = { onKeyInput("×") },
                hapticEnabled = hapticEnabled,
                modifier = Modifier.weight(1f),
                testTag = "btn_multiply"
            )
        }

        // Row 3: 4, 5, 6, −
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CalcButton(text = "4", onClick = { onKeyInput("4") }, hapticEnabled = hapticEnabled, modifier = Modifier.weight(1f))
            CalcButton(text = "5", onClick = { onKeyInput("5") }, hapticEnabled = hapticEnabled, modifier = Modifier.weight(1f))
            CalcButton(text = "6", onClick = { onKeyInput("6") }, hapticEnabled = hapticEnabled, modifier = Modifier.weight(1f))
            CalcButton(
                text = "−",
                type = CalcButtonType.OPERATOR,
                onClick = { onKeyInput("−") },
                hapticEnabled = hapticEnabled,
                modifier = Modifier.weight(1f),
                testTag = "btn_subtract"
            )
        }

        // Row 4: 1, 2, 3, +
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CalcButton(text = "1", onClick = { onKeyInput("1") }, hapticEnabled = hapticEnabled, modifier = Modifier.weight(1f))
            CalcButton(text = "2", onClick = { onKeyInput("2") }, hapticEnabled = hapticEnabled, modifier = Modifier.weight(1f))
            CalcButton(text = "3", onClick = { onKeyInput("3") }, hapticEnabled = hapticEnabled, modifier = Modifier.weight(1f))
            CalcButton(
                text = "+",
                type = CalcButtonType.OPERATOR,
                onClick = { onKeyInput("+") },
                hapticEnabled = hapticEnabled,
                modifier = Modifier.weight(1f),
                testTag = "btn_add"
            )
        }

        // Row 5: +/-, 0, ., =, ⌫ (or backspace embedded)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CalcButton(
                text = "+/−",
                type = CalcButtonType.OPERATOR,
                onClick = onTogglePlusMinus,
                hapticEnabled = hapticEnabled,
                modifier = Modifier.weight(1f),
                testTag = "btn_plus_minus"
            )
            CalcButton(text = "0", onClick = { onKeyInput("0") }, hapticEnabled = hapticEnabled, modifier = Modifier.weight(1f))
            CalcButton(text = ".", onClick = { onKeyInput(".") }, hapticEnabled = hapticEnabled, modifier = Modifier.weight(1f))
            CalcButton(
                text = "⌫",
                type = CalcButtonType.CLEAR,
                onClick = onBackspace,
                hapticEnabled = hapticEnabled,
                modifier = Modifier.weight(1f),
                testTag = "btn_backspace"
            )
            CalcButton(
                text = "=",
                type = CalcButtonType.EQUALS,
                onClick = onEqualsPressed,
                hapticEnabled = hapticEnabled,
                modifier = Modifier.weight(1f),
                testTag = "btn_equals"
            )
        }
    }
}
