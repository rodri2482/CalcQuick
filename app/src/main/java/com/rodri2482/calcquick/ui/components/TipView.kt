package com.rodri2482.calcquick.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TipView(
    billAmount: String,
    tipPercentage: Int,
    splitCount: Int,
    tipAmount: String,
    totalAmount: String,
    perPersonAmount: String,
    onInputChanged: (bill: String?, tipPct: Int?, split: Int?) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Card: Per Person Amount
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "AMOUNT PER PERSON",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "$$perPersonAmount",
                    fontSize = 38.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.testTag("text_per_person")
                )
            }
        }

        // Summary Breakdown: Tip Total & Grand Total
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.weight(1f)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Total Tip", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("$$tipAmount", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.weight(1f)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Grand Total", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("$$totalAmount", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Bill Amount Input
        OutlinedTextField(
            value = billAmount,
            onValueChange = { onInputChanged(it, null, null) },
            label = { Text("Bill Total ($)") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("input_bill_amount")
        )

        // Tip Percentage Selection Chips
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Tip Percentage ($tipPercentage%)", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(10, 15, 18, 20, 25).forEach { pct ->
                    FilterChip(
                        selected = tipPercentage == pct,
                        onClick = { onInputChanged(null, pct, null) },
                        label = { Text("$pct%") },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Split Bill Stepper
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Split Between", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Text("$splitCount ${if (splitCount == 1) "person" else "people"}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    FilledIconButton(
                        onClick = { onInputChanged(null, null, splitCount - 1) },
                        enabled = splitCount > 1,
                        shape = CircleShape
                    ) {
                        Icon(Icons.Default.Remove, contentDescription = "Decrease split")
                    }

                    Text(
                        text = "$splitCount",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )

                    FilledIconButton(
                        onClick = { onInputChanged(null, null, splitCount + 1) },
                        shape = CircleShape
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Increase split")
                    }
                }
            }
        }
    }
}
