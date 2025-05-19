package com.dev.meditation.fragment

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.meditation.R
import com.dev.meditation.adapter.AdapterMusicPlayerViewPager
import com.example.meditation.databinding.FragmentFemaleVoiceBinding
import com.dev.meditation.model.ModelMusicPlayerViewPager
import com.dev.meditation.`object`.GlobalMusicState

class FemaleVoiceFragment : Fragment()
{
    private lateinit var binding: FragmentFemaleVoiceBinding
    private lateinit var recyclerItems : MutableList<ModelMusicPlayerViewPager>
    private lateinit var adapter : AdapterMusicPlayerViewPager

    private val playbackReceiver = object : BroadcastReceiver() {
        @SuppressLint("NotifyDataSetChanged")
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == "com.dev.meditation.PLAYBACK_STATE_CHANGED") {
                val isPlaying = intent.getBooleanExtra("isPlaying", false)
                val position = intent.getIntExtra("position", -1)

                // Update global state
                GlobalMusicState.isPlaying = isPlaying
                GlobalMusicState.currentPlayingPosition = position

                // Refresh only if the playing song exists in this fragment's list
                if (position in 0 until adapter.itemCount) {
                    adapter.notifyDataSetChanged()
                } else {
                    // Force update just in case song exists with same title from liked list
                    adapter.notifyDataSetChanged()
                }
            }

        }
    }

    @SuppressLint("NewApi", "UnspecifiedRegisterReceiverFlag")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        binding = FragmentFemaleVoiceBinding.inflate(layoutInflater)
        val view = binding.root

        setRecyclerItems()

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            requireActivity().registerReceiver(
                playbackReceiver,
                IntentFilter("com.dev.meditation.PLAYBACK_STATE_CHANGED"),
                Context.RECEIVER_EXPORTED
            )
        } else {
            requireActivity().registerReceiver(
                playbackReceiver,
                IntentFilter("com.dev.meditation.PLAYBACK_STATE_CHANGED")
            )
        }

        return view
    }

    private fun setRecyclerItems(){
        recyclerItems = mutableListOf()

        recyclerItems.add(ModelMusicPlayerViewPager
            (resources.getString(R.string.focus_attention),resources.getString(R.string.two_min_twenty_five_sec),R.raw.focus_attention_music))
        recyclerItems.add(ModelMusicPlayerViewPager
            (resources.getString(R.string.flute),resources.getString(R.string.four_min),R.raw.flute_music))
        recyclerItems.add(ModelMusicPlayerViewPager
            (resources.getString(R.string.making_happiness),resources.getString(R.string.three_min_nineteen_sec),R.raw.making_happiness_music))
        recyclerItems.add(ModelMusicPlayerViewPager
            (resources.getString(R.string.guitar),resources.getString(R.string.one_min_forty_nine_sec),R.raw.guitar_music))

        val layoutManager : RecyclerView.LayoutManager =
            LinearLayoutManager(requireActivity(),RecyclerView.VERTICAL,false)
        binding.recyclerViewPager.layoutManager = layoutManager

        adapter = AdapterMusicPlayerViewPager(requireActivity(), recyclerItems) // Add fragment type
        binding.recyclerViewPager.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        requireActivity().unregisterReceiver(playbackReceiver)
    }

    override fun onResume() {
        super.onResume()
        GlobalMusicState.currentViewPagerAdapter = adapter
    }

    override fun onPause() {
        super.onPause()
        if (GlobalMusicState.currentViewPagerAdapter == adapter) {
            GlobalMusicState.currentViewPagerAdapter = null
        }
    }
}