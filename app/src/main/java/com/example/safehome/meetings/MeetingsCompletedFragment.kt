package com.example.safehome.meetings

import android.annotation.SuppressLint
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
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.safehome.HomeActivity
import com.example.safehome.R
import com.example.safehome.Utils
import com.example.safehome.adapter.YearAdapter
import com.example.safehome.custom.CustomProgressDialog
import com.example.safehome.databinding.FragmentMeetingCompletedBinding
import com.example.safehome.maintenance.HistoryFragment
import com.example.safehome.model.MeetingCompletedModel
import com.example.safehome.model.MeetingUpcomingModel
import com.example.safehome.repository.APIClient
import com.example.safehome.repository.APIInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Locale


class MeetingsCompletedFragment : Fragment() {


    private lateinit var binding: FragmentMeetingCompletedBinding
    private var meetingCompletedModelList: ArrayList<MeetingCompletedModel> = ArrayList()
    private lateinit var communityComplaintsAdapter: MeetingCompletedAdapter
    private lateinit var customProgressDialog: CustomProgressDialog
    private lateinit var apiInterface: APIInterface
    var User_Id: String? = ""
    var Auth_Token: String? = ""
    private lateinit var callMeetingCompletedModel: Call<MeetingCompletedModel>

    private var yearList: ArrayList<String> = ArrayList()
    private var complaintStatusList: ArrayList<String> = ArrayList()
    private var yearPopupWindow: PopupWindow? = null
    private var categoryPopupWindow: PopupWindow? = null

    private var viewComplaints: PopupWindow? = null
    private var viewComplaintsDialog: Dialog? = null

    private var selectedCategoryId: String? = null
    private var selectedYear: String? = null


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

//        getAllUpcomingEventsApiCall()
        addData()
        populateData(meetingCompletedModelList)
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


        return binding.root
    }


    @RequiresApi(Build.VERSION_CODES.Q)
    private fun getAllUpcomingEventsApiCall(complaintStatus: String? = null, year: String? = null) {
        customProgressDialog.progressDialogShow(requireContext(), this.getString(R.string.loading))

        // here sign up service call
        callMeetingCompletedModel =
            apiInterface.getMeetingCompleted("bearer " + Auth_Token, complaintStatus, year)
        callMeetingCompletedModel.enqueue(object : Callback<MeetingCompletedModel> {
            override fun onResponse(
                call: Call<MeetingCompletedModel>,
                response: Response<MeetingCompletedModel>
            ) {
                customProgressDialog.progressDialogDismiss()
                Log.e("Response: ", response.body().toString())

                if (response.isSuccessful && response.body() != null) {
                    if (meetingCompletedModelList.isNotEmpty()) {
                        meetingCompletedModelList.clear()
                    }
                    /* if(response.body()!!.statusCode == 1) {
                         MeetingCompletedModelList = response.body()!!.data.events as ArrayList<Events>
                     }else{
                         Toast.makeText(
                             requireContext(),
                             response.body()!!.message,
                             Toast.LENGTH_LONG
                         ).show()
                     }
                     populateData(MeetingCompletedModelList)*/

                }
            }

            override fun onFailure(call: Call<MeetingCompletedModel>, t: Throwable) {
                customProgressDialog.progressDialogDismiss()
                Utils.showToast(requireContext(), t.message.toString())
            }
        })
    }

    private fun addData() {


        val c1 = MeetingCompletedModel(
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


    }

    private fun populateData(meetingCompletedModelList: ArrayList<MeetingCompletedModel>) {
        //    upcomingList.clear()

        if (this.meetingCompletedModelList.size == 0) {
            binding.emptyEventsTxt.visibility = View.VISIBLE
        } else {
            binding.emptyEventsTxt.visibility = View.GONE
            binding.personalRecyclerView.layoutManager = LinearLayoutManager(requireContext())
            communityComplaintsAdapter =
                MeetingCompletedAdapter(requireContext(), meetingCompletedModelList)
            binding.personalRecyclerView.adapter = communityComplaintsAdapter
            communityComplaintsAdapter.setCallback(this@MeetingsCompletedFragment)

            communityComplaintsAdapter.notifyDataSetChanged()
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun clickAction(meetingCompletedModel: MeetingCompletedModel) {

    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun viewAgendaClickAction(meetingCompletedModel: MeetingCompletedModel) {

        val intent = Intent(requireContext(), MeetingMinutesActivity::class.java)
        startActivity(intent)

    }

    private fun addYearList() {
        yearList.add("2023")
        yearList.add("2022")
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
        /* if (selectedCategoryId != null){

             getAllUpcomingEventsApiCall(selectedCategoryId!!, selectedYear!!)
         }else{
             getAllUpcomingEventsApiCall(selectedYear!!)

         }*/

        Log.d(HistoryFragment.TAG, "$selectedCategoryId!! , $selectedYear!!")

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

        val myDuesList = ArrayList<MeetingCompletedModel>()
        val courseAry: ArrayList<MeetingCompletedModel> = meetingCompletedModelList

        for (eachCourse in courseAry) {

            if (
                !eachCourse.meetingName.isNullOrBlank() && eachCourse.meetingName.lowercase(
                    Locale.getDefault()
                ).contains(text.lowercase(Locale.getDefault()))

            ) {
                myDuesList.add(eachCourse)
            }
        }

        communityComplaintsAdapter.filterList(myDuesList);
    }


}