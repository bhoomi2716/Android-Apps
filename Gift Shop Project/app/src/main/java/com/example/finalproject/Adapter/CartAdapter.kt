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
import com.example.finalproject.Activity.PaymentActivity
import com.example.finalproject.ApiConfig.ApiClient
import com.example.finalproject.ApiConfig.ApiInterface
import com.example.finalproject.Model.CartModel
import com.example.finalproject.R
import com.google.android.material.textview.MaterialTextView
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CartAdapter(var context: Context, var cartItems : MutableList<CartModel>) : RecyclerView.Adapter<CartItemViewHolder>()
{
    lateinit var apiInterface: ApiInterface
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartItemViewHolder
    {
        var cartLayout = LayoutInflater.from(parent.context)
        var cartview = cartLayout.inflate(R.layout.design_cart_item,parent,false)
        return CartItemViewHolder(cartview)
    }

    override fun getItemCount(): Int
    {
        return cartItems.size
    }

    override fun onBindViewHolder(holder: CartItemViewHolder, @SuppressLint("RecyclerView") position: Int)
    {
        sharedPreferences = context.getSharedPreferences("FinalProject",Context.MODE_PRIVATE)
        apiInterface = ApiClient().Connection().create(ApiInterface::class.java)

        var idCart = cartItems[position].id
        var nameCart = cartItems[position].gift_name
        var priceCart = cartItems[position].gift_price
        var descCart = cartItems[position].gift_description
        var imgCart = cartItems[position].image

        holder.cartName.text = nameCart
        holder.cartPrice.text = priceCart
        holder.cartDesc.text = descCart
        Picasso.get()
            .load(imgCart)
            .into(holder.cartImg)

        holder.btnRemoveCartlist.setOnClickListener {

            var call : Call<Void> = apiInterface.deleteItemCart(idCart)
            call.enqueue(object : Callback<Void>
            {
                override fun onResponse(call: Call<Void>, response: Response<Void>)
                {
                    cartItems.removeAt(position)
                    Toast.makeText(context, "Item Removed From Cart", Toast.LENGTH_SHORT).show()
                    var i = Intent(context,CategoryItemActivity::class.java)
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context!!.startActivity(i)
                }

                override fun onFailure(call: Call<Void>, t: Throwable)
                {
                    Toast.makeText(context, "Failed To Remove Item From Cart", Toast.LENGTH_SHORT).show()
                }
            })
        }

        holder.btnPayment.setOnClickListener {

            var i = Intent(context,PaymentActivity::class.java)
            i.putExtra("id",cartItems[position].id)
            i.putExtra("name", cartItems[position].gift_name)
            i.putExtra("price", cartItems[position].gift_price)
            i.putExtra("desc", cartItems[position].gift_description)
            i.putExtra("image", cartItems[position].image)
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(i)

        }
    }

}

class CartItemViewHolder(cartView : View) : RecyclerView.ViewHolder(cartView)
{
    var cartImg : ImageView = cartView.findViewById(R.id.showImgCart)
    var cartName : MaterialTextView = cartView.findViewById(R.id.txtShowNameCart)
    var cartPrice : MaterialTextView = cartView.findViewById(R.id.txtShowPriceCart)
    var cartDesc : MaterialTextView = cartView.findViewById(R.id.txtShowDescriptionCart)
    var btnPayment : AppCompatButton = cartView.findViewById(R.id.btnShowPaymentCart)
    var btnRemoveCartlist : AppCompatButton = cartView.findViewById(R.id.btnShowRemoveCart)
}
