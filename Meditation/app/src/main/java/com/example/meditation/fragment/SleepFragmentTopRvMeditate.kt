package com.dev.meditation.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.meditation.R

class SleepFragmentTopRvMeditate : Fragment()
{
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View?
    {
        return inflater.inflate(R.layout.fragment_sleep_top_rv_meditate, container, false)
    }

}