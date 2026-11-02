package com.rodri2482.calcquick.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rodri2482.calcquick.viewmodel.ThemeStyle

@Composable
fun SettingsDialog(
    currentTheme: ThemeStyle,
    soundEnabled: Boolean,
    hapticEnabled: Boolean,
    decimalPrecision: Int,
    onThemeSelected: (ThemeStyle) -> Unit,
    onToggleSound: () -> Unit,
    onToggleHaptic: () -> Unit,
    onPrecisionSelected: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Calculator Settings", fontWeight = FontWeight.Bold, fontSize = 20.sp)
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Theme Selector
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Theme Style", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    ThemeStyle.values().forEach { style ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onThemeSelected(style) }
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = currentTheme == style,
                                onClick = { onThemeSelected(style) }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(style.displayName, fontSize = 14.sp)
                        }
                    }
                }

                Divider()

                // Decimal precision
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Decimal Places", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    SingleChoiceSegmentedButtonRow {
                        listOf(2, 4, 6, 8).forEachIndexed { index, places ->
                            SegmentedButton(
                                selected = decimalPrecision == places,
                                onClick = { onPrecisionSelected(places) },
                                shape = SegmentedButtonDefaults.itemShape(index = index, count = 4)
                            ) {
                                Text("$places", fontSize = 14.sp)
                            }
                        }
                    }
                }

                Divider()

                // Feedback Toggles
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Sound Feedback", fontSize = 14.sp)
                    Switch(checked = soundEnabled, onCheckedChange = { onToggleSound() })
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Haptic Vibration", fontSize = 14.sp)
                    Switch(checked = hapticEnabled, onCheckedChange = { onToggleHaptic() })
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss, modifier = Modifier.testTag("btn_close_settings")) {
                Text("Done")
            }
        },
        shape = RoundedCornerShape(24.dp)
    )
}
