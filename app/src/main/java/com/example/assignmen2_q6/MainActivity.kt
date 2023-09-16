package com.example.assignmen2_q6

import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.assignmen2_q6.databinding.ActivityMainBinding
import java.lang.Math.sqrt
import kotlin.math.sqrt


class MainActivity : AppCompatActivity() {

    /*
    this calculator only allows keyboard input for numbers, not operators

    to avoid errors, once the user inputs sqrt then consecutive digits will not be recorded
    i.e. 6, sqrt, 7 only logs 6, sqrt
    this effect stops once an operator or equal sign is pressed

     */


    private var mainList = mutableListOf<Any>()

    private fun reset_list(){
        mainList = mutableListOf<Any>()
    }



    private lateinit var binding: ActivityMainBinding


    private var canAddOperation = false //used to avoid error of consecutive operations
    private var canAddDecimal = true //used to ensure only one decimal

    private var holdingResult = false //used to handle use case where user calculates result and immediately operates on it
    private var holdingSqrt = false //used to avoid error where someone can input sqrt(3)3 for example



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Log.i("debug","testing")

        binding.workspace.addTextChangedListener(decimalLookout)
        binding.workspace.filters = arrayOf(decimalFilter)
        binding.workspace.setText("0")

        Log.i("debug","testing")
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
        Log.i("debug","testing")
        println("i want to see this")
        println("what place other than Logcat can i see this")
        Log.i("complain","nothing different with print???")

    }

    //sourced from Java code found at https://stackoverflow.com/questions/3349121/how-do-i-use-inputfilter-to-limit-characters-in-an-edittext-in-android
    private var decimalFilter:InputFilter =
        InputFilter { input, start, end, dest, dstart, dend ->
            if (!canAddDecimal and input.contains('.')){
                ""
            } else {
                input
            }
        }



    //using text watcher to be 'smart' and avoid multiple decimals when inputting a number
    //from keyboard
    //source: https://www.geeksforgeeks.org/how-to-implement-textwatcher-in-android/

    private fun dropFirstChar(){
        binding.workspace.text.delete(0,1)
    }

    private val decimalLookout: TextWatcher = object : TextWatcher{
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: Editable) {
            if (!s.contains('.')){ //this covers when user backspaces and deletes a decimal
                canAddDecimal = true
            } else {
                canAddDecimal = false
            }

            if (s.isEmpty()){
                s.append("0")
            } else {
                if (s.first() == '0' && s.length == 2 && s.last() != '.'){
                    dropFirstChar()
                }
            }


        }
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
        //slight alteration, as I have found that in calculator apps
        //typing an operation then another operation (without an operand in between)
        //overrides the last operation with the latest operation
        if(view is Button) {
            //binding.workspace.append(view.text)
            //more convenient if above line is omitted due to pre-existing char filters
            //also, having tested on a calculator app, operator symbols need not appear
            if (!canAddOperation) { //this means that last element in mainList should be an operator
                mainList.removeLast()
                mainList.add(view.text)
            } else {
                if (view.text == "sqrt") {
                    canAddOperation = true
                    holdingSqrt = true
                    mainList.add(binding.workspace.text.toString())
                    mainList.add(view.text.toString())
                    binding.workspace.setText("")

                } else{
                    if (!holdingSqrt){
                        mainList.add(binding.workspace.text.toString())
                    }
                    mainList.add(view.text.toString())
                    // with the above two lines, we are assuming that we will not hit an edge case
                    // where we have to check if input is empty
                    binding.workspace.setText("")
                    canAddOperation = false
                    holdingSqrt = false
                }

            }
            canAddDecimal = true
        }
    }


    fun equalsAction(view: View) {
        Log.d("debug","equals")
        if (canAddOperation && !holdingSqrt){
            mainList.add(binding.workspace.text.toString())
        }
        holdingSqrt = false
        canAddOperation = true

        //Toast.makeText(getApplicationContext(),mainList.toString(), Toast.LENGTH_SHORT).show()

        Log.d("debug","calculateResults:"+calculateResults())
        binding.workspace.setText(calculateResults())

        reset_list()
    }




    private fun calculateResults(): String
    {
        //val digitsOperators = digitsOperators()
        val digitsOperators = mainList
        //Toast.makeText(getApplicationContext(),mainList.toString(), Toast.LENGTH_SHORT).show()


        if(digitsOperators.isEmpty()) return ""

        val sqrt = sqrtCalculate(digitsOperators)
        if (sqrt.isEmpty()) return ""

        val timesDivision = timesDivisionCalculate(sqrt)
        if(timesDivision.isEmpty()) return ""

        val result = addSubtractCalculate(timesDivision)
        // TODO: result is inaccurate
        return result.toString()
    }

    private fun sqrtCalculate(passedList: MutableList<Any>): MutableList<Any>{
        /*
        check previous value if it exists and return square root of previous value
         */
        var result = passedList

        while (result.contains("sqrt")){
            val index = result.indexOf("sqrt")
            val prev = result[index-1].toString().toFloat()
            if (prev >= 0){
                result = (result.slice(0..<index-1 ) + mutableListOf(sqrt(prev)) + result.slice(index+1..< result.size)).toMutableList()
            } else {
                Toast.makeText(getApplicationContext(), "error: negative input to square root", Toast.LENGTH_SHORT).show()
            }
            //Toast.makeText(getApplicationContext(), result.toString(), Toast.LENGTH_SHORT).show()

        }

        return result
    }

    private fun addSubtractCalculate(passedList: MutableList<Any>): Float
    {
        Log.d("debug","addSubtractCalculate, before cast")
        Log.d("debug","casting ${passedList[0]} as float")
        var result = passedList[0].toString().toFloat()
        Log.d("debug","addSubtractCalculate, result: $result")
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
        //Toast.makeText(getApplicationContext(), passedList.toString(), Toast.LENGTH_SHORT).show()
        while (list.contains('*') || list.contains('/')) {
            Toast.makeText(getApplicationContext(), "testing", Toast.LENGTH_SHORT).show()
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
                val prevDigit = passedList[i - 1] as Double

                when(operator)
                {
                    '*' ->
                    {

                        val nextDigit = passedList[i + 1] as Double
                        newList.add(prevDigit * nextDigit)
                        restartIndex = i + 1
                    }
                    '/' ->
                    {
                        val nextDigit = passedList[i + 1] as Double
                        //remember to add division by zero error here
                        newList.add(prevDigit / nextDigit)
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
        //Toast.makeText(getApplicationContext(), newList.toString(), Toast.LENGTH_SHORT).show()
        return newList
    }

    /*
    //not using this particular function anymore

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
    */


}
