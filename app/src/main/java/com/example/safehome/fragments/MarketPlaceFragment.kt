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
import com.example.safehome.databinding.FragmentMarketPlaceBinding


class MarketPlaceFragment : Fragment() {
    private lateinit var binding: FragmentMarketPlaceBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMarketPlaceBinding.inflate(inflater, container, false)
        clickEvents()

        return binding.root
    }

    private fun clickEvents() {

    }

}