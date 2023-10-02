package com.example.safehome.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment

open class BaseFragment : Fragment() {
    private val TAG = "BaseFragment"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    fun replaceFragment(fragment: Fragment, containerId: Int) {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(containerId, fragment).addToBackStack(null).commit()
    }

}