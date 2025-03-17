package com.example.contacts

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

class UpdateContact : AppCompatActivity() {
    lateinit var toolbar: Toolbar
    lateinit var upname: EditText
    lateinit var upnumber: EditText
    lateinit var upbtn: Button
    lateinit var databaseObject: Database

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.update_contact)

        toolbar = findViewById(R.id.update_toolbar)
        upname = findViewById(R.id.edit_name)
        upnumber = findViewById(R.id.edit_number)
        upbtn = findViewById(R.id.updateBTN)
        databaseObject = Database(applicationContext)

        toolbar.title = "UPDATE CONTACT"

        val updateIntent = intent
        val id = updateIntent?.getIntExtra("contact_id", -1) ?: -1

        if (id == -1) {
            Toast.makeText(applicationContext, "Invalid Contact ID", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        upname.setText(updateIntent.getStringExtra("contact_name"))
        upnumber.setText(updateIntent.getStringExtra("contact_number"))

        upbtn.setOnClickListener {
            val name = upname.text.toString().trim()
            val number = upnumber.text.toString().trim()

            if (name.isEmpty() || number.isEmpty()) {
                Toast.makeText(this, "Name and Number cannot be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val fields = TableFields().apply {
                contact_id = id
                contact_name = name
                contact_number = number
            }

            databaseObject.UpdateContacts(fields)
            Toast.makeText(applicationContext, "Contact Updated", Toast.LENGTH_SHORT).show()
            startActivity(Intent(applicationContext, ViewContacts::class.java))
            finish()
        }
    }

    override fun onBackPressed() {
        finish()
        super.onBackPressed()
    }
}
