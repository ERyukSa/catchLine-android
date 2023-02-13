package com.eryuksa.catchthelines.ui.game

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class PosterViewPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    var posterUrls: List<String> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemCount(): Int =
        posterUrls.size

    override fun createFragment(position: Int): Fragment =
        PosterFragment.newInstance(posterUrl = posterUrls[position])
}
