package com.dev.meditation.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.meditation.R
import com.dev.meditation.adapter.AdapterRecyclerViewBottomSleepFragment
import com.example.meditation.databinding.FragmentMusicBinding
import com.dev.meditation.interfaces.onBackPress
import com.dev.meditation.model.ModelBottomRecyclerViewSleepFragment
import com.dev.meditation.`object`.Utils

class MusicFragment : Fragment(), onBackPress {
    private lateinit var binding : FragmentMusicBinding
    private lateinit var recyclerItems : MutableList<ModelBottomRecyclerViewSleepFragment>

    @SuppressLint("InternalInsetResource", "DiscouragedApi")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentMusicBinding.inflate(layoutInflater)
        val view = binding.root

        init()
        setRecyclerItems()

        return view
    }

    private fun init(){
        binding.root.setOnApplyWindowInsetsListener { _, insets ->
            val statusBarHeight = insets.systemWindowInsetTop
            val extraMarginInPx = resources.getDimensionPixelSize(R.dimen.twenty_top_margin)

            val layoutParams =
                binding.lytRelativeTopMusicFragment.layoutParams as ViewGroup.MarginLayoutParams
            layoutParams.topMargin = statusBarHeight + extraMarginInPx
            binding.lytRelativeTopMusicFragment.layoutParams = layoutParams

            insets
        }

        Utils.setStatusBarColor(requireActivity(), true)

        recyclerItems = mutableListOf()
    }

    private fun setRecyclerItems(){
        recyclerItems.add(
            ModelBottomRecyclerViewSleepFragment
                (
                R.drawable.night_island_toolbar_img,
                R.drawable.night_island_img,
                resources.getString(R.string.beauty),
                resources.getString(R.string.two_min_fourteen_sec),
                R.raw.beauty_music
            )
        )
        recyclerItems.add(
            ModelBottomRecyclerViewSleepFragment
                (
                R.drawable.sweet_sleep_toolbar_img,
                R.drawable.sweet_sleep_img,
                resources.getString(R.string.relaxing_piano),
                resources.getString(R.string.one_min_forty_nine_sec),
                R.raw.relexing_piano_music
            )
        )
        recyclerItems.add(
            ModelBottomRecyclerViewSleepFragment
                (
                R.drawable.good_night_toolbar_img,
                R.drawable.good_night_img,
                resources.getString(R.string.harmony_of_earth),
                resources.getString(R.string.two_min_twenty_nine_sec),
                R.raw.harmony_of_earth_music
            )
        )
        recyclerItems.add(
            ModelBottomRecyclerViewSleepFragment
                (
                R.drawable.moon_clouds_toolbar_img,
                R.drawable.moon_clouds_img,
                resources.getString(R.string.focus_attention),
                resources.getString(R.string.two_min_twenty_five_sec),
                R.raw.focus_attention_music
            )
        )
        recyclerItems.add(
            ModelBottomRecyclerViewSleepFragment
                (
                R.drawable.night_island_toolbar_img,
                R.drawable.night_island_img,
                resources.getString(R.string.night_island),
                resources.getString(R.string.two_min_thirty_one_sec),
                R.raw.night_island_music
            )
        )
        recyclerItems.add(
            ModelBottomRecyclerViewSleepFragment
                (
                R.drawable.sweet_sleep_toolbar_img,
                R.drawable.sweet_sleep_img,
                resources.getString(R.string.sweet_sleep),
                resources.getString(R.string.one_min_twenty_five_sec),
                R.raw.sweet_sleep_music
            )
        )
        recyclerItems.add(
            ModelBottomRecyclerViewSleepFragment
                (
                R.drawable.good_night_toolbar_img,
                R.drawable.good_night_img,
                resources.getString(R.string.good_night),
                resources.getString(R.string.three_min_twenty_nine_sec),
                R.raw.good_night_music
            )
        )
        recyclerItems.add(
            ModelBottomRecyclerViewSleepFragment
                (
                R.drawable.moon_clouds_toolbar_img,
                R.drawable.moon_clouds_img,
                resources.getString(R.string.moon_cloud),
                resources.getString(R.string.two_min_forty_eight_sec),
                R.raw.moon_cloud_music
            )
        )
        recyclerItems.add(
            ModelBottomRecyclerViewSleepFragment
                (
                R.drawable.sweet_sleep_toolbar_img,
                R.drawable.sweet_sleep_img,
                resources.getString(R.string.flute),
                resources.getString(R.string.four_min),
                R.raw.flute_music
            )
        )

        val layout: RecyclerView.LayoutManager =
            GridLayoutManager(requireActivity(), 2, GridLayoutManager.VERTICAL, false)
        binding.recyclerViewSleepMusic.layoutManager = layout

        val adapter = AdapterRecyclerViewBottomSleepFragment(requireActivity(), recyclerItems)
        binding.recyclerViewSleepMusic.adapter = adapter

        binding.btnBackSleepMusic.setOnClickListener {
            val layout = requireActivity().findViewById<LinearLayout>(R.id.navHome)
            layout.setBackgroundColor(resources.getColor(R.color.white))
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.frameLytHomeActivity, HomeFragment())
                .addToBackStack(null)
                .commit()
        }
    }
    override fun onBackPressed(): Boolean {
        val layout = requireActivity().findViewById<LinearLayout>(R.id.navHome)
        layout.setBackgroundColor(resources.getColor(R.color.white))
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.frameLytHomeActivity, HomeFragment())
            .addToBackStack(null)
            .commit()
        return true
    }
}