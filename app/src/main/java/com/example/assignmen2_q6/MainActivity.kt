package com.example.assignmen2_q6

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.example.assignmen2_q6.databinding.ActivityMainBinding
import kotlin.math.sqrt


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var canAddOperation = false
    private var canAddDecimal = true

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
            if (view.text == "."){
                if (canAddDecimal)
                    binding.workspace.append(view.text)
                canAddDecimal = false
            } else {
                binding.workspace.append(view.text)
            }
            canAddOperation = true
        }
    }

    private fun operationAction(view: View) {
        if(view is Button && canAddOperation) {
            binding.workspace.append(view.text)
            canAddOperation = false
            canAddDecimal = true
        }
    }

    fun equalsAction(view: View) {
        binding.workspace.text = calculateResults()
    }

    private fun calculateResults(): String
    {
        val digitsOperators = digitsOperators()
        if(digitsOperators.isEmpty()) return ""

        val sqrt = sqrtCalculate(digitsOperators)
        if(sqrt.isEmpty()) return ""

        val timesDivision = timesDivisionCalculate(sqrt)
        if(timesDivision.isEmpty()) return ""

        val result = addSubtractCalculate(timesDivision)
        return result.toString()
    }

    private fun sqrtCalculate(passedList: MutableList<Any>): MutableList<Any> {
        /*
        check previous value if it exists and return square root of previous value
         */
        var result = passedList

        while (result.contains("sqrt")) {
            val index = result.indexOf("sqrt")
            val next = result[index + 1].toString().toFloat()
            if (next >= 0) {
                result =
                    (result.slice(0..<index - 1) + mutableListOf(sqrt(next)) + result.slice(index + 1..<result.size)).toMutableList()
            } else {
                Toast.makeText(
                    getApplicationContext(),
                    "error: negative input to square root",
                    Toast.LENGTH_SHORT
                ).show()
            }
            //Toast.makeText(getApplicationContext(), result.toString(), Toast.LENGTH_SHORT).show()

        }
        return result
    }

    private fun addSubtractCalculate(passedList: MutableList<Any>): Float
    {
        var result = passedList[0] as Float

        for(i in passedList.indices)
        {
            if(passedList[i] is Char && i != passedList.lastIndex)
            {
                val operator = passedList[i]
                val nextDigit = passedList[i + 1] as Float
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
        while (list.contains('x') || list.contains('/'))
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
            if((passedList[i] is Char || passedList[i] =="sqrt") && i != passedList.lastIndex && i < restartIndex)
            {
                val operator = passedList[i]
                val prevDigit = passedList[i - 1] as Float
                val nextDigit = passedList[i + 1] as Float
                when(operator)
                {
                    '*' ->
                    {
                        newList.add(prevDigit * nextDigit)
                        restartIndex = i + 1
                    }
                    '/' ->
                    {
                        newList.add(prevDigit / nextDigit)
                        restartIndex = i + 1
                    }
                    "sqrt" ->
                    {
                        //not done yet
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

    private fun digitsOperators(): MutableList<Any>
    {
        val list = mutableListOf<Any>()
        var currentDigit = ""
        for(character in binding.workspace.text)
        {
            if(character.isDigit() || character == '.')
                currentDigit += character
            else
            {
                list.add(currentDigit.toFloat())
                currentDigit = ""
                list.add(character)
            }
        }

        if(currentDigit != "")
            list.add(currentDigit.toFloat())

        return list
    }

}
