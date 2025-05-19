package com.dev.meditation.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.meditation.R
import com.dev.meditation.adapter.AdapterRecyclerViewBottomFragment
import com.dev.meditation.adapter.AdapterRecyclerViewBottomSleepFragment
import com.example.meditation.databinding.FragmentAllTopRvSleepBinding
import com.dev.meditation.model.ModelBottomRecyclerViewFragment
import com.dev.meditation.model.ModelBottomRecyclerViewSleepFragment

class AllFragmentTopRvSleep : Fragment()
{
    private lateinit var binding : FragmentAllTopRvSleepBinding
    private lateinit var bottomRecyclerViewItems : MutableList<ModelBottomRecyclerViewSleepFragment>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        binding = FragmentAllTopRvSleepBinding.inflate(layoutInflater)
        val view = binding.root

        setRecyclerItems()

        return view
    }

    private fun setRecyclerItems(){
        bottomRecyclerViewItems = mutableListOf()

        bottomRecyclerViewItems.add(ModelBottomRecyclerViewSleepFragment
            (R.drawable.night_island_toolbar_img,R.drawable.night_island_img,resources.getString(R.string.night_island),
            resources.getString(R.string.two_min_thirty_one_sec),R.raw.night_island_music))
        bottomRecyclerViewItems.add(ModelBottomRecyclerViewSleepFragment
            (R.drawable.sweet_sleep_toolbar_img,R.drawable.sweet_sleep_img,resources.getString(R.string.sweet_sleep),
            resources.getString(R.string.one_min_twenty_five_sec),R.raw.sweet_sleep_music))
        bottomRecyclerViewItems.add(ModelBottomRecyclerViewSleepFragment
            (R.drawable.good_night_toolbar_img,R.drawable.good_night_img,resources.getString(R.string.good_night),
            resources.getString(R.string.three_min_twenty_nine_sec),R.raw.good_night_music))
        bottomRecyclerViewItems.add(ModelBottomRecyclerViewSleepFragment
            (R.drawable.moon_clouds_toolbar_img,R.drawable.moon_clouds_img,resources.getString(R.string.relaxing_piano),
            resources.getString(R.string.one_min_forty_nine_sec),R.raw.relexing_piano_music))

        val bottomLayout : RecyclerView.LayoutManager =
            GridLayoutManager(requireActivity(),2, GridLayoutManager.VERTICAL,false)
        binding.recyclerViewBottomSleepFragment.layoutManager = bottomLayout

        val bottomAdapter = AdapterRecyclerViewBottomSleepFragment(requireActivity(),bottomRecyclerViewItems)
        binding.recyclerViewBottomSleepFragment.adapter = bottomAdapter
    }
}