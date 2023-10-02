package com.example.safehome.communityview

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.safehome.R
import com.example.safehome.adapter.CommunityBlockAdapter
import com.example.safehome.adapter.EmergencyContactAdapter
import com.example.safehome.adapter.ResidentsAdapter
import com.example.safehome.databinding.ActivityEmergencyInCommunityBinding
import com.example.safehome.databinding.ActivityResidentsBinding
import com.example.safehome.model.CommunityBlock
import com.example.safehome.model.EmergencyContact
import com.example.safehome.model.Residents

class EmergencyContactActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEmergencyInCommunityBinding
    private var emergrncyContactList: ArrayList<EmergencyContact> = ArrayList()
    private var blockList: ArrayList<CommunityBlock> = ArrayList()
    private lateinit var emergencyContactAdapter: EmergencyContactAdapter
    private var blockPopupWindow: PopupWindow? = null
    private var selectedBlock: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEmergencyInCommunityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        clickEvents()
        addMemberData()
        emergencyContactAdapter()
    }

    private fun clickEvents() {
        binding.backBtnClick.setOnClickListener {
            try {
                finish()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }


    }



    private fun emergencyContactAdapter() {
        binding.emergencyContactsRecyclerView.layoutManager = LinearLayoutManager(this)
        emergencyContactAdapter = EmergencyContactAdapter(this, emergrncyContactList)
        binding.emergencyContactsRecyclerView.adapter = emergencyContactAdapter
    }

    private fun addMemberData() {
        emergrncyContactList.add(EmergencyContact("Doctor", "Apoorva Reddy", "General Physician"))
        emergrncyContactList.add(EmergencyContact("Nutritionist", "Mounika", ""))
    }
}