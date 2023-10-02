package com.example.safehome.communityview

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.safehome.adapter.AssociationMemberAdapter
import com.example.safehome.databinding.ActivityAssociationMembersBinding
import com.example.safehome.model.AssociationMember
import java.lang.Exception

class AssociationMembersActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAssociationMembersBinding
    private var memberList: ArrayList<AssociationMember> = ArrayList()
    private lateinit var associationMemberAdapter: AssociationMemberAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAssociationMembersBinding.inflate(layoutInflater)
        setContentView(binding.root)
        clickEvents()
        addMemberData()
        populateMemberData()
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

    private fun populateMemberData() {
        binding.associationMemberRecyclerView.layoutManager = LinearLayoutManager(this)
        associationMemberAdapter = AssociationMemberAdapter(this, memberList)
        binding.associationMemberRecyclerView.adapter = associationMemberAdapter
    }

    private fun addMemberData() {
        memberList.add(AssociationMember("Admin", "Tejaswini Pasham", "D-101"))
        memberList.add(AssociationMember("President", "Tejaswini Pasham", "D-101"))
        memberList.add(AssociationMember("Treasurer", "Tejaswini Pasham", "D-101"))
    }
}