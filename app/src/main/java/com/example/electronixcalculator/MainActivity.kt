package com.example.electronixcalculator

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.mariuszgromada.math.mxparser.Expression
import org.mariuszgromada.math.mxparser.mXparser

class MainActivity : AppCompatActivity() {

    private lateinit var expressionTextView: TextView
    private lateinit var resultTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Set mXparser to use degrees for trigonometric functions
        mXparser.setDegreesMode()

        expressionTextView = findViewById(R.id.expressionTextView)
        resultTextView = findViewById(R.id.resultTextView)

        val buttons = listOf<Button>(
            findViewById(R.id.button0), findViewById(R.id.button1), findViewById(R.id.button2),
            findViewById(R.id.button3), findViewById(R.id.button4), findViewById(R.id.button5),
            findViewById(R.id.button6), findViewById(R.id.button7), findViewById(R.id.button8),
            findViewById(R.id.button9), findViewById(R.id.buttonDot), findViewById(R.id.buttonAdd),
            findViewById(R.id.buttonSubtract), findViewById(R.id.buttonMultiply), findViewById(R.id.buttonDivide),
            findViewById(R.id.buttonSin), findViewById(R.id.buttonCos), findViewById(R.id.buttonTan),
            findViewById(R.id.buttonLog), findViewById(R.id.buttonLn), findViewById(R.id.buttonLeftParen),
            findViewById(R.id.buttonRightParen), findViewById(R.id.buttonPower), findViewById(R.id.buttonPi),
            findViewById(R.id.buttonE)
        )

        for (button in buttons) {
            button.setOnClickListener { onDigitOrOperatorClick(it) }
        }

        findViewById<Button>(R.id.buttonAC).setOnClickListener { onClearClick() }
        findViewById<Button>(R.id.buttonBackspace).setOnClickListener { onBackspaceClick() }
        findViewById<Button>(R.id.buttonEquals).setOnClickListener { onEqualsClick() }
        findViewById<Button>(R.id.buttonFactorial).setOnClickListener { onFactorialClick() }
        findViewById<Button>(R.id.buttonInverse).setOnClickListener { onInverseClick() }
        findViewById<Button>(R.id.buttonPercent).setOnClickListener { onPercentClick() }

    }

    private fun onDigitOrOperatorClick(view: View) {
        val button = view as Button
        val buttonText = button.text.toString()

        expressionTextView.append(when (buttonText) {
            "sin" -> "sin("
            "cos" -> "cos("
            "tan" -> "tan("
            "log" -> "log10("
            "ln" -> "ln("
            "x^y" -> "^"
            "π" -> "pi"
            "e" -> "e"
            else -> buttonText
        })
    }

    private fun onPercentClick() {
        val expression = expressionTextView.text.toString()
        if (expression.isEmpty()) return

        val lastNumberRegex = "([0-9.]+)$".toRegex()
        val match = lastNumberRegex.find(expression)

        if (match != null) {
            val lastNumberStr = match.value
            val baseExprStr = expression.substring(0, match.range.first)

            if (baseExprStr.isEmpty()) {
                val number = lastNumberStr.toDouble() / 100
                expressionTextView.text = number.toString()
            } else {
                val operator = baseExprStr.last()
                val baseValueStr = baseExprStr.dropLast(1)

                val baseValueExpression = Expression(baseValueStr)
                val baseValue = baseValueExpression.calculate()

                if (!baseValue.isNaN()) {
                    val percentNumber = lastNumberStr.toDouble()
                    val percentageValue = when (operator) {
                        '+', '-' -> baseValue * percentNumber / 100
                        '×', '÷' -> percentNumber / 100
                        else -> percentNumber / 100
                    }
                    val newExpression = baseExprStr + percentageValue.toString()
                    expressionTextView.text = newExpression
                } else {
                    resultTextView.text = getString(R.string.error)
                }
            }
        }
    }

    private fun onClearClick() {
        expressionTextView.text = ""
        resultTextView.text = ""
    }

    private fun onBackspaceClick() {
        val currentExpression = expressionTextView.text.toString()
        if (currentExpression.isNotEmpty()) {
            expressionTextView.text = currentExpression.dropLast(1)
        }
    }

    private fun onEqualsClick() {
        val expressionString = expressionTextView.text.toString()
            .replace("×", "*")
            .replace("÷", "/")

        val expression = Expression(expressionString)
        val result = expression.calculate()

        if (!result.isNaN()) {
            resultTextView.text = result.toString()
        } else {
            resultTextView.text = getString(R.string.error)
        }
    }

    private fun onFactorialClick() {
        expressionTextView.append("!")
    }

    private fun onInverseClick() {
        expressionTextView.append("^(-1)")
    }
}
