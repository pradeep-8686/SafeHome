package com.example.safehome.visitors.guest

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.safehome.R
import com.example.safehome.databinding.FragmentAllowOnceBinding
import com.example.safehome.databinding.FragmentInviteVisitorsBinding


class AllowOnceFragment : Fragment() {

    private lateinit var binding : FragmentAllowOnceBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAllowOnceBinding.inflate(inflater, container, false)


        return binding.root
    }

}