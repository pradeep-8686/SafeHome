package com.example.safehome.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.safehome.communityview.AssociationMembersActivity
import com.example.safehome.communityview.ResidentsActivity
import com.example.safehome.databinding.FragmentAroundMeBinding
import com.example.safehome.databinding.FragmentCommunityBinding


class AroundMeFragment : Fragment() {
    private lateinit var binding: FragmentAroundMeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAroundMeBinding.inflate(inflater, container, false)
        clickEvents()

        return binding.root
    }

    private fun clickEvents() {

    }

}