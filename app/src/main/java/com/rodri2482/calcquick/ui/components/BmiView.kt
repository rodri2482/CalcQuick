package com.rodri2482.calcquick.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun BmiView(
    isMetric: Boolean,
    height: String,
    weight: String,
    bmiScore: Double,
    category: String,
    onInputChanged: (height: String?, weight: String?, isMetric: Boolean?) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    val (badgeColor, textColor) = when (category) {
        "Underweight" -> Color(0xFF0288D1) to Color.White
        "Normal Weight" -> Color(0xFF2E7D32) to Color.White
        "Overweight" -> Color(0xFFF57C00) to Color.White
        else -> Color(0xFFC62828) to Color.White
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Unit System Switcher
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            SingleChoiceSegmentedButtonRow {
                SegmentedButton(
                    selected = isMetric,
                    onClick = { onInputChanged(null, null, true) },
                    shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2)
                ) {
                    Text("Metric (cm / kg)")
                }
                SegmentedButton(
                    selected = !isMetric,
                    onClick = { onInputChanged(null, null, false) },
                    shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2)
                ) {
                    Text("Imperial (in / lbs)")
                }
            }
        }

        // Hero Card: BMI Result
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "YOUR BMI SCORE",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = String.format("%.1f", bmiScore),
                    fontSize = 44.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.testTag("text_bmi_score")
                )
                Spacer(modifier = Modifier.height(8.dp))

                Surface(
                    color = badgeColor,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = category,
                        color = textColor,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                            .testTag("badge_bmi_category")
                    )
                }
            }
        }

        // Inputs
        OutlinedTextField(
            value = height,
            onValueChange = { onInputChanged(it, null, null) },
            label = { Text(if (isMetric) "Height (cm)" else "Height (inches)") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("input_bmi_height")
        )

        OutlinedTextField(
            value = weight,
            onValueChange = { onInputChanged(null, it, null) },
            label = { Text(if (isMetric) "Weight (kg)" else "Weight (lbs)") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("input_bmi_weight")
        )
    }
}
