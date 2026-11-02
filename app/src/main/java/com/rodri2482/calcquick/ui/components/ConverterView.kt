package com.rodri2482.calcquick.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rodri2482.calcquick.model.UnitCategory
import com.rodri2482.calcquick.model.UnitConverterData
import com.rodri2482.calcquick.model.UnitItem

@Composable
fun ConverterView(
    category: UnitCategory,
    inputValue: String,
    resultValue: String,
    fromUnit: UnitItem,
    toUnit: UnitItem,
    onCategorySelected: (UnitCategory) -> Unit,
    onInputChanged: (String) -> Unit,
    onFromUnitChanged: (UnitItem) -> Unit,
    onToUnitChanged: (UnitItem) -> Unit,
    onSwapUnits: () -> Unit,
    hapticEnabled: Boolean,
    modifier: Modifier = Modifier
) {
    val availableUnits = remember(category) { UnitConverterData.getUnitsForCategory(category) }
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Category chips row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(scrollState),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            UnitCategory.values().forEach { cat ->
                FilterChip(
                    selected = cat == category,
                    onClick = { onCategorySelected(cat) },
                    label = { Text(cat.displayName, fontSize = 12.sp) },
                    modifier = Modifier.height(32.dp)
                )
            }
        }

        // Conversion Display Cards Area
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // FROM Unit Block
                UnitInputBlock(
                    label = "From",
                    value = inputValue,
                    unit = fromUnit,
                    availableUnits = availableUnits,
                    onUnitSelected = onFromUnitChanged,
                    isEditable = true
                )

                // Swap Button Row
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(
                        onClick = onSwapUnits,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary)
                            .testTag("btn_swap_units")
                    ) {
                        Icon(
                            imageVector = Icons.Default.SwapVert,
                            contentDescription = "Swap units",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }

                // TO Unit Block
                UnitInputBlock(
                    label = "To",
                    value = resultValue,
                    unit = toUnit,
                    availableUnits = availableUnits,
                    onUnitSelected = onToUnitChanged,
                    isEditable = false
                )
            }
        }

        // Mini Numeric Keypad for Converter Input
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                CalcButton(text = "7", height = 52.dp, onClick = { onInputChanged(appendDigit(inputValue, "7")) }, hapticEnabled = hapticEnabled, modifier = Modifier.weight(1f))
                CalcButton(text = "8", height = 52.dp, onClick = { onInputChanged(appendDigit(inputValue, "8")) }, hapticEnabled = hapticEnabled, modifier = Modifier.weight(1f))
                CalcButton(text = "9", height = 52.dp, onClick = { onInputChanged(appendDigit(inputValue, "9")) }, hapticEnabled = hapticEnabled, modifier = Modifier.weight(1f))
                CalcButton(text = "⌫", height = 52.dp, type = CalcButtonType.CLEAR, onClick = { onInputChanged(backspace(inputValue)) }, hapticEnabled = hapticEnabled, modifier = Modifier.weight(1f))
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                CalcButton(text = "4", height = 52.dp, onClick = { onInputChanged(appendDigit(inputValue, "4")) }, hapticEnabled = hapticEnabled, modifier = Modifier.weight(1f))
                CalcButton(text = "5", height = 52.dp, onClick = { onInputChanged(appendDigit(inputValue, "5")) }, hapticEnabled = hapticEnabled, modifier = Modifier.weight(1f))
                CalcButton(text = "6", height = 52.dp, onClick = { onInputChanged(appendDigit(inputValue, "6")) }, hapticEnabled = hapticEnabled, modifier = Modifier.weight(1f))
                CalcButton(text = "C", height = 52.dp, type = CalcButtonType.CLEAR, onClick = { onInputChanged("0") }, hapticEnabled = hapticEnabled, modifier = Modifier.weight(1f))
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                CalcButton(text = "1", height = 52.dp, onClick = { onInputChanged(appendDigit(inputValue, "1")) }, hapticEnabled = hapticEnabled, modifier = Modifier.weight(1f))
                CalcButton(text = "2", height = 52.dp, onClick = { onInputChanged(appendDigit(inputValue, "2")) }, hapticEnabled = hapticEnabled, modifier = Modifier.weight(1f))
                CalcButton(text = "3", height = 52.dp, onClick = { onInputChanged(appendDigit(inputValue, "3")) }, hapticEnabled = hapticEnabled, modifier = Modifier.weight(1f))
                CalcButton(text = ".", height = 52.dp, onClick = { onInputChanged(appendDigit(inputValue, ".")) }, hapticEnabled = hapticEnabled, modifier = Modifier.weight(1f))
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                CalcButton(text = "0", height = 52.dp, onClick = { onInputChanged(appendDigit(inputValue, "0")) }, hapticEnabled = hapticEnabled, modifier = Modifier.weight(2f))
                CalcButton(text = "00", height = 52.dp, onClick = { onInputChanged(appendDigit(inputValue, "00")) }, hapticEnabled = hapticEnabled, modifier = Modifier.weight(1f))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UnitInputBlock(
    label: String,
    value: String,
    unit: UnitItem,
    availableUnits: List<UnitItem>,
    onUnitSelected: (UnitItem) -> Unit,
    isEditable: Boolean
) {
    var expanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(
                text = value.ifEmpty { "0" },
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = if (isEditable) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
            )
        }

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it }
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 2.dp,
                modifier = Modifier
                    .menuAnchor()
                    .clickable { expanded = true }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(text = "${unit.name} (${unit.symbol})", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                }
            }

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                availableUnits.forEach { item ->
                    DropdownMenuItem(
                        text = { Text("${item.name} (${item.symbol})") },
                        onClick = {
                            onUnitSelected(item)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

private fun appendDigit(current: String, digit: String): String {
    if (current == "0" && digit != ".") return digit
    if (digit == "." && current.contains(".")) return current
    return current + digit
}

private fun backspace(current: String): String {
    if (current.length <= 1) return "0"
    return current.dropLast(1)
}
