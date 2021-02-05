package com.example.calculator.common

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*

const val PLUS = "plus"
const val MINUS = "minus"
const val MULTIPLY = "multiply"
const val DIVIDE = "divide"
const val PERCENT = "percent"
const val POWER = "power"
const val SQRT = "root"
const val DECIMAL = "decimal"
var numberClicked = false

object Utils {
    fun doubleToString(d: Double): String {
        val symbols = DecimalFormatSymbols(Locale.US)
        symbols.decimalSeparator = '.'
        symbols.groupingSeparator = ','

        val formatter = DecimalFormat()
        formatter.maximumFractionDigits = 12
        formatter.decimalFormatSymbols = symbols
        formatter.isGroupingUsed = true
        return formatter.format(d)
    }

    fun Double.format(): String = doubleToString(this)

    fun stringToDouble(str: String) = str.replace(",", "").toDouble()

    fun addGroupingSeparators(str: String) = doubleToString(stringToDouble(str))

}
