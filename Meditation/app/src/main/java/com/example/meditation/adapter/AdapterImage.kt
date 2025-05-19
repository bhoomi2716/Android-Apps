package com.dev.meditation.adapter

import android.content.Intent
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import java.io.File

class AdapterImage(private val images: List<String>,
                   private val onImageClick: (String) -> Unit) : RecyclerView.Adapter<AdapterImage.ImageViewHolder>()
{
    inner class ImageViewHolder(val imageView: ImageView) : RecyclerView.ViewHolder(imageView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val img = ImageView(parent.context)
        img.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 300)
        img.scaleType = ImageView.ScaleType.CENTER_CROP
        return ImageViewHolder(img)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val imagePath = images[position]
        Glide.with(holder.imageView.context)
            .load(File(imagePath))
            .into(holder.imageView)

        holder.imageView.setOnClickListener {
            onImageClick(imagePath) // invoke callback
        }
    }

    override fun getItemCount() = images.size
}