package com.dev.meditation.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.meditation.R
import com.dev.meditation.adapter.AdapterRecyclerViewBottomFragment
import com.example.meditation.databinding.FragmentAllTopRvMeditateBinding
import com.dev.meditation.model.ModelBottomRecyclerViewFragment

class AllFragmentTopRvMeditate : Fragment()
{
    private lateinit var binding : FragmentAllTopRvMeditateBinding
    private lateinit var bottomRecyclerViewItems : MutableList<ModelBottomRecyclerViewFragment>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        binding = FragmentAllTopRvMeditateBinding.inflate(layoutInflater)
        val view = binding.root

        setRecyclerItems()

        return view
    }

    private fun setRecyclerItems(){
        bottomRecyclerViewItems = mutableListOf()

        bottomRecyclerViewItems.add(ModelBottomRecyclerViewFragment
            (R.drawable.seven_days_clam_img,resources.getString(R.string.seven_days_clam)))
        bottomRecyclerViewItems.add(ModelBottomRecyclerViewFragment
            (R.drawable.anxitey_release_img,resources.getString(R.string.anxiety_release)))
        bottomRecyclerViewItems.add(ModelBottomRecyclerViewFragment
            (R.drawable.anxitey_release,resources.getString(R.string.anxiety_release)))
        bottomRecyclerViewItems.add(ModelBottomRecyclerViewFragment
            (R.drawable.seven_days_clam,resources.getString(R.string.seven_days_clam)))
        bottomRecyclerViewItems.add(ModelBottomRecyclerViewFragment
            (R.drawable.seven_days_clam_img,resources.getString(R.string.seven_days_clam)))
        bottomRecyclerViewItems.add(ModelBottomRecyclerViewFragment
            (R.drawable.anxitey_release_img,resources.getString(R.string.anxiety_release)))
        bottomRecyclerViewItems.add(ModelBottomRecyclerViewFragment
            (R.drawable.anxitey_release,resources.getString(R.string.anxiety_release)))
        bottomRecyclerViewItems.add(ModelBottomRecyclerViewFragment
            (R.drawable.seven_days_clam,resources.getString(R.string.seven_days_clam)))
        bottomRecyclerViewItems.add(ModelBottomRecyclerViewFragment
            (R.drawable.seven_days_clam_img,resources.getString(R.string.seven_days_clam)))
        bottomRecyclerViewItems.add(ModelBottomRecyclerViewFragment
            (R.drawable.anxitey_release_img,resources.getString(R.string.anxiety_release)))
        bottomRecyclerViewItems.add(ModelBottomRecyclerViewFragment
            (R.drawable.anxitey_release,resources.getString(R.string.anxiety_release)))
        bottomRecyclerViewItems.add(ModelBottomRecyclerViewFragment
            (R.drawable.seven_days_clam,resources.getString(R.string.seven_days_clam)))

        val bottomLayout : RecyclerView.LayoutManager =
            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        binding.recyclerViewBottomMeditateFragment.layoutManager = bottomLayout

        val bottomAdapter = AdapterRecyclerViewBottomFragment(requireActivity(),bottomRecyclerViewItems)
        binding.recyclerViewBottomMeditateFragment.adapter = bottomAdapter
    }

}