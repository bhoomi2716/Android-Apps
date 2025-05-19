package com.example.meditation.activity

import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.example.meditation.R
import com.example.meditation.databinding.ActivityGetStartedSleepBinding
import com.dev.meditation.`object`.Utils

class GetStartedSleepActivity : AppCompatActivity() {
    private lateinit var binding : ActivityGetStartedSleepBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGetStartedSleepBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        init()
        addListener()
    }

    private fun init(){
        binding.root.setOnApplyWindowInsetsListener { _, insets ->
            val statusBarHeight = insets.systemWindowInsetTop
            val extraMarginInPx = resources.getDimensionPixelSize(R.dimen.three_top_margin)
            val layoutParams = binding.ivMoonWlcSleep.layoutParams as ViewGroup.MarginLayoutParams
            layoutParams.topMargin = statusBarHeight + extraMarginInPx
            binding.ivMoonWlcSleep.layoutParams = layoutParams
            insets
        }

        Utils.setStatusBarColor(this,true)
    }

    private fun addListener(){
        binding.btnGetStartedWelcomeSleep.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            intent.putExtra("openFragment", "sleep")
            startActivity(intent)
            finish()
        }
    }

    @Deprecated("This method has been deprecated in favor of using the\n      {@link OnBackPressedDispatcher} via {@link #getOnBackPressedDispatcher()}.\n      The OnBackPressedDispatcher controls how back button events are dispatched\n      to one or more {@link OnBackPressedCallback} objects.")
    override fun onBackPressed() {
        val intent = Intent(this,HomeActivity::class.java)
        startActivity(intent)
        super.onBackPressed()
    }
}