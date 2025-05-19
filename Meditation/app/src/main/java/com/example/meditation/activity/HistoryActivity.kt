package com.example.meditation.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.dev.meditation.`object`.Utils
import com.example.meditation.R
import com.example.meditation.adapter.AdapterDownloadSong
import com.example.meditation.dataClass.DownloadDatabase
import com.example.meditation.databinding.ActivityHistoryBinding
import com.example.meditation.model.ModelDownloadSong
import kotlinx.coroutines.launch

class HistoryActivity : AppCompatActivity()
{
    private lateinit var binding : ActivityHistoryBinding
    private lateinit var historyList : MutableList<ModelDownloadSong>
    private lateinit var downloadDb : DownloadDatabase

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
        showHistory()

    }

    private fun init(){
        binding.root.setOnApplyWindowInsetsListener { _, insets ->
            val statusBarHeight = insets.systemWindowInsetTop
            val extraMarginInPx = resources.getDimensionPixelSize(R.dimen.twenty_top_margin)

            val layoutParams =
                binding.toolbarHistory.layoutParams as ViewGroup.MarginLayoutParams
            layoutParams.topMargin = statusBarHeight + extraMarginInPx
            binding.toolbarHistory.layoutParams = layoutParams

            insets
        }

        Utils.setStatusBarColor(this, false)
        historyList = mutableListOf()
    }

    private fun showHistory(){
        downloadDb = Room.databaseBuilder(applicationContext, DownloadDatabase::class.java, "historyDatabase")
            .allowMainThreadQueries()
            .build()

        binding.rvHistory.layoutManager = LinearLayoutManager(this)

        lifecycleScope.launch {
            val historyList = downloadDb.daoClass().showDownloadHistory()
            val adapter = AdapterDownloadSong(applicationContext, historyList)
            binding.rvHistory.adapter = adapter
        }

    }
}