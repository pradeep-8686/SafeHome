package com.example.safehome.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.safehome.communityview.AssociationMembersActivity
import com.example.safehome.communityview.EmergencyContactActivity
import com.example.safehome.communityview.ResidentsActivity
import com.example.safehome.databinding.FragmentCommunityBinding


class CommunityFragment : Fragment() {
    private lateinit var binding: FragmentCommunityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentCommunityBinding.inflate(inflater, container, false)
        clickEvents()

        return binding.root
    }

    private fun clickEvents() {
        binding.associationLayout.setOnClickListener {
            val memberIntent = Intent(requireContext(), AssociationMembersActivity::class.java)
            startActivity(memberIntent)
        }
        binding.residantLayout.setOnClickListener {
            val residentIntent = Intent(requireContext(), ResidentsActivity::class.java)
            startActivity(residentIntent)
        }
        binding.emergencyContactsLayout.setOnClickListener {
            val residentIntent = Intent(requireContext(), EmergencyContactActivity::class.java)
            startActivity(residentIntent)
        }
    }

}