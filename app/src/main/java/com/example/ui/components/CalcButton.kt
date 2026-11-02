package com.example.ui.components

import android.view.HapticFeedbackConstants
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.EqualsBtnBgNeon
import com.example.ui.theme.EqualsBtnTextDark
import com.example.ui.theme.FunctionBtnBgDark
import com.example.ui.theme.NumberBtnBgDark
import com.example.ui.theme.OperatorBtnBgDark

enum class CalcButtonType {
    NUMBER,
    OPERATOR,
    FUNCTION,
    EQUALS,
    CLEAR
}

@Composable
fun CalcButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    type: CalcButtonType = CalcButtonType.NUMBER,
    hapticEnabled: Boolean = true,
    fontSize: TextUnit = 22.sp,
    height: Dp = 64.dp,
    testTag: String = "btn_$text"
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val view = LocalView.current

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1.0f,
        animationSpec = tween(durationMillis = 80),
        label = "btnScale"
    )

    val backgroundColor = when (type) {
        CalcButtonType.NUMBER -> MaterialTheme.colorScheme.surfaceVariant
        CalcButtonType.OPERATOR -> MaterialTheme.colorScheme.secondary.copy(alpha = 0.25f)
        CalcButtonType.FUNCTION -> MaterialTheme.colorScheme.tertiary.copy(alpha = 0.2f)
        CalcButtonType.EQUALS -> MaterialTheme.colorScheme.primary
        CalcButtonType.CLEAR -> MaterialTheme.colorScheme.error.copy(alpha = 0.2f)
    }

    val textColor = when (type) {
        CalcButtonType.NUMBER -> MaterialTheme.colorScheme.onSurface
        CalcButtonType.OPERATOR -> MaterialTheme.colorScheme.secondary
        CalcButtonType.FUNCTION -> MaterialTheme.colorScheme.tertiary
        CalcButtonType.EQUALS -> MaterialTheme.colorScheme.onPrimary
        CalcButtonType.CLEAR -> MaterialTheme.colorScheme.error
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .height(height)
            .scale(scale)
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            // Accessibility: expose role + a readable label so TalkBack
            // announces operator symbols correctly instead of silence.
            .semantics {
                role = Role.Button
                contentDescription = text
            }
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = {
                    if (hapticEnabled) {
                        view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                    }
                    onClick()
                }
            )
            .testTag(testTag)
            .padding(4.dp)
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = fontSize,
            fontWeight = if (type == CalcButtonType.EQUALS || type == CalcButtonType.OPERATOR) FontWeight.Bold else FontWeight.Medium
        )
    }
}
