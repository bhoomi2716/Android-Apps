package com.example.meditation.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.meditation.R
import com.dev.meditation.adapter.AdapterRecyclerViewBottomSleepFragment
import com.example.meditation.databinding.ActivityPlayOptionSleepBinding
import com.dev.meditation.model.ModelBottomRecyclerViewSleepFragment
import com.dev.meditation.`object`.Utils


class PlayOptionSleepActivity : AppCompatActivity()
{
    private lateinit var binding : ActivityPlayOptionSleepBinding
    private lateinit var recyclerViewItems : MutableList<ModelBottomRecyclerViewSleepFragment>
    private var favouriteNumber: Int = 24234
    private var isLiked: Boolean = false
    private var musicList: ArrayList<ModelBottomRecyclerViewSleepFragment> = arrayListOf()
    private var currentPosition: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayOptionSleepBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val sharedPreferences = getSharedPreferences("course_details_prefs", MODE_PRIVATE)
        isLiked = sharedPreferences.getBoolean("isLiked", false)
        favouriteNumber = sharedPreferences.getInt("favouriteNumber", 24234)

        init()
        addListeners()
        updateLikeIcon(isLiked)
        setFavouriteNumber(favouriteNumber)
        setRecyclerItems()
    }

    private fun init(){
        Utils.setStatusBarColor(this,true)
        setFavouriteNumber(favouriteNumber)
        recyclerViewItems = mutableListOf()
    }

    @SuppressLint("SetTextI18n")
    private fun addListeners(){
        val sharedPreferences = getSharedPreferences("course_details_prefs", MODE_PRIVATE)

        binding.ivLikeToolbarPlayOption.setOnClickListener {
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

        binding.ivDownloadToolbarPlayOption.setOnClickListener {
            Toast.makeText(applicationContext, "Downloaded...!!", Toast.LENGTH_SHORT).show()
        }

        val imageResId = intent.getIntExtra("Image", -1)
        val time = intent.getIntExtra("time",0)
        musicList = intent.getSerializableExtra("musicList") as? ArrayList<ModelBottomRecyclerViewSleepFragment> ?: arrayListOf()
        currentPosition = intent.getIntExtra("position", 0)
        if (imageResId != -1) {
            binding.ivToolbarPlayOption.setImageResource(imageResId)
        }

        binding.ivToolbarPlayOption.setImageResource(imageResId)
        binding.tvSongTitlePlayOption.text = musicList[currentPosition].name
        binding.tvMinMusicPlayOption.text = time.toString() + " " + resources.getString(R.string.min_sleep_music)

        binding.ivBackToolbarPlayOption.setOnClickListener {
            val intent = Intent(this,HomeActivity::class.java)
            startActivity(intent)
            finish()
        }
        binding.btnPlayPlayOptions.setOnClickListener {
            val intent = Intent(applicationContext,PlayMusicDarkActivity::class.java)
            intent.putParcelableArrayListExtra("musicList", ArrayList(musicList))
            // pass full list
            intent.putExtra("position", currentPosition)
            startActivity(intent)
        }
    }

    private fun updateLikeIcon(isLiked: Boolean) {
        if (isLiked) {
            binding.ivLikeToolbarPlayOption.setImageResource(R.drawable.selected_like_toolbar)
        } else {
            binding.ivLikeToolbarPlayOption.setImageResource(R.drawable.btn_like_toolbar)
        }
    }

    private fun setRecyclerItems(){
        recyclerViewItems.add(ModelBottomRecyclerViewSleepFragment
            (R.drawable.night_island_toolbar_img,R.drawable.night_island_img,resources.getString(R.string.night_island),
            resources.getString(R.string.two_min_thirty_one_sec),R.raw.night_island_music))
        recyclerViewItems.add(ModelBottomRecyclerViewSleepFragment
            (R.drawable.sweet_sleep_toolbar_img,R.drawable.sweet_sleep_img,resources.getString(R.string.sweet_sleep),
            resources.getString(R.string.one_min_twenty_five_sec),R.raw.sweet_sleep_music))
        recyclerViewItems.add(ModelBottomRecyclerViewSleepFragment
            (R.drawable.good_night_toolbar_img,R.drawable.good_night_img,resources.getString(R.string.beauty),
            resources.getString(R.string.two_min_fourteen_sec),R.raw.beauty_music))
        recyclerViewItems.add(ModelBottomRecyclerViewSleepFragment
            (R.drawable.moon_clouds_toolbar_img,R.drawable.moon_clouds_img,resources.getString(R.string.harmony_of_earth),
            resources.getString(R.string.two_min_twenty_nine_sec),R.raw.harmony_of_earth_music))

        val bottomLayout : RecyclerView.LayoutManager =
            GridLayoutManager(applicationContext,1, GridLayoutManager.HORIZONTAL,
                false)
        binding.recyclerViewPlayOptions.layoutManager = bottomLayout

        val bottomAdapter = AdapterRecyclerViewBottomSleepFragment (this,recyclerViewItems)
        binding.recyclerViewPlayOptions.adapter = bottomAdapter
    }

    @SuppressLint("SetTextI18n")
    private fun setFavouriteNumber(favouriteNumber : Int)
    {
        binding.tvFavouriteNumber.text = favouriteNumber.toString() + " " + resources.getString(R.string.favorites)
    }

    @Deprecated("This method has been deprecated in favor of using the\n      {@link OnBackPressedDispatcher} via {@link #getOnBackPressedDispatcher()}.\n      The OnBackPressedDispatcher controls how back button events are dispatched\n      to one or more {@link OnBackPressedCallback} objects.")
    override fun onBackPressed() {
        val intent = Intent(this,HomeActivity::class.java)
        startActivity(intent)
        super.onBackPressed()
    }
}