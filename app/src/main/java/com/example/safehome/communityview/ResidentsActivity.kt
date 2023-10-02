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
import com.example.safehome.adapter.ResidentsAdapter
import com.example.safehome.databinding.ActivityResidentsBinding
import com.example.safehome.model.CommunityBlock
import com.example.safehome.model.Residents

class ResidentsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResidentsBinding
    private var residentsList: ArrayList<Residents> = ArrayList()
    private var blockList: ArrayList<CommunityBlock> = ArrayList()
    private lateinit var residentsAdapter: ResidentsAdapter
    private var blockPopupWindow: PopupWindow? = null
    private var selectedBlock: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResidentsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        addBlockData()
        clickEvents()
        addMemberData()
        populateMemberData()
    }

    private fun addBlockData() {
        blockList.add(CommunityBlock("A", "1", false))
        blockList.add(CommunityBlock("B", "2", false))
        blockList.add(CommunityBlock("C", "3", false))
        blockList.add(CommunityBlock("D", "4", false))
    }

    private fun clickEvents() {
        binding.backBtnClick.setOnClickListener {
            try {
                finish()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        binding.selectBlockCl.setOnClickListener {
            if (blockPopupWindow != null) {
                if (blockPopupWindow!!.isShowing) {
                    blockPopupWindow!!.dismiss()
                } else {
                    blockTypeDropDown()
                }
            } else {
                blockTypeDropDown()
            }
        }

    }

    private fun blockTypeDropDown() {
        val layoutInflater: LayoutInflater =
            this.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view: View =
            layoutInflater.inflate(R.layout.community_block_drop_down_popup_window, null)

        val width = binding.selectBlockCl.measuredWidth * 0.5.toInt()

        blockPopupWindow = PopupWindow(
            view,
           // width,
            binding.selectBlockTxt.measuredWidth,
            ViewGroup.LayoutParams.WRAP_CONTENT
            //(Utils.screenHeight * 0.3).toInt()
        )
        if (blockList.isNotEmpty()) {
            val linearLayoutManager = LinearLayoutManager(this)
            val dropDownRecyclerView = view.findViewById<RecyclerView>(R.id.drop_down_recycler_view)
            dropDownRecyclerView.layoutManager = linearLayoutManager
            val communityBlockAdapter = CommunityBlockAdapter(this, blockList)

            dropDownRecyclerView.adapter = communityBlockAdapter
            communityBlockAdapter.setCallback(this@ResidentsActivity)
        }
        blockPopupWindow!!.elevation = 10F
        blockPopupWindow!!.showAsDropDown(binding.selectBlockTxt, 0, 0, Gravity.CENTER)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun selectVehicleType(s: CommunityBlock) {
        //  binding.selectVehilceTypeErrorCase.visibility = View.GONE
        if (blockPopupWindow != null) {
            if (blockPopupWindow!!.isShowing) {
                blockPopupWindow!!.dismiss()
            }
        }
        if (s.blockName.isNotEmpty()) {
            selectedBlock = s.blockName
            binding.selectBlockTxt.text = selectedBlock
        }

//        for(i in blockList){
//            if(i.blockId.equals(s.blockId)){
//                blockList.add(CommunityBlock(s.blockName, s.blockId, true))
//            }else{
//                blockList.add(CommunityBlock(i.blockName, i.blockId, false))
//            }
//        }
    }

    private fun populateMemberData() {
        binding.residentsRecyclerView.layoutManager = LinearLayoutManager(this)
        residentsAdapter = ResidentsAdapter(this, residentsList)
        binding.residentsRecyclerView.adapter = residentsAdapter
    }

    private fun addMemberData() {
        residentsList.add(Residents("A-Flat 101", "Tejaswini Pasham", "Owner"))
        residentsList.add(Residents("A-Flat 102", "Sai Pasham", "Owner"))
        residentsList.add(Residents("A-Flat 103", "Shashi Pasham", "Tenant"))
    }
}