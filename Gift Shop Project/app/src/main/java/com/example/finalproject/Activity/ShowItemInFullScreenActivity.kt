package com.example.finalproject.Activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.telecom.Call
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.finalproject.ApiConfig.ApiClient
import com.example.finalproject.ApiConfig.ApiInterface
import com.example.finalproject.R
import com.example.finalproject.databinding.ActivityShowItemInFullScreenBinding
import retrofit2.Callback
import retrofit2.Response

class ShowItemInFullScreenActivity : AppCompatActivity()
{
    private lateinit var binding: ActivityShowItemInFullScreenBinding
    lateinit var apiinterface: ApiInterface
    lateinit var sharedPreferences: SharedPreferences

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        binding = ActivityShowItemInFullScreenBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)



        var intent = intent
        var giftImg = intent.getStringExtra("image")
        var giftName = intent.getStringExtra("name")
        var giftPrice = intent.getStringExtra("price")
        var giftDescription = intent.getStringExtra("desc")

        apiinterface = ApiClient().Connection().create(ApiInterface::class.java)
        sharedPreferences = getSharedPreferences("FinalProject",Context.MODE_PRIVATE)
        var mobNum = sharedPreferences.getString("PhoneNumber", "")

        Glide.with(this)
            .load(giftImg)
            .placeholder(R.drawable.category_item_img)
            .into(binding.imageViewFullScreen)

        binding.txtItemNameFullScreen.setText(giftName)
        binding.txtItemPriceFullScreen.setText(giftPrice + " ₹")
        binding.txtItemDescFullScreen.setText(giftDescription)

        binding.btnCancleFullScreen.setOnClickListener {

            startActivity(Intent(applicationContext,CategoryItemActivity::class.java))
        }

        binding.btnWishlistFullScreen.setOnClickListener {

            var call : retrofit2.Call<Void> = apiinterface.addItemsWishlist(giftName,giftDescription,giftPrice,giftImg,mobNum)
            call.enqueue(object : Callback<Void>
            {
                override fun onResponse(call: retrofit2.Call<Void>, response: Response<Void>)
                {
                    Toast.makeText(applicationContext, "Item Added In Wishlist", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(applicationContext,WishListActivity::class.java))
                }

                override fun onFailure(call: retrofit2.Call<Void>, t: Throwable)
                {
                    Toast.makeText(applicationContext, "No Internet Connection", Toast.LENGTH_SHORT).show()
                }
            })
        }

        binding.btnAddtocartFullScreen.setOnClickListener {

            var call : retrofit2.Call<Void> = apiinterface.addItemCart(giftName,giftDescription,giftPrice,giftImg,mobNum)
            call.enqueue(object : Callback<Void>
            {
                override fun onResponse(call: retrofit2.Call<Void>, response: Response<Void>)
                {
                    Toast.makeText(applicationContext, "Item Added In Cart", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(applicationContext,CartActivity::class.java))
                }

                override fun onFailure(call: retrofit2.Call<Void>, t: Throwable)
                {
                    Toast.makeText(applicationContext, "No Internet Connection", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
    override fun onBackPressed()
    {
        startActivity(Intent(applicationContext,CategoryItemActivity::class.java))
        finish()
        super.onBackPressed()
    }
}