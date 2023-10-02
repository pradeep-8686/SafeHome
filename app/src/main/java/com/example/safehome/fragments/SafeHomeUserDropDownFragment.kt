package com.example.safehome.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.safehome.R
import com.example.safehome.adapter.FaciHistoryAdapter
import com.example.safehome.adapter.SafeHomeMainUserAdapter
import com.example.safehome.databinding.FragmentFacilitiesHistoryBinding
import com.example.safehome.databinding.SafeHomeUserDropdownLayoutBinding
import com.example.safehome.model.SafeHomeUserModelList

class SafeHomeUserDropDownFragment : Fragment() {
    private lateinit var safeHomeUserDropdownLayoutBinding: SafeHomeUserDropdownLayoutBinding
    private var safeHomeUserList:ArrayList<SafeHomeUserModelList> = ArrayList()
    private lateinit var safeHomeMainUserAdapter: SafeHomeMainUserAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        safeHomeUserDropdownLayoutBinding = SafeHomeUserDropdownLayoutBinding.inflate(inflater, container, false)
        addSafeHomeUserData()
        addPopulateData()
        return safeHomeUserDropdownLayoutBinding.root
    }

    private fun addPopulateData() {
        safeHomeUserDropdownLayoutBinding.safehomeUserRecyclerview.layoutManager = LinearLayoutManager(requireContext())
        safeHomeMainUserAdapter = SafeHomeMainUserAdapter(requireContext(), safeHomeUserList)
        safeHomeUserDropdownLayoutBinding.safehomeUserRecyclerview.adapter = safeHomeMainUserAdapter
        safeHomeMainUserAdapter.notifyDataSetChanged()
    }

    private fun addSafeHomeUserData() {
        safeHomeUserList.add(SafeHomeUserModelList("Hyderabad", "Raheja","D-103", R.drawable.add_flat_icon, true))
        safeHomeUserList.add(SafeHomeUserModelList("Banglore", "My Home","B-206", R.drawable.add_flat_icon, false))
    }

}