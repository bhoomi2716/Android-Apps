package com.example.contacts

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ViewContacts : AppCompatActivity()
{
    lateinit var toolbar: Toolbar
    lateinit var addContactsTXT: TextView
    lateinit var contactListview: RecyclerView
    lateinit var contactsList: MutableList<TableFields>
    lateinit var databaseObject: Database

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.view_contacts)

        toolbar = findViewById(R.id.view_toolbar)
        addContactsTXT = findViewById(R.id.addContact)
        contactListview = findViewById(R.id.ContactList)

        contactsList = ArrayList()
        toolbar.setTitle("CONTACTS")

        addContactsTXT.setOnClickListener {
            startActivity(Intent(applicationContext, AddContact::class.java))
        }

        databaseObject = Database(applicationContext)

        contactsList = databaseObject.ViewContacts()

        val contactAdapter = ContactAdapter(applicationContext, contactsList)
        contactListview.adapter = contactAdapter
        contactListview.layoutManager = LinearLayoutManager(this)
    }

    override fun onBackPressed() {
        finishAffinity()
        super.onBackPressed()
    }
}
