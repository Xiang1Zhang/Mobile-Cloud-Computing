package com.example.client.ui.taskList.main

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.client.ui.taskList.FirstFragment
import com.example.client.ui.taskList.SecondFragment
import com.example.client.ui.taskList.ThirdFragment


/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
class SectionsPagerAdapter(fm: FragmentManager, val projectId: String ) : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        return when (position) {
            0 -> FirstFragment(projectId)
            1 -> SecondFragment(projectId)
            else -> ThirdFragment(projectId)
        }
    }

    override fun getPageTitle(position: Int): CharSequence {
        return when (position) {
            0 -> "TASKS"
            1 -> "PICTURES"
            else -> "FILES"

        }
    }

    override fun getCount(): Int {
        // Show 3 total pages.
        return 3
    }
}