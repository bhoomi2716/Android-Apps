package com.example.videoplayerappdemo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class VideoFolderAdapter(
    private val onVideoClick: (Video) -> Unit,
    private val onMoreOptionsClick: (Video, View) -> Unit
) : ListAdapter<VideoFolder, VideoFolderAdapter.FolderViewHolder>(FolderDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FolderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_video_folder, parent, false)
        return FolderViewHolder(view)
    }

    override fun onBindViewHolder(holder: FolderViewHolder, position: Int) {
        val folder = getItem(position)
        holder.bind(folder)
    }

    inner class FolderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val folderName: TextView = itemView.findViewById(R.id.folderName)
        private val videosRecyclerView: RecyclerView = itemView.findViewById(R.id.videosRecyclerView)

        fun bind(folder: VideoFolder) {
            folderName.text = folder.name

            val videoAdapter = VideoAdapter(onVideoClick, onMoreOptionsClick)
            videosRecyclerView.layoutManager = LinearLayoutManager(itemView.context)
            videosRecyclerView.adapter = videoAdapter
            videoAdapter.submitList(folder.videos.sortedBy { it.title }) // Sort videos by title within each folder
        }
    }
}

class FolderDiffCallback : DiffUtil.ItemCallback<VideoFolder>() {
    override fun areItemsTheSame(oldItem: VideoFolder, newItem: VideoFolder): Boolean {
        return oldItem.path == newItem.path
    }

    override fun areContentsTheSame(oldItem: VideoFolder, newItem: VideoFolder): Boolean {
        // This is a shallow check, deep comparison for video list would be complex.
        // For simplicity, we just compare name and path. If video list changes, notifyDataSetChanged might be needed on parent.
        return oldItem.name == newItem.name && oldItem.path == newItem.path && oldItem.videos.size == newItem.videos.size
    }
}