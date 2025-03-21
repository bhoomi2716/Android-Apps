package com.example.prectice1

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toolbar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class HistoryActivity : AppCompatActivity()
{
    lateinit var listview : ListView
    lateinit var toolbar: androidx.appcompat.widget.Toolbar
    lateinit var database: Database

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        listview = findViewById(R.id.listview)
        toolbar = findViewById(R.id.historyToolbar)
        database = Database(this)

        toolbar.setTitle("HISTORY")

        var result = database.viewColumn()
        var adapter = ArrayAdapter(applicationContext,android.R.layout.simple_list_item_1,result)

        listview.adapter = adapter
    }
}