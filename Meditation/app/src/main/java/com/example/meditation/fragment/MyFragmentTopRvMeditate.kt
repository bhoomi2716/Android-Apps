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
import com.dev.meditation.adapter.AdapterMyFragmentMeditate
import com.example.meditation.databinding.FragmentMyTopRvMeditateBinding
import com.dev.meditation.model.ModelMyFragment
import com.dev.meditation.`object`.GlobalMusicState
import io.realm.Realm

class MyFragmentTopRvMeditate : Fragment()
{
    private lateinit var binding : FragmentMyTopRvMeditateBinding
    private lateinit var likedList : MutableList<ModelMyFragment>
    private lateinit var realmDb : Realm
    private lateinit var adapter : AdapterMyFragmentMeditate

    private val playbackReceiver = object : BroadcastReceiver() {
        @SuppressLint("NotifyDataSetChanged")
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == "com.dev.meditation.PLAYBACK_STATE_CHANGED") {
                val fragmentType = intent.getStringExtra("fragmentType") ?: ""
                if (fragmentType == "meditate") {
                    adapter.notifyDataSetChanged()
                }
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentMyTopRvMeditateBinding.inflate(layoutInflater)

        realmDb = Realm.getInstance(Realm.getDefaultConfiguration()!!)
        setLikedListUsingDatabase()

        return binding.root
    }

    private fun setLikedListUsingDatabase() {
        likedList = realmDb.where(ModelMyFragment::class.java)
            .equalTo("songType", "meditate")
            .findAll()
            .map { it }
            .toMutableList()

        binding.myRecyclerviewMeditateFragment.layoutManager = LinearLayoutManager(requireContext())
        adapter = AdapterMyFragmentMeditate(requireActivity(), likedList)
        binding.myRecyclerviewMeditateFragment.adapter = adapter
    }

    @SuppressLint("NotifyDataSetChanged", "UnspecifiedRegisterReceiverFlag")
    override fun onResume() {
        super.onResume()
        GlobalMusicState.currentMyFragmentMeditate = adapter
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
        if (GlobalMusicState.currentMyFragmentMeditate == adapter) {
            GlobalMusicState.currentMyFragmentMeditate = null
        }
        requireActivity().unregisterReceiver(playbackReceiver)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        realmDb.close()
    }
}