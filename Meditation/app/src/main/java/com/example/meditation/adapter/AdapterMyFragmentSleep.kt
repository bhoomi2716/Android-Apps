package com.dev.meditation.adapter

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.meditation.R
import com.example.meditation.activity.PlayMusicDarkActivity
import com.example.meditation.activity.PlayMusicLightActivity
import com.example.meditation.databinding.DesignLikeMusicBinding
import com.dev.meditation.model.ModelBottomRecyclerViewSleepFragment
import com.dev.meditation.model.ModelMusicPlayerViewPager
import com.dev.meditation.model.ModelMyFragment
import com.dev.meditation.`object`.GlobalMusicState
import com.dev.meditation.`object`.MusicPlayerManager
import com.dev.meditation.service.MusicPlayerService
import io.realm.Realm


class AdapterMyFragmentSleep(var context: FragmentActivity,
                             var recyclerItems: MutableList<ModelMyFragment>)
    : RecyclerView.Adapter<AdapterMyFragmentSleep.MySleepViewHolder>()
{
    private lateinit var realmDb : Realm
    private var currentPlayingPosition: Int = -1

    inner class MySleepViewHolder(val binding : DesignLikeMusicBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MySleepViewHolder {
        val view = DesignLikeMusicBinding.inflate(LayoutInflater.from(context),parent,false)
        return MySleepViewHolder(view)
    }

    @SuppressLint("SetTextI18n", "UseCompatLoadingForDrawables")
    override fun onBindViewHolder(holder: MySleepViewHolder, @SuppressLint("RecyclerView") position: Int) {
        holder.binding.tvMusicTitleMyFragment.text = recyclerItems[position].songTitle
        val time = getMinutes(recyclerItems[position].songDuration)
        holder.binding.tvMusicMinuteMyFragment.text = "$time MIN"
        holder.binding.ivPlayMusicMyFragment.setImageDrawable(context.resources.getDrawable(R.drawable.music_player_icon_sleep))
        holder.binding.ivCancelMyFragment.setImageDrawable(context.resources.getDrawable(R.drawable.btn_cancel_my_sleep))
        holder.binding.tvMusicTitleMyFragment.setTextColor(context.resources.getColor(R.color.recycler_txt_color))
        holder.binding.tvMusicMinuteMyFragment.setTextColor(context.resources.getColor(R.color.recycler_txt_color))

        val isCurrentPlaying = isCurrentPlayingItem(recyclerItems[position], position)

        // Update play/pause icon
        holder.binding.ivPlayMusicMyFragment.setImageResource(
            if (isCurrentPlaying && GlobalMusicState.isPlaying) R.drawable.clickable_music_player_icon
            else R.drawable.music_player_icon_sleep
        )

        realmDb = Realm.getInstance(Realm.getDefaultConfiguration()!!)

        holder.binding.ivPlayMusicMyFragment.setOnClickListener {
            if (isCurrentPlaying && GlobalMusicState.isPlaying) {
                // Pause current playback
                val pauseIntent = Intent(context, MusicPlayerService::class.java).apply {
                    action = MusicPlayerService.PLAY_PAUSE
                }
                context.startService(pauseIntent)
            } else {
                // Start new playback or resume
                playSong(position)
            }
        }

        holder.binding.ivCancelMyFragment.setOnClickListener {
            removeFromLikes(position)
        }

        holder.itemView.setOnClickListener {
            openPlayMusicActivity(position)
        }
    }

    override fun getItemCount(): Int {
        return recyclerItems.size
    }

    private fun isCurrentPlayingItem(item: ModelMyFragment, position: Int): Boolean {
        return GlobalMusicState.currentSongSleep?.let { currentSong ->
            currentSong.name == item.songTitle &&
                    currentSong.time == item.songDuration
        } ?: false
    }

    private fun playSong(position: Int) {
        val previousPosition = currentPlayingPosition
        currentPlayingPosition = position

        // Convert to ModelMusicPlayerViewPager for service
        val songList = recyclerItems.map {
            ModelBottomRecyclerViewSleepFragment(
                name = it.songTitle,
                time = it.songDuration,
                song = it.songLyrics
            )
        }

        // Start service with current song
        val playIntent = Intent(context, MusicPlayerService::class.java).apply {
            putParcelableArrayListExtra("songList", ArrayList(songList))
            putExtra("currentPosition", position)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(playIntent)
        } else {
            context.startService(playIntent)
        }

        // Update UI
        notifyItemChanged(position)
        if (previousPosition != -1 && previousPosition != position) {
            notifyItemChanged(previousPosition)
        }
    }

    private fun removeFromLikes(position: Int) {
        val title = recyclerItems[position].songTitle
        val duration = recyclerItems[position].songDuration
        val lyrics = recyclerItems[position].songLyrics
        val type = recyclerItems[position].songType

        Realm.getDefaultInstance().executeTransactionAsync({ realm ->
            val itemToDelete = realm.where(ModelMyFragment::class.java)
                .equalTo("songTitle", title)
                .equalTo("songDuration", duration)
                .equalTo("songLyrics", lyrics)
                .equalTo("songType", type)
                .findFirst()

            itemToDelete?.deleteFromRealm()
        }, {
            // If we're removing the currently playing song, stop playback
            if (currentPlayingPosition == position) {
                context.stopService(Intent(context, MusicPlayerService::class.java))
                currentPlayingPosition = -1
            }

            recyclerItems.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, recyclerItems.size)
            Toast.makeText(context, "Removed From Likes", Toast.LENGTH_SHORT).show()
        }, { error ->
            Log.e("RealmError", "Failed to delete item", error)
        })
    }

    private fun openPlayMusicActivity(position: Int) {
        val convertedList = recyclerItems.map {
            ModelBottomRecyclerViewSleepFragment(
                name = it.songTitle,
                time = it.songDuration,
                song = it.songLyrics
            )
        }

        val intent = Intent(context, PlayMusicDarkActivity::class.java).apply {
            putParcelableArrayListExtra("musicList", ArrayList(convertedList))
            putExtra("position", position)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
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

    fun resetPlayerState() {
        if (currentPlayingPosition != -1) {
            val prev = currentPlayingPosition
            currentPlayingPosition = -1
            notifyItemChanged(prev)
        }
    }
}