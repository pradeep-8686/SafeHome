package com.example.safehome.eventsview

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.safehome.R
import com.example.safehome.Utils
import com.example.safehome.adapter.EventHistoryAdapter
import com.example.safehome.adapter.UpcomingAdapter
import com.example.safehome.adapter.YearAdapter
import com.example.safehome.custom.CustomProgressDialog
import com.example.safehome.databinding.FragmentEventsHistoryBinding
import com.example.safehome.model.Events
import com.example.safehome.model.MaintenanceHistoryModel
import com.example.safehome.model.UpcomingEventsModel
import com.example.safehome.repository.APIClient
import com.example.safehome.repository.APIInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Locale


class EventsHistoryFragment : Fragment() {

    private var yearList: ArrayList<String> = ArrayList()
    private var yearPopupWindow: PopupWindow? = null
    private lateinit var binding: FragmentEventsHistoryBinding

    private var eventsHistoryList: ArrayList<Events> = ArrayList()
    private lateinit var upcomingAdapter: EventHistoryAdapter
    private lateinit var customProgressDialog: CustomProgressDialog
    private lateinit var apiInterface: APIInterface
    var User_Id: String? = ""
    var Auth_Token: String? = ""
    private lateinit var callEventsHistory: Call<UpcomingEventsModel>
    private var selectedYear: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEventsHistoryBinding.inflate(inflater, container, false)

        customProgressDialog = CustomProgressDialog()
        apiInterface = APIClient.getClient(requireContext())
        User_Id = Utils.getStringPref(requireContext(), "residentId", "")
        Auth_Token = Utils.getStringPref(requireContext(), "Token", "")

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
                if (eventsHistoryList.isNotEmpty()){

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

        val myDuesList = ArrayList<Events>()

        val courseAry: ArrayList<Events> = eventsHistoryList

        for (eachCourse in courseAry) {
            if (!eachCourse.eventName.isNullOrBlank() && eachCourse.eventName!!.lowercase(Locale.getDefault()).contains(text.lowercase(Locale.getDefault())) ||
                !eachCourse.facilityName.isNullOrBlank() && eachCourse.facilityName!!.lowercase(Locale.getDefault()).contains(text.lowercase(Locale.getDefault())) ||
                !eachCourse.startDate.isNullOrBlank() && eachCourse.startDate!!.lowercase(Locale.getDefault()).contains(text.lowercase(Locale.getDefault())) ||
                !eachCourse.endDate.isNullOrBlank() && eachCourse.endDate!!.lowercase(Locale.getDefault()).contains(text.lowercase(Locale.getDefault())) ||
                !eachCourse.startTime.isNullOrBlank() && eachCourse.startTime!!.lowercase(Locale.getDefault()).contains(text.lowercase(Locale.getDefault())) ||
                !eachCourse.endTime.isNullOrBlank() && eachCourse.endTime!!.lowercase(Locale.getDefault()).contains(text.lowercase(Locale.getDefault())) ||
                !eachCourse.createdByName.isNullOrBlank() && eachCourse.createdByName!!.lowercase(Locale.getDefault()).contains(text.lowercase(Locale.getDefault()))
            ) {
                myDuesList.add(eachCourse)
            }
        }

        upcomingAdapter.filterList(myDuesList);
    }


    @RequiresApi(Build.VERSION_CODES.Q)
    private fun addYearList() {
        yearList.add("2023")
        yearList.add("2022")
        yearList.add("2021")
        yearList.add("2020")

        binding.yearTxt.text =  yearList[0]
        selectedYear = yearList[0]

        getAllUpcomingEventsApiCall(yearList[0])

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
            yearAdapter.setCallbackEventsHistoryFrag(this@EventsHistoryFragment)
        }
        yearPopupWindow!!.elevation = 10F
        yearPopupWindow!!.showAsDropDown(binding.yearTxt, 0, 0, Gravity.CENTER)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun selectEventsHistoryYear(year: String) {
        if (yearPopupWindow != null) {
            if (yearPopupWindow!!.isShowing) {
                yearPopupWindow!!.dismiss()
            }
        }
        if(year!= null){
            binding.yearTxt.text = year
            selectedYear = year

            getAllUpcomingEventsApiCall(selectedYear)
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun getAllUpcomingEventsApiCall(year: String? = null) {
        customProgressDialog.progressDialogShow(requireContext(), this.getString(R.string.loading))
        // here sign up service call
       // callEventsHistory = apiInterface.getAllcompletedEvents("bearer " + Auth_Token, 10, 1, 0)
        callEventsHistory = apiInterface.getAllcompletedEvents("bearer " + Auth_Token, 50, 1, year, User_Id)
//        callEventsHistory = apiInterface.getAllcompletedEvents("bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiI5ODk3ODc2ODU2IiwiZW1haWwiOiIiLCJqdGkiOiI3ZTM3MjQ1MC04ZjQ5LTRhOGEtOGI0MS1mZTJlMDYxOGM2YzUiLCJyb2xlIjoiQWRtaW4iLCJjb21tdW5pdHlJZCI6ImxSalZPSTM5SzQxdktnc0lUYzVWT3c9PSIsImNvbW11bml0eUFsaWFzIjoiY0N0bk11NWwzb01rbDlyREtuYjZWUnY5TmpRVVNUVWRBSDcxTU1HQXBuUkV5Q21LcHQvZUtNK3NJcEJWTDlhQiIsImNvbW11bml0eU5hbWUiOiIrQW1oSEx2ZEl5Ym5qTklZYnBBRlJOSkhNaTMxM29wRDdZSTVoeXRUenBPYmlVbk9qWnVndVdjNHlXZnZCUWt4IiwiZmlyc3ROYW1lIjoiSjJxRUVIMlU5YlJqRmp6QWpFKzlMQT09IiwibGFzdE5hbWUiOiJzdGk1RGVpcTBBeFFLZjJRa0dGZkpBPT0iLCJ1c2VySWQiOiIyMyIsIm5iZiI6MTY5Mjg0MjI2NSwiZXhwIjoxNzI0Mzc4MjY1LCJpYXQiOjE2OTI4NDIyNjUsImlzcyI6IlNhZmVIb21lLmNvbSIsImF1ZCI6IlNhZmVIb21lLmNvbSJ9.jNQcCDBWfJdrA2yvLT9EkK7HMvJrsKXffiG2V6_eoJc" , 50, 1, year, "7")

        callEventsHistory.enqueue(object : Callback<UpcomingEventsModel> {
            override fun onResponse(
                call: Call<UpcomingEventsModel>,
                response: Response<UpcomingEventsModel>
            ) {
                customProgressDialog.progressDialogDismiss()
                Log.e("Response: ", response.body().toString())

                if (response.isSuccessful && response.body() != null) {
                    if (eventsHistoryList.isNotEmpty()) {
                        eventsHistoryList.clear()
                    }
                    if(response.body()!!.statusCode == 1) {
                        eventsHistoryList = response.body()!!.data.events as ArrayList<Events>

                    }else{
                        Toast.makeText(
                            requireContext(),
                            response.body()!!.message,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
                populateData(eventsHistoryList)

            }

            override fun onFailure(call: Call<UpcomingEventsModel>, t: Throwable) {
                customProgressDialog.progressDialogDismiss()
                Utils.showToast(requireContext(), t.message.toString())
            }
        })
    }

    private fun populateData(upcomingList: ArrayList<Events>) {
        if (upcomingList.size == 0){
            binding.emptyEventsTxt.visibility = View.VISIBLE
        }else {
            binding.emptyEventsTxt.visibility = View.GONE

            binding.eventsHistory.layoutManager = LinearLayoutManager(requireContext())
            upcomingAdapter = EventHistoryAdapter(requireContext(), upcomingList)
            binding.eventsHistory.adapter = upcomingAdapter
            upcomingAdapter.notifyDataSetChanged()

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