package com.example.safehome.services

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.safehome.R
import com.example.safehome.Utils
import com.example.safehome.adapter.YearAdapter
import com.example.safehome.custom.CustomProgressDialog
import com.example.safehome.databinding.FragmentServicesPaymentHistoryBinding
import com.example.safehome.model.DailyHelpMemberList
import com.example.safehome.model.GetAllHistoryServiceTypes
import com.example.safehome.model.ServiceTypesList
import com.example.safehome.model.ServicesStaffHistoryList
import com.example.safehome.model.YearModel
import com.example.safehome.repository.APIClient
import com.example.safehome.repository.APIInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Locale


/**
 * A simple [Fragment] subclass.
 * Use the [ServicesPaymentHistoryFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ServicesPaymentHistoryFragment : Fragment() {
    private lateinit var yearModel : Call<YearModel>
    private var residentId: String?= null
    private lateinit var customProgressDialog: CustomProgressDialog
    private lateinit var apiInterface: APIInterface
    var Auth_Token: String? = ""
    private lateinit var binding: FragmentServicesPaymentHistoryBinding
    private var dailyHelpPaymentHistoryList: ArrayList<DailyHelpMemberList> = ArrayList()
    private lateinit var servicesPaymentHistoryAdapter: ServicesPaymentHistoryAdapter
    private var yearList: ArrayList<YearModel.Data> = ArrayList()
    private var serviceTypeList: ArrayList<GetAllHistoryServiceTypes.Data> = ArrayList()
    private var yearPopupWindow: PopupWindow? = null
    private var serviceTypePopupWindow: PopupWindow? = null
    private lateinit var servicePaymentHistoryCall: Call<ServicesStaffHistoryList>
    private lateinit var serviceHistoryTypesCall: Call<GetAllHistoryServiceTypes>
    private var servicesStaffHistoryList: ArrayList<ServicesStaffHistoryList.Data> = ArrayList()

    private var selectedStaff : Int? = null
    private var selectedYear : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentServicesPaymentHistoryBinding.inflate(inflater, container, false)

        inIt()

        binding.sampleEditText.setOnTouchListener { v, event ->
            if (yearPopupWindow != null) {
                if (yearPopupWindow!!.isShowing) {
                    yearPopupWindow!!.dismiss()
                }
            }
            if (serviceTypePopupWindow != null) {
                if (serviceTypePopupWindow!!.isShowing) {
                    serviceTypePopupWindow!!.dismiss()
                }
            }

            v?.onTouchEvent(event) ?: true
        }

        binding.sampleEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if (servicesStaffHistoryList.isNotEmpty()){

                    filter(p0.toString())
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })


        return binding.root
    }

    fun filter(text: String) {
        val servicesDataMemberList = ArrayList<ServicesStaffHistoryList.Data>()
        val courseAry: ArrayList<ServicesStaffHistoryList.Data> = servicesStaffHistoryList

        for (eachCourse in courseAry) {
            if (!eachCourse.staffName.isNullOrBlank() && eachCourse.staffName.lowercase(Locale.getDefault()).contains(text.lowercase(Locale.getDefault())) ||
                !eachCourse.staffTypeName.isNullOrBlank() && eachCourse.staffTypeName.lowercase(Locale.getDefault()).contains(text.lowercase(Locale.getDefault())) ||
                !eachCourse.paidDate.isNullOrBlank() && eachCourse.paidDate.lowercase(Locale.getDefault()).contains(text.lowercase(Locale.getDefault())) ||
                !eachCourse.paymentMode.isNullOrBlank() && eachCourse.paymentMode.lowercase(Locale.getDefault()).contains(text.lowercase(Locale.getDefault())) ||
                !eachCourse.amount.isNullOrBlank() && eachCourse.amount.lowercase(Locale.getDefault()).contains(text.lowercase(Locale.getDefault()))
            ) {
                servicesDataMemberList.add(eachCourse)
            }
        }
        servicesPaymentHistoryAdapter.filterList(servicesDataMemberList);
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun inIt() {
        customProgressDialog = CustomProgressDialog()
        apiInterface = APIClient.getClient(requireContext())
        residentId = Utils.getStringPref(requireContext(), "residentId", "")
        Auth_Token = Utils.getStringPref(requireContext(), "Token", "")

//        addDailyHelpMemberListData()
//        populateData()
        addYearList()
        addServiceTypeList()
        getServicesHistoryNetworkCall()
        binding.yearCl.setOnClickListener {
            if (serviceTypePopupWindow != null) {
                if (serviceTypePopupWindow!!.isShowing) {
                    serviceTypePopupWindow!!.dismiss()
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
        binding.serviceTypeCl.setOnClickListener {
            if (yearPopupWindow != null) {
                if (yearPopupWindow!!.isShowing) {
                    yearPopupWindow!!.dismiss()
                }
            }
            if (serviceTypePopupWindow != null) {
                if (serviceTypePopupWindow!!.isShowing) {
                    serviceTypePopupWindow!!.dismiss()
                } else {
                    serviceTypeDropDown()
                }
            } else {
                serviceTypeDropDown()
            }
        }    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun getServicesHistoryNetworkCall(staffId : Int? = null, year : String?= null) {
        customProgressDialog.progressDialogShow(
            requireContext(),
            this.getString(R.string.loading)
        )
//        val token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiI5OTg5NDU2NDU2IiwiZW1haWwiOiJhZG1pbkB5b3BtYWlsLmNvbSIsImp0aSI6ImIwODI3OGVkLTE0ZjMtNDdkNi04Y2Q5LWQ4NGM1YjU1MjFhYSIsInJvbGUiOiJBZG1pbiIsImNvbW11bml0eUlkIjoibFJqVk9JMzlLNDF2S2dzSVRjNVZPdz09IiwiY29tbXVuaXR5QWxpYXMiOiJjQ3RuTXU1bDNvTWtsOXJES25iNlZSdjlOalFVU1RVZEFINzFNTUdBcG5SRXlDbUtwdC9lS00rc0lwQlZMOWFCIiwiY29tbXVuaXR5TmFtZSI6IitBbWhITHZkSXlibmpOSVlicEFGUk5KSE1pMzEzb3BEN1lJNWh5dFR6cE9iaVVuT2padWd1V2M0eVdmdkJRa3giLCJmaXJzdE5hbWUiOiJINWRNTjExQk1TbXFNLzNoRE1PODdBPT0iLCJsYXN0TmFtZSI6Im40RnA0R1VIT0xQYnB0Yk11alVOL0E9PSIsInVzZXJJZCI6IjEiLCJuYmYiOjE2OTM0NDkwMjUsImV4cCI6MTcyNDk4NTAyNSwiaWF0IjoxNjkzNDQ5MDI1LCJpc3MiOiJTYWZlSG9tZS5jb20iLCJhdWQiOiJTYWZlSG9tZS5jb20ifQ.iTw2TQLFXOP_B7st5HrrT9FDI48ugqcu_KUknjlhnv4"

//        Auth_Token = token
        // here sign up service call
        servicePaymentHistoryCall = apiInterface.getStaffPaymentHistory(
            "Bearer " + Auth_Token, residentId = residentId, staffTypeId =  staffId.toString(), year = year
        )
        servicePaymentHistoryCall.enqueue(object : Callback<ServicesStaffHistoryList> {
            override fun onResponse(
                call: Call<ServicesStaffHistoryList>,
                response: Response<ServicesStaffHistoryList>
            ) {
                customProgressDialog.progressDialogDismiss()
                // here successfully response
                if (response.isSuccessful && response.body() != null) {
                    if (response.body()?.data != null && response.body()!!.data.isNotEmpty()) {
                        if (servicesStaffHistoryList.isNotEmpty()) {
                            servicesStaffHistoryList.clear()
                        }
                        servicesStaffHistoryList = response.body()!!.data as ArrayList<ServicesStaffHistoryList.Data>

                    } else {
                        // vehilceModelDropDown()
                    }
                    populateData()

                } else {
                    //  vehilceModelDropDown()
                }
            }

            override fun onFailure(call: Call<ServicesStaffHistoryList>, t: Throwable) {
                customProgressDialog.progressDialogDismiss()
                Utils.showToast(requireContext(), t.message.toString())
            }
        })
    }

    private fun addYearList() {
      /*  yearList.add("2023")
        yearList.add("2022")
        yearList.add("2021")
        yearList.add("2020")*/

        yearModel = apiInterface.yearList(
            "Bearer " + Auth_Token
        )
        yearModel.enqueue(object : Callback<YearModel> {
            override fun onResponse(
                call: Call<YearModel>,
                response: Response<YearModel>
            ) {
//                customProgressDialog.progressDialogDismiss()
                // here successfully response
                if (response.isSuccessful && response.body() != null) {
                    if (response.body()?.data != null && response.body()!!.data.isNotEmpty()) {
                        if (yearList.isNotEmpty()) {
                            yearList.clear()
                        }
                        val facilitiesModel = response.body() as YearModel
                        yearList = facilitiesModel.data as ArrayList<YearModel.Data>

                    } else {
                        // vehilceModelDropDown()
                    }
                } else {
                    //  vehilceModelDropDown()
                }
            }

            override fun onFailure(call: Call<YearModel>, t: Throwable) {
//                customProgressDialog.progressDialogDismiss()
                Utils.showToast(requireContext(), t.message.toString())
            }
        })

    }


    private fun addServiceTypeList() {
        /*serviceTypeList.add("AC Services & Repair")
        serviceTypeList.add("Maid")
*/
        customProgressDialog = CustomProgressDialog()
        serviceHistoryTypesCall = apiInterface.getAllServiceTypes("bearer "+Auth_Token)
        serviceHistoryTypesCall.enqueue(object : Callback<GetAllHistoryServiceTypes>{
            override fun onResponse(
                call: Call<GetAllHistoryServiceTypes>,
                response: Response<GetAllHistoryServiceTypes>
            ) {
                customProgressDialog.progressDialogDismiss()
                // here successfully response
                if (response.isSuccessful && response.body() != null) {

                    if (response.body()!!.statusCode!= null){
                        when(response.body()!!.statusCode){
                            1 -> {
                                if (response.body()!!.message != null && response.body()!!.message.isNotEmpty()) {
                                  //  Utils.showToast(requireContext(), response.body()!!.message.toString())
                                    if (serviceTypeList.isNotEmpty()){
                                        serviceTypeList.clear()
                                    }
                                    serviceTypeList = response.body()!!.data as ArrayList<GetAllHistoryServiceTypes.Data>
//                                    populateData()
                                }
                            }
                            else -> {
                                if (response.body()!!.message != null && response.body()!!.message.isNotEmpty()) {
                                    Utils.showToast(requireContext(), response.body()!!.message.toString())
                                }
                            }
                        }
                    }
                } else {
                    customProgressDialog.progressDialogDismiss()
                    Utils.showToast(requireContext(), response.body()!!.message.toString())
                }
            }

            override fun onFailure(call: Call<GetAllHistoryServiceTypes>, t: Throwable) {
                customProgressDialog.progressDialogDismiss()
                Utils.showToast(requireContext(), t.message.toString())
            }

        })

    }

    private fun addDailyHelpMemberListData() {
        dailyHelpPaymentHistoryList.add(
            DailyHelpMemberList(
                "Shanthi", "5 A.M-6 A.M", "Weekdays", "B104,B102",
                "9876543210", "4.1", "20/08/23", "", "", "", "AC Service&Repair"
            )
        )
        dailyHelpPaymentHistoryList.add(
            DailyHelpMemberList(
                "Suresh", "5 A.M-6 A.M", "Weekdays", "B104,B102",
                "9876543210", "4.1", "20/08/23", "", "", "", "AC Service&Repair"
            )
        )
        dailyHelpPaymentHistoryList.add(
            DailyHelpMemberList(
                "Harish", "5 A.M-6 A.M", "Weekend", "B104,B102",
                "9876543210", "4.1", "20/08/23", "", "", "", "AC Service&Repair"
            )
        )
    }

    private fun populateData() {
        if (servicesStaffHistoryList.size == 0){
            binding.emptyBookingFacilitiesTxt.visibility = View.VISIBLE
        }else {
            binding.emptyBookingFacilitiesTxt.visibility = View.GONE
            binding.dailyHelpPaymentHistoryRecyclerView.layoutManager = LinearLayoutManager(requireContext())
            servicesPaymentHistoryAdapter =
                ServicesPaymentHistoryAdapter(requireContext(), servicesStaffHistoryList)
            binding.dailyHelpPaymentHistoryRecyclerView.adapter = servicesPaymentHistoryAdapter
            servicesPaymentHistoryAdapter.setCallback(this@ServicesPaymentHistoryFragment)
        }


    }

    fun selectedPayment(dailyHelpMemberList: DailyHelpMemberList) {

    }

    private fun yearDropDown() {
        val layoutInflater: LayoutInflater =
            requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view: View = layoutInflater.inflate(R.layout.drop_down_layout, null)

        yearPopupWindow = PopupWindow(
            view,
            binding.yearTxt.measuredWidth,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        if (yearList.isNotEmpty()) {

            val linearLayoutManager = LinearLayoutManager(requireContext())
            val dropDownRecyclerView = view.findViewById<RecyclerView>(R.id.drop_down_recycler_view)
            dropDownRecyclerView.layoutManager = linearLayoutManager
            val yearsArray = yearList.map { it.year.toString() }
            val yearAdapter = YearAdapter(requireContext(), yearsArray)
            dropDownRecyclerView.adapter = yearAdapter
            yearAdapter.setCallbackServicesPaymentHistory(this@ServicesPaymentHistoryFragment)
        }
        yearPopupWindow!!.elevation = 10F
        yearPopupWindow!!.showAsDropDown(binding.yearTxt, 0, 0, Gravity.CENTER)
    }
    private fun serviceTypeDropDown() {
        val layoutInflater: LayoutInflater =
            requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view: View = layoutInflater.inflate(R.layout.drop_down_layout, null)

        serviceTypePopupWindow = PopupWindow(
            view,
            binding.serviveTypeTv.measuredWidth,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        if (serviceTypeList.isNotEmpty()) {

            val linearLayoutManager = LinearLayoutManager(requireContext())
            val dropDownRecyclerView = view.findViewById<RecyclerView>(R.id.drop_down_recycler_view)
            dropDownRecyclerView.layoutManager = linearLayoutManager
            val servicesTypeAdapter = ServicesTypeAdapter(requireContext(), serviceTypeList)

            dropDownRecyclerView.adapter = servicesTypeAdapter
            servicesTypeAdapter.setCallbackServiceType(this@ServicesPaymentHistoryFragment)
        }
        serviceTypePopupWindow!!.elevation = 10F
        serviceTypePopupWindow!!.showAsDropDown(binding.serviveTypeTv, 0, 0, Gravity.CENTER)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun setCallbackServicePaymentHistoryYear(year: String) {
        if (yearPopupWindow != null) {
            if (yearPopupWindow!!.isShowing) {
                yearPopupWindow!!.dismiss()
            }
        }
        if(year!= null){
            binding.yearTxt.text = year
            selectedYear = year

            getServicesHistoryNetworkCall(selectedStaff, selectedYear)

        }
    }

//    fun setCallbackServiceTypePaymentHistory(serviceType: String) {
//        if (serviceTypePopupWindow != null) {
//            if (serviceTypePopupWindow!!.isShowing) {
//                serviceTypePopupWindow!!.dismiss()
//            }
//        }
//        if(serviceType!= null){
//            binding.serviveTypeTv.text = serviceType
//        }
//    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (yearPopupWindow != null) {
            if (yearPopupWindow!!.isShowing) {
                yearPopupWindow!!.dismiss()
            }
        }else  super.onDestroyView()
        if (serviceTypePopupWindow != null) {
            if (serviceTypePopupWindow!!.isShowing) {
                serviceTypePopupWindow!!.dismiss()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun setServiceType(serviceType: GetAllHistoryServiceTypes.Data) {
        if (serviceTypePopupWindow != null) {
            if (serviceTypePopupWindow!!.isShowing) {
                serviceTypePopupWindow!!.dismiss()
            }
        }
        if(serviceType!= null){
            binding.serviveTypeTv.text = serviceType.serviceTypeName
            selectedStaff = serviceType.serviceTypeId

            getServicesHistoryNetworkCall(selectedStaff, selectedYear)

        }
    }

}