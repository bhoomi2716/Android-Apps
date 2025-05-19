package com.dev.meditation.fragment

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.dev.meditation.adapter.AdapterMyFragmentSleep
import com.example.meditation.databinding.FragmentMyTopRvSleepBinding
import com.dev.meditation.model.ModelMyFragment
import com.dev.meditation.`object`.GlobalMusicState
import com.dev.meditation.`object`.MusicPlayerManager
import com.dev.meditation.service.MusicPlayerService
import io.realm.Realm

class MyFragmentTopRvSleep : Fragment() {

    lateinit var binding : FragmentMyTopRvSleepBinding
    private lateinit var likedList : MutableList<ModelMyFragment>
    private lateinit var realmDb : Realm
    private lateinit var adapter : AdapterMyFragmentSleep

    private val playbackReceiver = object : BroadcastReceiver() {
        @SuppressLint("NotifyDataSetChanged")
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == "com.dev.meditation.PLAYBACK_STATE_CHANGED") {
                adapter.notifyDataSetChanged()
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        binding = FragmentMyTopRvSleepBinding.inflate(layoutInflater)
        val view = binding.root

        realmDb = Realm.getInstance(Realm.getDefaultConfiguration()!!)
        setLikedListUsingDatabase()

        return view
    }

    private fun setLikedListUsingDatabase() {
        likedList = realmDb.where(ModelMyFragment::class.java)
            .equalTo("songType", "sleep")
            .findAll()
            .map { it }  // convert RealmResults to MutableList
            .toMutableList()

        binding.myRecyclerviewSleepFragment.layoutManager = LinearLayoutManager(requireContext())
        adapter = AdapterMyFragmentSleep(requireActivity(), likedList)
        binding.myRecyclerviewSleepFragment.adapter = adapter
    }

    @SuppressLint("NotifyDataSetChanged", "UnspecifiedRegisterReceiverFlag")
    override fun onResume() {
        super.onResume()
        GlobalMusicState.currentMyFragmentSleep = adapter
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
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
        adapter.notifyDataSetChanged()
    }

    override fun onPause() {
        super.onPause()
        if (GlobalMusicState.currentMyFragmentSleep == adapter) {
            GlobalMusicState.currentMyFragmentSleep = null
        }
        requireActivity().unregisterReceiver(playbackReceiver)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        realmDb.close()
    }
}