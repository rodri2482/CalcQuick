package com.rodri2482.calcquick.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
fun DiscountView(
    originalPrice: String,
    discountPercent: String,
    taxPercent: String,
    finalPrice: String,
    savedAmount: String,
    onInputChanged: (price: String?, discount: String?, tax: String?) -> Unit,
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
        // Hero Card: Final Price
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "FINAL SALE PRICE",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.7f)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "$$finalPrice",
                    fontSize = 38.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onTertiaryContainer,
                    modifier = Modifier.testTag("text_final_price")
                )
                Spacer(modifier = Modifier.height(6.dp))
                SuggestionChip(
                    onClick = { },
                    label = { Text("You Save $$savedAmount", fontWeight = FontWeight.Bold) }
                )
            }
        }

        // Original Price Input
        OutlinedTextField(
            value = originalPrice,
            onValueChange = { onInputChanged(it, null, null) },
            label = { Text("Original Price ($)") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("input_orig_price")
        )

        // Discount Percent Input & Quick Chips
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = discountPercent,
                onValueChange = { onInputChanged(null, it, null) },
                label = { Text("Discount (%)") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf("10", "15", "20", "25", "30", "50").forEach { pct ->
                    FilterChip(
                        selected = discountPercent == pct,
                        onClick = { onInputChanged(null, pct, null) },
                        label = { Text("$pct%") },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Tax Percent Input
        OutlinedTextField(
            value = taxPercent,
            onValueChange = { onInputChanged(null, null, it) },
            label = { Text("Sales Tax (%)") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth()
        )
    }
}
