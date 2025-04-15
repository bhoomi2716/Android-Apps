package com.example.finalproject.Admin.Adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.finalproject.Admin.Model.AdminDashboardModel
import com.example.finalproject.R

class AdminDashboardAdapter(var context: Context, var itemlist : MutableList<AdminDashboardModel>) : BaseAdapter()
{
    override fun getCount(): Int
    {
        return itemlist.size
    }

    override fun getItem(p0: Int): Any
    {
        return itemlist.get(p0)
    }

    override fun getItemId(p0: Int): Long
    {
        return p0.toLong()
    }

    @SuppressLint("MissingInflatedId", "ViewHolder")
    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View
    {
        var layout = LayoutInflater.from(context)
        var view = layout.inflate(R.layout.design_admin_dashboard,p2,false)

        var categoryImg : ImageView = view.findViewById(R.id.admindashboardShowImg)
        var categoryName : TextView = view.findViewById(R.id.admindashboardShowName)

        categoryImg.setImageResource(itemlist.get(p0).adminCategoryImage)
        categoryName.setText(itemlist.get(p0).adminCategoryName)

        return view
    }
}