package com.rodri2482.calcquick

import com.rodri2482.calcquick.evaluator.MathExpressionEvaluator
import com.rodri2482.calcquick.evaluator.MathExpressionEvaluator.EvaluationException
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

/**
 * Unit tests for the hand-written expression evaluator.
 *
 * These run on the plain JVM (no Android dependency in the evaluator),
 * so they work locally and in CI without an emulator.
 */
class MathExpressionEvaluatorTest {

    // --- Basic arithmetic ---

    @Test
    fun addition() = assertEquals("4", MathExpressionEvaluator.evaluate("2+2"))

    @Test
    fun precedence() = assertEquals("14", MathExpressionEvaluator.evaluate("2+3*4"))

    @Test
    fun parenthesesPrecedence() = assertEquals("20", MathExpressionEvaluator.evaluate("(2+3)*4"))

    @Test
    fun divisionResultHasUsDecimalPoint() {
        // Guards the locale bug: results must always parse with '.' as the
        // decimal separator regardless of the device JVM default locale.
        val result = MathExpressionEvaluator.evaluate("1/8")
        assertEquals("0.125", result)
        assertTrue(!result.contains(","))
    }

    // --- Errors ---

    @Test
    fun divideByZeroThrows() {
        assertEvaluationError("Cannot divide by zero") { MathExpressionEvaluator.evaluate("10/0") }
    }

    @Test
    fun sqrtOfNegativeThrowsDomainError() {
        assertEvaluationError("Domain Error") { MathExpressionEvaluator.evaluate("sqrt(-1)") }
    }

    @Test
    fun factorialAbove170Throws() {
        assertEvaluationError("Invalid Factorial") { MathExpressionEvaluator.evaluate("171!") }
    }

    @Test
    fun overflowReportsResultTooLarge() {
        // Used to be misreported as "Cannot divide by zero"
        assertEvaluationError("Result too large") { MathExpressionEvaluator.evaluate("9^99^9") }
    }

    @Test
    fun deepParenNestingFailsGracefully() {
        // Used to throw a StackOverflowError (uncaught) and crash the app
        val expression = "(".repeat(300)
        assertEvaluationError { MathExpressionEvaluator.evaluate(expression) }
    }

    @Test
    fun syntaxErrorForGarbage() {
        assertEvaluationError { MathExpressionEvaluator.evaluate("2++*") }
        assertEvaluationError { MathExpressionEvaluator.evaluate("foo(2)") }
    }

    // --- Percent & factorial ---

    @Test
    fun percentIsPostfixDivideBy100() =
        assertEquals("0.5", MathExpressionEvaluator.evaluate("50%"))

    @Test
    fun percentOfOperandOnAddition() {
        // Conventional: A + B% == A + A%*B
        assertEquals("15", MathExpressionEvaluator.evaluate("10+50%"))
        assertEquals("3", MathExpressionEvaluator.evaluate("2+50%"))
    }

    @Test
    fun percentOfOperandOnSubtraction() {
        // Conventional: A - B% == A - A%*B
        assertEquals("50", MathExpressionEvaluator.evaluate("100-50%"))
        assertEquals("5", MathExpressionEvaluator.evaluate("10-50%"))
    }

    @Test
    fun multiplyByPercentDividesBy100() =
        assertEquals("5", MathExpressionEvaluator.evaluate("10*50%"))

    @Test
    fun percentOfRunningTotalMatchesConventionalCalculators() {
        // Windows/Google calculator convention: the percent applies to the
        // whole expression so far
        assertEquals("440", MathExpressionEvaluator.evaluate("200*2+10%"))
        assertEquals("93.6", MathExpressionEvaluator.evaluate("(2+50)*2-10%"))
    }

    @Test
    fun moduloOperator() {
        // Binary % was previously unreachable (postfix percent consumed it)
        assertEquals("1", MathExpressionEvaluator.evaluate("10%3"))
        assertEquals("0", MathExpressionEvaluator.evaluate("10%2"))
    }

    @Test
    fun moduloByZeroThrows() {
        assertEvaluationError("Cannot divide by zero") { MathExpressionEvaluator.evaluate("10%0") }
    }

    @Test
    fun factorial() = assertEquals("120", MathExpressionEvaluator.evaluate("5!"))

    // --- Powers & implicit multiplication ---

    @Test
    fun powerOperator() = assertEquals("1024", MathExpressionEvaluator.evaluate("2^10"))

    @Test
    fun implicitMultiplicationWithParentheses() =
        assertEquals("6", MathExpressionEvaluator.evaluate("2(3)"))

    @Test
    fun implicitMultiplicationWithConstants() {
        // 2 * PI rounded to 4 dp
        assertEquals("6.2832", MathExpressionEvaluator.evaluate("2PI", decimalPrecision = 4))
    }

    // --- Functions ---

    @Test
    fun degreeModeTrig() {
        assertEquals("1", MathExpressionEvaluator.evaluate("sin(90)", decimalPrecision = 4))
        assertEquals("0.5", MathExpressionEvaluator.evaluate("cos(60)", decimalPrecision = 4))
    }

    @Test
    fun radianModeTrig() {
        // sin(pi/2) = 1
        assertEquals("1", MathExpressionEvaluator.evaluate("sin(PI/2)", isDegreeMode = false, decimalPrecision = 6))
    }

    @Test
    fun logarithms() {
        assertEquals("3", MathExpressionEvaluator.evaluate("log(1000)", decimalPrecision = 4))
        assertEquals("2.3026", MathExpressionEvaluator.evaluate("ln(10)", decimalPrecision = 4))
    }

    @Test
    fun scientificResultsRemainParseable() {
        // A result like "1.5E12" must be reusable as input (it is set as the
        // expression after '='), so scientific notation is normalized
        assertEquals("0", MathExpressionEvaluator.evaluate("1.5E12-1500000000000"))
    }

    private fun assertEvaluationError(
        message: String? = null,
        block: () -> String
    ) {
        try {
            block()
            fail("Expected EvaluationException but evaluation succeeded")
        } catch (e: EvaluationException) {
            if (message != null) assertEquals(message, e.message)
        } catch (e: Throwable) {
            fail("Expected EvaluationException but got ${e.javaClass.simpleName}: ${e.message}")
        }
    }
}
