package com.rodri2482.calcquick.evaluator

import java.math.BigDecimal
import java.math.RoundingMode
import java.util.Locale

object MathExpressionEvaluator {

    class EvaluationException(message: String) : Exception(message)

    // Recursion guard: the recursive-descent parser recurses once per nested
    // parentheses/function level. Cap nesting so a user cannot trigger a
    // StackOverflowError (an Error, which is NOT caught by catch blocks below)
    // by mashing the "(" button.
    private const val MAX_PAREN_NESTING = 150

    fun evaluate(expression: String, isDegreeMode: Boolean = true, decimalPrecision: Int = 8): String {
        if (expression.isBlank()) return ""
        try {
            val sanitized = sanitize(expression)
            if (sanitized.isEmpty()) return ""
            guardNesting(sanitized)
            val result = Parser(sanitized, isDegreeMode).parse()
            if (result.isNaN()) throw EvaluationException("Undefined")
            if (result.isInfinite()) throw EvaluationException("Result too large")

            return formatResult(result, decimalPrecision)
        } catch (e: EvaluationException) {
            throw e
        } catch (e: Exception) {
            throw EvaluationException("Syntax Error")
        }
    }

    fun tryEvaluatePreview(expression: String, isDegreeMode: Boolean = true, decimalPrecision: Int = 8): String? {
        if (expression.isBlank()) return null
        return try {
            val sanitized = sanitize(expression)
            if (sanitized.isEmpty()) return null

            guardNesting(sanitized)

            // Auto-close open parentheses for preview calculation
            var balanced = sanitized
            val openParens = balanced.count { it == '(' }
            val closeParens = balanced.count { it == ')' }
            if (openParens > closeParens) {
                balanced += ")".repeat(openParens - closeParens)
            }

            val result = Parser(balanced, isDegreeMode).parse()
            if (result.isNaN() || result.isInfinite()) null else formatResult(result, decimalPrecision)
        } catch (e: Exception) {
            null
        }
    }

    private fun sanitize(input: String): String {
        val replaced = input
            .replace("×", "*")
            .replace("÷", "/")
            .replace("−", "-")
            .replace("π", "PI")
            .replace("√", "sqrt")
            .replace("²", "^2")
            .replace("³", "^3")
        return normalizeScientificNotation(replaced.trim())
    }

    /**
     * Rewrites scientific notation ("1.5E12", "2.4e-9") into parser-ready
     * "1.5*10^12" form. Needed because results formatted with an exponent
     * (formatResult) feed back into the parser when the user keeps
     * calculating, and the bare parser would misread 'E' as Euler's number.
     * A trailing 'E' without exponent digits stays untouched (Euler).
     */
    private fun normalizeScientificNotation(input: String): String {
        if (input.none { it == 'E' || it == 'e' }) return input
        val sb = StringBuilder(input.length + 16)
        var i = 0
        while (i < input.length) {
            val c = input[i]
            if ((c == 'E' || c == 'e') && i > 0 && (input[i - 1].isDigit() || input[i - 1] == '.')) {
                var j = i + 1
                if (j < input.length && (input[j] == '+' || input[j] == '-')) j++
                if (j < input.length && input[j].isDigit()) {
                    sb.append("*10^")
                    while (j < input.length && input[j].isDigit()) {
                        sb.append(input[j])
                        j++
                    }
                    i = j
                    continue
                }
            }
            sb.append(c)
            i++
        }
        return sb.toString()
    }

    private fun guardNesting(sanitized: String) {
        if (sanitized.count { it == '(' } > MAX_PAREN_NESTING) {
            throw EvaluationException("Expression too complex")
        }
    }

    private fun formatResult(value: Double, precision: Int): String {
        // Handle whole numbers cleanly (Locale.US so the decimal point is
        // always '.' internally; the UI formats for the user's locale)
        if (value % 1.0 == 0.0 && Math.abs(value) < 1e12) {
            return String.format(Locale.US, "%.0f", value)
        }

        // Handle scientific notation for extremely large/small values
        if (Math.abs(value) >= 1e12 || (Math.abs(value) < 1e-6 && value != 0.0)) {
            return String.format(
                Locale.US,
                "%.6e",
                value
            ).replace("e+", "E").replace("e-", "E-")
        }

        val bd = BigDecimal(value.toString()).setScale(precision, RoundingMode.HALF_UP).stripTrailingZeros()
        return bd.toPlainString()
    }

    private class Parser(private val str: String, private val isDegreeMode: Boolean) {
        private var pos = -1
        private var ch = -1

        private fun nextChar() {
            ch = if (++pos < str.length) str[pos].code else -1
        }

        private fun eat(charToEat: Int): Boolean {
            while (ch == ' '.code) nextChar()
            if (ch == charToEat) {
                nextChar()
                return true
            }
            return false
        }

        fun parse(): Double {
            nextChar()
            val x = parseExpression()
            if (pos < str.length) throw EvaluationException("Unexpected character: " + ch.toChar())
            return x
        }

        // Grammar:
        // expression = term | expression `+` term | expression `-` term
        // term = factor | term `*` factor | term `/` factor | term `%` factor (modulo)
        // factor = `+` factor | `-` factor | power
        // power = base `^` factor
        // base = primary | primary `!` | postfix `%` (only at end of term)
        // primary = number | constant | function `(` expression `)` | `(` expression `)`
        //
        // Percent semantics (conventional calculator behavior):
        // - "A +/- B%" means A +/- (B% of A)   e.g. 100-50% = 50
        // - "A * B%" or standalone "%" means divide by 100
        // - "%" followed by a number is modulo  e.g. 10%3 = 1

        private fun parseExpression(): Double {
            var x = parseTerm()
            while (true) {
                when {
                    eat('+'.code) -> x += parseTerm(x)
                    eat('-'.code) -> x -= parseTerm(x)
                    else -> return x
                }
            }
        }

        /**
         * [percentBase] carries the left-hand result when this term is the
         * right operand of + or - (conventional percent-of-operand behavior).
         */
        private fun parseTerm(percentBase: Double? = null): Double {
            val firstIsPercent = BooleanArray(1)
            var x = parseFactor(firstIsPercent)
            if (firstIsPercent[0] && percentBase != null) {
                // A +/- B% == A +/- |B|% of A (the operand's own sign is
                // absorbed, e.g. 10-50% == 10 - 5)
                x = percentBase * Math.abs(x)
            }
            while (true) {
                when {
                    eat('*'.code) -> x *= parseFactor()
                    eat('/'.code) -> {
                        val divisor = parseFactor()
                        if (divisor == 0.0) throw EvaluationException("Cannot divide by zero")
                        x /= divisor
                    }
                    eat('%'.code) -> {
                        val right = parseFactor()
                        if (right == 0.0) throw EvaluationException("Cannot divide by zero")
                        x %= right
                    }
                    else -> {
                        // Implicit multiplication e.g., 2(3) or 2PI or 2sqrt(4)
                        if (ch == '('.code || ch in 'a'.code..'z'.code || ch in 'A'.code..'Z'.code) {
                            x *= parseFactor()
                        } else {
                            return x
                        }
                    }
                }
            }
        }

        private fun parseFactor(percentHolder: BooleanArray? = null): Double {
            if (eat('+'.code)) return parseFactor(percentHolder)
            if (eat('-'.code)) return -parseFactor(percentHolder)

            var x = parseBase(percentHolder)

            // Handle power operator ^
            if (eat('^'.code)) {
                val exponent = parseFactor()
                x = Math.pow(x, exponent)
            }

            return x
        }

        private fun parseBase(percentHolder: BooleanArray? = null): Double {
            var x = parsePrimary()

            // Postfix factorial !
            while (eat('!'.code)) {
                if (x < 0 || x % 1.0 != 0.0 || x > 170) throw EvaluationException("Invalid Factorial")
                x = factorial(x.toLong()).toDouble()
            }

            // Postfix percentage % — only when it ENDS the term. When a
            // number/parenthesis follows, '%' is the modulo operator instead
            // (handled by parseTerm), so 10%3 works.
            if (eatPercentAsPostfix()) {
                x /= 100.0
                if (percentHolder != null) percentHolder[0] = true
            }

            return x
        }

        /**
         * Consumes '%' when it means "divide by 100" (i.e. nothing meaning
         * "value" follows); returns false when '%' starts a modulo operation.
         * Consuming is required so parseTerm's modulo branch doesn't see the
         * same '%' again.
         */
        private fun eatPercentAsPostfix(): Boolean {
            while (ch == ' '.code) nextChar()
            if (ch != '%'.code) return false
            var peek = pos + 1
            while (peek < str.length && str[peek] == ' ') peek++
            if (peek >= str.length) {
                nextChar()
                return true
            }
            val next = str[peek].code
            val isPostfix = !(next in '0'.code..'9'.code ||
                    next in 'a'.code..'z'.code ||
                    next in 'A'.code..'Z'.code ||
                    next == '('.code ||
                    next == '.'.code)
            if (isPostfix) nextChar()
            return isPostfix
        }

        private fun parsePrimary(): Double {
            val startPos = pos
            if (eat('('.code)) {
                val x = parseExpression()
                if (!eat(')'.code)) throw EvaluationException("Missing ')'")
                return x
            }

            if (ch in '0'.code..'9'.code || ch == '.'.code) {
                while (ch in '0'.code..'9'.code || ch == '.'.code) nextChar()
                val numStr = str.substring(startPos, pos)
                return numStr.toDoubleOrNull() ?: throw EvaluationException("Invalid number")
            }

            if (ch in 'a'.code..'z'.code || ch in 'A'.code..'Z'.code) {
                while (ch in 'a'.code..'z'.code || ch in 'A'.code..'Z'.code) nextChar()
                val func = str.substring(startPos, pos)

                return when (func) {
                    "PI" -> Math.PI
                    "E", "e" -> Math.E
                    "sin" -> parseFunc { arg -> if (isDegreeMode) Math.sin(Math.toRadians(arg)) else Math.sin(arg) }
                    "cos" -> parseFunc { arg -> if (isDegreeMode) Math.cos(Math.toRadians(arg)) else Math.cos(arg) }
                    "tan" -> parseFunc { arg ->
                        val res = if (isDegreeMode) Math.tan(Math.toRadians(arg)) else Math.tan(arg)
                        if (Math.abs(res) > 1e15) throw EvaluationException("Undefined")
                        res
                    }
                    "asin" -> parseFunc { arg ->
                        if (arg < -1.0 || arg > 1.0) throw EvaluationException("Domain Error")
                        val rad = Math.asin(arg)
                        if (isDegreeMode) Math.toDegrees(rad) else rad
                    }
                    "acos" -> parseFunc { arg ->
                        if (arg < -1.0 || arg > 1.0) throw EvaluationException("Domain Error")
                        val rad = Math.acos(arg)
                        if (isDegreeMode) Math.toDegrees(rad) else rad
                    }
                    "atan" -> parseFunc { arg ->
                        val rad = Math.atan(arg)
                        if (isDegreeMode) Math.toDegrees(rad) else rad
                    }
                    "sqrt" -> parseFunc { arg ->
                        if (arg < 0) throw EvaluationException("Domain Error")
                        Math.sqrt(arg)
                    }
                    "log" -> parseFunc { arg ->
                        if (arg <= 0) throw EvaluationException("Domain Error")
                        Math.log10(arg)
                    }
                    "ln" -> parseFunc { arg ->
                        if (arg <= 0) throw EvaluationException("Domain Error")
                        Math.log(arg)
                    }
                    "abs" -> parseFunc { arg -> Math.abs(arg) }
                    else -> throw EvaluationException("Unknown function: $func")
                }
            }

            throw EvaluationException("Unexpected expression element")
        }

        private inline fun parseFunc(func: (Double) -> Double): Double {
            if (!eat('('.code)) throw EvaluationException("Expected '(' after function")
            val arg = parseExpression()
            if (!eat(')'.code)) throw EvaluationException("Expected ')' after function argument")
            return func(arg)
        }

        private fun factorial(n: Long): Long {
            if (n == 0L || n == 1L) return 1L
            var res = 1L
            for (i in 2..n) res *= i
            return res
        }
    }
}
