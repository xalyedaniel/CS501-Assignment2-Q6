package com.example.assignmen2_q6

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import com.example.assignmen2_q6.databinding.ActivityMainBinding
import kotlin.math.sqrt
import android.widget.Toast


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var canAddNumber = true
    private var canAddOperation = false
    private var canAddDecimal = true //accepting pattern "\.\d+"
    private var canAddSqrt = false //enter number first

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
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

    private fun numberAction(view: View){
        if (view is Button){
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
        }
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
        Log.d("debug","$canAddSqrt, $view")

        if(view is Button) {
            if (view.text == "sqrt"){
                if (canAddSqrt){
                    // https://www.baeldung.com/kotlin/regular-expressions#replacing
                    // https://regexr.com/
                    val regex = """((sqrt)*\d+(\.\d+)?)${'$'}""".toRegex()
                    val beforeReplace = binding.workspace.text.toString()
                    Log.d("debug","$beforeReplace")
                    val addSqrtToLastNumber = regex.replace(beforeReplace){
                        m -> "sqrt"+ m.value
                    }
                    Log.d("debug","$addSqrtToLastNumber")

                    binding.workspace.text = addSqrtToLastNumber
                    canAddDecimal = false
                    canAddNumber = false
                    canAddOperation = true
                    canAddSqrt = true
                }
            }
            else if (canAddOperation){
                // TODO: override last operator
                binding.workspace.append(view.text)
                canAddDecimal = true
                canAddNumber = true
                canAddOperation = true
                canAddSqrt = false
            }
        }
    }

    fun equalsAction(view: View) {
        val result = calculateResults()
        if (result.isNotEmpty()) {
            binding.workspace.text = result
        } else {
            showToast("Error: Invalid expression")
        }
    }


    private fun calculateResults(): String
    {
        val digitsOperators = digitsOperators()
        if(digitsOperators.isEmpty()) return ""

        val timesDivision = timesDivisionCalculate(digitsOperators)
        if(timesDivision.isEmpty()) return ""

        val result = addSubtractCalculate(timesDivision)
        return result.toString()
    }

    private fun addSubtractCalculate(passedList: MutableList<Any>): Double
    {
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

        return result
    }

    private fun timesDivisionCalculate(passedList: MutableList<Any>): MutableList<Any>
    {
        var list = passedList
        while (list.contains('x') || list.contains('/')|| list.contains("sqrt"))
        {
            list = calcTimesDiv(list)
        }
        return list
    }

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
                            showToast("Error: Invalid input")
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
                        newList.add(prevDigit)
                        newList.add(operator)
                    }
                }
            }

            if(i > restartIndex)
                newList.add(passedList[i])
        }

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

    private fun isOperater(char: Char): Boolean{
        return char in "+-*/"
    }

    private fun digitsOperators(): MutableList<Any>
    {
        val list = mutableListOf<Any>()
        var currentDigit = ""
        var parsingSqrt = false
        for(character in binding.workspace.text)
        {
            if(character.isDigit() || character == '.')
                currentDigit += character
            else if (!isOperater(character)){
                currentDigit += character
                parsingSqrt = true
            }
            else if (isOperater(character)){
                if (parsingSqrt)
                    list.add(parseSqrt(currentDigit))
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

        return list
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        binding.workspace.text = ""
    }

}
