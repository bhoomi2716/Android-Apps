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
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.finalproject.Adapter.CartAdapter
import com.example.finalproject.Adapter.WishlistAdapter
import com.example.finalproject.ApiConfig.ApiClient
import com.example.finalproject.ApiConfig.ApiInterface
import com.example.finalproject.Model.CartModel
import com.example.finalproject.Model.WishlistModel
import com.example.finalproject.R
import com.example.finalproject.databinding.ActivityCartBinding
import com.example.finalproject.databinding.ActivityWishListBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CartActivity : AppCompatActivity()
{
    private lateinit var binding: ActivityCartBinding
    lateinit var apiInterface : ApiInterface
    lateinit var sharedPreferences: SharedPreferences
    lateinit var cartRecyclerViewItem : MutableList<CartModel>
    lateinit var cartAdapter: CartAdapter

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        binding = ActivityCartBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.toolbarCart.setTitle("My Cart")
        setSupportActionBar(binding.toolbarCart)

        //Set Statusbar Colour
        if (Build.VERSION.SDK_INT >= 21)
        {
            val window = this.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.statusBarColor = this.resources.getColor(R.color.statusbarColour)
        }

        cartRecyclerViewItem = ArrayList()
        cartAdapter = CartAdapter(applicationContext,cartRecyclerViewItem)
        binding.cartRecyclerView.layoutManager = LinearLayoutManager(this)

        sharedPreferences = getSharedPreferences("FinalProject", MODE_PRIVATE)
        var mobNum = sharedPreferences.getString("PhoneNumber","")

        apiInterface = ApiClient().Connection().create(ApiInterface::class.java)
        var call : Call<List<CartModel>> = apiInterface.viewItemCart(mobNum)
        call.enqueue(object : Callback<List<CartModel>>
        {
            override fun onResponse(call: Call<List<CartModel>>, response: Response<List<CartModel>>)
            {
                cartRecyclerViewItem = response.body() as MutableList<CartModel>
                cartAdapter = CartAdapter(applicationContext,cartRecyclerViewItem)
                binding.cartRecyclerView.adapter = cartAdapter
            }

            override fun onFailure(call: Call<List<CartModel>>, t: Throwable)
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