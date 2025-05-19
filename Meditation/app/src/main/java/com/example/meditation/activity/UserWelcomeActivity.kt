package com.example.meditation.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat.setOnApplyWindowInsetsListener
import com.example.meditation.R
import com.example.meditation.databinding.ActivityUserWelcomeBinding
import com.dev.meditation.`object`.Utils

class UserWelcomeActivity : AppCompatActivity()
{
    private lateinit var binding: ActivityUserWelcomeBinding
    private lateinit var sharedPreferences: SharedPreferences

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        binding = ActivityUserWelcomeBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        init()
        addListeners()
    }

    @SuppressLint("SetTextI18n")
    private fun init(){
        binding.root.setOnApplyWindowInsetsListener { _, insets ->
            val statusBarHeight = insets.systemWindowInsetTop

            val extraMarginInPx = resources.getDimensionPixelSize(R.dimen.twenty_top_margin)

            val layoutParams = binding.lytLinearTopLogo.layoutParams as ViewGroup.MarginLayoutParams
            layoutParams.topMargin = statusBarHeight + extraMarginInPx
            binding.lytLinearTopLogo.layoutParams = layoutParams

            insets
        }

        Utils.setStatusBarColor(this,true)

        sharedPreferences = applicationContext.getSharedPreferences("Authentication", Context.MODE_PRIVATE)

        val username = sharedPreferences.getString("username", "User")
        binding.tvUsername.text = "$username,"

    }

    private fun addListeners(){
        binding.btnGetStartedUW.setOnClickListener {

            startActivity(Intent(applicationContext,ChooseTopicActivity::class.java))

        }
    }
    @Deprecated("This method has been deprecated in favor of using the\n      {@link OnBackPressedDispatcher} via {@link #getOnBackPressedDispatcher()}.\n      The OnBackPressedDispatcher controls how back button events are dispatched\n      to one or more {@link OnBackPressedCallback} objects.")
    override fun onBackPressed() {
        finishAffinity()
        super.onBackPressed()
    }
}