package com.rodri2482.calcquick.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*

enum class CalcMode(
    val title: String,
    val description: String,
    val tag: String
) {
    STANDARD("Standard", "Basic arithmetic & percentage", "standard_mode"),
    SCIENTIFIC("Scientific", "Trigonometry, log, pow, & roots", "scientific_mode"),
    CONVERTER("Unit Converter", "Length, weight, temp, volume, & data", "converter_mode"),
    LOAN("Loan & Mortgage", "Monthly payment & total interest", "loan_mode"),
    TIP_SPLIT("Tip & Split Bill", "Split bills & calculate tips easily", "tip_mode"),
    DISCOUNT("Discount & Tax", "Sale savings & final price with tax", "discount_mode"),
    BMI("BMI Health", "Body Mass Index & weight health check", "bmi_mode"),
    HISTORY("History Log", "View & recall past calculations", "history_mode")
}
