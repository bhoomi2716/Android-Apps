package com.example.finalproject.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.finalproject.Activity.CategoryItemActivity
import com.example.finalproject.Model.DashboardModel
import com.example.finalproject.R
import com.google.android.material.textview.MaterialTextView
import com.squareup.picasso.Picasso

class DashboardAdapter(var context: Context, var dashboardItems : MutableList<DashboardModel>) : RecyclerView.Adapter<dashboardViewHolder>()
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): dashboardViewHolder
    {
        var dashboardlayout = LayoutInflater.from(context)
        var dashboardview = dashboardlayout.inflate(R.layout.design_dashboard_items,parent,false)
        return dashboardViewHolder(dashboardview)
    }

    override fun getItemCount(): Int
    {
        return dashboardItems.size
    }

    override fun onBindViewHolder(holder: dashboardViewHolder, position: Int)
    {
        Picasso.get().load(dashboardItems.get(position).url).into(holder.dashboardImg)
        holder.dashboardName.setText(dashboardItems.get(position).name)

        holder.itemView.setOnClickListener {

            var dashboardIntent = Intent(context,CategoryItemActivity::class.java)
            dashboardIntent.putExtra("itemID",dashboardItems.get(position).id)
            dashboardIntent.putExtra("itemNAME",dashboardItems.get(position).name)
            dashboardIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(dashboardIntent)

        }
    }

}

class dashboardViewHolder(dashboardView : View ) : RecyclerView.ViewHolder(dashboardView)
{
    var dashboardImg : ImageView = dashboardView.findViewById(R.id.showDashboardItemImg)
    var dashboardName : MaterialTextView = dashboardView.findViewById(R.id.showDashboardItemText)
}
