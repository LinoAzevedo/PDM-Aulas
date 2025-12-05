package ipca.example.calculator

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ipca.example.calculator.ui.theme.CalculatorTheme
import kotlin.collections.plusAssign
import kotlin.math.sqrt

@Composable
fun CalculatorView(
    modifier: Modifier = Modifier
){

    var displayText by remember { mutableStateOf("0") }

    val calculatorBrain by remember { mutableStateOf(CalculatorBrain()) }

    var userIsTypingNumber by remember { mutableStateOf(false) }

    val onDigitPressed : (String) -> Unit = { digit ->


        if(userIsTypingNumber) {
            if (digit == ".") {
                if (!displayText.contains('.')) {
                        displayText += digit
                }
            } else {
                if (displayText == "0") {
                    displayText = digit
                } else {
                    displayText += digit
                }
            }
        }else {
            if(digit == ".") {
                displayText = "0."
            }else{
                displayText = digit
            }
        }

        userIsTypingNumber = true;
    }

    val formatResult: (Double) -> String = { value ->
        when {
            value.isNaN() || value.isInfinite() -> "Error"
            (value % 1.0) == 0.0 -> value.toLong().toString()
            else -> value.toString()
        }
    }

    val onOperationPressed : (String) -> Unit = { op ->
        if(op == "AC"){
            displayText = "0"
            calculatorBrain.operand = 0.0
            calculatorBrain.operation = null
            userIsTypingNumber = false
        }
        else if (op == "C") {
            if (userIsTypingNumber && displayText.length > 1) {
                displayText = displayText.dropLast(1)
            } else {
                displayText = "0"
                userIsTypingNumber = false
            }
        }
        else{
            val parsedOperation = CalculatorBrain.Operation.parseOperation(op)
            when (parsedOperation) {
                CalculatorBrain.Operation.SQRT -> {
                    val currentValue = displayText.toDoubleOrNull()
                    if (currentValue == null || currentValue < 0.0) {
                        displayText = "Error"
                        calculatorBrain.operand = 0.0
                        calculatorBrain.operation = null
                    } else {
                        val result = sqrt(currentValue)
                        if (result.isNaN() || result.isInfinite()) {
                            displayText = "Error"
                            calculatorBrain.operand = 0.0
                            calculatorBrain.operation = null
                        } else {
                            displayText = formatResult(result)
                            if (calculatorBrain.operation == null) {
                                calculatorBrain.operand = result
                            }
                        }
                    }
                    userIsTypingNumber = false
                }
                CalculatorBrain.Operation.PERCENT -> {
                    val currentValue = displayText.toDoubleOrNull()
                    if (currentValue == null) {
                        displayText = "Error"
                        calculatorBrain.operation = null
                    } else {
                        val percentResult = when (val activeOperation = calculatorBrain.operation) {
                            CalculatorBrain.Operation.ADD -> {
                                val percentValue = calculatorBrain.operand * currentValue / 100.0
                                calculatorBrain.operand + percentValue
                            }
                            CalculatorBrain.Operation.SUBTRACT -> {
                                val percentValue = calculatorBrain.operand * currentValue / 100.0
                                calculatorBrain.operand - percentValue
                            }
                            CalculatorBrain.Operation.MULTIPLY -> {
                                calculatorBrain.operand * (currentValue / 100.0)
                            }
                            CalculatorBrain.Operation.DIVIDE -> {
                                val percentValue = currentValue / 100.0
                                if (percentValue == 0.0) null else calculatorBrain.operand / percentValue
                            }
                            null -> currentValue / 100.0
                            else -> currentValue / 100.0
                        }
                        if (percentResult == null || percentResult.isNaN() || percentResult.isInfinite()) {
                            displayText = "Error"
                            calculatorBrain.operand = 0.0
                            calculatorBrain.operation = null
                        } else {
                            displayText = formatResult(percentResult)
                            calculatorBrain.operand = percentResult
                            calculatorBrain.operation = null
                        }
                    }
                    userIsTypingNumber = false
                }
                else -> {
                    val newOperand = displayText.toDoubleOrNull()
                    if (newOperand == null) {
                        displayText = "Error"
                        calculatorBrain.operation = null
                    } else {
                        calculatorBrain.doOperation(
                            newOperand,
                            parsedOperation
                        )
                        val result = calculatorBrain.operand
                        if (result.isNaN() || result.isInfinite()) {
                            displayText = "Error"
                            calculatorBrain.operand = 0.0
                            calculatorBrain.operation = null
                        } else {
                            displayText = formatResult(result)
                        }
                    }
                    userIsTypingNumber = false
                }
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Text(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            text = displayText,
            textAlign = TextAlign.End,
            style = MaterialTheme.typography.displayLarge
        )
        Row {
            CalculatorButton(label="AC", onNumPressed = onOperationPressed, isOperation = true )
            CalculatorButton(label="C", onNumPressed = onOperationPressed, isOperation = true )
            CalculatorButton(label="√", onNumPressed = onOperationPressed, isOperation = true )
            CalculatorButton(label="%", onNumPressed = onOperationPressed, isOperation = true)
        }
        //Linha 2
        Row {
            CalculatorButton(label="7", onNumPressed = onDigitPressed )
            CalculatorButton(label="8", onNumPressed = onDigitPressed )
            CalculatorButton(label="9", onNumPressed = onDigitPressed )
            CalculatorButton(label="+",
                onNumPressed = onOperationPressed,
                isOperation = true)
        }

        Row {
            CalculatorButton(label="4", onNumPressed = onDigitPressed )
            CalculatorButton(label="5", onNumPressed = onDigitPressed )
            CalculatorButton(label="6", onNumPressed = onDigitPressed )
            CalculatorButton(label="-",
                onNumPressed = onOperationPressed,
                isOperation = true)
        }

        Row {
            CalculatorButton(label="1", onNumPressed = onDigitPressed )
            CalculatorButton(label="2", onNumPressed = onDigitPressed )
            CalculatorButton(label="3", onNumPressed = onDigitPressed )
            CalculatorButton(label="÷",
                onNumPressed = onOperationPressed,
                isOperation = true)
        }

        Row {
            CalculatorButton(label="0", onNumPressed = onDigitPressed )
            CalculatorButton(label=".", onNumPressed = onDigitPressed )
            CalculatorButton(label="=",
                onNumPressed = onOperationPressed,
                isOperation = true)
            CalculatorButton(label="×",
                onNumPressed = onOperationPressed,
                isOperation = true)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CalculatorViewPreview(){
    CalculatorTheme {
        CalculatorView()
    }
}
