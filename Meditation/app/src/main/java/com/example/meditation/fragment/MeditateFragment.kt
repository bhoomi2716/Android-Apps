package com.dev.meditation.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.meditation.R
import com.dev.meditation.adapter.AdapterRecyclerViewTopFragment
import com.example.meditation.databinding.FragmentMeditateBinding
import com.dev.meditation.interfaces.OnTopItemClickListener
import com.dev.meditation.interfaces.onBackPress
import com.dev.meditation.model.ModelTopRecyclerViewFragment
import com.dev.meditation.`object`.Utils

class MeditateFragment : Fragment(), OnTopItemClickListener, onBackPress
{
    private lateinit var binding : FragmentMeditateBinding
    private lateinit var topRecyclerViewItems : MutableList<ModelTopRecyclerViewFragment>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        binding = FragmentMeditateBinding.inflate(layoutInflater)
        val view = binding.root

        init()
        setRecyclerItems()

        return view
    }

    private fun init(){
        binding.root.setOnApplyWindowInsetsListener { _, insets ->
            val statusBarHeight = insets.systemWindowInsetTop

            val extraMarginInPx = resources.getDimensionPixelSize(R.dimen.fifteen_top_margin)

            val layoutParams = binding.tvMeditateHeading.layoutParams as ViewGroup.MarginLayoutParams
            layoutParams.topMargin = statusBarHeight + extraMarginInPx
            binding.tvMeditateHeading.layoutParams = layoutParams

            insets
        }

        Utils.setStatusBarColor(requireActivity(),false)

        topRecyclerViewItems = mutableListOf()
    }

    private fun setRecyclerItems(){
        topRecyclerViewItems.add(ModelTopRecyclerViewFragment
            (R.drawable.all_recycler_view, resources.getString(R.string.all),
            R.drawable.all_rv_selected, resources.getColor(R.color.heading_colour_primary),
            resources.getColor(R.color.nav_txt_color),AllFragmentTopRvMeditate()))
        topRecyclerViewItems.add(ModelTopRecyclerViewFragment
            (R.drawable.my_recycler_view, resources.getString(R.string.my),
            R.drawable.my_rv_selected, resources.getColor(R.color.heading_colour_primary),
            resources.getColor(R.color.nav_txt_color),MyFragmentTopRvMeditate()))
        topRecyclerViewItems.add(ModelTopRecyclerViewFragment
            (R.drawable.anxious_recycler_view,resources.getString(R.string.anxious),
            R.drawable.anxious_rv_selected, resources.getColor(R.color.heading_colour_primary),
            resources.getColor(R.color.nav_txt_color),AnxiousFragmentTopRvMeditate()))
        topRecyclerViewItems.add(ModelTopRecyclerViewFragment
            (R.drawable.sleep_recycler_view,resources.getString(R.string.title_sleep),
            R.drawable.sleep_rv_selected, resources.getColor(R.color.heading_colour_primary),
            resources.getColor(R.color.nav_txt_color),SleepFragmentTopRvMeditate()))
        topRecyclerViewItems.add(ModelTopRecyclerViewFragment
            (R.drawable.kids_recycler_view,resources.getString(R.string.kids),
            R.drawable.kids_rv_selected, resources.getColor(R.color.heading_colour_primary),
            resources.getColor(R.color.nav_txt_color),KidsFragmentTopRvMeditate()))
        topRecyclerViewItems.add(ModelTopRecyclerViewFragment
            (R.drawable.all_recycler_view,resources.getString(R.string.all),
            R.drawable.all_rv_selected, resources.getColor(R.color.heading_colour_primary),
            resources.getColor(R.color.nav_txt_color),AllFragmentTopRvMeditate()))
        topRecyclerViewItems.add(ModelTopRecyclerViewFragment
            (R.drawable.my_recycler_view,resources.getString(R.string.my),
            R.drawable.my_rv_selected, resources.getColor(R.color.heading_colour_primary),
            resources.getColor(R.color.nav_txt_color),MyFragmentTopRvMeditate()))
        topRecyclerViewItems.add(ModelTopRecyclerViewFragment
            (R.drawable.anxious_recycler_view,resources.getString(R.string.anxious),
            R.drawable.anxious_rv_selected, resources.getColor(R.color.heading_colour_primary),
            resources.getColor(R.color.nav_txt_color),AnxiousFragmentTopRvMeditate()))
        topRecyclerViewItems.add(ModelTopRecyclerViewFragment
            (R.drawable.sleep_recycler_view,resources.getString(R.string.title_sleep),
            R.drawable.sleep_rv_selected, resources.getColor(R.color.heading_colour_primary),
            resources.getColor(R.color.nav_txt_color),SleepFragmentTopRvMeditate()))
        topRecyclerViewItems.add(ModelTopRecyclerViewFragment
            (R.drawable.kids_recycler_view,resources.getString(R.string.kids),
            R.drawable.kids_rv_selected, resources.getColor(R.color.heading_colour_primary),
            resources.getColor(R.color.nav_txt_color),KidsFragmentTopRvMeditate()))

        val topLayout : RecyclerView.LayoutManager =
            GridLayoutManager(requireActivity(),1,RecyclerView.HORIZONTAL,false)
        binding.recyclerViewTopMeditateFragment.layoutManager = topLayout

        val topAdapter = AdapterRecyclerViewTopFragment(requireActivity(),topRecyclerViewItems,this)
        binding.recyclerViewTopMeditateFragment.adapter = topAdapter

        topAdapter.setDefaultSelection(0)
    }

    override fun onTopItemClick(fragment: Fragment) {
        childFragmentManager.beginTransaction()
            .replace(R.id.frmNestedMeditate, fragment)
            .commit()
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