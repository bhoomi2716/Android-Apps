package com.dev.meditation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.meditation.databinding.DesignRecyclerviewBottomFragmentBinding
import com.dev.meditation.model.ModelBottomRecyclerViewFragment

class AdapterRecyclerViewBottomFragment(
    var context : FragmentActivity,
    private var recyclerItems: MutableList<ModelBottomRecyclerViewFragment>)
    : RecyclerView.Adapter<AdapterRecyclerViewBottomFragment.FragmentRecyclerBottomViewHolder>()
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FragmentRecyclerBottomViewHolder
    {
        val view = DesignRecyclerviewBottomFragmentBinding.inflate(LayoutInflater.from(context),
            parent,false)
        return FragmentRecyclerBottomViewHolder(view)
    }

    override fun getItemCount(): Int
    {
        return recyclerItems.size
    }

    override fun onBindViewHolder(holder: FragmentRecyclerBottomViewHolder, position: Int)
    {
        holder.binding.ivRecyclerViewBottom.setImageResource(recyclerItems[position].img)
        holder.binding.tvRecyclerViewBottom.text = recyclerItems[position].name
    }

    inner class FragmentRecyclerBottomViewHolder(val binding : DesignRecyclerviewBottomFragmentBinding)
        : RecyclerView.ViewHolder(binding.root)

}