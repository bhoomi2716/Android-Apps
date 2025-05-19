package com.dev.meditation.`object`

import com.dev.meditation.adapter.AdapterMusicPlayerViewPager
import com.dev.meditation.adapter.AdapterMyFragmentMeditate
import com.dev.meditation.adapter.AdapterMyFragmentSleep
import com.dev.meditation.model.ModelBottomRecyclerViewSleepFragment
import com.dev.meditation.model.ModelMusicPlayerViewPager

object GlobalMusicState {
    var isPlaying: Boolean = false
    var currentPlayingPosition: Int = -1
    var currentSong: ModelMusicPlayerViewPager? = null
    var currentSongSleep : ModelBottomRecyclerViewSleepFragment? = null
    var currentViewPagerAdapter: AdapterMusicPlayerViewPager? = null
    var currentMyFragmentMeditate: AdapterMyFragmentMeditate? = null
    var currentMyFragmentSleep: AdapterMyFragmentSleep? = null
}