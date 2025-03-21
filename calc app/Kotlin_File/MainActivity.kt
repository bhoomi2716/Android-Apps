package com.example.prectice1

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity()
{
    lateinit var value1 : EditText
    lateinit var value2 : EditText
    lateinit var spinner : Spinner
    lateinit var btncalculate : Button
    lateinit var tvans : TextView
    lateinit var btnhistory : Button
    lateinit var dbhelper : Database
    var operarionArray = arrayOf("+","-","*","/","%")

    @SuppressLint("MissingInflatedId", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        value1 = findViewById(R.id.value1)
        value2 = findViewById(R.id.value2)
        spinner = findViewById(R.id.opSpinner)
        btncalculate = findViewById(R.id.btnCalculate)
        tvans = findViewById(R.id.tvAns)
        btnhistory = findViewById(R.id.btnHistory)
        dbhelper = Database(this)

        var spinadapter = ArrayAdapter(applicationContext,android.R.layout.simple_spinner_dropdown_item,operarionArray)
        spinner.adapter = spinadapter

        btncalculate.setOnClickListener {
            calculate()
        }

        btnhistory.setOnClickListener {
            startActivity(Intent(applicationContext,HistoryActivity::class.java))
        }


    }

    private fun calculate()
    {
        var num1 = value1.text.toString().toDoubleOrNull()
        var num2 = value1.text.toString().toDoubleOrNull()

        if(num1 == null || num2 == null)
        {
            Toast.makeText(applicationContext, "Please Enter Number !!!", Toast.LENGTH_SHORT).show()
            return
        }

        var selectop = spinner.selectedItem.toString()
        var result = when(selectop)
        {
            "+" -> num1 + num2
            "-" -> num1 - num2
            "*" -> num1 * num2
            "/" -> if(num2!=0.0) num1 / num2 else "Can Not Divide By 0"
            "%" -> num1 % num2

            else -> 0.0
        }

        tvans.text = result.toString()

        val calculation = "$num1 $selectop $num2 = $result"
        dbhelper.insertColumn(calculation)
    }
}