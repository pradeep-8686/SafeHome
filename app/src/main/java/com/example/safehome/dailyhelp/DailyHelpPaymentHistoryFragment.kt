package com.example.safehome.dailyhelp

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
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
import com.example.safehome.databinding.FragmentDailyHelpBookingBinding
import com.example.safehome.databinding.FragmentDailyHelpPaymentHistoryBinding
import com.example.safehome.model.CategoryModel
import com.example.safehome.model.DailyHelpHistoryModel
import com.example.safehome.model.DailyHelpMemberList
import com.example.safehome.model.FaciBookings
import com.example.safehome.model.StaffModel
import com.example.safehome.model.YearModel
import com.example.safehome.repository.APIClient
import com.example.safehome.repository.APIInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.Year
import java.util.Locale


/**
 * A simple [Fragment] subclass.
 * Use the [DailyHelpPaymentHistoryFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DailyHelpPaymentHistoryFragment : Fragment() {
   private lateinit var binding: FragmentDailyHelpPaymentHistoryBinding
    private var dailyHelpPaymentHistoryList: ArrayList<DailyHelpHistoryModel.Data> = ArrayList()
    private lateinit var dailyHelpPaymentHistoryAdapter: DailyHelpPaymentHistoryAdapter
    private var yearList: ArrayList<YearModel.Data> = ArrayList()
    private var staffListList: ArrayList<StaffModel.Data> = ArrayList()
    private var yearPopupWindow: PopupWindow? = null
    private var staffPopupWindow: PopupWindow? = null
    private lateinit var customProgressDialog: CustomProgressDialog
    private lateinit var apiInterface: APIInterface
    var User_Id: String? = ""
    var Auth_Token: String? = ""
    private lateinit var dailyHelpHistoryModel : Call<DailyHelpHistoryModel>
    private lateinit var yearModel : Call<YearModel>
    private lateinit var staffModel : Call<StaffModel>
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
        binding = FragmentDailyHelpPaymentHistoryBinding.inflate(inflater, container, false)

        customProgressDialog = CustomProgressDialog()
        apiInterface = APIClient.getClient(requireContext())
        User_Id = Utils.getStringPref(requireContext(), "residentId", "")
        Auth_Token = Utils.getStringPref(requireContext(), "Token", "")

//        populateData()
        addYearList()
        staffList()

        binding.yearCl.setOnClickListener {
            if (staffPopupWindow != null) {
                if (staffPopupWindow!!.isShowing) {
                    staffPopupWindow!!.dismiss()
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
        binding.staffSpinner.setOnClickListener {
            if (yearPopupWindow != null) {
                if (yearPopupWindow!!.isShowing) {
                    yearPopupWindow!!.dismiss()
                }
            }
            if (staffPopupWindow != null) {
                if (staffPopupWindow!!.isShowing) {
                    staffPopupWindow!!.dismiss()
                } else {
                    staffDropDown()
                }
            } else {
                staffDropDown()
            }
        }

        binding.sampleEditText.setOnTouchListener { v, event ->
            if (yearPopupWindow != null) {
                if (yearPopupWindow!!.isShowing) {
                    yearPopupWindow!!.dismiss()
                }
            }
            if (staffPopupWindow != null) {
                if (staffPopupWindow!!.isShowing) {
                    staffPopupWindow!!.dismiss()
                }
            }

            v?.onTouchEvent(event) ?: true
        }

        binding.sampleEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if (dailyHelpPaymentHistoryList.isNotEmpty()) {
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

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun addYearList() {

        //progressbar
/*        customProgressDialog.progressDialogShow(
            requireContext(),
            this.getString(R.string.loading)
        )*/
//        val token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiI5OTg5NDU2NDU2IiwiZW1haWwiOiJhZG1pbkB5b3BtYWlsLmNvbSIsImp0aSI6ImIwODI3OGVkLTE0ZjMtNDdkNi04Y2Q5LWQ4NGM1YjU1MjFhYSIsInJvbGUiOiJBZG1pbiIsImNvbW11bml0eUlkIjoibFJqVk9JMzlLNDF2S2dzSVRjNVZPdz09IiwiY29tbXVuaXR5QWxpYXMiOiJjQ3RuTXU1bDNvTWtsOXJES25iNlZSdjlOalFVU1RVZEFINzFNTUdBcG5SRXlDbUtwdC9lS00rc0lwQlZMOWFCIiwiY29tbXVuaXR5TmFtZSI6IitBbWhITHZkSXlibmpOSVlicEFGUk5KSE1pMzEzb3BEN1lJNWh5dFR6cE9iaVVuT2padWd1V2M0eVdmdkJRa3giLCJmaXJzdE5hbWUiOiJINWRNTjExQk1TbXFNLzNoRE1PODdBPT0iLCJsYXN0TmFtZSI6Im40RnA0R1VIT0xQYnB0Yk11alVOL0E9PSIsInVzZXJJZCI6IjEiLCJuYmYiOjE2OTM0NDkwMjUsImV4cCI6MTcyNDk4NTAyNSwiaWF0IjoxNjkzNDQ5MDI1LCJpc3MiOiJTYWZlSG9tZS5jb20iLCJhdWQiOiJTYWZlSG9tZS5jb20ifQ.iTw2TQLFXOP_B7st5HrrT9FDI48ugqcu_KUknjlhnv4"

//        Auth_Token = token
        // here sign up service call
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

                        binding.yearTxt.text = yearList[0].year.toString()
                        selectedYear = yearList[0].year.toString()

                        addDailyHelpMemberListData(year = selectedYear)

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
    @RequiresApi(Build.VERSION_CODES.Q)
    private fun staffList() {

        //progressbar
        /*        customProgressDialog.progressDialogShow(
                    requireContext(),
                    this.getString(R.string.loading)
                )*/
//        val token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiI5OTg5NDU2NDU2IiwiZW1haWwiOiJhZG1pbkB5b3BtYWlsLmNvbSIsImp0aSI6ImIwODI3OGVkLTE0ZjMtNDdkNi04Y2Q5LWQ4NGM1YjU1MjFhYSIsInJvbGUiOiJBZG1pbiIsImNvbW11bml0eUlkIjoibFJqVk9JMzlLNDF2S2dzSVRjNVZPdz09IiwiY29tbXVuaXR5QWxpYXMiOiJjQ3RuTXU1bDNvTWtsOXJES25iNlZSdjlOalFVU1RVZEFINzFNTUdBcG5SRXlDbUtwdC9lS00rc0lwQlZMOWFCIiwiY29tbXVuaXR5TmFtZSI6IitBbWhITHZkSXlibmpOSVlicEFGUk5KSE1pMzEzb3BEN1lJNWh5dFR6cE9iaVVuT2padWd1V2M0eVdmdkJRa3giLCJmaXJzdE5hbWUiOiJINWRNTjExQk1TbXFNLzNoRE1PODdBPT0iLCJsYXN0TmFtZSI6Im40RnA0R1VIT0xQYnB0Yk11alVOL0E9PSIsInVzZXJJZCI6IjEiLCJuYmYiOjE2OTM0NDkwMjUsImV4cCI6MTcyNDk4NTAyNSwiaWF0IjoxNjkzNDQ5MDI1LCJpc3MiOiJTYWZlSG9tZS5jb20iLCJhdWQiOiJTYWZlSG9tZS5jb20ifQ.iTw2TQLFXOP_B7st5HrrT9FDI48ugqcu_KUknjlhnv4"

//        Auth_Token = token
        // here sign up service call
        staffModel = apiInterface.staffList(
            "Bearer " + Auth_Token
        )
        staffModel.enqueue(object : Callback<StaffModel> {
            override fun onResponse(
                call: Call<StaffModel>,
                response: Response<StaffModel>
            ) {
//                customProgressDialog.progressDialogDismiss()
                // here successfully response
                if (response.isSuccessful && response.body() != null) {
                    if (response.body()?.data != null && response.body()!!.data.isNotEmpty()) {
                        if (staffListList.isNotEmpty()) {
                            staffListList.clear()
                        }
                        val facilitiesModel = response.body() as StaffModel
                        staffListList = facilitiesModel.data as ArrayList<StaffModel.Data>

                    } else {
                        // vehilceModelDropDown()
                    }
                } else {
                    //  vehilceModelDropDown()
                }
            }

            override fun onFailure(call: Call<StaffModel>, t: Throwable) {
//                customProgressDialog.progressDialogDismiss()
                Utils.showToast(requireContext(), t.message.toString())
            }
        })

    }


    fun filter(text: String) {

        val myDuesList = ArrayList<DailyHelpHistoryModel.Data>()

        val courseAry: ArrayList<DailyHelpHistoryModel.Data> = dailyHelpPaymentHistoryList

        for (eachCourse in courseAry) {
            if (!eachCourse.staffName.isNullOrBlank() && eachCourse.staffName.lowercase(Locale.getDefault()).contains(text.lowercase(Locale.getDefault())) ||
                !eachCourse.staffTypeName.isNullOrBlank() && eachCourse.staffTypeName.lowercase(Locale.getDefault()).contains(text.lowercase(Locale.getDefault())) ||
                !eachCourse.paymentMode.isNullOrBlank() && eachCourse.paymentMode.lowercase(Locale.getDefault()).contains(text.lowercase(Locale.getDefault())) ||
                !eachCourse.paidDate.isNullOrBlank() && eachCourse.paidDate.lowercase(Locale.getDefault()).contains(text.lowercase(Locale.getDefault()))
            ) {
                myDuesList.add(eachCourse)
            }
        }

        dailyHelpPaymentHistoryAdapter.filterList(myDuesList);
    }



    @RequiresApi(Build.VERSION_CODES.Q)
    private fun addDailyHelpMemberListData(staffId : Int? = null, year : String?= null) {
        //progressbar
        customProgressDialog.progressDialogShow(
            requireContext(),
            this.getString(R.string.loading)
        )
//        val token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiI5OTg5NDU2NDU2IiwiZW1haWwiOiJhZG1pbkB5b3BtYWlsLmNvbSIsImp0aSI6ImIwODI3OGVkLTE0ZjMtNDdkNi04Y2Q5LWQ4NGM1YjU1MjFhYSIsInJvbGUiOiJBZG1pbiIsImNvbW11bml0eUlkIjoibFJqVk9JMzlLNDF2S2dzSVRjNVZPdz09IiwiY29tbXVuaXR5QWxpYXMiOiJjQ3RuTXU1bDNvTWtsOXJES25iNlZSdjlOalFVU1RVZEFINzFNTUdBcG5SRXlDbUtwdC9lS00rc0lwQlZMOWFCIiwiY29tbXVuaXR5TmFtZSI6IitBbWhITHZkSXlibmpOSVlicEFGUk5KSE1pMzEzb3BEN1lJNWh5dFR6cE9iaVVuT2padWd1V2M0eVdmdkJRa3giLCJmaXJzdE5hbWUiOiJINWRNTjExQk1TbXFNLzNoRE1PODdBPT0iLCJsYXN0TmFtZSI6Im40RnA0R1VIT0xQYnB0Yk11alVOL0E9PSIsInVzZXJJZCI6IjEiLCJuYmYiOjE2OTM0NDkwMjUsImV4cCI6MTcyNDk4NTAyNSwiaWF0IjoxNjkzNDQ5MDI1LCJpc3MiOiJTYWZlSG9tZS5jb20iLCJhdWQiOiJTYWZlSG9tZS5jb20ifQ.iTw2TQLFXOP_B7st5HrrT9FDI48ugqcu_KUknjlhnv4"

//        Auth_Token = token
        // here sign up service call
        dailyHelpHistoryModel = apiInterface.getDailyHelpHistory(
            "Bearer " + Auth_Token, staffId, year
        )
        dailyHelpHistoryModel.enqueue(object : Callback<DailyHelpHistoryModel> {
            override fun onResponse(
                call: Call<DailyHelpHistoryModel>,
                response: Response<DailyHelpHistoryModel>
            ) {
                customProgressDialog.progressDialogDismiss()
                // here successfully response
                if (response.isSuccessful && response.body() != null) {
                    if (response.body()?.data != null && response.body()!!.data.isNotEmpty()) {
                        if (dailyHelpPaymentHistoryList.isNotEmpty()) {
                            dailyHelpPaymentHistoryList.clear()
                        }
                        val facilitiesModel = response.body() as DailyHelpHistoryModel
                        dailyHelpPaymentHistoryList = facilitiesModel.data as ArrayList<DailyHelpHistoryModel.Data>

                    } else {
                        if (dailyHelpPaymentHistoryList.isNotEmpty()) {
                            dailyHelpPaymentHistoryList.clear()
                        }
                        // vehilceModelDropDown()
                    }
                    populateData()

                } else {
                    //  vehilceModelDropDown()
                }
            }

            override fun onFailure(call: Call<DailyHelpHistoryModel>, t: Throwable) {
                customProgressDialog.progressDialogDismiss()
                Utils.showToast(requireContext(), t.message.toString())
            }
        })
    }

    private fun populateData() {
        if (dailyHelpPaymentHistoryList.size == 0){
            binding.emptyEventsTxt.visibility = View.VISIBLE
        }else {
            binding.emptyEventsTxt.visibility = View.GONE

            binding.dailyHelpPaymentHistoryRecyclerView.layoutManager =
                LinearLayoutManager(requireContext())
            dailyHelpPaymentHistoryAdapter =
                DailyHelpPaymentHistoryAdapter(requireContext(), dailyHelpPaymentHistoryList)
            binding.dailyHelpPaymentHistoryRecyclerView.adapter = dailyHelpPaymentHistoryAdapter
            dailyHelpPaymentHistoryAdapter.setCallback(this@DailyHelpPaymentHistoryFragment)
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
            yearAdapter.setCallbackPaymentHistory(this@DailyHelpPaymentHistoryFragment)
        }
        yearPopupWindow!!.elevation = 10F
        yearPopupWindow!!.showAsDropDown(binding.yearTxt, 0, 0, Gravity.CENTER)
    }

    private fun staffDropDown() {
        val layoutInflater: LayoutInflater =
            requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view: View = layoutInflater.inflate(R.layout.drop_down_layout, null)

        staffPopupWindow = PopupWindow(
            view,
            binding.yearTxt.measuredWidth,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        if (yearList.isNotEmpty()) {

            val linearLayoutManager = LinearLayoutManager(requireContext())
            val dropDownRecyclerView = view.findViewById<RecyclerView>(R.id.drop_down_recycler_view)
            dropDownRecyclerView.layoutManager = linearLayoutManager
            val staffArray = staffListList.map { it.staffTypeName.toString() }
            val staffAdapter = StaffAdapter(requireContext(), staffArray)

            dropDownRecyclerView.adapter = staffAdapter
            staffAdapter.setCallbackStaff(this@DailyHelpPaymentHistoryFragment)
        }
        staffPopupWindow!!.elevation = 10F
        staffPopupWindow!!.showAsDropDown(binding.staffTv, 0, 0, Gravity.CENTER)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun setCallbackPaymentHistory(year: String) {
        if (yearPopupWindow != null) {
            if (yearPopupWindow!!.isShowing) {
                yearPopupWindow!!.dismiss()
            }
        }
        if(year!= null){
            binding.yearTxt.text = year
            selectedYear = year
            addDailyHelpMemberListData(selectedStaff, selectedYear)

        }
    }
    @RequiresApi(Build.VERSION_CODES.Q)
    fun setCallbackStaff(staff: String, position : Int) {
        if (staffPopupWindow != null) {
            if (staffPopupWindow!!.isShowing) {
                staffPopupWindow!!.dismiss()
            }
        }
        if(staff!= null){
            binding.staffTv.text = staff
            selectedStaff = staffListList[position].staffTypeId
            addDailyHelpMemberListData(selectedStaff, selectedYear)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (yearPopupWindow != null) {
            if (yearPopupWindow!!.isShowing) {
                yearPopupWindow!!.dismiss()
            }
        }else if(staffPopupWindow != null){
            if (staffPopupWindow!!.isShowing) {
                staffPopupWindow!!.dismiss()
            }
        }
    }

}