package com.example.finalproject.Activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.finalproject.Adapter.WishlistAdapter
import com.example.finalproject.ApiConfig.ApiClient
import com.example.finalproject.ApiConfig.ApiInterface
import com.example.finalproject.Model.CategoryModel
import com.example.finalproject.Model.WishlistModel
import com.example.finalproject.R
import com.example.finalproject.databinding.ActivityShowItemInFullScreenBinding
import com.example.finalproject.databinding.ActivityWishListBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WishListActivity : AppCompatActivity()
{
    private lateinit var binding: ActivityWishListBinding
    lateinit var apiInterface : ApiInterface
    lateinit var sharedPreferences: SharedPreferences
    lateinit var wishlistRecyclerViewItem : MutableList<WishlistModel>
    lateinit var wishlistAdapter: WishlistAdapter


    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        binding = ActivityWishListBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        sharedPreferences = getSharedPreferences("FinalProject", Context.MODE_PRIVATE)
        binding.toolbarWishlist.setTitle("My Wishlist")
        setSupportActionBar(binding.toolbarWishlist)

        //Set Statusbar Colour
        if (Build.VERSION.SDK_INT >= 21)
        {
            val window = this.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.statusBarColor = this.resources.getColor(R.color.statusbarColour)
        }

        wishlistRecyclerViewItem = ArrayList()
        wishlistAdapter = WishlistAdapter(applicationContext,wishlistRecyclerViewItem)
        binding.wishlistRecyclerView.layoutManager = LinearLayoutManager(this)

        sharedPreferences = getSharedPreferences("FinalProject", MODE_PRIVATE)
        var mobNum = sharedPreferences.getString("PhoneNumber","PhoneNumber_Error")

        apiInterface = ApiClient().Connection().create(ApiInterface::class.java)
        var call : Call<List<WishlistModel>> = apiInterface.viewItemsWishlist(mobNum)
        call.enqueue(object : Callback<List<WishlistModel>> {
            override fun onResponse(call: Call<List<WishlistModel>>, response: Response<List<WishlistModel>>)
            {
                wishlistRecyclerViewItem = response.body() as MutableList<WishlistModel>
                wishlistAdapter = WishlistAdapter(applicationContext,wishlistRecyclerViewItem)
                binding.wishlistRecyclerView.adapter = wishlistAdapter
            }

            override fun onFailure(call: Call<List<WishlistModel>>, t: Throwable)
            {
                Toast.makeText(applicationContext, "No Internet Connection", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onBackPressed()
    {
        startActivity(Intent(applicationContext,CategoryItemActivity::class.java))
        finish()
        super.onBackPressed()
    }
}