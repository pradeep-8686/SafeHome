package com.example.safehome.services

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.safehome.R
import com.example.safehome.Utils
import com.example.safehome.adapter.AvailablityTimeAdapter
import com.example.safehome.adapter.YearAdapter
import com.example.safehome.custom.CustomProgressDialog
import com.example.safehome.databinding.ActivityServicesMemberListBinding
import com.example.safehome.model.AvailabilityTime
import com.example.safehome.model.DailyHelpMemberList
import com.example.safehome.model.MaintenanceHistoryModel
import com.example.safehome.model.ServiceDataList
import com.example.safehome.model.ServiceProvidedTypesList
import com.example.safehome.repository.APIClient
import com.example.safehome.repository.APIInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Locale

class ServicesMemberListActivity : AppCompatActivity() {
    private var serviceTypeName: String? = null
    private var serviceTypeId: String? = null
    private var servicesDataList: ArrayList<ServiceDataList.Data> = ArrayList()
    private var Auth_token: String? = null
    private lateinit var apiInterface: APIInterface
    private lateinit var customProgressDialog: CustomProgressDialog
    private lateinit var binding: ActivityServicesMemberListBinding
    private var dailyHelpMembersList: ArrayList<DailyHelpMemberList> = ArrayList()
    private lateinit var servicesMemberListAdapter: ServicesMemberListAdapter
    private var availabiltyTimeList: ArrayList<AvailabilityTime> = ArrayList()
    private var availabilityTimePopWindow: PopupWindow? = null
    private var avilabilityOnList: ArrayList<String> = ArrayList()
    private var availabilityOnPopupWindow: PopupWindow? = null
    private var SelectedService: String? = ""
    private var SelectedAvailableOn: String? = ""
    private lateinit var getServicesDataCall: Call<ServiceDataList>

    private lateinit var serviceProvidedByTypeCall: Call<ServiceProvidedTypesList>
    private var sevicesProvidedTypesList: ArrayList<ServiceProvidedTypesList.Data> = ArrayList()


    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityServicesMemberListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        inIt()
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun inIt() {
        customProgressDialog = CustomProgressDialog()
        apiInterface = APIClient.getClient(this@ServicesMemberListActivity)
        Auth_token = Utils.getStringPref(this@ServicesMemberListActivity, "Token", "")!!

        serviceTypeId = intent.getIntExtra("serviceTypeId", 0).toString()
        Log.e("serviceTypeId", "" + serviceTypeId)
        serviceTypeName = intent.getStringExtra("serviceTypeName")
        binding.tittleTxt.setText(serviceTypeName)

//        addDailyHelpMemberListData()
//        populateData(servicesDataList)
//        addAvailableOnList()
        addAvailableTimeList()
        dropdowns()
        getServceDataNetworkCall(SelectedService, SelectedAvailableOn)
        selectProvideTypesNetworkCall()

        binding.backBtn.setOnClickListener {
            val intent = Intent(this, ServicesActivity::class.java)
            startActivity(intent)
            finish()
        }
        binding.sampleEditText.setOnTouchListener { v, event ->
            if (availabilityTimePopWindow != null) {
                if (availabilityTimePopWindow!!.isShowing) {
                    availabilityTimePopWindow!!.dismiss()
                }
            }
            if (availabilityOnPopupWindow != null) {
                if (availabilityOnPopupWindow!!.isShowing) {
                    availabilityOnPopupWindow!!.dismiss()
                }
            }

            v?.onTouchEvent(event) ?: true
        }

        binding.sampleEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if (servicesDataList.isNotEmpty()){

                        filter(p0.toString())
                }

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })


    }

    fun filter(text: String) {

        val servicesDataMemberList = ArrayList<ServiceDataList.Data>()
        val courseAry: ArrayList<ServiceDataList.Data> = servicesDataList

        for (eachCourse in courseAry) {
            var sortList = ArrayList<ServiceDataList.Data.ServiceProvide>()
/*
            var sortListString : String = ""
            if (eachCourse.serviceProvides != null && eachCourse.serviceProvides.isNotEmpty()) {
                for (model in eachCourse.serviceProvides){
                    if (model.serviceProvideName != null){
                        sortList.add(model)
                    }
                }
                sortListString = sortList.joinToString(", ") { it1 -> it1.serviceProvideName }
            }*/


            if (!eachCourse.personName.isNullOrEmpty() &&eachCourse.personName.lowercase(Locale.getDefault()).contains(text.lowercase(Locale.getDefault())) ||
                !eachCourse.companyName.isNullOrEmpty() &&eachCourse.companyName.lowercase(Locale.getDefault()).contains(text.lowercase(Locale.getDefault())) ||
                !eachCourse.availableOn.isNullOrEmpty() &&eachCourse.availableOn.lowercase(Locale.getDefault()).contains(text.lowercase(Locale.getDefault()))
//                !sortListString.isNullOrEmpty() && sortListString.lowercase(Locale.getDefault()).contains(text.lowercase(Locale.getDefault())) ||
            ) {
                servicesDataMemberList.add(eachCourse)
            }
        }

        servicesMemberListAdapter.filterList(servicesDataMemberList);
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun getServceDataNetworkCall(
        serviceProvideId: String? = "",
        availableOn: String? = ""
    ) {
        customProgressDialog.progressDialogShow(
            this@ServicesMemberListActivity,
            this.getString(R.string.loading)
        )
        getServicesDataCall = apiInterface.GetServiceDataByServiceType(
            "bearer " + Auth_token,
            serviceTypeId!!,
            serviceProvideId,
            availableOn
        )
        getServicesDataCall.enqueue(object : Callback<ServiceDataList> {
            override fun onResponse(
                call: Call<ServiceDataList>,
                response: Response<ServiceDataList>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    customProgressDialog.progressDialogDismiss()
                    if (response.body()!!.statusCode != null) {
                        when (response.body()!!.statusCode) {
                            1 -> {
                                if (response.body()!!.message != null && response.body()!!.message.isNotEmpty()) {
                                    if (servicesDataList.isNotEmpty()) {
                                        servicesDataList.clear()
                                    }
                                    servicesDataList =
                                        response.body()!!.data as ArrayList<ServiceDataList.Data>
                                    //   Utils.showToast(this@ServicesMemberListActivity, response.body()!!.message.toString())
                                    Log.e("servicesDataList", "" + servicesDataList)
                                }
                                populateData(servicesDataList)

                            }

                            else -> {
                                if (response.body()!!.message != null && response.body()!!.message.isNotEmpty()) {
                                    Utils.showToast(
                                        this@ServicesMemberListActivity,
                                        response.body()!!.message.toString()
                                    )
                                }
                            }
                        }
                    }
                }
            }

            override fun onFailure(call: Call<ServiceDataList>, t: Throwable) {
                customProgressDialog.progressDialogDismiss()
                Utils.showToast(this@ServicesMemberListActivity, t.message.toString())
            }

        })
    }

    private fun addAvailableTimeList() {
        availabiltyTimeList.add(AvailabilityTime("Weekdays"))
        availabiltyTimeList.add(AvailabilityTime("Weekends"))
        availabiltyTimeList.add(AvailabilityTime("AllDays"))

    }

    private fun addAvailableOnList() {
        avilabilityOnList.add("Install&UnIstall")
        avilabilityOnList.add("Servicing")
        avilabilityOnList.add("Deep Clean Services")

        SelectedService = avilabilityOnList[0].toString()

    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun dropdowns() {
        binding.serviceProvideCl.setOnClickListener {
            if (availabilityOnPopupWindow != null) {
                if (availabilityOnPopupWindow!!.isShowing) {
                    availabilityOnPopupWindow!!.dismiss()
                } else {
                    availabilityOnDropDown()
                }
            } else {
                availabilityOnDropDown()

            }
        }

        binding.availableTimeCl.setOnClickListener {
            if (availabilityTimePopWindow != null) {
                if (availabilityTimePopWindow!!.isShowing) {
                    availabilityTimePopWindow!!.dismiss()
                } else {

                    if (availabiltyTimeList.isNotEmpty()) {

                        availabilityTimeDropDown()
                    }
                }
            } else {
                if (availabiltyTimeList.isNotEmpty()) {

                    availabilityTimeDropDown()
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun selectProvideTypesNetworkCall() {
        /*  val customProgressDialog = CustomProgressDialog()
          customProgressDialog.progressDialogShow(
              this@ServicesMemberListActivity,
              this.getString(R.string.loading)
          )*/
        serviceProvidedByTypeCall = apiInterface.getServiceProvidedByServiceType(
            "bearer " + Auth_token,
            serviceTypeId!!.toInt()
        )
        serviceProvidedByTypeCall.enqueue(object : Callback<ServiceProvidedTypesList> {
            override fun onResponse(
                call: Call<ServiceProvidedTypesList>,
                response: Response<ServiceProvidedTypesList>
            ) {
                if (response.isSuccessful && response.body() != null) {
//                    customProgressDialog.progressDialogDismiss()
                    if (response.body()!!.statusCode != null) {
                        when (response.body()!!.statusCode) {
                            1 -> {
                                if (response.body()!!.message != null && response.body()!!.message.isNotEmpty()) {
                                    // Utils.showToast(this@ServiceSelectedMemberInfoActivity, response.body()!!.message.toString())
                                    sevicesProvidedTypesList =
                                        response.body()!!.data as ArrayList<ServiceProvidedTypesList.Data>


                                }
                            }

                            else -> {
                                if (response.body()!!.message != null && response.body()!!.message.isNotEmpty()) {
                                    Utils.showToast(
                                        this@ServicesMemberListActivity,
                                        response.body()!!.message.toString()
                                    )
                                }
                            }
                        }
                    }
                } else {
//                    customProgressDialog.progressDialogDismiss()
                    Utils.showToast(
                        this@ServicesMemberListActivity,
                        response.body()!!.message.toString()
                    )
                }
            }

            override fun onFailure(call: Call<ServiceProvidedTypesList>, t: Throwable) {
                customProgressDialog.progressDialogDismiss()
                Utils.showToast(this@ServicesMemberListActivity, t.message.toString())
            }

        })
    }

    //availableOn
    private fun availabilityTimeDropDown() {
        val layoutInflater: LayoutInflater =
            getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view: View = layoutInflater.inflate(R.layout.drop_down_layout, null)

        availabilityTimePopWindow = PopupWindow(
            view,
            binding.availableOnTxt.measuredWidth,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        if (availabiltyTimeList.isNotEmpty()) {
            val linearLayoutManager = LinearLayoutManager(this)
            val dropDownRecyclerView = view.findViewById<RecyclerView>(R.id.drop_down_recycler_view)
            dropDownRecyclerView.layoutManager = linearLayoutManager
            val availableTimeAdapter = AvailablityTimeAdapter(this, availabiltyTimeList)
            dropDownRecyclerView.adapter = availableTimeAdapter
            availableTimeAdapter.setCallbackServicesMemberListActivity(this@ServicesMemberListActivity)
        }
        availabilityTimePopWindow!!.elevation = 10F
        availabilityTimePopWindow!!.showAsDropDown(binding.availableOnTxt, 0, 0, Gravity.CENTER)
    }

    private fun availabilityOnDropDown() {
        val layoutInflater: LayoutInflater =
            getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view: View = layoutInflater.inflate(R.layout.drop_down_layout, null)

        availabilityOnPopupWindow = PopupWindow(
            view,
            binding.serviceProvTxt.measuredWidth,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        if (sevicesProvidedTypesList.isNotEmpty()) {
            val linearLayoutManager = LinearLayoutManager(this)
            val dropDownRecyclerView = view.findViewById<RecyclerView>(R.id.drop_down_recycler_view)
            dropDownRecyclerView.layoutManager = linearLayoutManager
            val servicesProvidedTypesListAdapter =
                ServicesProvidedTypesListAdapter(this, sevicesProvidedTypesList)
            dropDownRecyclerView.adapter = servicesProvidedTypesListAdapter
            servicesProvidedTypesListAdapter.setCallbackServiceTypeService(this@ServicesMemberListActivity)
        }
        availabilityOnPopupWindow!!.elevation = 10F
        availabilityOnPopupWindow!!.showAsDropDown(binding.serviceProvTxt, 0, 0, Gravity.CENTER)
    }

    private fun addDailyHelpMemberListData() {
        dailyHelpMembersList.add(
            DailyHelpMemberList(
                "Raj", "5 A.M-6 A.M", "Weekdays", "B104,B102",
                "9876543210", "4.1", "", "", "", "", ""
            )
        )
        dailyHelpMembersList.add(
            DailyHelpMemberList(
                "Suresh", "5 A.M-6 A.M", "Weekdays", "B104,B102",
                "9876543210", "4.1", "", "", "", "", ""
            )
        )
        dailyHelpMembersList.add(
            DailyHelpMemberList(
                "Harish", "5 A.M-6 A.M", "Weekend", "B104,B102",
                "9876543210", "4.1", "", "", "", "", ""
            )
        )
    }

    private fun populateData(servicesDataList: ArrayList<ServiceDataList.Data>) {

        if (servicesDataList.size == 0){
            binding.emptyBookingFacilitiesTxt.visibility = View.VISIBLE
        }else {
            binding.emptyBookingFacilitiesTxt.visibility = View.GONE
            binding.dailyHelpMemberListRecyclerView.layoutManager = LinearLayoutManager(this)
            servicesMemberListAdapter = ServicesMemberListAdapter(this, servicesDataList)
            binding.dailyHelpMemberListRecyclerView.adapter = servicesMemberListAdapter
            servicesMemberListAdapter.setCallback(this@ServicesMemberListActivity)
        }


    }

    fun selectedMember(servicesMemberList: ServiceDataList.Data) {
        val eIntent = Intent(this, ServiceSelectedMemberInfoActivity::class.java)
        eIntent.putExtra("serviceTypeName", serviceTypeName)
        eIntent.putExtra("servicesMemberList", servicesMemberList)
        eIntent.putExtra("ServiceSelectedSpinner", SelectedService)
        eIntent.putExtra("availabilityOn", binding.availableOnTxt.text.toString())
        eIntent.putExtra("serviceTypeId", serviceTypeId)
        eIntent.putExtra("from", "booking")
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        startActivity(eIntent)
        finish()
    }

    override fun onBackPressed() {
        val intent = Intent(this, ServicesActivity::class.java)
        startActivity(intent)
        finish()
    }

    // service provide
    fun setCallbackServiceListActivity(avilabilityOn: String) {
        if (availabilityOnPopupWindow != null) {
            if (availabilityOnPopupWindow!!.isShowing) {
                availabilityOnPopupWindow!!.dismiss()
            }
        }

        if (avilabilityOn != null) {
            binding.serviceProvTxt.text = avilabilityOn
            SelectedService = avilabilityOn
        }
    }

    // available on
    @RequiresApi(Build.VERSION_CODES.Q)
    fun setCallbackDailyHelpAvilabilityTime(availabilityTime: AvailabilityTime) {
        if (availabilityTimePopWindow != null) {
            if (availabilityTimePopWindow!!.isShowing) {
                availabilityTimePopWindow!!.dismiss()
            }
        }

//        for (availabiltyTimeList in availabiltyTimeList) {
//            availabiltyTimeList.add(AvailabilityTime("5A.M - 6A.M", "false"))
//        }
        if (availabilityTime != null) {
            binding.availableOnTxt.text = availabilityTime.Time
            SelectedAvailableOn = availabilityTime.Time
            getServceDataNetworkCall(SelectedService, SelectedAvailableOn)


        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun setCallbackServiceTypeService(data: ServiceProvidedTypesList.Data) {
        if (availabilityOnPopupWindow != null) {
            if (availabilityOnPopupWindow!!.isShowing) {
                availabilityOnPopupWindow!!.dismiss()
            }
        }

        if (data != null) {
            binding.serviceProvTxt.text = data.serviceProvideName
            SelectedService = data.serviceProvideId.toString()
            getServceDataNetworkCall(SelectedService, SelectedAvailableOn)

        }
    }


}