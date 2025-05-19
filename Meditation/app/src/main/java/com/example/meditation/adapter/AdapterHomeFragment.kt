package com.dev.meditation.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.meditation.R
import com.dev.meditation.model.ModelChooseTopic
import com.dev.meditation.model.ModelHomeFragment
import com.google.android.material.textview.MaterialTextView

class AdapterHomeFragment(private var context: Context, private var recyclerItems : MutableList<ModelHomeFragment>)
    : RecyclerView.Adapter<HomeFragmentViewHolder>()
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeFragmentViewHolder
    {
        val layout = LayoutInflater.from(context)
        val viewHomeFragment = layout.inflate(R.layout.desgin_recyclerview_home_fragment,parent,false)
        return HomeFragmentViewHolder(viewHomeFragment)
    }

    override fun getItemCount(): Int
    {
       return recyclerItems.size
    }

    override fun onBindViewHolder(holder: HomeFragmentViewHolder, position: Int)
    {
        holder.img.setImageResource(recyclerItems[position].img)
        holder.name.text = recyclerItems[position].name
    }
}

class HomeFragmentViewHolder(viewHomeFragment :View) : RecyclerView.ViewHolder(viewHomeFragment)
{
    var img : AppCompatImageView = viewHomeFragment.findViewById(R.id.ivDesignHomeFragment)
    var name : MaterialTextView = viewHomeFragment.findViewById(R.id.tvDesignHomeFragment)
}
