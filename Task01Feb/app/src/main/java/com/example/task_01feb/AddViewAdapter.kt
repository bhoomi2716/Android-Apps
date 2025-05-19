package com.example.task_01feb

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView

class AddViewAdapter (var context: Context, var listitem : MutableList<AddViewModel>) : BaseAdapter()
{
    override fun getCount(): Int
    {
        return listitem.size
    }

    override fun getItem(position: Int): Any
    {
        return listitem.get(position)
    }

    override fun getItemId(position: Int): Long
    {
        return position.toLong()
    }

    @SuppressLint("MissingInflatedId", "ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View
    {
        var gridlayout = LayoutInflater.from(context)
        var gridview =gridlayout.inflate(R.layout.add_view_design_gridview,parent,false)

        var img : ImageView = gridview.findViewById(R.id.girdImg)
        var name : TextView = gridview.findViewById(R.id.gridname)

        img.setImageResource(listitem.get(position).img)
        name.setText(listitem.get(position).gridname)

        return gridview
    }

}