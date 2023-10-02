package com.example.safehome.facilitiesview

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
import com.example.safehome.adapter.FaciBookingsAdapter
import com.example.safehome.adapter.FaciHistoryAdapter
import com.example.safehome.adapter.YearAdapter
import com.example.safehome.custom.CustomProgressDialog
import com.example.safehome.databinding.FragmentFacilitiesHistoryBinding
import com.example.safehome.model.Events
import com.example.safehome.model.FaciBookings
import com.example.safehome.repository.APIClient
import com.example.safehome.repository.APIInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Locale


class FacilitiesHistoryFragment : Fragment() {

    private lateinit var binding: FragmentFacilitiesHistoryBinding
    private var bookingList: ArrayList<FaciBookings.Data.Facilility> = ArrayList()
    private var bookingListMain: ArrayList<FaciBookings.Data.Facilility> = ArrayList()
    private lateinit var faciHistoryAdapter: FaciHistoryAdapter
    private var yearList: ArrayList<String> = ArrayList()
    private var yearPopupWindow: PopupWindow? = null

    private lateinit var customProgressDialog: CustomProgressDialog
    private lateinit var apiInterface: APIInterface
    var User_Id: String? = ""
    var Auth_Token: String? = ""

    private lateinit var facilitiesModel : Call<FaciBookings>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentFacilitiesHistoryBinding.inflate(inflater, container, false)
        customProgressDialog = CustomProgressDialog()
        apiInterface = APIClient.getClient(requireContext())
        User_Id = Utils.getStringPref(requireContext(), "residentId", "")
        Auth_Token = Utils.getStringPref(requireContext(), "Token", "")


        getAllBookedFacilities()
        addYearList()
        binding.yearCl.setOnClickListener{
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

        binding.sampleEditText.setOnTouchListener { v, event ->
            if (yearPopupWindow != null) {
                if (yearPopupWindow!!.isShowing) {
                    yearPopupWindow!!.dismiss()
                }
            }

            v?.onTouchEvent(event) ?: true
        }


        binding.sampleEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if (bookingList.isNotEmpty()) {
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

        val myDuesList = ArrayList<FaciBookings.Data.Facilility>()

        val courseAry: ArrayList<FaciBookings.Data.Facilility> = bookingList

        for (eachCourse in courseAry) {
            if (
                !eachCourse.name.isNullOrBlank() && eachCourse.name!!.lowercase(Locale.getDefault()).contains(text.lowercase(Locale.getDefault())) ||
                !eachCourse.totalAmount.toString().isNullOrBlank() && eachCourse.totalAmount.toString().lowercase(Locale.getDefault()).contains(text.lowercase(Locale.getDefault())) ||
                !eachCourse.dateOfBooking.isNullOrBlank() && eachCourse.dateOfBooking.lowercase(Locale.getDefault()).contains(text.lowercase(Locale.getDefault())) ||
                !eachCourse.startDate.isNullOrBlank() && eachCourse.startDate.lowercase(Locale.getDefault()).contains(text.lowercase(Locale.getDefault())) ||
                !eachCourse.endDate.isNullOrBlank() && eachCourse.endDate.lowercase(Locale.getDefault()).contains(text.lowercase(Locale.getDefault())) ||
                !eachCourse.startTime.isNullOrBlank() && eachCourse.startTime.lowercase(Locale.getDefault()).contains(text.lowercase(Locale.getDefault())) ||
                !eachCourse.endTime.isNullOrBlank() && eachCourse.endTime.lowercase(Locale.getDefault()).contains(text.lowercase(Locale.getDefault())) ||
                !eachCourse.dateOfBooking.isNullOrBlank() && eachCourse.dateOfBooking.lowercase(Locale.getDefault()).contains(text.lowercase(Locale.getDefault())) ||
                        "UPI".contains(text)  //paymentmode
            ) {
                myDuesList.add(eachCourse)
            }
        }

        faciHistoryAdapter.filterList(myDuesList);
    }


    @RequiresApi(Build.VERSION_CODES.Q)
    private fun getAllBookedFacilities() {
        //progressbar
        customProgressDialog.progressDialogShow(
            requireContext(),
            this.getString(R.string.loading)
        )

        // here sign up service call
        facilitiesModel = apiInterface.getAllBookedFacilities(
            "Bearer " + Auth_Token
        )
        facilitiesModel.enqueue(object : Callback<FaciBookings> {
            override fun onResponse(
                call: Call<FaciBookings>,
                response: Response<FaciBookings>
            ) {
                customProgressDialog.progressDialogDismiss()
                // here successfully response
                if (response.isSuccessful && response.body() != null) {
                    if (response.body()?.data != null && response.body()!!.data.facililities.isNotEmpty()) {
                        if (bookingListMain.isNotEmpty()) {
                            bookingListMain.clear()
                        }
                        val facilitiesModel = response.body() as FaciBookings
                        bookingListMain = facilitiesModel.data.facililities as ArrayList<FaciBookings.Data.Facilility>
                        bookingList = bookingListMain.filter { it.paymentStatusId == 3 } as ArrayList<FaciBookings.Data.Facilility>

                    } else {
                        // vehilceModelDropDown()
                    }
                    populateData()

                } else {
                    //  vehilceModelDropDown()
                }
            }

            override fun onFailure(call: Call<FaciBookings>, t: Throwable) {
                customProgressDialog.progressDialogDismiss()
                Utils.showToast(requireContext(), t.message.toString())
            }
        })
    }

    private fun addYearList() {
        yearList.add("2023")
        yearList.add("2022")
        yearList.add("2021")
        yearList.add("2020")
    }

    private fun populateData() {
        if (bookingList.size == 0){
            binding.emptyBookingFacilitiesTxt.visibility = View.VISIBLE
        }else {
            binding.emptyBookingFacilitiesTxt.visibility = View.GONE
            binding.historyRecyclerView.layoutManager = LinearLayoutManager(requireContext())
            faciHistoryAdapter = FaciHistoryAdapter(requireContext(), bookingList)
            binding.historyRecyclerView.adapter = faciHistoryAdapter
        }


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
            val yearAdapter = YearAdapter(requireContext(), yearList)

            dropDownRecyclerView.adapter = yearAdapter
            yearAdapter.setCallbackFacilitiesHistoryFrag(this@FacilitiesHistoryFragment)
        }
        yearPopupWindow!!.elevation = 10F
        yearPopupWindow!!.showAsDropDown(binding.yearTxt, 0, 0, Gravity.CENTER)
    }

    fun selectYear(year: String) {
        if (yearPopupWindow != null) {
            if (yearPopupWindow!!.isShowing) {
                yearPopupWindow!!.dismiss()
            }
        }
        if(year!= null){
            binding.yearTxt.text = year
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (yearPopupWindow != null) {
            if (yearPopupWindow!!.isShowing) {
                yearPopupWindow!!.dismiss()
            }
        }
    }
}