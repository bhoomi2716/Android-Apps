package com.example.finalproject.Adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.RecyclerView
import com.example.finalproject.Activity.CategoryItemActivity
import com.example.finalproject.Activity.WishListActivity
import com.example.finalproject.ApiConfig.ApiClient
import com.example.finalproject.ApiConfig.ApiInterface
import com.example.finalproject.Model.WishlistModel
import com.example.finalproject.R
import com.google.android.material.textview.MaterialTextView
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WishlistAdapter(var context: Context, var wishlistItems : MutableList<WishlistModel>) : RecyclerView.Adapter<WishlistViewHolder>()
{
    lateinit var apiInterface: ApiInterface
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WishlistViewHolder
    {
        var wishlistLayout = LayoutInflater.from(parent.context)
        var wishlistview = wishlistLayout.inflate(R.layout.design_wishlist_item,parent,false)
        return WishlistViewHolder(wishlistview)
    }

    override fun getItemCount(): Int
    {
        return wishlistItems.size
    }

    override fun onBindViewHolder(holder: WishlistViewHolder, @SuppressLint("RecyclerView") position: Int)
    {
        sharedPreferences = context.getSharedPreferences("FinalProject",Context.MODE_PRIVATE)
        apiInterface = ApiClient().Connection().create(ApiInterface::class.java)

        var idWishlist = wishlistItems[position].id
        var nameWishlist = wishlistItems[position].gift_name
        var priceWishlist = wishlistItems[position].gift_price
        var descWishlist = wishlistItems[position].gift_description
        var imgWishlist = wishlistItems[position].image

        holder.wishlistName.text = nameWishlist
        holder.wishlistPrice.text = priceWishlist
        holder.wishlistDesc.text = descWishlist
        Picasso.get()
            .load(imgWishlist)
            .into(holder.wishlistImg)

        holder.btnAddToCart.setOnClickListener {

            var mobNum = sharedPreferences.getString("PhoneNumber","")
            var call : Call<Void> = apiInterface.addItemCart(nameWishlist,descWishlist,priceWishlist,imgWishlist,mobNum)
            call.enqueue(object : Callback<Void>
            {
                override fun onResponse(call: Call<Void>, response: Response<Void>)
                {
                    Toast.makeText(context, "Item Added Into Cart", Toast.LENGTH_SHORT).show()

                    var deleteWishlistCall = apiInterface.deleteItemWishlist(idWishlist)
                    deleteWishlistCall.enqueue(object : Callback<Void>
                    {
                        override fun onResponse(call: Call<Void>, response: Response<Void>)
                        {
                            var i = Intent(context,CategoryItemActivity::class.java)
                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            context.startActivity(i)
                        }

                        override fun onFailure(call: Call<Void>, t: Throwable)
                        {
                            Toast.makeText(context, "No Internet Connection", Toast.LENGTH_SHORT).show()
                        }
                    })
                }
                override fun onFailure(call: Call<Void>, t: Throwable)
                {
                    Toast.makeText(context, "No Internet Connection", Toast.LENGTH_SHORT).show()
                }
            })
        }

        holder.btnRemoveWishlist.setOnClickListener {

            var call : Call<Void> = apiInterface.deleteItemWishlist(idWishlist)
            call.enqueue(object : Callback<Void>
            {
                override fun onResponse(call: Call<Void>, response: Response<Void>)
                {
                    wishlistItems.removeAt(position)

                    Toast.makeText(context, "Item Removed From Wishlist", Toast.LENGTH_SHORT).show()
                    var intent = Intent(context,WishListActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(intent)
                }
                override fun onFailure(call: Call<Void>, t: Throwable)
                {
                    Toast.makeText(context, "Failed To Remove Item From Wishlist", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}

class WishlistViewHolder(wishlistView : View) : RecyclerView.ViewHolder(wishlistView)
{
    var wishlistImg : ImageView = wishlistView.findViewById(R.id.showImgWishlist)
    var wishlistName : MaterialTextView = wishlistView.findViewById(R.id.txtShowNameWishlist)
    var wishlistPrice : MaterialTextView = wishlistView.findViewById(R.id.txtShowPriceWishlist)
    var wishlistDesc : MaterialTextView = wishlistView.findViewById(R.id.txtShowDescriptionWishlist)
    var btnAddToCart : AppCompatButton = wishlistView.findViewById(R.id.btnShowAddtocartWishlist)
    var btnRemoveWishlist : AppCompatButton = wishlistView.findViewById(R.id.btnShowRemoveWishlist)
}
