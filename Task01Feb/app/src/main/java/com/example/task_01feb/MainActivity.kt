package com.example.task_01feb

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.GridView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity()
{
    lateinit var items : GridView
    lateinit var itemlist : MutableList<AddViewModel>

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        items = findViewById(R.id.grid)
        itemlist = ArrayList()

        itemlist.add(AddViewModel(R.drawable.add,"ADD PRODUCT"))
        itemlist.add(AddViewModel(R.drawable.view,"VIEW PRODUCT"))

        var adapter = AddViewAdapter(applicationContext,itemlist)
        items.adapter = adapter

        items.setOnItemClickListener { parent, view, position, id ->


            if (position==0)
            {
                startActivity(Intent(applicationContext,AddActivity::class.java))
            }

            if (position==1)
            {
                startActivity(Intent(applicationContext,ViewActivity::class.java))
            }
        }
    }

    override fun onBackPressed() {
        finishAffinity()
        super.onBackPressed()
    }
}