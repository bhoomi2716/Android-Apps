package com.example.meditation.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.icu.util.Calendar
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.dev.meditation.adapter.AdapterViewPager
import com.dev.meditation.fragment.FemaleVoiceFragment
import com.dev.meditation.fragment.MaleVoiceFragment
import com.dev.meditation.`object`.Utils
import com.example.meditation.R
import com.example.meditation.databinding.ActivityCourseDetailsBinding

class CourseDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCourseDetailsBinding
    private var favouriteNumber: Int = 24234
    private var isLiked: Boolean = false

    @SuppressLint("ObsoleteSdkInt", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCourseDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Load saved like state and favourite number
        val sharedPreferences = getSharedPreferences("course_details_prefs", MODE_PRIVATE)
        isLiked = sharedPreferences.getBoolean("isLiked", false)
        favouriteNumber = sharedPreferences.getInt("favouriteNumber", 24234)

        init()
        addListener()
        updateLikeIcon(isLiked)
        setFavouriteNumber(favouriteNumber)
        setUpViewPager()
    }

    private fun init() {
        Utils.setStatusBarColor(this, true)

        binding.tvHappyDay.text = when (Calendar.getInstance()[Calendar.HOUR_OF_DAY]) {
            in 0..11 -> getString(R.string.happy_morning)
            in 12..15 -> getString(R.string.happy_afternoon)
            in 16..20 -> getString(R.string.happy_evening)
            in 21..23 -> getString(R.string.happy_night)
            else -> getString(R.string.happy_day)
        }
    }

    private fun addListener() {
        val sharedPreferences = getSharedPreferences("course_details_prefs", MODE_PRIVATE)

        binding.tabLytCourseDetails.setupWithViewPager(binding.viewpagerCourseDetails)

        binding.ivBackToolbarCourseDetails.setOnClickListener {
            startActivity(Intent(applicationContext, HomeActivity::class.java))
            finish()
        }

        binding.ivLikeToolbarCourseDetails.setOnClickListener {
            isLiked = !isLiked

            if (isLiked) {
                favouriteNumber++
                Toast.makeText(applicationContext, "Added To Like...!!", Toast.LENGTH_SHORT).show()
            } else {
                favouriteNumber--
                Toast.makeText(applicationContext, "Removed From Like...!!", Toast.LENGTH_SHORT).show()
            }

            // Save updated values
            with(sharedPreferences.edit()) {
                putBoolean("isLiked", isLiked)
                putInt("favouriteNumber", favouriteNumber)
                apply()
            }

            updateLikeIcon(isLiked)
            setFavouriteNumber(favouriteNumber)
        }

        binding.ivDownloadToolbarCourseDetails.setOnClickListener {
            Toast.makeText(applicationContext, "Downloaded...!!", Toast.LENGTH_SHORT).show()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setFavouriteNumber(favouriteNumber: Int) {
        binding.tvFavouriteNumber.text = "$favouriteNumber ${getString(R.string.favorites)}"
    }

    private fun updateLikeIcon(isLiked: Boolean) {
        if (isLiked) {
            binding.ivLikeToolbarCourseDetails.setImageResource(R.drawable.selected_like_toolbar)
        } else {
            binding.ivLikeToolbarCourseDetails.setImageResource(R.drawable.btn_like_toolbar)
        }
    }

    private fun setUpViewPager() {
        val adapter = AdapterViewPager(supportFragmentManager)
        adapter.setData(MaleVoiceFragment(), getString(R.string.male_voice))
        adapter.setData(FemaleVoiceFragment(), getString(R.string.female_voice))
        binding.viewpagerCourseDetails.adapter = adapter
    }

    @Deprecated("Use OnBackPressedDispatcher instead")
    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(applicationContext, HomeActivity::class.java))
        finish()
    }
}
