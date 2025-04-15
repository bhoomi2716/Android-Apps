package com.example.contacts

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class AddContact : AppCompatActivity()
{
    lateinit var toolbar : Toolbar
    lateinit var nameedt : EditText
    lateinit var numberedt : EditText
    lateinit var addbtn : Button
    lateinit var databaseObject : Database

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_contact)

        toolbar = findViewById(R.id.add_toolbar)
        nameedt = findViewById(R.id.add_name)
        numberedt = findViewById(R.id.add_number)
        addbtn = findViewById(R.id.addBTN)
        databaseObject = Database(applicationContext)

        toolbar.setTitle("CONTACTS")

        addbtn.setOnClickListener {

            var name = nameedt.text.toString().trim()
            var number = numberedt.text.toString().trim()

            if (name.isEmpty()) {
                Toast.makeText(applicationContext, "Enter a Name", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if(number.length!=10)
            {
                Toast.makeText(applicationContext, "Enter Valid Number", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            var tablefield = TableFields()
            tablefield.contact_name = name
            tablefield.contact_number = number

            databaseObject.InsertContact(tablefield)
            Toast.makeText(applicationContext,"Contact Added",Toast.LENGTH_SHORT).show()
            startActivity(Intent(applicationContext, ViewContacts::class.java))
            finish()
        }
    }


    override fun onBackPressed() {
        startActivity(Intent(applicationContext,ViewContacts::class.java))
        finish()
        super.onBackPressed()
    }
}