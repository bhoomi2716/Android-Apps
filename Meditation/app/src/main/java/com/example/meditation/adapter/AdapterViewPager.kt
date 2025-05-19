package com.dev.meditation.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

class  AdapterViewPager(fragmentManager : FragmentManager) : FragmentStatePagerAdapter(fragmentManager)
{
    private var listFragment:ArrayList<Fragment> = ArrayList()
    private var listTitle:ArrayList<String> = ArrayList()

    override fun getCount(): Int
    {
        return listFragment.size
    }

    override fun getItem(position: Int): Fragment
    {
        return listFragment[position]
    }
    override fun getPageTitle(position: Int): CharSequence
    {
        return listTitle[position]
    }

    fun setData(fm:Fragment,data:String)
    {
        listTitle.add(data)
        listFragment.add(fm)
    }

}