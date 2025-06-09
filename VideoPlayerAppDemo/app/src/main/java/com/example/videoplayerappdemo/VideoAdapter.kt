package com.example.videoplayerappdemo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import java.util.concurrent.TimeUnit

class VideoAdapter(
    private val onVideoClick: (Video) -> Unit,
    private val onMoreOptionsClick: (Video, View) -> Unit
) : ListAdapter<Video, VideoAdapter.VideoViewHolder>(VideoDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_video, parent, false)
        return VideoViewHolder(view)
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        val video = getItem(position)
        holder.bind(video)
    }

    inner class VideoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val videoTitle: TextView = itemView.findViewById(R.id.videoTitle)
        private val videoDuration: TextView = itemView.findViewById(R.id.videoDuration)
        private val videoSize: TextView = itemView.findViewById(R.id.videoSize)
        private val videoThumbnail: ImageView = itemView.findViewById(R.id.videoThumbnail) // Placeholder
        private val moreOptions: ImageView = itemView.findViewById(R.id.moreOptions)

        fun bind(video: Video) {
            videoTitle.text = video.title
            videoDuration.text = formatDuration(video.duration)
            videoSize.text = formatSize(video.size)

            // For thumbnail, you would typically use a library like Glide or Coil
            // to load a thumbnail from video.uri. For simplicity, we use a placeholder here.
            // Glide.with(itemView.context).load(video.uri).into(videoThumbnail)

            itemView.setOnClickListener {
                onVideoClick(video)
            }

            moreOptions.setOnClickListener {
                onMoreOptionsClick(video, it)
            }
        }

        private fun formatDuration(durationMillis: Long): String {
            val minutes = TimeUnit.MILLISECONDS.toMinutes(durationMillis)
            val seconds = TimeUnit.MILLISECONDS.toSeconds(durationMillis) -
                    TimeUnit.MINUTES.toSeconds(minutes)
            return String.format("%02d:%02d", minutes, seconds)
        }

        private fun formatSize(bytes: Long): String {
            if (bytes < 1024) return "$bytes B"
            val kb = bytes / 1024.0
            if (kb < 1024) return String.format("%.2f KB", kb)
            val mb = kb / 1024.0
            if (mb < 1024) return String.format("%.2f MB", mb)
            val gb = mb / 1024.0
            return String.format("%.2f GB", gb)
        }
    }
}

class VideoDiffCallback : DiffUtil.ItemCallback<Video>() {
    override fun areItemsTheSame(oldItem: Video, newItem: Video): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Video, newItem: Video): Boolean {
        return oldItem == newItem // Data class equals checks all properties
    }
}