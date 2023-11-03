package com.example.safehome.visitors

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.safehome.R
import com.example.safehome.databinding.FragmentVisitorsListBinding


class VisitorsListFragment : Fragment() {

    private lateinit var binding : FragmentVisitorsListBinding
    private var visitorsList:ArrayList<VisitorsListModel> = ArrayList()
    private lateinit var visitorsListAdapter: VisitorsListAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentVisitorsListBinding.inflate(inflater, container, false)
        addVisitorsList()
        populateData()
        return binding.root
    }

    private fun populateData() {
        if (visitorsList.size == 0) {
            binding.emptyVisitorsTxt.visibility = View.VISIBLE
        } else {
            binding.emptyVisitorsTxt.visibility = View.GONE
            binding.visitorsListRecyclerview.layoutManager = LinearLayoutManager(requireContext())
            visitorsListAdapter =
                VisitorsListAdapter(requireContext(), visitorsList)
            binding.visitorsListRecyclerview.adapter = visitorsListAdapter
            visitorsListAdapter.setCallback(this@VisitorsListFragment)
            visitorsListAdapter.notifyDataSetChanged()
        }
    }

    private fun addVisitorsList() {
        visitorsList.add(
            VisitorsListModel("Cab", "Ola", "TS 13 Ab 0001", "10 July 2023",
        "Allowed By Teja", "3:00 PM", "Once", "", R.drawable.visitor_cab)
        )
        visitorsList.add(VisitorsListModel("Delivery", "Swiggy", "", "10 July 2023",
            "Allowed By Teja", "10:30 PM", "Once", "", R.drawable.visitor_delivery))

        visitorsList.add(VisitorsListModel("Guest", "", "", "10 July 2023-10 August 2023",
            "Allowed By Teja", "10:30 PM", "Frequently(Monthly)", "Ramesh", R.drawable.visitor_guest))

        visitorsList.add(VisitorsListModel("Carpenter", "", "", "10 July 2023-18 July 2023",
            "Allowed By Teja", "10:30 PM", "Frequently(Weekly)", "Srinivas", R.drawable.visitor_staff))

    }


}