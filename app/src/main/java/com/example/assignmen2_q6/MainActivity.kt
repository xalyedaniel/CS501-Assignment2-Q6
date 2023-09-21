package com.example.assignmen2_q6

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import com.example.assignmen2_q6.databinding.ActivityMainBinding
import kotlin.math.sqrt
import android.widget.Toast


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    // TODO: move cursor to right after button pressed
    // allowed actions
    private var canAddNumber = true
    private var canAddOperation = false
    private var canAddDecimal = true //accepting pattern "\.\d+"
    private var canAddSqrt = false //enter number first

    // expression formats
    private val validFormat = """((sqrt)*\d+(\.\d+)?)|[+\-*/]""".toRegex()
    private val validNumberFormat = """((sqrt)*\d+(\.\d+)?)""".toRegex()
    private val validLastNumberFormat = """((sqrt)*\d+(\.\d+)?)${'$'}""".toRegex()

    // calculator state
    private var stopExecution = false // user can edit the expression if there is an error
    private var displayingResult = false
    private var changeTriggeredByButton = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.workspace.addTextChangedListener(userUnpredictableActivityWatcher)
        binding.button1.setOnClickListener { numberAction(it) }
        binding.button2.setOnClickListener { numberAction(it) }
        binding.button3.setOnClickListener { numberAction(it) }
        binding.button4.setOnClickListener { operationAction(it) }
        binding.button5.setOnClickListener { operationAction(it) }
        binding.button6.setOnClickListener { numberAction(it) }
        binding.button7.setOnClickListener { numberAction(it) }
        binding.button8.setOnClickListener { numberAction(it) }
        binding.button9.setOnClickListener { operationAction(it) }
        binding.button10.setOnClickListener { operationAction(it) }
        binding.button11.setOnClickListener { numberAction(it) }
        binding.button12.setOnClickListener { numberAction(it) }
        binding.button13.setOnClickListener { numberAction(it) }
        binding.button14.setOnClickListener { operationAction(it) }
        binding.button15.setOnClickListener { numberAction(it) }
        binding.button16.setOnClickListener { numberAction(it) }
        binding.button17.setOnClickListener { equalsAction(it) }
    }

    // https://www.geeksforgeeks.org/how-to-implement-textwatcher-in-android/
    private val userUnpredictableActivityWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: Editable) {
            if (!changeTriggeredByButton) {
                displayingResult = false
                // nothing fancy, just to update the allowed actions
                val lastChar = s.last()
                when{
                    lastChar.isOperator() -> {
                        canAddDecimal = true
                        canAddNumber = true
                        canAddOperation = true
                        canAddSqrt = false
                    }
                    lastChar == '.' -> {
                        canAddDecimal = false
                        canAddNumber = true
                        canAddOperation = false
                        canAddSqrt = false
                    }
                    lastChar.isDigit() -> {
                        // any malformed input will be rejected during calculation
                        // too many cases to be handled here whether canAddDecimal
                        // mostly relating to sqrt
                        // just checking the . count in the last term
                        val lastOperand = """[+\-*/]?([sqrt.\d])+${'$'}""".toRegex()
                        val matchResult = lastOperand.find(s.toString())
                        canAddDecimal = matchResult?.value?.contains('.') == false
                        canAddNumber = true
                        canAddOperation = true
                        canAddSqrt = true
                        Log.d("canAddDecimal",canAddDecimal.toString())
                    }
                }
            }
        }
    }

    private fun numberAction(view: View){
        if (view is Button){
            changeTriggeredByButton = true
            if (displayingResult) binding.workspace.text.clear()
            displayingResult = false
            // . pressed
            if (view.text == "."){
                if (canAddDecimal) {
                    binding.workspace.append(view.text)
                    canAddDecimal = false
                    canAddNumber = true
                    canAddOperation = false
                    canAddSqrt = false
                }
            }
            // number pressed
            else {
                if (canAddNumber) {
                    binding.workspace.append(view.text)
                    canAddDecimal = canAddDecimal
                    canAddNumber = true
                    canAddOperation = true
                    canAddSqrt = true
                }
            }
            changeTriggeredByButton = false
        }
    }

    // https://kotlinlang.org/docs/extensions.html#extension-functions
    private fun String.endsWithOperator(): Boolean {
        // this string can be empty after user keyboard edit
        if (this.isEmpty())
            return false
        return this.last().isOperator()
    }

    private fun operationAction(view: View) {
//        if(view is Button && canAddOperation) {
//            binding.workspace.append(view.text)
//            canAddOperation = false
//            canAddDecimal = true
//            canAddSqrt = true
//        }
//        else if(view is Button && canAddSqrt){
//            binding.workspace.append(view.text)
//            canAddOperation = false
//            canAddSqrt = false
//            canAddDecimal = true
//        }
        if(view is Button) {
            changeTriggeredByButton = true
            Log.d("operationAction","${view.text}")
            displayingResult = false
            if (view.text == "sqrt"){
                if (canAddSqrt){
                    // https://www.baeldung.com/kotlin/regular-expressions#replacing
                    // https://regexr.com/
                    val beforeReplace = binding.workspace.text.toString()
                    Log.d("sqrt",beforeReplace)
                    val addSqrtToLastNumber = validLastNumberFormat.replace(beforeReplace){
                        m -> "sqrt"+ m.value
                    }
                    Log.d("sqrt",addSqrtToLastNumber)

                    binding.workspace.setText(addSqrtToLastNumber)
                    canAddDecimal = false
                    canAddNumber = false
                    canAddOperation = true
                    canAddSqrt = true
                }
            }
            else if (canAddOperation){
                val workspaceText = binding.workspace.text.toString()
                Log.d("+-*/",workspaceText)
                if (workspaceText.endsWithOperator()){
                    binding.workspace.setText(workspaceText.dropLast(1))
                    Log.d("+-*/",binding.workspace.text.toString())
                }
                binding.workspace.append(view.text)
                // TODO: cursor jumping to the beginning
                canAddDecimal = true
                canAddNumber = true
                canAddOperation = true
                canAddSqrt = false
            }
            changeTriggeredByButton = false
        }
    }

    fun equalsAction(view: View) {
        if (view is Button) {
            changeTriggeredByButton = true
            stopExecution = false
            val result = calculateResults()
            if (result.isNotEmpty()) {
                binding.workspace.setText(result)
                if (!stopExecution) displayingResult = true
            } else {
                showToast("Error: Empty expression")
            }
            changeTriggeredByButton = true
        }
    }

    private fun calculateResults(): String
    {
//        val digitsOperators = digitsOperators()
//        if(digitsOperators.isEmpty()) return ""

        // result default to the expression it self
        var result = binding.workspace.text.toString()

        val expression = parseExpression()
        expression.checksItIsGoodExpression()

//        val sqrt = sqrtCalculate(digitsOperators)
//        if (sqrt.isEmpty()) return ""

        // initializing outside condition
        var sqrtResolved = mutableListOf<Any>()
        // if there is error in previous step, stop execution
        if (!stopExecution) {
            sqrtResolved = resolveSqrt(expression)
            if (sqrtResolved.isEmpty()) return ""
        }

        // same logic
        var timesDivision = mutableListOf<Any>()
        if (!stopExecution) {
            timesDivision = timesDivisionCalculate(sqrtResolved)
            if(timesDivision.isEmpty()) return ""
        }

        if (!stopExecution) {
            result = addSubtractCalculate(timesDivision)
        }

        // giving user second or more chance
        if (stopExecution)
            return binding.workspace.text.toString()
        return result
    }

    private fun resolveSqrt(expression: MutableList<Any>): MutableList<Any> {
        // returns a list that the elements are either char or Double
        Log.d("resolve sqrt", "error before $stopExecution")
        expression.forEach { Log.d("before sqrt",it.toString()+it::class.java.simpleName) }
        // https://kotlinlang.org/docs/collection-transformations.html#map
        val sqrtResolved: MutableList<Any> = expression.map { if (it is Char) it else parseSqrt(it.toString()) }.toMutableList()
        sqrtResolved.forEach { Log.d("resolved sqrt",it.toString()+it::class.java.simpleName) }
        return sqrtResolved
    }

    private fun addSubtractCalculate(passedList: MutableList<Any>): String
    {
        Log.d("add sub", "error before $stopExecution")
        var result = passedList[0] as Double

        for(i in passedList.indices)
        {
            if(passedList[i] is Char && i != passedList.lastIndex)
            {
                val operator = passedList[i]
                val nextDigit = passedList[i + 1] as Double
                if (operator == '+')
                    result += nextDigit
                if (operator == '-')
                    result -= nextDigit
            }
        }

        return result.toString()
    }

    private fun timesDivisionCalculate(passedList: MutableList<Any>): MutableList<Any>
    {
        Log.d("mul div", "error before $stopExecution")
        val mulDivResolved = mutableListOf<Any>()
        var lastNumber = 1.0
        var mulDivAccumulator = 1.0
        var doingMul = false
        var doingDiv = false
        for (element in passedList){
            if (element.toString().isOperator()){
                when(val operator = element.toString().first()){
                    '*' -> doingMul = true
                    '/' -> doingDiv = true
                    // operator is +-
                    else -> {
                        mulDivResolved.add(lastNumber)
                        mulDivResolved.add(operator)
                        doingMul = false
                        doingDiv = false
                    }
                }
            }
            // element is number
            else {
                val number = element.toString().toDouble()
                if (doingMul) lastNumber *= number
                else if (doingDiv) {
                    if (number == 0.0) showToast("Error: Division by zero")
                    else lastNumber /= number
                }
                else lastNumber = number
            }
        }
        mulDivResolved.add(lastNumber)
//        var list = passedList
//        while (list.contains('*') || list.contains('/')|| list.contains("sqrt"))
//        {
//            list = calcTimesDiv(list)
//        }
        mulDivResolved.forEach { Log.d("mul div",it.toString()+it::class.simpleName) }
        return mulDivResolved
    }

    //https://www.youtube.com/watch?v=2hSHgungOKI
    @Deprecated("use timesDivisionCalculate()")
    private fun calcTimesDiv(passedList: MutableList<Any>): MutableList<Any>
    {
        val newList = mutableListOf<Any>()
        var restartIndex = passedList.size

        for(i in passedList.indices)
        {
            if((passedList[i] == '*' || passedList[i] == '/' || passedList[i] =="sqrt") && i != passedList.lastIndex && i < restartIndex)
            {
                val operator = passedList[i]
                val prevDigit = passedList[i - 1] as Double
                val nextDigit = passedList[i + 1] as Double
                when(operator)
                {
                    '*' ->
                    {
                        newList.add(prevDigit * nextDigit)
                        restartIndex = i + 1
                    }
                    '/' ->
                    {
                        if(nextDigit.toInt() == 0){
                            showToast("Error: Division by zero")
                        }
                        else{
                            newList.add(prevDigit / nextDigit)
                            restartIndex = i + 1
                        }
                    }
                    "sqrt" ->
                    {
                        newList.add(sqrt(nextDigit))
                        restartIndex = i + 1
                    }
                    else ->
                    {
                        Log.d("not*/",operator.toString())
                        newList.add(prevDigit)
                        newList.add(operator)
                    }
                }
            }
            // additions before multiplication is omitted
            if(i > restartIndex)
                newList.add(passedList[i])
        }
        newList.forEach { Log.d("mul div",it.toString()+it::class.simpleName) }
        return newList
    }

    private fun parseSqrt(string: String): Double{
        if (string.toDoubleOrNull() != null){
            return string.toDouble()
        }
        else{
            return sqrt(parseSqrt(string.drop(4)))
        }
    }

    private fun Char.isOperator(): Boolean{
        return (this in "+-*/")
    }

    private fun String.isOperator(): Boolean{
        return (this.length==1 && this.first().isOperator())
    }

    //https://www.youtube.com/watch?v=2hSHgungOKI
    private fun digitsOperators(): MutableList<Any>
    {
        val list = mutableListOf<Any>()
        var currentDigit = ""
        var parsingSqrt = false
        for(character in binding.workspace.text)
        {
            if(character.isDigit() || character == '.')
                currentDigit += character
            else if (!character.isOperator()){
                currentDigit += character
                parsingSqrt = true
            }
            else if (character.isOperator()){
                if (parsingSqrt)
                    list.add(parseSqrt(currentDigit))
                else
                    list.add(currentDigit.toDouble())
                currentDigit = ""
                list.add(character)
            }
        }

        if(currentDigit != "")
            if (parsingSqrt)
                list.add(parseSqrt(currentDigit))
            else
                list.add(currentDigit.toDouble())
        list.forEach { item -> Log.d("expression element",item.toString()) }
        return list
    }


    private fun checkMultipleDot(string: String) {
        // https://www.baeldung.com/kotlin/string-character-count-occurrences#using-count
        if (string.count { it == '.' } > 1)
            showToast("Error: Multiple decimal")
    }

    private fun checkSqrtIsComplete(string: String) {
        if ("sqrt".any { string.contains(it) } and !string.contains("sqrt"))
            showToast("Error: Incomplete sqrt in $string")
    }

    private fun checkFormat(string: String){
        if (!validFormat.matches(string))
            showToast("Error: Invalid input $string")
    }

    private fun Any.checksItFitsInExpression(){
        checkMultipleDot(this.toString())
        checkSqrtIsComplete(this.toString())
        checkFormat(this.toString())
    }

    private fun MutableList<Any>.checkBeginsWithNumber(): MutableList<Any> {
        if (!validNumberFormat.matches(this[0].toString()))
            showToast("Error: Expression should begin with a number")
        return this
    }

    private fun MutableList<Any>.checkEndsWithNumber(): MutableList<Any> {
        if (!validNumberFormat.matches(this.last().toString()))
            showToast("Error: Expression should end with a number")
        return this
    }

    private fun MutableList<Any>.checksItIsGoodExpression(){
        this
            .checkBeginsWithNumber()
            .checkEndsWithNumber()
        this.forEach { it.checksItFitsInExpression() }
    }

    // parse expression into operator and operands
    private fun parseExpression(): MutableList<Any>{
        val expressionList = mutableListOf<Any>()
        val expressionString = binding.workspace.text.toString()
        var operand = ""
        for (char in expressionString){
            if (char.isOperator()){
                expressionList.add(operand)
                expressionList.add(char)
                operand=""
            }else{
                operand+=char
            }
        }
        // add last number to expression
        expressionList.add(operand)
        return expressionList
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        stopExecution = true
//        binding.workspace.setText("")
    }

}
