package com.example.safehome.complaints

import android.app.Dialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.safehome.HomeActivity
import com.example.safehome.R
import com.example.safehome.Utils
import com.example.safehome.adapter.CategoryRaiseComplaintAdapter
import com.example.safehome.adapter.ComplaintTypeAdapter
import com.example.safehome.adapter.PriorityAdapter
import com.example.safehome.adapter.YearAdapter
import com.example.safehome.custom.CustomProgressDialog
import com.example.safehome.databinding.ActivityComplaintsBinding
import com.example.safehome.databinding.ActivityRaiseComplaintBinding
import com.example.safehome.maintenance.HistoryFragment
import com.example.safehome.repository.APIClient
import com.example.safehome.repository.APIInterface

class RaiseComplaintActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRaiseComplaintBinding
    private lateinit var customProgressDialog: CustomProgressDialog
    private lateinit var apiInterface: APIInterface
    var User_Id: String? = ""
    var Auth_Token: String? = ""
    private var categoryPopupWindow: PopupWindow? = null
    private var priorityPopupWindow: PopupWindow? = null
    private var complaintTypePopupWindow: PopupWindow? = null

    private var complaintTypeList: ArrayList<String> = ArrayList()
    private var categoryList: ArrayList<String> = ArrayList()
    private var priorityList: ArrayList<String> = ArrayList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRaiseComplaintBinding.inflate(layoutInflater)
        setContentView(binding.root)

        customProgressDialog = CustomProgressDialog()
        apiInterface = APIClient.getClient(this)
        User_Id = Utils.getStringPref(this, "User_Id", "")
        Auth_Token = Utils.getStringPref(this, "Token", "")

        binding.backBtnClick.setOnClickListener {
            val intent = Intent(this, ComplaintsActivity::class.java)
            startActivity(intent)
            finish()
        }

        dropDown()
    }

    private fun dropDown() {
        addComplaintist()
        addCategoryList()
        addPriorityList()

        binding.clTvComplaintType.setOnClickListener {
            complaintType()
        }
        binding.tvCategoryCl.setOnClickListener {
            categorypopUp()
        }
        binding.priorityCl.setOnClickListener {
            priorityPopUp()
        }
    }

    private fun addComplaintist() {
        complaintTypeList.add("Personal Level")
        complaintTypeList.add("Community Level")
    }

    private fun addCategoryList() {
        categoryList.add("Common Area")
    }

    private fun addPriorityList() {
        priorityList.add("High")
        priorityList.add("Medium")
        priorityList.add("Low")
    }

    private fun priorityPopUp() {
        if (priorityPopupWindow != null) {
            if (priorityPopupWindow!!.isShowing) {
                priorityPopupWindow!!.dismiss()
            } else {
                priorityDropDown()
            }
        } else {
            priorityDropDown()
        }

    }

    private fun categorypopUp() {
        if (categoryPopupWindow != null) {
            if (categoryPopupWindow!!.isShowing) {
                categoryPopupWindow!!.dismiss()
            } else {
                categoryDropDown()
            }
        } else {
            categoryDropDown()
        }

    }

    private fun complaintType() {
        if (complaintTypePopupWindow != null) {
            if (complaintTypePopupWindow!!.isShowing) {
                complaintTypePopupWindow!!.dismiss()
            } else {
                complaintTypeDropDown()
            }
        } else {
            complaintTypeDropDown()
        }
    }

    private fun categoryDropDown() {
        val layoutInflater: LayoutInflater =
            getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view: View = layoutInflater.inflate(R.layout.drop_down_layout, null)

        categoryPopupWindow = PopupWindow(
            view,
            binding.tvCategoryTxt.measuredWidth,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        if (categoryList.isNotEmpty()) {
            val linearLayoutManager = LinearLayoutManager(this@RaiseComplaintActivity)
            val dropDownRecyclerView = view.findViewById<RecyclerView>(R.id.drop_down_recycler_view)
            dropDownRecyclerView.layoutManager = linearLayoutManager
            val categoryAdapter = CategoryRaiseComplaintAdapter(this@RaiseComplaintActivity, categoryList)

            dropDownRecyclerView.adapter = categoryAdapter
            categoryAdapter.setCallbackCategory(this@RaiseComplaintActivity)
        }
        categoryPopupWindow!!.elevation = 10F
        categoryPopupWindow!!.showAsDropDown(binding.tvCategoryTxt, 0, 0, Gravity.CENTER)
    }

    private fun priorityDropDown() {
        val layoutInflater: LayoutInflater =
            getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view: View = layoutInflater.inflate(R.layout.drop_down_layout, null)

        priorityPopupWindow = PopupWindow(
            view,
            binding.priorityTxt.measuredWidth,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        if (priorityList.isNotEmpty()) {
            val linearLayoutManager = LinearLayoutManager(this@RaiseComplaintActivity)
            val dropDownRecyclerView = view.findViewById<RecyclerView>(R.id.drop_down_recycler_view)
            dropDownRecyclerView.layoutManager = linearLayoutManager
            val categoryAdapter = PriorityAdapter(this@RaiseComplaintActivity, priorityList)

            dropDownRecyclerView.adapter = categoryAdapter
            categoryAdapter.setCallbackPriority(this@RaiseComplaintActivity)
        }
        priorityPopupWindow!!.elevation = 10F
        priorityPopupWindow!!.showAsDropDown(binding.priorityTxt, 0, 0, Gravity.CENTER)
    }

    private fun complaintTypeDropDown() {
        val layoutInflater: LayoutInflater =
            getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view: View = layoutInflater.inflate(R.layout.drop_down_layout, null)

        complaintTypePopupWindow = PopupWindow(
            view,
            binding.complaintTypeTxt.measuredWidth,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        if (complaintTypeList.isNotEmpty()) {
            val linearLayoutManager = LinearLayoutManager(this@RaiseComplaintActivity)
            val dropDownRecyclerView = view.findViewById<RecyclerView>(R.id.drop_down_recycler_view)
            dropDownRecyclerView.layoutManager = linearLayoutManager
            val categoryAdapter = ComplaintTypeAdapter(this@RaiseComplaintActivity, complaintTypeList)

            dropDownRecyclerView.adapter = categoryAdapter
            categoryAdapter.setCallbackComplaintType(this@RaiseComplaintActivity)
        }
        complaintTypePopupWindow!!.elevation = 10F
        complaintTypePopupWindow!!.showAsDropDown(binding.complaintTypeTxt, 0, 0, Gravity.CENTER)
    }

    fun setCallbackComplaintType(complaintType: String) {
        if (complaintTypePopupWindow != null) {
            if (complaintTypePopupWindow!!.isShowing) {
                complaintTypePopupWindow!!.dismiss()
            }
        }
        if (complaintType != null) {
            binding.complaintTypeTxt.text = complaintType
           // selectedCategoryId = ComplaintStatus
        }
        Log.d(HistoryFragment.TAG, "$complaintType")
    }

    fun setCallbackCategory(category: String) {
        if (categoryPopupWindow != null) {
            if (categoryPopupWindow!!.isShowing) {
                categoryPopupWindow!!.dismiss()
            }
        }
        if (category != null) {
            binding.tvCategoryTxt.text = category
            // selectedCategoryId = ComplaintStatus
        }
        Log.d(HistoryFragment.TAG, "$category")
    }

    fun setCallbackPriority(priority: String) {
        if (priorityPopupWindow != null) {
            if (priorityPopupWindow!!.isShowing) {
                priorityPopupWindow!!.dismiss()
            }
        }
        if (priority != null) {
            binding.priorityTxt.text = priority
            // selectedCategoryId = ComplaintStatus
        }
        Log.d(HistoryFragment.TAG, "$priority")
    }
}