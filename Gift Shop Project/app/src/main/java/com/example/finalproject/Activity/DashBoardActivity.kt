package com.example.finalproject.Activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.finalproject.Adapter.DashboardAdapter
import com.example.finalproject.ApiConfig.ApiClient
import com.example.finalproject.ApiConfig.ApiInterface
import com.example.finalproject.Model.DashboardModel
import com.example.finalproject.R
import com.example.finalproject.databinding.ActivityDashBoardBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DashBoardActivity : AppCompatActivity()
{
    //Declare Variables
    private lateinit var binding: ActivityDashBoardBinding
    lateinit var sharedPreferences: SharedPreferences
    lateinit var apiInterface: ApiInterface
    lateinit var dashboardRecyclerViewItem : MutableList<DashboardModel>

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        binding = ActivityDashBoardBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        apiInterface = ApiClient().Connection().create(ApiInterface::class.java)
        sharedPreferences = getSharedPreferences("FinalProject", Context.MODE_PRIVATE)
        binding.dashboardToolbar.setTitle("WELCOME "+sharedPreferences.getString("PhoneNumber",""))
        setSupportActionBar(binding.dashboardToolbar)

        //Set Statusbar Colour
        if (Build.VERSION.SDK_INT >= 21)
        {
            val window = this.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.statusBarColor = this.resources.getColor(R.color.statusbarColour)
        }

        var dashboardLayout : RecyclerView.LayoutManager = GridLayoutManager(this,2)
        binding.dashboardRecyclerView.layoutManager = dashboardLayout

        var call : Call<List<DashboardModel>> = apiInterface.dashboardViewData()
        call.enqueue(object : Callback<List<DashboardModel>>
        {
            override fun onResponse(call: Call<List<DashboardModel>>, response: Response<List<DashboardModel>>)
            {
                dashboardRecyclerViewItem = response.body() as MutableList<DashboardModel>

                var dashboardAdapter = DashboardAdapter(applicationContext,dashboardRecyclerViewItem)
                binding.dashboardRecyclerView.adapter = dashboardAdapter
            }

            override fun onFailure(call: Call<List<DashboardModel>>, t: Throwable)
            {
                Toast.makeText(applicationContext, "No Internet Connection!!", Toast.LENGTH_SHORT).show()
            }
        })
    }

    @SuppressLint("MissingSuperCall")
    override fun onBackPressed()
    {
        //super.onBackPressed()

        var alertDialog = AlertDialog.Builder(this)
        alertDialog.setTitle("Are Your sure Want To Exit ??")
        alertDialog.setPositiveButton("YES", { dialogInterface: DialogInterface, i: Int ->

            finishAffinity()

        })
        alertDialog.setNegativeButton("NO",{ dialogInterface: DialogInterface, i: Int ->
            dialogInterface.cancel()
        })
        alertDialog.show()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean
    {
        menuInflater.inflate(R.menu.dashboard_toolbar_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean
    {
        when(item.itemId)
        {
            R.id.logout_toolbar_dashboard->
            {
                sharedPreferences.edit().clear().commit()
                startActivity(Intent(applicationContext,SignInActivity::class.java))
                finish()
            }
            R.id.wishlist_toolbar_dashboard->
            {
                startActivity(Intent(applicationContext,WishListActivity::class.java))
                finish()
            }
            R.id.addtocart_toolbar_dashboard->
            {
                startActivity(Intent(applicationContext,CartActivity::class.java))
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}