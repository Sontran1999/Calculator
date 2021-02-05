package com.example.calculator.viewmodel

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.calculator.common.*
import com.example.calculator.common.Utils.format
import com.example.calculator.model.Count
import com.example.calculator.model.Formula
import com.example.calculator.model.Result
import kotlinx.android.synthetic.main.activity_main.*
import net.objecthunter.exp4j.Expression
import net.objecthunter.exp4j.ExpressionBuilder

class ViewModelCalculator : ViewModel() {
    var count: MutableLiveData<String> = MutableLiveData()
    var result: MutableLiveData<String> = MutableLiveData()
    var formula: MutableLiveData<String> = MutableLiveData()
    var clear: MutableLiveData<String> = MutableLiveData()
    var editorCount: MutableLiveData<String> = MutableLiveData()
    var editorResult: MutableLiveData<String> = MutableLiveData()

    private var inputDisplayed: String = "0"
    private var outputDisplayed: String = ""
    private val operations = listOf("+", "-", "×", "÷", "^", "%", "√")
    private val operationCount = listOf("+", "-", "×", "÷", "^", "√")
    private var lastOperation = ""
    private val operationsRegex = "[-+×÷^%√]".toPattern()
    private val numbersRegex = "[^0-9,.]".toRegex()
    private val numberPercent = "[^0-9,.%]".toRegex()
    private lateinit var expression: Expression
    var numberClicked = false
    private var charBracketCloseCount = 0
    private var charBracketOpenCount = 0

    fun zeroClicked() {
        if (inputDisplayed != "0" || inputDisplayed.contains(".")) {
            add("0")
        }
    }

    private fun addThousandsDelimiter() {
        val valuesToCheck = numbersRegex.split(inputDisplayed).filter {
            it.trim().isNotEmpty()
        }
        valuesToCheck.forEach {
            var newString = Utils.addGroupingSeparators(it)

            if (it.contains(".")) {
                newString = newString.substringBefore(".") + ".${it.substringAfter(".")}"
            }

            inputDisplayed = inputDisplayed.replace(it, newString)
        }
    }

    fun add(number: String) {
        nameClear("C")
        if (inputDisplayed == "0") {
            inputDisplayed = ""
        }
        if (outputDisplayed != "") {
            inputDisplayed = ""
            showNewResult("")
            outputDisplayed = ""
        }

        inputDisplayed += number
        addThousandsDelimiter()
        showNewCount(inputDisplayed)
        numberClicked = true
    }

    fun delete() {
        var newValue = inputDisplayed.dropLast(1)
        if (newValue == "") {
            newValue = "0"
        }

        newValue = newValue.trimEnd(',')
        inputDisplayed = newValue
        showNewCount(inputDisplayed)
    }

    fun clear(input: String, output: String) {
        inputDisplayed = input
        outputDisplayed = output
    }

    private fun getSign(lastOperation: String) = when (lastOperation) {
        MINUS -> "-"
        MULTIPLY -> "×"
        DIVIDE -> "÷"
        PERCENT -> "%"
        POWER -> "^"
        SQRT -> "√"
        else -> "+"
    }

    fun operation(operation: String) {
        if (inputDisplayed == Double.NaN.toString()) {
            inputDisplayed = "0"
        }

        if (inputDisplayed == "") {
            inputDisplayed = "0"
        }

        if (operation == SQRT && inputDisplayed == "0") {
            if (inputDisplayed == "0") {
                inputDisplayed = "√"
            }
        }

        val lastChar = inputDisplayed.last().toString()
        if (lastChar == ".") {
            inputDisplayed = inputDisplayed.dropLast(1)
        } else if (operationCount.contains(lastChar)) {
            inputDisplayed = inputDisplayed.dropLast(1)
            inputDisplayed += getSign(operation)
        } else if (outputDisplayed != "") {
            inputDisplayed = outputDisplayed + getSign(operation)
            outputDisplayed = ""
            showNewResult("")
        } else {
            inputDisplayed += getSign(operation)
        }

        lastOperation = operation
        showNewCount(inputDisplayed)
    }

    fun dotClick() {
        val valueToCheck = inputDisplayed.trimStart('-').replace(",", "")
        val value = valueToCheck.substring(valueToCheck.indexOfAny(operations) + 1)
        if (!value.contains(".")) {
            when {
                value == "0" && !valueToCheck.contains(operationsRegex.toRegex()) -> inputDisplayed =
                    "0."
                value == "" -> inputDisplayed += "0."
                else -> inputDisplayed += "."
            }
        }
        showNewCount(inputDisplayed)
    }

    private fun handlePercent() {
        val valuesToCheck = numberPercent.split(inputDisplayed).filter {
            it.trim().isNotEmpty()
        }
        valuesToCheck.forEach {
            if (it.contains("%")) {
                var number = it.dropLast(1)
                var newString = number.toDouble() / 100
                inputDisplayed = inputDisplayed.replace(it, newString.toString())
            }
        }
    }

    private fun checkBracketsNumber() {
        charBracketCloseCount = 0
        charBracketOpenCount = 0
        var charInExceed = 0
        for (i in inputDisplayed.indices) {
            if (inputDisplayed[i] == '(') {
                charBracketOpenCount++
            } else if (inputDisplayed[i] == ')') {
                charBracketCloseCount++
            }
        }
        if (charBracketOpenCount > charBracketCloseCount) {
            charInExceed = charBracketOpenCount - charBracketCloseCount
            for (j in 0 until charInExceed) {
                inputDisplayed += ")"
                showNewCount(inputDisplayed)
            }
        }
    }


    fun result() {
        checkBracketsNumber()
        editorCount(inputDisplayed)
        if (!numberClicked) {
        } else if (charBracketOpenCount < charBracketCloseCount) {
            showNewResult("error")
        } else {
            handlePercent()
            expression = ExpressionBuilder(
                inputDisplayed.replace("√", "sqrt")
                    .replace("×", "*")
                    .replace("÷", "/")
                    .replace(",", "")
            ).build()
            try {
                var value = expression.evaluate().format()
                showNewResult("= $value")
                editorResult("= $value")
                outputDisplayed = value
            } catch (e: ArithmeticException) {
                showNewResult("Can't divide by 0")
            }
        }
    }

    fun showNewResult(value: String) {
        result.postValue(value)
    }

    fun showNewFormula(value: String) {
        formula.postValue(value)
    }

    fun showNewCount(value: String) {
        count.postValue(value)
    }

    fun nameClear(value: String) {
        clear.postValue(value)
    }

    fun editorCount(value: String) {
        editorCount.postValue(value)
    }

    fun editorResult(value: String) {
        editorResult.postValue(value)
    }
}