package com.dev.meditation.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.meditation.databinding.DesignRecyclerviewTopFragmentBinding
import com.dev.meditation.interfaces.OnTopItemClickListener
import com.dev.meditation.model.ModelTopRecyclerViewFragment

class AdapterRecyclerViewTopFragment(
    var context: FragmentActivity,
    private var recyclerItems: MutableList<ModelTopRecyclerViewFragment>,
    private var listener : OnTopItemClickListener
) : RecyclerView.Adapter<AdapterRecyclerViewTopFragment.FragmentRecyclerTopViewHolder>()
{
    private var selectedPosition = RecyclerView.NO_POSITION

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FragmentRecyclerTopViewHolder
    {
        val view  = DesignRecyclerviewTopFragmentBinding.inflate(LayoutInflater.from(context),
            parent, false)
        return FragmentRecyclerTopViewHolder(view)
    }

    override fun getItemCount(): Int
    {
        return recyclerItems.size
    }

    override fun onBindViewHolder(holder: FragmentRecyclerTopViewHolder, @SuppressLint("RecyclerView") position: Int)
    {
        holder.binding.ivRecyclerviewTop.setImageResource(recyclerItems[position].img)
        holder.binding.tvRecyclerViewTop.text = recyclerItems[position].name

        if (position == selectedPosition) {
            holder.binding.ivRecyclerviewTop.setImageResource(recyclerItems[position].imgSelected)
            holder.binding.tvRecyclerViewTop.setTextColor(recyclerItems[position].textSelectedColor)
        } else {
            holder.binding.ivRecyclerviewTop.setImageResource(recyclerItems[position].img)
            holder.binding.tvRecyclerViewTop.setTextColor(recyclerItems[position].textColor)
        }

        holder.binding.root.setOnClickListener {
            val previousPosition = selectedPosition
            selectedPosition = position

            notifyItemChanged(previousPosition)
            notifyItemChanged(selectedPosition)

            listener.onTopItemClick(recyclerItems[position].fragment)
        }
    }
    fun setDefaultSelection(position: Int) {
        if (position in recyclerItems.indices) {
            selectedPosition = position
            notifyItemChanged(position)
            listener.onTopItemClick(recyclerItems[position].fragment)
        }
    }

    inner class FragmentRecyclerTopViewHolder(val binding: DesignRecyclerviewTopFragmentBinding)
        : RecyclerView.ViewHolder(binding.root)

}

