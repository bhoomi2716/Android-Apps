package com.example.meditation.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.example.meditation.R
import com.example.meditation.databinding.ActivityWelcomeBinding
import com.dev.meditation.`object`.Utils
import com.google.firebase.FirebaseApp

class WelcomeActivity : AppCompatActivity()
{
    private lateinit var binding: ActivityWelcomeBinding
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        FirebaseApp.initializeApp(applicationContext)

        init()
        addListener()
    }

    private fun init()
    {
        binding.root.setOnApplyWindowInsetsListener { _, insets ->
            val statusBarHeight = insets.systemWindowInsetTop

            val extraMarginInPx = resources.getDimensionPixelSize(R.dimen.twenty_top_margin)

            val layoutParams = binding.lytLinearFrame.layoutParams as ViewGroup.MarginLayoutParams
            layoutParams.topMargin = statusBarHeight + extraMarginInPx
            binding.lytLinearFrame.layoutParams = layoutParams

            insets
        }

        Utils.setStatusBarColor(this,false)

        sharedPreferences = getSharedPreferences("Authentication", Context.MODE_PRIVATE)

        if(sharedPreferences.getBoolean("Authentication",false) && sharedPreferences.getString("username","")!!
                .isNotEmpty()) {
            val i = Intent(applicationContext,UserWelcomeActivity::class.java)
            startActivity(i)
            finish()
        }
    }

    private fun addListener() {
        binding.btnSignUp.setOnClickListener {
            startActivity(Intent(applicationContext,SignUpActivity::class.java))
        }

        binding.tvLogin.setOnClickListener {
            startActivity(Intent(applicationContext,LogInActivity::class.java))
        }
    }

    @Deprecated("This method has been deprecated in favor of using the\n      {@link OnBackPressedDispatcher} via {@link #getOnBackPressedDispatcher()}.\n      The OnBackPressedDispatcher controls how back button events are dispatched\n      to one or more {@link OnBackPressedCallback} objects.")
    override fun onBackPressed() {
        finishAffinity()
        super.onBackPressed()
    }

}