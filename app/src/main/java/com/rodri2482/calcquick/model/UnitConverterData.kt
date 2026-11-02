package com.rodri2482.calcquick.model

enum class UnitCategory(val displayName: String) {
    LENGTH("Length"),
    WEIGHT("Weight / Mass"),
    TEMPERATURE("Temperature"),
    AREA("Area"),
    VOLUME("Volume"),
    SPEED("Speed"),
    TIME("Time"),
    DATA("Data Storage"),
    ENERGY("Energy")
}

data class UnitItem(
    val name: String,
    val symbol: String,
    val factorToBase: Double = 1.0 // ratio to base unit in SI
)

object UnitConverterData {

    fun getUnitsForCategory(category: UnitCategory): List<UnitItem> {
        return when (category) {
            UnitCategory.LENGTH -> listOf(
                UnitItem("Meter", "m", 1.0),
                UnitItem("Kilometer", "km", 1000.0),
                UnitItem("Centimeter", "cm", 0.01),
                UnitItem("Millimeter", "mm", 0.001),
                UnitItem("Mile", "mi", 1609.344),
                UnitItem("Yard", "yd", 0.9144),
                UnitItem("Foot", "ft", 0.3048),
                UnitItem("Inch", "in", 0.0254)
            )
            UnitCategory.WEIGHT -> listOf(
                UnitItem("Kilogram", "kg", 1.0),
                UnitItem("Gram", "g", 0.001),
                UnitItem("Milligram", "mg", 0.000001),
                UnitItem("Metric Ton", "t", 1000.0),
                UnitItem("Pound", "lb", 0.45359237),
                UnitItem("Ounce", "oz", 0.028349523125)
            )
            UnitCategory.TEMPERATURE -> listOf(
                UnitItem("Celsius", "°C", 1.0),
                UnitItem("Fahrenheit", "°F", 1.0),
                UnitItem("Kelvin", "K", 1.0)
            )
            UnitCategory.AREA -> listOf(
                UnitItem("Square Meter", "m²", 1.0),
                UnitItem("Square Kilometer", "km²", 1000000.0),
                UnitItem("Square Foot", "ft²", 0.092903),
                UnitItem("Acre", "ac", 4046.86),
                UnitItem("Hectare", "ha", 10000.0)
            )
            UnitCategory.VOLUME -> listOf(
                UnitItem("Liter", "L", 1.0),
                UnitItem("Milliliter", "mL", 0.001),
                UnitItem("Cubic Meter", "m³", 1000.0),
                UnitItem("Gallon (US)", "gal", 3.78541),
                UnitItem("Quart (US)", "qt", 0.946353),
                UnitItem("Cup", "cup", 0.24)
            )
            UnitCategory.SPEED -> listOf(
                UnitItem("Meter/sec", "m/s", 1.0),
                UnitItem("Kilometer/h", "km/h", 0.277778),
                UnitItem("Miles/h", "mph", 0.44704),
                UnitItem("Knot", "kn", 0.514444)
            )
            UnitCategory.TIME -> listOf(
                UnitItem("Second", "s", 1.0),
                UnitItem("Minute", "min", 60.0),
                UnitItem("Hour", "h", 3600.0),
                UnitItem("Day", "d", 86400.0),
                UnitItem("Week", "wk", 604800.0),
                UnitItem("Year", "yr", 31536000.0)
            )
            UnitCategory.DATA -> listOf(
                UnitItem("Byte", "B", 1.0),
                UnitItem("Kilobyte", "KB", 1024.0),
                UnitItem("Megabyte", "MB", 1048576.0),
                UnitItem("Gigabyte", "GB", 1073741824.0),
                UnitItem("Terabyte", "TB", 1099511627776.0)
            )
            UnitCategory.ENERGY -> listOf(
                UnitItem("Joule", "J", 1.0),
                UnitItem("Kilojoule", "kJ", 1000.0),
                UnitItem("Calorie", "cal", 4.184),
                UnitItem("Kilocalorie", "kcal", 4184.0),
                UnitItem("Kilowatt-hour", "kWh", 3600000.0)
            )
        }
    }

    fun convert(inputValue: Double, category: UnitCategory, fromUnit: UnitItem, toUnit: UnitItem): Double {
        if (fromUnit == toUnit) return inputValue
        
        if (category == UnitCategory.TEMPERATURE) {
            // Convert to Celsius first
            val celsius = when (fromUnit.symbol) {
                "°C" -> inputValue
                "°F" -> (inputValue - 32.0) * 5.0 / 9.0
                "K" -> inputValue - 273.15
                else -> inputValue
            }
            // Convert from Celsius to target
            return when (toUnit.symbol) {
                "°C" -> celsius
                "°F" -> (celsius * 9.0 / 5.0) + 32.0
                "K" -> celsius + 273.15
                else -> celsius
            }
        }

        // Standard proportional conversion via base factor
        val baseValue = inputValue * fromUnit.factorToBase
        return baseValue / toUnit.factorToBase
    }
}
