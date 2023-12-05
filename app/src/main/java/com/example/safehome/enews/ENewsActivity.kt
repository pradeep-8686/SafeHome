package com.example.safehome.enews

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.safehome.HomeActivity
import com.example.safehome.R
import com.example.safehome.Utils
import com.example.safehome.activity.BaseActivity
import com.example.safehome.adapter.YearAdapter
import com.example.safehome.custom.CustomProgressDialog
import com.example.safehome.databinding.ActivityENewsBinding
import com.example.safehome.repository.APIClient
import com.example.safehome.repository.APIInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ENewsActivity : BaseActivity() {
    private lateinit var eNewsUserListCall: Call<UserENewsListModel>
    private var eNewsUsersList: ArrayList<UserENewsListModel.Data.ENew> = ArrayList()
    private lateinit var binding: ActivityENewsBinding
    private lateinit var customProgressDialog: CustomProgressDialog
    private lateinit var apiInterface: APIInterface
    var User_Id: String? = ""
    var Auth_Token: String? = ""
    var ScreenFrom: String? = ""
    private var yearList: ArrayList<String> = ArrayList()
    private var monthList: ArrayList<String> = ArrayList()
    private var yearPopupWindow: PopupWindow? = null
    private var monthPopupWindow: PopupWindow? = null
    private var year: String = "2023"
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityENewsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        inIt()

    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun inIt() {
        ScreenFrom = intent.getStringExtra("ScreenFrom")
        // progressbar
        customProgressDialog = CustomProgressDialog()
        apiInterface = APIClient.getClient(this)
        User_Id = Utils.getStringPref(this, "User_Id", "")
        Auth_Token = Utils.getStringPref(this, "Token", "")
        binding.backBtnClick.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            intent.putExtra("ScreenFrom", ScreenFrom)
            startActivity(intent)
            finish()
        }
        addYearList()
        addMonthsList()
        getUserEnewsListApiCall(year, "")
        binding.yearCl.setOnClickListener {
            if (monthPopupWindow != null) {
                if (monthPopupWindow!!.isShowing) {
                    monthPopupWindow!!.dismiss()
                }
            }

            if (yearPopupWindow != null) {
                if (yearPopupWindow!!.isShowing) {
                    yearPopupWindow!!.dismiss()
                } else {
                    yearDropDown()
                }
            } else {
                yearDropDown()
            }
        }

        binding.clMonth.setOnClickListener {
            if (yearPopupWindow != null) {
                if (yearPopupWindow!!.isShowing) {
                    yearPopupWindow!!.dismiss()
                }
            }

            if (monthPopupWindow != null) {
                if (monthPopupWindow!!.isShowing) {
                    monthPopupWindow!!.dismiss()
                } else {
                    monthlyDropDown()
                }
            } else {
                monthlyDropDown()
            }
        }


    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun getUserEnewsListApiCall(year: String, month: String) {
        customProgressDialog.progressDialogShow(this@ENewsActivity, this.getString(R.string.loading))
        eNewsUserListCall = apiInterface.getAllUserENewsDetails("bearer "+Auth_Token, User_Id!!,
            "", this.year, "", "", "1", "10")
        eNewsUserListCall.enqueue(object: Callback<UserENewsListModel> {
            override fun onResponse(
                call: Call<UserENewsListModel>,
                response: Response<UserENewsListModel>
            ) {
                if (response.isSuccessful && response.body()!= null){

                    customProgressDialog.progressDialogDismiss()
                    if (response.body()!!.statusCode!= null){
                        when(response.body()!!.statusCode){
                            1 -> {
                                if (eNewsUsersList.isNotEmpty()){
                                    eNewsUsersList.clear()
                                }
                                if (response.body()!!.message != null && response.body()!!.message.isNotEmpty()) {
                              //      Utils.showToast(this@ENewsActivity, response.body()!!.message.toString())
                                    eNewsUsersList = response.body()!!.data.eNews as ArrayList<UserENewsListModel.Data.ENew>
                                    populateData(eNewsUsersList)
                                }
                            }
                            else -> {
                                if (response.body()!!.message != null && response.body()!!.message.isNotEmpty()) {
                                    Utils.showToast(this@ENewsActivity, response.body()!!.message.toString())
                                }
                            }
                        }
                    }
                }else{

                    customProgressDialog.progressDialogDismiss()
                }
            }

            override fun onFailure(call: Call<UserENewsListModel>, t: Throwable) {
                customProgressDialog.progressDialogDismiss()
                Utils.showToast(this@ENewsActivity, t.message.toString())

            }

        })
    }

    private fun populateData(eNewsUsersList: ArrayList<UserENewsListModel.Data.ENew>) {
        if (eNewsUsersList.size == 0) {
            binding.emptyEnewsTxt.visibility = View.VISIBLE
        } else {
            binding.emptyEnewsTxt.visibility = View.GONE
            binding.eNewsRecycler.layoutManager = LinearLayoutManager(this)
            val eNewsAdapter =
                ENewsAdapter(this@ENewsActivity, eNewsUsersList)
            binding.eNewsRecycler.adapter = eNewsAdapter
            eNewsAdapter.setCallback(this@ENewsActivity)
            eNewsAdapter.notifyDataSetChanged()
        }
    }

    private fun monthlyDropDown() {
        val layoutInflater: LayoutInflater =
         getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view: View = layoutInflater.inflate(R.layout.drop_down_layout, null)
        monthPopupWindow = PopupWindow(
            view,
            binding.tvMonth.measuredWidth,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        if (monthList.isNotEmpty()) {

            val linearLayoutManager = LinearLayoutManager(this@ENewsActivity)
            val dropDownRecyclerView = view.findViewById<RecyclerView>(R.id.drop_down_recycler_view)
            dropDownRecyclerView.layoutManager = linearLayoutManager
            val monthlyAdapter = MonthlyAdapter(this@ENewsActivity, monthList)

            dropDownRecyclerView.adapter = monthlyAdapter
            monthlyAdapter.setCallbackType(this@ENewsActivity)
        }
        monthPopupWindow!!.elevation = 10F
        monthPopupWindow!!.showAsDropDown(binding.tvMonth, 0, 0, Gravity.CENTER)
    }


    private fun yearDropDown() {
        val layoutInflater: LayoutInflater =
            getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view: View = layoutInflater.inflate(R.layout.drop_down_layout, null)

        yearPopupWindow = PopupWindow(
            view,
            binding.yearTxt.measuredWidth,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        if (yearList.isNotEmpty()) {

            val linearLayoutManager = LinearLayoutManager(this@ENewsActivity)
            val dropDownRecyclerView = view.findViewById<RecyclerView>(R.id.drop_down_recycler_view)
            dropDownRecyclerView.layoutManager = linearLayoutManager
            val yearAdapter = YearAdapter(this@ENewsActivity, yearList)

            dropDownRecyclerView.adapter = yearAdapter
            yearAdapter.setEnewCallback(this@ENewsActivity)
        }
        yearPopupWindow!!.elevation = 10F
        yearPopupWindow!!.showAsDropDown(binding.yearTxt, 0, 0, Gravity.CENTER)
    }


    private fun addMonthsList() {
        monthList.add("January")
        monthList.add("February")
        monthList.add("March")
        monthList.add("April")
        monthList.add("May")
        monthList.add("June")
        monthList.add("July")
        monthList.add("August")
        monthList.add("September")
        monthList.add("October")
        monthList.add("November")
        monthList.add("December")
    }

    private fun addYearList() {
        yearList.add("2023")
        yearList.add("2022")
        yearList.add("2021")
        yearList.add("2020")
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun setMonth(month: String) {
        if (monthPopupWindow != null) {
            if (monthPopupWindow!!.isShowing) {
                monthPopupWindow!!.dismiss()
            }
        }
        if (month != null) {
            binding.tvMonth.text = month
            if(!binding.yearTxt.toString().isNullOrEmpty()) {
                getUserEnewsListApiCall(binding.yearTxt.toString(), binding.tvMonth.toString())
            }else{
                getUserEnewsListApiCall("2023", binding.tvMonth.toString())
            }

        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun setEnewsYear(year: String) {
        if (yearPopupWindow != null) {
            if (yearPopupWindow!!.isShowing) {
                yearPopupWindow!!.dismiss()
            }
        }
        if (year != null) {
            binding.yearTxt.text = year
            getUserEnewsListApiCall(binding.yearTxt.toString(), "")
        }
    }

    @SuppressLint("SuspiciousIndentation")
    fun itemClick(eNewItem: UserENewsListModel.Data.ENew) {
      val intent = Intent(this@ENewsActivity, ENewsDetailActivity::class.java)
        intent.putExtra("eNewItem", eNewItem)
        startActivity(intent)
    }
}