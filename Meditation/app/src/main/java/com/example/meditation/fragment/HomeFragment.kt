package com.dev.meditation.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.icu.util.Calendar
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.meditation.R
import com.example.meditation.activity.ChooseTopicActivity
import com.example.meditation.activity.CourseDetailsActivity
import com.dev.meditation.adapter.AdapterHomeFragment
import com.example.meditation.databinding.FragmentHomeBinding
import com.dev.meditation.interfaces.onBackPress
import com.dev.meditation.model.ModelHomeFragment
import com.dev.meditation.`object`.Utils
import com.example.meditation.activity.HistoryActivity


class HomeFragment : Fragment(), onBackPress {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var recyclerItems : MutableList<ModelHomeFragment>
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        val view = binding.root

        init()
        addListeners()
        setRecyclerItems()

        return view
    }

    @SuppressLint("SetTextI18n")
    private fun init(){
        binding.root.setOnApplyWindowInsetsListener { _, insets ->
            val statusBarHeight = insets.systemWindowInsetTop
            val extraMarginInPx = resources.getDimensionPixelSize(R.dimen.twenty_top_margin)

            val layoutParams = binding.lytLinearLogoHomeFrag.layoutParams as ViewGroup.MarginLayoutParams
            layoutParams.topMargin = statusBarHeight + extraMarginInPx
            binding.lytLinearLogoHomeFrag.layoutParams = layoutParams

            insets
        }

        Utils.setStatusBarColor(requireActivity(),false)

        sharedPreferences = requireActivity().getSharedPreferences("Authentication", Context.MODE_PRIVATE)

        val currentTime = Calendar.getInstance()[Calendar.HOUR_OF_DAY]

        binding.tvWelcome.text = when (currentTime) {
            in 0..11 -> {
                resources.getString(R.string.good_morning_msg_Home)
            }
            in 12..15 -> {
                resources.getString(R.string.good_afternoon_msg_Home)
            }
            in 16..20 -> {
                resources.getString(R.string.good_evening_msg_Home)
            }
            in 21..23 -> {
                resources.getString(R.string.good_night_msg_Home)
            }
            else->{
                resources.getString(R.string.hello)
            }
        }

        val username = sharedPreferences.getString("username", "User,")
        binding.tvUsername.text = " $username"

        recyclerItems = mutableListOf()
    }

    private fun addListeners(){
        binding.btnStartLightCourseDetails.setOnClickListener {
            startActivity(Intent(requireActivity(), CourseDetailsActivity::class.java))
        }
        binding.btnStartDarkRelaxMusic.setOnClickListener {
            startActivity(Intent(requireActivity(), HistoryActivity::class.java))
        }
    }

    private fun setRecyclerItems(){
        recyclerItems.add(ModelHomeFragment(R.drawable.focus_home_frag,resources.getString(R.string.focus)))
        recyclerItems.add(ModelHomeFragment(R.drawable.happiness_home_frag,resources.getString(R.string.happiness)))
        recyclerItems.add(ModelHomeFragment(R.drawable.focus_home_frag,resources.getString(R.string.focus)))
        recyclerItems.add(ModelHomeFragment(R.drawable.happiness_home_frag,resources.getString(R.string.happiness)))
        recyclerItems.add(ModelHomeFragment(R.drawable.focus_home_frag,resources.getString(R.string.focus)))
        recyclerItems.add(ModelHomeFragment(R.drawable.happiness_home_frag,resources.getString(R.string.happiness)))
        recyclerItems.add(ModelHomeFragment(R.drawable.focus_home_frag,resources.getString(R.string.focus)))
        recyclerItems.add(ModelHomeFragment(R.drawable.happiness_home_frag,resources.getString(R.string.happiness)))
        recyclerItems.add(ModelHomeFragment(R.drawable.focus_home_frag,resources.getString(R.string.focus)))
        recyclerItems.add(ModelHomeFragment(R.drawable.happiness_home_frag,resources.getString(R.string.happiness)))

        val layoutManager : RecyclerView.LayoutManager = GridLayoutManager(requireActivity(),1,
            RecyclerView.HORIZONTAL,false)
        binding.recyclerViewHomeFrag.layoutManager = layoutManager

        val adapter = AdapterHomeFragment(requireActivity(),recyclerItems)
        binding.recyclerViewHomeFrag.adapter = adapter
    }

    override fun onBackPressed(): Boolean {
        val intent = Intent(requireContext(), ChooseTopicActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
        return true
    }

}