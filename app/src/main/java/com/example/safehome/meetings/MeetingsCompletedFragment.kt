package com.example.safehome.meetings

import android.app.Dialog
import android.content.Context
import android.content.Intent
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
import com.example.safehome.adapter.YearAdapter
import com.example.safehome.custom.CustomProgressDialog
import com.example.safehome.databinding.FragmentMeetingCompletedBinding
import com.example.safehome.model.UpcomingMeetingsModel
import com.example.safehome.repository.APIClient
import com.example.safehome.repository.APIInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Locale


class MeetingsCompletedFragment : Fragment() {


    private lateinit var binding: FragmentMeetingCompletedBinding
    private var meetingCompletedModelList: ArrayList<UpcomingMeetingsModel.Data.MeetingData> = ArrayList()
    private lateinit var communityComplaintsAdapter: MeetingCompletedAdapter
    private lateinit var customProgressDialog: CustomProgressDialog
    private lateinit var apiInterface: APIInterface
    var User_Id: String? = ""
    var Auth_Token: String? = ""
    private lateinit var callMeetingCompletedModel: Call<UpcomingMeetingsModel>

    private var yearList: ArrayList<String> = ArrayList()
    private var complaintStatusList: ArrayList<String> = ArrayList()
    private var yearPopupWindow: PopupWindow? = null
    private var categoryPopupWindow: PopupWindow? = null

    private var viewComplaints: PopupWindow? = null
    private var viewComplaintsDialog: Dialog? = null

    private var selectedCategoryId: String? = null
    private var selectedYear: String? = null


    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMeetingCompletedBinding.inflate(inflater, container, false)


        customProgressDialog = CustomProgressDialog()
        apiInterface = APIClient.getClient(requireContext())
        User_Id = Utils.getStringPref(requireContext(), "residentId", "")
        Auth_Token = Utils.getStringPref(requireContext(), "Token", "")

        getAllUpcomingEventsApiCall(year = "2023")
        addData()
     //   populateData(meetingCompletedModelList)
        addYearList()

        binding.yearCl.setOnClickListener {
            if (categoryPopupWindow != null) {
                if (categoryPopupWindow!!.isShowing) {
                    categoryPopupWindow!!.dismiss()
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

        binding.sampleEditText.setOnTouchListener { v, event ->
            if (yearPopupWindow != null) {
                if (yearPopupWindow!!.isShowing) {
                    yearPopupWindow!!.dismiss()
                }
            }
            if (categoryPopupWindow != null) {
                if (categoryPopupWindow!!.isShowing) {
                    categoryPopupWindow!!.dismiss()
                }
            }

            v?.onTouchEvent(event) ?: true
        }

        binding.sampleEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if (meetingCompletedModelList.isNotEmpty()) {

                    filter(p0.toString())
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })

//        binding.yearTxt.text = yearList[0]

        return binding.root
    }


    @RequiresApi(Build.VERSION_CODES.Q)
    private fun getAllUpcomingEventsApiCall(complaintStatus: String? = null, year: String? = null) {
        customProgressDialog.progressDialogShow(requireContext(), this.getString(R.string.loading))

        // here sign up service call
        callMeetingCompletedModel = apiInterface.getMeetingCompleted("bearer " + Auth_Token, year, 1, "10")
        callMeetingCompletedModel.enqueue(object : Callback<UpcomingMeetingsModel> {
            override fun onResponse(
                call: Call<UpcomingMeetingsModel>,
                response: Response<UpcomingMeetingsModel>
            ) {
                customProgressDialog.progressDialogDismiss()
                Log.e("Response: ", response.body().toString())

                if (response.isSuccessful && response.body() != null) {
                    if (meetingCompletedModelList.isNotEmpty()) {
                        meetingCompletedModelList.clear()
                    }
                     if(response.body()!!.statusCode == 1) {
                         meetingCompletedModelList = response.body()!!.data.meetingData as ArrayList<UpcomingMeetingsModel.Data.MeetingData>
                     }else{
                         Toast.makeText(
                             requireContext(),
                             response.body()!!.message,
                             Toast.LENGTH_LONG
                         ).show()
                     }
                     populateData(meetingCompletedModelList)

                }
            }

            override fun onFailure(call: Call<UpcomingMeetingsModel>, t: Throwable) {
                customProgressDialog.progressDialogDismiss()
                Utils.showToast(requireContext(), t.message.toString())
            }
        })
    }

    private fun addData() {


    /*    val c1 = MeetingCompletedModel(
            "General Body Meeting",
            "Club House",
            "Admin",
            "25/08/2023",
            "10:00 A.M - 12:00 P.M",
            true
        )
        meetingCompletedModelList.add(c1)

        val c2 = MeetingCompletedModel(
            "Safety & Security",
            "Banquet Hall",
            "Admin",
            "25/08/2023",
            "11:00 A.M - 1:00 P.M",
            false
        )
        meetingCompletedModelList.add(c2)
*/

    }

    private fun populateData(meetingCompletedModelList: ArrayList<UpcomingMeetingsModel.Data.MeetingData>) {
        //    upcomingList.clear()

        if (meetingCompletedModelList.isEmpty()) {
            binding.emptyEventsTxt.visibility = View.VISIBLE
            binding.personalRecyclerView.visibility = View.GONE

        } else {
            binding.emptyEventsTxt.visibility = View.GONE
            binding.personalRecyclerView.visibility = View.VISIBLE
            binding.personalRecyclerView.layoutManager = LinearLayoutManager(requireContext())
            communityComplaintsAdapter =
                MeetingCompletedAdapter(requireContext(), meetingCompletedModelList)
            binding.personalRecyclerView.adapter = communityComplaintsAdapter
            communityComplaintsAdapter.setCallback(this@MeetingsCompletedFragment)

            communityComplaintsAdapter.notifyDataSetChanged()
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun clickAction(meetingCompletedModel: UpcomingMeetingsModel.Data.MeetingData) {

    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun viewAgendaClickAction(meetingCompletedModel: UpcomingMeetingsModel.Data.MeetingData) {

        val intent = Intent(requireContext(), MeetingMinutesActivity::class.java)
        intent.putExtra("Meetingdata", meetingCompletedModel)
        startActivity(intent)

    }

    private fun addYearList() {
        yearList.add("2023")
        yearList.add("2022")
        yearList.add("2021")
        yearList.add("2020")

        binding.yearTxt.text =  yearList[0]
        selectedYear = yearList[0]
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
            yearAdapter.setCallbackComplaintsYear(this@MeetingsCompletedFragment)
        }
        yearPopupWindow!!.elevation = 10F
        yearPopupWindow!!.showAsDropDown(binding.yearTxt, 0, 0, Gravity.CENTER)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun selectComplaintYear(year: String) {
        if (yearPopupWindow != null) {
            if (yearPopupWindow!!.isShowing) {
                yearPopupWindow!!.dismiss()
            }
        }
        if (year != null) {
            binding.yearTxt.text = year
        }
        selectedYear = year
         if (year != null){

             getAllUpcomingEventsApiCall(year = year!!)
         }

    }


    override fun onDestroyView() {
        super.onDestroyView()
        if (yearPopupWindow != null) {
            if (yearPopupWindow!!.isShowing) {
                yearPopupWindow!!.dismiss()
            }
        }

        if (viewComplaints != null) {
            if (viewComplaints!!.isShowing) {
                viewComplaints!!.dismiss()
            }
        }

    }

    fun filter(text: String) {

        val meetingsCompletedList = ArrayList<UpcomingMeetingsModel.Data.MeetingData>()
        val courseAry: ArrayList<UpcomingMeetingsModel.Data.MeetingData> = meetingCompletedModelList
        for (eachCourse in courseAry) {
            if (
                !eachCourse.topicName.isNullOrBlank() && eachCourse.topicName.lowercase(Locale.getDefault()).contains(text.lowercase(Locale.getDefault())) ||
                !eachCourse.facilityName.isNullOrBlank() && eachCourse.facilityName.lowercase(Locale.getDefault()).contains(text.lowercase(Locale.getDefault())) ||
                !eachCourse.organisedBy.isNullOrBlank() && eachCourse.organisedBy.lowercase(Locale.getDefault()).contains(text.lowercase(Locale.getDefault())) ||
                !eachCourse.meetingDate.isNullOrBlank() && eachCourse.meetingDate.lowercase(Locale.getDefault()).contains(text.lowercase(Locale.getDefault())) ||
                !eachCourse.startTime.isNullOrBlank() && eachCourse.startTime.lowercase(Locale.getDefault()).contains(text.lowercase(Locale.getDefault())) ||
                !eachCourse.endTime.isNullOrBlank() && eachCourse.endTime.lowercase(Locale.getDefault()).contains(text.lowercase(Locale.getDefault()))
            ) {
                meetingsCompletedList.add(eachCourse)
            }
        }

        communityComplaintsAdapter.filterList(meetingsCompletedList);
    }


}