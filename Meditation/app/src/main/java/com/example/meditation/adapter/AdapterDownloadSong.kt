package com.example.meditation.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.meditation.databinding.ItemDownloadListBinding
import com.example.meditation.model.ModelDownloadSong

class AdapterDownloadSong(var context: Context, private var downloadList : MutableList<ModelDownloadSong>)
    : RecyclerView.Adapter<AdapterDownloadSong.DownloadViewHolder>(){

    inner class DownloadViewHolder(val binding : ItemDownloadListBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DownloadViewHolder {
        val view = ItemDownloadListBinding.inflate(LayoutInflater.from(context),parent,false)
        return DownloadViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: DownloadViewHolder, position: Int) {
        holder.binding.tvMusicTitleDownloadList.text = downloadList[position].songTitle
        val time = getMinutes(downloadList[position].songDuration)
        holder.binding.tvMusicMinuteDownloadList.text = "$time MIN"
    }

    override fun getItemCount(): Int {
        return downloadList.size
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