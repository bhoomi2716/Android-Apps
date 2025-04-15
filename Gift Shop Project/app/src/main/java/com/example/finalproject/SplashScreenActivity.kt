package com.example.finalproject

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.example.finalproject.Activity.SignInActivity
import com.example.finalproject.databinding.ActivitySplashScreenBinding

class SplashScreenActivity : AppCompatActivity()
{
    //Initialize Variables
    private lateinit var binding: ActivitySplashScreenBinding

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        //Set Statusbar Colour
        if (Build.VERSION.SDK_INT >= 21)
        {
            val window = this.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.statusBarColor = this.resources.getColor(R.color.statusbarColour)
        }

        //Catch SplashScreen For 2 Second With Using Of Handler() Class
        Handler().postDelayed(Runnable {

            startActivity(Intent(applicationContext,SignInActivity::class.java))

        },2000)
    }

    //When User Click On Back, App Will Close
    override fun onBackPressed()
    {
        finishAffinity()
        super.onBackPressed()
    }
}