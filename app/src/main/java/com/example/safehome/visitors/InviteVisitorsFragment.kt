package com.example.safehome.visitors

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.safehome.R
import com.example.safehome.databinding.FragmentInviteVisitorsBinding
import com.example.safehome.visitors.guest.GuestActivity


class InviteVisitorsFragment : Fragment() {

    private lateinit var binding : FragmentInviteVisitorsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentInviteVisitorsBinding.inflate(inflater, container, false)

        clickEvents()

        return binding.root
    }

    private fun clickEvents() {
        binding.clGuest.setOnClickListener {

            val intent = Intent(requireContext(), GuestActivity::class.java)
            startActivity(intent)

        }
    }

}