package com.dev.meditation.adapter

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.meditation.R
import com.example.meditation.activity.PlayOptionSleepActivity
import com.example.meditation.databinding.DesignRecyclerviewBottomSleepFragmentBinding
import com.dev.meditation.model.ModelBottomRecyclerViewSleepFragment

class AdapterRecyclerViewBottomSleepFragment
    (var context : FragmentActivity, private var recyclerItems : MutableList<ModelBottomRecyclerViewSleepFragment>)
    : RecyclerView.Adapter<AdapterRecyclerViewBottomSleepFragment.SleepFragmentViewHolder>()
{
    inner  class SleepFragmentViewHolder(val binding : DesignRecyclerviewBottomSleepFragmentBinding)
        : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SleepFragmentViewHolder {
        val view = DesignRecyclerviewBottomSleepFragmentBinding.inflate(LayoutInflater.from(context),
            parent,false)

        return SleepFragmentViewHolder(view)
    }

    override fun getItemCount(): Int {
        return recyclerItems.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: SleepFragmentViewHolder, position: Int) {
        holder.binding.ivDesignSleepFragment.setImageResource(recyclerItems[position].img)
        holder.binding.tvTitleDesignSleepFragment.text = recyclerItems[position].name
        val time = getMinutes(recyclerItems[position].time) ?: 0
        holder.binding.tvMinSleepFragment.text = "$time ${context.resources.getString(R.string.min_sleep_music)}"

        holder.itemView.setOnClickListener {
            val intent = Intent(context,PlayOptionSleepActivity::class.java)
            intent.putExtra("Image",recyclerItems[position].toolbarImg)
            intent.putExtra("time",time)
            intent.putParcelableArrayListExtra("musicList", ArrayList(recyclerItems))
            intent.putExtra("position", position)
            context.startActivity(intent)
        }
    }

    private fun getMinutes(timeString: String): Int {
        return try {
            val parts = timeString.split(":")
            if (parts.size == 2) {
                val minutes = parts[0].toInt()
                val seconds = parts[1].toInt()
                if (seconds > 30) minutes + 1 else minutes
            } else {
                0
            }
        } catch (e: Exception) {
            0
        }
    }
}