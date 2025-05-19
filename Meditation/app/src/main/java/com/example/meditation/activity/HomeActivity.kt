package com.example.meditation.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.meditation.R
import com.example.meditation.databinding.ActivityHomeBinding
import com.dev.meditation.fragment.HomeFragment
import com.dev.meditation.fragment.MeditateFragment
import com.dev.meditation.fragment.MusicFragment
import com.dev.meditation.fragment.ProfileFragment
import com.dev.meditation.fragment.SleepFragment
import com.dev.meditation.interfaces.onBackPress
import com.dev.meditation.`object`.Utils

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var sharedPreferences: SharedPreferences

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
        addListeners()
    }

    private fun init(){
        Utils.setStatusBarColor(this,false)

        sharedPreferences = applicationContext.getSharedPreferences("Authentication", Context.MODE_PRIVATE)

        val username = sharedPreferences.getString("username", "User,")
        binding.tvProfileNav.text = "$username"
    }

    private fun addListeners(){
        val openFragment = intent.getStringExtra("openFragment")

        when (openFragment) {
            "sleep" -> selectTab(
                binding.cvSleepNav,
                SleepFragment(),
                binding.lytSleepNav,
                "sleep"
            )
            else -> selectTab(
                binding.cvHomeNav,
                HomeFragment(),
                binding.lytHomeNav,
                "home"
            )
        }

        binding.lytHomeNav.setOnClickListener {
            selectTab(binding.cvHomeNav, HomeFragment(), binding.lytHomeNav, "home")
        }

        binding.lytSleepNav.setOnClickListener {
            setNavBarBackground("sleep")
            startActivity(Intent(this, GetStartedSleepActivity::class.java))
        }

        binding.lytMeditateNav.setOnClickListener {
            selectTab(binding.cvMeditateNav, MeditateFragment(), binding.lytMeditateNav, "meditate")
        }

        binding.lytMusicNav.setOnClickListener {
            selectTab(binding.cvMusicNav, MusicFragment(), binding.lytMusicNav, "music")
        }

        binding.lytProfileNav.setOnClickListener {
            selectTab(binding.cvProfileNav, ProfileFragment(), binding.lytProfileNav, "profile")
        }
    }

    private fun selectTab(
        selectedCardView: CardView,
        selectedFragment: Fragment,
        selectedLayout: LinearLayout,
        tabKey: String
    ) {
        resetAllNavBgColors()
        selectedCardView.background = ContextCompat.getDrawable(this, R.drawable.change_bg_nav_home)
        setFragment(selectedFragment)
        setSelectedTab(selectedLayout)
        updateNavIcons(tabKey)
        updateNavTextColors(tabKey)
        setNavBarBackground(tabKey)
    }

    private fun resetAllNavBgColors() {
        val defaultColor = ContextCompat.getColor(this, android.R.color.transparent)
        binding.cvHomeNav.setBackgroundColor(defaultColor)
        binding.cvSleepNav.setBackgroundColor(defaultColor)
        binding.cvMeditateNav.setBackgroundColor(defaultColor)
        binding.cvMusicNav.setBackgroundColor(defaultColor)
        binding.cvProfileNav.setBackgroundColor(defaultColor)
    }

    private fun resetAllIconAndTextColour(){
        binding.cvHomeNav.background = ContextCompat.getDrawable(this, R.drawable.change_bg_nav_home)
        binding.ivHomeNav.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.home_selected))
        binding.tvHomeNav.setTextColor(resources.getColor(R.color.highlights_and_btn_primary))
        binding.tvSleepNav.setTextColor(resources.getColor(R.color.nav_txt_color))
        binding.tvMeditateNav.setTextColor(resources.getColor(R.color.nav_txt_color))
        binding.tvMusicNav.setTextColor(resources.getColor(R.color.nav_txt_color))
        binding.tvProfileNav.setTextColor(resources.getColor(R.color.nav_txt_color))
        binding.ivSleepNav.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.sleep_nav_home))
        binding.ivMeditateNav.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.meditate_nav_home))
        binding.ivMusicNav.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.music_nav_home))
        binding.ivProfileNav.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.profile_nav_home))
    }

    private fun updateNavIcons(selected: String) {
        binding.ivHomeNav.setImageDrawable(
            ContextCompat.getDrawable(this, if (selected == "home") R.drawable.home_selected else R.drawable.home_nav_home)
        )
        binding.ivSleepNav.setImageDrawable(
            ContextCompat.getDrawable(this, if (selected == "sleep") R.drawable.sleep_selected else R.drawable.sleep_nav_home)
        )
        binding.ivMeditateNav.setImageDrawable(
            ContextCompat.getDrawable(this, if (selected == "meditate") R.drawable.meditate_selected else R.drawable.meditate_nav_home)
        )
        binding.ivMusicNav.setImageDrawable(
            ContextCompat.getDrawable(this, if (selected == "music") R.drawable.music_selected else R.drawable.music_nav_home)
        )
        binding.ivProfileNav.setImageDrawable(
            ContextCompat.getDrawable(this, if (selected == "profile") R.drawable.profile_selected else R.drawable.profile_nav_home)
        )
    }

    private fun updateNavTextColors(selected: String) {
        val selectedColorLightScreen = ContextCompat.getColor(this, R.color.highlights_and_btn_primary)
        val selectedColorDarkScreen = ContextCompat.getColor(this, R.color.recycler_txt_color)
        val defaultColor = ContextCompat.getColor(this, R.color.nav_txt_color)

        binding.tvHomeNav.setTextColor(if (selected == "home") selectedColorLightScreen else defaultColor)
        binding.tvSleepNav.setTextColor(if (selected == "sleep") selectedColorDarkScreen  else defaultColor)
        binding.tvMeditateNav.setTextColor(if (selected == "meditate") selectedColorLightScreen else defaultColor)
        binding.tvMusicNav.setTextColor(if (selected == "music") selectedColorDarkScreen else defaultColor)
        binding.tvProfileNav.setTextColor(if (selected == "profile") selectedColorDarkScreen else defaultColor)
    }

    private fun setFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.frameLytHomeActivity, fragment)
            .commit()
    }

    private fun setSelectedTab(selectedLayout: LinearLayout) {
        val allLayouts = listOf(
            binding.lytHomeNav,
            binding.lytSleepNav,
            binding.lytMeditateNav,
            binding.lytMusicNav,
            binding.lytProfileNav
        )
        for (layout in allLayouts) {
            layout.isSelected = layout == selectedLayout
        }
    }

    override fun onBackPressed() {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.frameLytHomeActivity)

        resetAllNavBgColors()
        resetAllIconAndTextColour()

        if (currentFragment is onBackPress) {
            if (currentFragment.onBackPressed()) {
                return
            }
        }
        super.onBackPressed()
    }

    private fun setNavBarBackground(tabKey: String) {
        val colorRes = when (tabKey) {
            "sleep" -> R.color.main_sleep_bg
            "music" -> R.color.main_sleep_bg
            "profile" -> R.color.main_sleep_bg
            else -> android.R.color.white
        }
        binding.navHome.setBackgroundColor(ContextCompat.getColor(this, colorRes))
    }
}