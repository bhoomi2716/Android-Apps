package com.example.finalproject.Admin.Activity

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import android.widget.GridView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.finalproject.Admin.Adapter.AdminDashboardAdapter
import com.example.finalproject.Admin.Model.AdminDashboardModel
import com.example.finalproject.R
import com.example.finalproject.databinding.ActivityAdminDashboardBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton

class AdminDashboardActivity : AppCompatActivity()
{
    lateinit var binding: ActivityAdminDashboardBinding
    lateinit var sharedPreferences: SharedPreferences
    lateinit var gridviewItems : MutableList<AdminDashboardModel>

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminDashboardBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        sharedPreferences = getSharedPreferences("FinalProject", Context.MODE_PRIVATE)
        binding.adminDashboardToolbar.setTitle("WELCOME "+sharedPreferences.getString("PhoneNumber",""))
        setSupportActionBar(binding.adminDashboardToolbar)

        //Set Statusbar Colour
        if (Build.VERSION.SDK_INT >= 21)
        {
            val window = this.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.statusBarColor = this.resources.getColor(R.color.statusbarColour)
        }

        gridviewItems = ArrayList()

        gridviewItems.add(AdminDashboardModel(R.drawable.icon_add_category,"Add Category"))
        gridviewItems.add(AdminDashboardModel(R.drawable.icon_add_product,"Add Product"))
        gridviewItems.add(AdminDashboardModel(R.drawable.icon_view_orders,"View Orders"))

        var adapter = AdminDashboardAdapter(applicationContext,gridviewItems)
        binding.gridviewAdminDashboard.adapter = adapter

    }
}