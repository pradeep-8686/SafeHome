package com.example.safehome.meetings

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Gravity
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
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.safehome.R
import com.example.safehome.Utils
import com.example.safehome.custom.CustomProgressDialog
import com.example.safehome.databinding.FragmentMeetingUpcomingBinding
import com.example.safehome.model.MeetingUpcomingModel
import com.example.safehome.repository.APIClient
import com.example.safehome.repository.APIInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MeetingsUpcomingFragment : Fragment() {

    private lateinit var binding: FragmentMeetingUpcomingBinding
    private var meetingUpcomingModelList: ArrayList<MeetingUpcomingModel> = ArrayList()
    private lateinit var personalComplaintsAdapter: MeetingUpcomingAdapter
    private lateinit var customProgressDialog: CustomProgressDialog
    private lateinit var apiInterface: APIInterface
    var User_Id: String? = ""
    var Auth_Token: String? = ""
    private lateinit var callMeetingUpcomingModel: Call<MeetingUpcomingModel>

    private var viewComplaints: PopupWindow? = null
    private var viewComplaintsDialog: Dialog? = null
    private var selectResponseDialog: Dialog?= null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentMeetingUpcomingBinding.inflate(inflater, container, false)

        customProgressDialog = CustomProgressDialog()
        apiInterface = APIClient.getClient(requireContext())
        User_Id = Utils.getStringPref(requireContext(), "residentId", "")
        Auth_Token = Utils.getStringPref(requireContext(), "Token", "")

//        getAllUpcomingEventsApiCall()
        addData()
        populateData(meetingUpcomingModelList)

        return binding.root
    }


    @RequiresApi(Build.VERSION_CODES.Q)
    private fun getAllUpcomingEventsApiCall(complaintStatus: String? = null, year: String? = null) {
        customProgressDialog.progressDialogShow(requireContext(), this.getString(R.string.loading))

        // here sign up service call
        callMeetingUpcomingModel =
            apiInterface.getMeetingUpcoming("bearer " + Auth_Token, complaintStatus, year)
        callMeetingUpcomingModel.enqueue(object : Callback<MeetingUpcomingModel> {
            override fun onResponse(
                call: Call<MeetingUpcomingModel>, response: Response<MeetingUpcomingModel>
            ) {
                customProgressDialog.progressDialogDismiss()
                Log.e("Response: ", response.body().toString())

                if (response.isSuccessful && response.body() != null) {
                    if (meetingUpcomingModelList.isNotEmpty()) {
                        meetingUpcomingModelList.clear()
                    }/* if(response.body()!!.statusCode == 1) {
                         MeetingUpcomingModelList = response.body()!!.data.events as ArrayList<Events>
                     }else{
                         Toast.makeText(
                             requireContext(),
                             response.body()!!.message,
                             Toast.LENGTH_LONG
                         ).show()
                     }
                     populateData(MeetingUpcomingModelList)*/

                }
            }

            override fun onFailure(call: Call<MeetingUpcomingModel>, t: Throwable) {
                customProgressDialog.progressDialogDismiss()
                Utils.showToast(requireContext(), t.message.toString())
            }
        })
    }

    private fun addData() {

        val c1 = MeetingUpcomingModel(
            "General Body Meeting",
            "Club House",
            "Admin",
            "25/08/2023",
            "10:00 A.M - 12:00 P.M"
        )
        meetingUpcomingModelList.add(c1)

        val c2 = MeetingUpcomingModel(
            "Safety & Security",
            "Banquet Hall",
            "Admin",
            "25/08/2023",
            "11:00 A.M - 1:00 P.M"
        )
        meetingUpcomingModelList.add(c2)


    }

    private fun populateData(meetingUpcomingModelList: ArrayList<MeetingUpcomingModel>) {
        //    upcomingList.clear()

        if (meetingUpcomingModelList.size == 0) {
            binding.emptyEventsTxt.visibility = View.VISIBLE
        } else {
            binding.emptyEventsTxt.visibility = View.GONE
            binding.personalRecyclerView.layoutManager = LinearLayoutManager(requireContext())
            personalComplaintsAdapter =
                MeetingUpcomingAdapter(requireContext(), meetingUpcomingModelList)
            binding.personalRecyclerView.adapter = personalComplaintsAdapter
            personalComplaintsAdapter.setCallback(this@MeetingsUpcomingFragment)
            personalComplaintsAdapter.notifyDataSetChanged()
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun clickAction(meetingUpcomingModel: MeetingUpcomingModel){
        if (viewComplaints != null) {
            if (viewComplaints!!.isShowing) {
                viewComplaints!!.dismiss()
            } else {
                availableTimePopup(meetingUpcomingModel)
            }
        } else {
            availableTimePopup(meetingUpcomingModel)
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun viewAgendaClickAction(meetingUpcomingModel: MeetingUpcomingModel){
        if (viewComplaints != null) {
            if (viewComplaints!!.isShowing) {
                viewComplaints!!.dismiss()
            } else {
                availableTimePopup(meetingUpcomingModel)
            }
        } else {
            availableTimePopup(meetingUpcomingModel)
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()

        if (viewComplaints != null) {
            if (viewComplaints!!.isShowing) {
                viewComplaints!!.dismiss()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    @SuppressLint("MissingInflatedId")
    private fun availableTimePopup(meetingUpcomingModel: MeetingUpcomingModel) {
        val layoutInflater: LayoutInflater =
            requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = layoutInflater.inflate(R.layout.view_agenda_layout, null)
        viewComplaintsDialog = Dialog(requireContext(), R.style.CustomAlertDialog)
        viewComplaintsDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        viewComplaintsDialog!!.setContentView(view)
        viewComplaintsDialog!!.setCanceledOnTouchOutside(true)
        viewComplaintsDialog!!.setCancelable(true)

        val lp = WindowManager.LayoutParams()
        lp.copyFrom(viewComplaintsDialog!!.window!!.attributes)

        lp.width = (Utils.screenWidth * 0.9).toInt()
        lp.height = LinearLayout.LayoutParams.WRAP_CONTENT
        lp.gravity = Gravity.CENTER
        viewComplaintsDialog!!.window!!.attributes = lp

        val close = view.findViewById<ImageView>(R.id.close)
        val yes_btn: TextView = view.findViewById(R.id.yes_btn)

        yes_btn.setOnClickListener{
            if (viewComplaintsDialog!!.isShowing) {
                viewComplaintsDialog!!.dismiss()
            }
        }


        close.setOnClickListener {
            if (viewComplaintsDialog!!.isShowing) {
                viewComplaintsDialog!!.dismiss()
            }
        }

        viewComplaintsDialog!!.show()
    }

    private fun selectResponseDialog() {
        val layoutInflater: LayoutInflater =
            requireActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = layoutInflater.inflate(R.layout.select_response_in_meeting, null)
        selectResponseDialog = Dialog(requireContext(), R.style.CustomAlertDialog)
        selectResponseDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        selectResponseDialog!!.setContentView(view)
        selectResponseDialog!!.setCanceledOnTouchOutside(true)
        selectResponseDialog!!.setCancelable(true)

        val lp = WindowManager.LayoutParams()
        lp.copyFrom(selectResponseDialog!!.window!!.attributes)

        lp.width = (Utils.screenWidth * 0.9).toInt()
        lp.height = LinearLayout.LayoutParams.WRAP_CONTENT
        lp.gravity = Gravity.CENTER
        selectResponseDialog!!.window!!.attributes = lp

        // here logic ..
        val close = view.findViewById<ImageView>(R.id.close_btn_click)
        val continue_btn = view.findViewById<TextView>(R.id.continue_btn)

        val radioGroup = view.findViewById<RadioGroup>(R.id.rdGroup) as RadioGroup
        val yes_radio_option = view.findViewById<RadioButton>(R.id.yes) as RadioButton
        val no_radio_Option = view.findViewById<RadioButton>(R.id.no) as RadioButton
        val may_be_later_radio_Option = view.findViewById<RadioButton>(R.id.may_be_later) as RadioButton

        val selectedId: Int = radioGroup.getCheckedRadioButtonId()
        continue_btn.setOnClickListener {
            if (radioGroup?.getCheckedRadioButtonId() == -1) {
                Toast.makeText(requireContext(), "Please select response", Toast.LENGTH_LONG).show()
            } else {
                if (selectResponseDialog!!.isShowing) {
                    selectResponseDialog!!.dismiss()
                }
            }
        }

        close.setOnClickListener {
            if (selectResponseDialog!!.isShowing) {
                selectResponseDialog!!.dismiss()
            }
        }
        selectResponseDialog!!.show()
    }

    fun selectResponse(meetingUpcomingModel: MeetingUpcomingModel) {
        selectResponseDialog()
    }


}