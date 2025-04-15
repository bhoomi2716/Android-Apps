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
import androidx.recyclerview.widget.RecyclerView
import com.example.finalproject.Adapter.CategoryAdapter
import com.example.finalproject.ApiConfig.ApiClient
import com.example.finalproject.ApiConfig.ApiInterface
import com.example.finalproject.Model.CategoryModel
import com.example.finalproject.R
import com.example.finalproject.databinding.ActivityCategoryItemBinding
import com.example.finalproject.databinding.ActivityDashBoardBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CategoryItemActivity : AppCompatActivity()
{
    private lateinit var binding: ActivityCategoryItemBinding
    lateinit var apiInterface : ApiInterface
    lateinit var sharedPreferences: SharedPreferences
    lateinit var categityRecyclerViewItme : MutableList<CategoryModel>

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        binding = ActivityCategoryItemBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        sharedPreferences = getSharedPreferences("FinalProject", Context.MODE_PRIVATE)
        binding.categoryToolbar.setTitle("WELCOME "+sharedPreferences.getString("PhoneNumber","PhoneNumber_Error"))
        setSupportActionBar(binding.categoryToolbar)

        //Set Statusbar Colour
        if (Build.VERSION.SDK_INT >= 21)
        {
            val window = this.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.statusBarColor = this.resources.getColor(R.color.statusbarColour)
        }

        apiInterface = ApiClient().Connection().create(ApiInterface::class.java)

        var layoutmanager : RecyclerView.LayoutManager = LinearLayoutManager(this)
        binding.categoryRecyclerView.layoutManager=layoutmanager

        var categoryIntent = intent
        var categoryId = categoryIntent.getIntExtra("itemID",101)
        //var categoryName = categoryIntent.getStringExtra("itemNAME")

        var call : Call<List<CategoryModel>> = apiInterface.categoryItemViewData(categoryId)

        call.enqueue(object : Callback<List<CategoryModel>>
        {
            override fun onResponse(call: Call<List<CategoryModel>>, response: Response<List<CategoryModel>>)
            {
                categityRecyclerViewItme = response.body() as MutableList<CategoryModel>
                var adapter = CategoryAdapter(applicationContext,categityRecyclerViewItme)
                binding.categoryRecyclerView.adapter=adapter
            }

            override fun onFailure(call: Call<List<CategoryModel>>, t: Throwable)
            {
                Toast.makeText(applicationContext, "No Internet Connection"+t.message, Toast.LENGTH_SHORT).show()
            }

        })
    }

    override fun onBackPressed()
    {
        startActivity(Intent(applicationContext,DashBoardActivity::class.java))
        finish()
        super.onBackPressed()
    }
}