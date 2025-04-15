package com.example.finalproject.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.RecyclerView
import com.example.finalproject.Activity.ShowItemInFullScreenActivity
import com.example.finalproject.Activity.WishListActivity
import com.example.finalproject.ApiConfig.ApiClient
import com.example.finalproject.ApiConfig.ApiInterface
import com.example.finalproject.Model.CategoryModel
import com.example.finalproject.R
import com.google.android.material.textview.MaterialTextView
import com.squareup.picasso.Picasso
import retrofit2.Callback
import retrofit2.Response

class CategoryAdapter(var context: Context, var categoryItems : MutableList<CategoryModel>) : RecyclerView.Adapter<CategoryViewHolder>()
{
    lateinit var apiInterface: ApiInterface

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder
    {
        var categoryLayout = LayoutInflater.from(context)
        var categoryview = categoryLayout.inflate(R.layout.design_category_item,parent,false)
        apiInterface = ApiClient().Connection().create(ApiInterface::class.java)
        return CategoryViewHolder(categoryview)
    }

    override fun getItemCount(): Int
    {
        return categoryItems.size
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int)
    {
        holder.categoryName.setText(categoryItems.get(position).name)
        holder.categoryPrice.setText(categoryItems.get(position).price)
        holder.categoryDescription.setText(categoryItems.get(position).description)
        Picasso.get().load(categoryItems.get(position).url).into(holder.categoryImg)
        holder.itemView.setOnClickListener {

            var passDataIntent = Intent(context,ShowItemInFullScreenActivity::class.java)
            passDataIntent.putExtra("image",categoryItems.get(position).url)
            passDataIntent.putExtra("name",categoryItems.get(position).name)
            passDataIntent.putExtra("price",categoryItems.get(position).price)
            passDataIntent.putExtra("desc",categoryItems.get(position).description)
            passDataIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(passDataIntent)
        }
    }

}

class CategoryViewHolder (categoryView : View) : RecyclerView.ViewHolder(categoryView)
{
    var categoryImg : ImageView = categoryView.findViewById(R.id.showImgCategoryItem)
    var categoryName : MaterialTextView = categoryView.findViewById(R.id.txtShowNameCategoryItem)
    var categoryPrice : MaterialTextView = categoryView.findViewById(R.id.txtShowPriceCategoryItem)
    var categoryDescription : MaterialTextView = categoryView.findViewById(R.id.txtShowDescriptionCategoryItem)
}
