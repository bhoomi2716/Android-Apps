package com.dev.meditation.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.meditation.R
import com.example.meditation.activity.HomeActivity
import com.dev.meditation.model.ModelChooseTopic

class AdapterChooseTopic(private var context: Context, private var recyclerItems : MutableList<ModelChooseTopic>)
    : RecyclerView.Adapter<ChooseTopicViewHolder>()
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChooseTopicViewHolder
    {
        val layout = LayoutInflater.from(context)
        val viewChooseTopic = layout.inflate(R.layout.item_rv_choose_topic,parent,false)
        return ChooseTopicViewHolder(viewChooseTopic)
    }

    override fun getItemCount(): Int
    {
        return recyclerItems.size
    }

    override fun onBindViewHolder(holder: ChooseTopicViewHolder, position: Int)
    {
        holder.img.setImageResource(recyclerItems[position].img)
        holder.name.text = recyclerItems[position].name
        holder.name.setTextColor(ContextCompat.getColor(holder.itemView.context,recyclerItems[position].txtColor))

        holder.itemView.setOnClickListener {
            Toast.makeText(context, recyclerItems[position].name, Toast.LENGTH_SHORT).show()
            val i = Intent(context,HomeActivity::class.java)
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(i)
        }
    }

}

class ChooseTopicViewHolder(viewChooseTopic: View) : RecyclerView.ViewHolder(viewChooseTopic)
{
    var img : ImageView = viewChooseTopic.findViewById(R.id.ivDesignChooseTopic)
    var name : TextView = viewChooseTopic.findViewById(R.id.tvDesignChooseTopic)
}
