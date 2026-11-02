package com.rodri2482.calcquick

import com.rodri2482.calcquick.evaluator.MathExpressionEvaluator
import com.rodri2482.calcquick.model.UnitCategory
import com.rodri2482.calcquick.model.UnitConverterData
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun testBasicArithmetic() {
        val result1 = MathExpressionEvaluator.evaluate("2 + 3 * 4")
        assertEquals("14", result1)

        val result2 = MathExpressionEvaluator.evaluate("(2 + 3) * 4")
        assertEquals("20", result2)

        val result3 = MathExpressionEvaluator.evaluate("10 / 4")
        assertEquals("2.5", result3)
    }

    @Test
    fun testTrigonometricFunctions() {
        val sin30Deg = MathExpressionEvaluator.evaluate("sin(30)", isDegreeMode = true)
        assertEquals("0.5", sin30Deg)

        val cos0Rad = MathExpressionEvaluator.evaluate("cos(0)", isDegreeMode = false)
        assertEquals("1", cos0Rad)
    }

    @Test
    fun testFactorialAndPercentages() {
        val fact = MathExpressionEvaluator.evaluate("5!")
        assertEquals("120", fact)

        val pct = MathExpressionEvaluator.evaluate("50 * 20%")
        assertEquals("10", pct)
    }

    @Test
    fun testUnitConverter() {
        val units = UnitConverterData.getUnitsForCategory(UnitCategory.LENGTH)
        val meter = units.first { it.symbol == "m" }
        val cm = units.first { it.symbol == "cm" }

        val converted = UnitConverterData.convert(2.5, UnitCategory.LENGTH, meter, cm)
        assertEquals(250.0, converted, 0.001)
    }

    @Test(expected = MathExpressionEvaluator.EvaluationException::class)
    fun testDivisionByZero() {
        MathExpressionEvaluator.evaluate("10 / 0")
    }
}
