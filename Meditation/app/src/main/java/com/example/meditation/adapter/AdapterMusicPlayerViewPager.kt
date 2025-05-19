package com.dev.meditation.adapter

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.dev.meditation.`object`.GlobalMusicState
import com.example.meditation.R
import com.dev.meditation.model.ModelMusicPlayerViewPager
import com.dev.meditation.`object`.MusicPlayerManager
import com.dev.meditation.`object`.MusicSessionFlags
import com.dev.meditation.service.MusicPlayerService
import com.example.meditation.activity.PlayMusicLightActivity

class AdapterMusicPlayerViewPager(
    var context: FragmentActivity,
    private var recyclerItems: MutableList<ModelMusicPlayerViewPager>
) : RecyclerView.Adapter<MusicPlayerViewHolder>() {

    private var currentPlayingPosition: Int = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusicPlayerViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.design_music_player_view_pager, parent, false)
        return MusicPlayerViewHolder(view)
    }

    override fun getItemCount(): Int = recyclerItems.size

    @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
    override fun onBindViewHolder(holder: MusicPlayerViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val item = recyclerItems[position]

        holder.title.text = item.songTitle
        val time = getMinutes(item.songTime) ?: 0
        holder.minute.text = "$time MIN"

        // Update play icon based on service state
        val isCurrentPlaying = item.songTitle == GlobalMusicState.currentSong?.songTitle &&
                GlobalMusicState.isPlaying

        holder.playIcon.setImageResource(
            if (isCurrentPlaying) R.drawable.clickable_music_player_icon
            else R.drawable.music_player_icon
        )

        holder.itemView.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (context.checkSelfPermission(Manifest.permission.READ_MEDIA_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                    sendMusicList(position)
                } else {
                    context.requestPermissions(arrayOf(Manifest.permission.READ_MEDIA_AUDIO), 1002)
                }
            } else {
                sendMusicList(position)
            }
        }

        holder.playIcon.setOnClickListener {
            if (GlobalMusicState.currentPlayingPosition == position && GlobalMusicState.isPlaying) {
                // Pause music without stopping service
                val pauseIntent = Intent(context, MusicPlayerService::class.java).apply {
                    action = MusicPlayerService.PLAY_PAUSE
                }
                context.startService(pauseIntent)
            } else {
                // Start or resume playback
                val playIntent = Intent(context, MusicPlayerService::class.java).apply {
                    putParcelableArrayListExtra("songList", ArrayList(recyclerItems))
                    putExtra("currentPosition", position)
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(playIntent)
                } else {
                    context.startService(playIntent)
                }

                // Update global state
                GlobalMusicState.currentPlayingPosition = position
                GlobalMusicState.isPlaying = true
                GlobalMusicState.currentViewPagerAdapter = this

                notifyItemChanged(position)
                if (currentPlayingPosition != -1 && currentPlayingPosition != position) {
                    notifyItemChanged(currentPlayingPosition)
                }
                currentPlayingPosition = position
            }
        }
    }

    private fun sendMusicList(position: Int){
        MusicSessionFlags.isNavigatingToMusicActivity = true

        // Don't stop service here - just pass the current state
        val intent = Intent(context, PlayMusicLightActivity::class.java).apply {
            putParcelableArrayListExtra("musicList", ArrayList(recyclerItems))
            putExtra("position", position)
            putExtra("isPlaying", GlobalMusicState.isPlaying)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }

    fun resetPlayerState() {
        if (currentPlayingPosition != -1) {
            val prev = currentPlayingPosition
            currentPlayingPosition = -1
            notifyItemChanged(prev)
        }
    }

    private fun getMinutes(timeString: String): Int {
        return try {
            val parts = timeString.split(":")
            if (parts.size == 2) {
                val minutes = parts[0].toInt()
                val seconds = parts[1].toInt()
                if (seconds > 30) minutes + 1 else minutes
            } else { 0 }
        } catch (e: Exception) { 0 }
    }
}

class MusicPlayerViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    var title: TextView = view.findViewById(R.id.tvMusicTitleViewPager)
    var minute: TextView = view.findViewById(R.id.tvMusicMinuteViewPager)
    var playIcon: ImageView = view.findViewById(R.id.ivPlayMusicViewPager)
}
