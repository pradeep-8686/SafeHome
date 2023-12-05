package com.example.safehome.visitors.others

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.PopupWindow
import android.widget.RadioButton
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.safehome.R
import com.example.safehome.Utils
import com.example.safehome.custom.CustomProgressDialog
import com.example.safehome.databinding.FragmentOthersAllowFrequentlyBinding
import com.example.safehome.databinding.FragmentStaffAllowFrequentlyBinding
import com.example.safehome.repository.APIClient
import com.example.safehome.repository.APIInterface
import com.example.safehome.visitors.ApprovalStatusModel
import com.example.safehome.visitors.GetAllVisitorDetailsModel
import com.example.safehome.visitors.ScheduleModel
import com.example.safehome.visitors.ServiceProviderModel
import com.example.safehome.visitors.VisitorActivity
import com.example.safehome.visitors.VisitorPostResponse
import com.example.safehome.visitors.delivery.DeliveryActivity
import com.example.safehome.visitors.guest.GuestScheduleAdapter
import com.example.safehome.visitors.guest.GuestTypeAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class OthersAllowFrequentlyFragment(private val visitorTypeId: Int? = null,
                                    private val visitorListItem: GetAllVisitorDetailsModel.Data.Event? = null,
                                    private val approvalStatus: ArrayList<ApprovalStatusModel.Data>? = null) : Fragment() {
    private lateinit var binding: FragmentOthersAllowFrequentlyBinding
    private var schedulePopupWindow: PopupWindow? = null
    var cal_startDate = Calendar.getInstance()
    var cal_endDate = Calendar.getInstance()

    private var scheduleList: ArrayList<ScheduleModel.Data> = ArrayList()
    private lateinit var getScheduleCall: Call<ScheduleModel>
  //  private var scheduleId : Int ?= null

    private var serviceProviderList: ArrayList<ServiceProviderModel.Data> = ArrayList()
    private lateinit var getServiceProviderCall: Call<ServiceProviderModel>
    private lateinit var customProgressDialog: CustomProgressDialog
    private lateinit var apiInterface: APIInterface
    private var User_Id: String? = ""
    private var Auth_Token: String? = ""
//    private var serviceProviderId : Int ?= null

    private lateinit var postVisitorCall: Call<VisitorPostResponse>
    private var residentId: String? = ""
    //Request Data
//    private var sGuestType: Int? = null
    private var sGuestName: String? = ""
    private var sGuestNumber: String? = ""
    private var sGuestDate: String? = ""
    private var sGuestTime: String? = ""
    private var isPublic: Boolean? = null
    private var sGuestEndDate: String? = ""
    private var sScheduleId: Int? = null
    private var visitorId: Int? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOthersAllowFrequentlyBinding.inflate(inflater, container, false)

        inIt()
        setIntentData()
        setSchedule()
        clickEvents()

        setRadioButton()

        return binding.root
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun setIntentData() {
        if (visitorListItem != null){

            Log.e("visitorListItem", visitorListItem.toString())
            visitorId = visitorListItem.visitorId

            if (visitorListItem.name != null){

                binding.etGuestName.text = Editable.Factory.getInstance().newEditable(visitorListItem.name)
            }
            if (visitorListItem.mobileNo != null){

                binding.etMobileNumber.text = Editable.Factory.getInstance().newEditable(visitorListItem.mobileNo)
            }
            if (visitorListItem.startDate != null){

                binding.startDateTxt.text = Editable.Factory.getInstance().newEditable(
                    Utils.formatDateMonthYear(
                        visitorListItem.startDate
                    ))
            }
            if (visitorListItem.endDate != null){

                binding.endDateTxt.text = Editable.Factory.getInstance().newEditable(Utils.formatDateMonthYear(visitorListItem.endDate))
            }
            if (visitorListItem.inTime != null){

                binding.startTimeText.text = Editable.Factory.getInstance().newEditable(visitorListItem.inTime)
            }

            if (visitorListItem.scheduleType != null){

                binding.scheduleTxt.text = visitorListItem.scheduleType
            }
            if (visitorListItem.repeatId != null){

                sScheduleId = visitorListItem.repeatId
            }

        }
    }


    private fun setRadioButton() {
        binding.radioGroup.setOnCheckedChangeListener { view, checkedId ->
            val selectedRadioButton = view.findViewById<RadioButton>(checkedId)

            // Check which radio button is selected
            when (selectedRadioButton) {
                binding.radioButtonYes -> {
                    isPublic = true
                }

                binding.radioButtonNo -> {
                    isPublic = false

                }
            }
        }


    }


    private fun inIt() {
        customProgressDialog = CustomProgressDialog()
        apiInterface = APIClient.getClient(requireContext())
        User_Id = Utils.getStringPref(requireContext(), "User_Id", "")
        Auth_Token = Utils.getStringPref(requireContext(), "Token", "")
    }
    private fun setSchedule() {
        getScheduleCall = apiInterface.getScheduleCall("bearer "+Auth_Token, visitorTypeId!!)
        getScheduleCall.enqueue(object: Callback<ScheduleModel> {
            @SuppressLint("SuspiciousIndentation")
            override fun onResponse(
                call: Call<ScheduleModel>,
                response: Response<ScheduleModel>
            ) {
                if (response.isSuccessful && response.body()!= null){
                    if (response.body()!!.statusCode!= null){
                        when(response.body()!!.statusCode){
                            1 -> {
                                if (response.body()!!.message != null && response.body()!!.message.isNotEmpty()) {
                                    if (scheduleList.isNotEmpty()) {
                                        scheduleList.clear()
                                    }
                                    scheduleList = response.body()!!.data as ArrayList<ScheduleModel.Data>
                                    //     Utils.showToast(requireContext(), response.body()!!.message.toString())

                                }

                            }

                            else -> {
                                if (response.body()!!.message != null && response.body()!!.message.isNotEmpty()) {
                                    Utils.showToast(requireContext(), response.body()!!.message.toString())
                                }
                            }
                        }
                    }else{

                    }
                }
            }

            override fun onFailure(call: Call<ScheduleModel>, t: Throwable) {
                customProgressDialog.progressDialogDismiss()
                Utils.showToast(requireContext(), t.message.toString())
            }

        })

    }

    private fun updateInviteVisitorAPICall(visitorOptionsMap: MutableMap<String, Any>) {

        postVisitorCall =
            apiInterface.updateVisitorAPI("bearer $Auth_Token", visitorOptionsMap)
        postVisitorCall.enqueue(object : Callback<VisitorPostResponse> {
            override fun onResponse(
                call: Call<VisitorPostResponse>,
                response: Response<VisitorPostResponse>
            ) {
                if (response.isSuccessful && response.body() != null) {

                    Log.e("VisitorResponse", response.body().toString())

                    if (response.body()!!.statusCode != null) {
                        when (response.body()!!.statusCode) {
                            1 -> {
                                if (response.body()!!.message.isNotEmpty()) {

                                    Utils.showToast(
                                        requireContext(),
                                        response.body()!!.message.toString()
                                    )
                                    val intent = Intent(
                                        requireActivity(),
                                        VisitorActivity::class.java
                                    )
                                    startActivity(intent)
                                    requireActivity().finish()

                                }

                            }

                            else -> {
                                if (response.body()!!.message != null && response.body()!!.message.isNotEmpty()) {
                                    Utils.showToast(
                                        requireContext(),
                                        response.body()!!.message.toString()
                                    )
                                }
                            }
                        }
                    } else {

                    }
                }
            }

            override fun onFailure(call: Call<VisitorPostResponse>, t: Throwable) {
                customProgressDialog.progressDialogDismiss()
                Utils.showToast(requireContext(), t.message.toString())
            }

        })

    }

    private fun clickEvents() {

        binding.clSchedule.setOnClickListener {

            if (schedulePopupWindow != null) {
                if (schedulePopupWindow!!.isShowing) {
                    schedulePopupWindow!!.dismiss()
                } else {
                    scheduleDropDown()
                }
            } else {
                scheduleDropDown()
            }
        }
        binding.startDateTxt?.setOnClickListener {
            val startDateDialog = DatePickerDialog(
                requireContext(),
                dateSetListener_book_now_start_date,
                // set DatePickerDialog to point to today's date when it loads up
                cal_startDate.get(Calendar.YEAR),
                cal_startDate.get(Calendar.MONTH),
                cal_startDate.get(Calendar.DAY_OF_MONTH)
            )

            startDateDialog.datePicker.minDate = System.currentTimeMillis() - 1000
            startDateDialog.show()
        }

        binding.endDateTxt?.setOnClickListener {
            val endDateDialog = DatePickerDialog(
                requireContext(),
                dateSetListener__book_now_end_date,
                // set DatePickerDialog to point to today's date when it loads up
                cal_endDate.get(Calendar.YEAR),
                cal_endDate.get(Calendar.MONTH),
                cal_endDate.get(Calendar.DAY_OF_MONTH)
            )
            // Set a minimum date to disable previous dates
            try {
                val dateInMillis = Utils.convertStringToDateMillis(
                    binding.startDateTxt!!.text.toString(),
                    "dd/MM/yyyy"
                )
                endDateDialog.datePicker.minDate = dateInMillis
            } catch (e: Exception) {
                endDateDialog.datePicker.minDate =
                    System.currentTimeMillis() - 1000 // Subtract 1 second to prevent selecting the current day
            }
            // endDateDialog.datePicker.minDate = System.currentTimeMillis() - 1000
            endDateDialog.show()
        }

        binding.startTimeText?.setOnClickListener {
            val mcurrentTime = Calendar.getInstance()
            val hour = mcurrentTime[Calendar.HOUR_OF_DAY]
            val minute = mcurrentTime[Calendar.MINUTE]
            val mTimePicker: TimePickerDialog
            mTimePicker = TimePickerDialog(
                requireContext(),
                { timePicker, selectedHour, selectedMinute ->
                    //setTime("StartTime", selectedHour, selectedMinute)
                    binding.startTimeText!!.text = String.format(
                        "%02d:%02d %s", selectedHour, selectedMinute,
                        if (selectedHour < 12) "AM" else "PM"
                    )

                },
                hour,
                minute,
                true
            ) //Yes 24 hour time

            mTimePicker.setTitle("Select Time")
            mTimePicker.show()
        }

        binding.cancelBtn.setOnClickListener {
            val intent = Intent(requireActivity(), VisitorActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }
        binding.inviteBtn.setOnClickListener {

            if (validateData()) {

                val visitorOptionsMap: MutableMap<String, Any> = HashMap()
                visitorOptionsMap["VisitorTypeName"] = "Others"
                visitorOptionsMap["ResidentId"] = residentId!!
                visitorOptionsMap["VisitorTypeId"] = visitorTypeId!!
//                visitorOptionsMap["VisitorTypeServiceProviderId"] = sGuestType!!
                visitorOptionsMap["Name"] = sGuestName!!
                visitorOptionsMap["MobileNo"] = sGuestNumber!!
                visitorOptionsMap["StartDate"] = sGuestDate!!.replace("/", "-")
                visitorOptionsMap["EndDate"] = sGuestEndDate!!.replace("/", "-")
                visitorOptionsMap["InTime"] = sGuestTime!!
//                visitorOptionsMap["ApprovalStatusId"] = 1
                if (isPublic!!) {
                    visitorOptionsMap["ApprovalStatusId"] =
                        approvalStatus?.get(0)?.statusId!!
                } else {
                    visitorOptionsMap["ApprovalStatusId"] =
                        approvalStatus?.get(1)?.statusId!!
                }
                visitorOptionsMap["AllowFor"] = "Schedule"
                visitorOptionsMap["RepeatId"] = sScheduleId!!


                if (visitorId != null){
                    visitorOptionsMap["visitorId"] = visitorId!!
                    updateInviteVisitorAPICall(visitorOptionsMap)

                }else{
                    inviteVisitorAPICall(visitorOptionsMap)

                }
            }
        }

    }
    private fun validateData(): Boolean {

//        sGuestType = serviceProviderId
        sGuestName = binding.etGuestName.text.toString()
        sGuestNumber = binding.etMobileNumber.text.toString()
        sGuestDate = binding.startDateTxt.text.toString()
        sGuestEndDate = binding.endDateTxt.text.toString()
        sGuestTime = binding.startTimeText.text.toString()
/*
        if (sGuestType == null) {
            Utils.showToast(requireContext(), "Please select guest type")
            return false
        }*/

        if (sGuestName!!.isEmpty()) {
            Utils.showToast(requireContext(), "Please enter name")
            return false
        }

        if (sGuestNumber!!.isEmpty()) {
            Utils.showToast(requireContext(), "Please enter mobile number")
            return false
        }


        if (sScheduleId == null){
            Utils.showToast(requireContext(), "Please select schedule")
            return false
        }else{
            if (binding.scheduleTxt.text.equals("Custom")) {
                if (sGuestDate!!.isEmpty()) {
                    Utils.showToast(requireContext(), "Please select date")
                    return false
                }

                if (sGuestEndDate!!.isEmpty()) {
                    Utils.showToast(requireContext(), "Please select end date")
                    return false
                }
            }

        }
        if (sGuestTime!!.isEmpty()) {
            Utils.showToast(requireContext(), "Please select time")
            return false
        }

        if (sGuestNumber!!.length < 10) {
            Utils.showToast(requireContext(), "Please enter valid mobile number")
            return false
        }

        if (sGuestNumber!!.length > 10) {
            Utils.showToast(requireContext(), "Please enter valid mobile number")
            return false
        }

        if (isPublic == null){
            Utils.showToast(requireContext(), "Do you want to pre-approve the visitor?")
            return false
        }
        return true
    }

    private fun inviteVisitorAPICall(visitorOptionsMap: MutableMap<String, Any>) {

        postVisitorCall =
            apiInterface.postVisitorAPICall("bearer $Auth_Token", visitorOptionsMap)
        postVisitorCall.enqueue(object : Callback<VisitorPostResponse> {
            override fun onResponse(
                call: Call<VisitorPostResponse>,
                response: Response<VisitorPostResponse>
            ) {
                if (response.isSuccessful && response.body() != null) {

                    Log.e("VisitorResponse", response.body().toString())

                    if (response.body()!!.statusCode != null) {
                        when (response.body()!!.statusCode) {
                            1 -> {
                                if (response.body()!!.message.isNotEmpty()) {

                                    Utils.showToast(
                                        requireContext(),
                                        response.body()!!.message.toString()
                                    )
                                    val intent = Intent(
                                        requireActivity(),
                                        VisitorActivity::class.java
                                    )
                                    startActivity(intent)
                                    requireActivity().finish()

                                }

                            }

                            else -> {
                                if (response.body()!!.message != null && response.body()!!.message.isNotEmpty()) {
                                    Utils.showToast(
                                        requireContext(),
                                        response.body()!!.message.toString()
                                    )
                                }
                            }
                        }
                    } else {

                    }
                }
            }

            override fun onFailure(call: Call<VisitorPostResponse>, t: Throwable) {
                customProgressDialog.progressDialogDismiss()
                Utils.showToast(requireContext(), t.message.toString())
            }

        })

    }

    private fun scheduleDropDown() {
        val layoutInflater: LayoutInflater =
            requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view: View = layoutInflater.inflate(R.layout.drop_down_layout, null)
        schedulePopupWindow = PopupWindow(
            view,
            binding.scheduleTxt.measuredWidth,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        if (scheduleList.isNotEmpty()) {

            val linearLayoutManager = LinearLayoutManager(requireContext())
            val dropDownRecyclerView = view.findViewById<RecyclerView>(R.id.drop_down_recycler_view)
            dropDownRecyclerView.layoutManager = linearLayoutManager
            val guestTypeAdapter = GuestScheduleAdapter(requireContext(), scheduleList)

            dropDownRecyclerView.adapter = guestTypeAdapter
            guestTypeAdapter.setCallbackOthersDate(this@OthersAllowFrequentlyFragment)
        }
        schedulePopupWindow!!.elevation = 10F
        schedulePopupWindow!!.showAsDropDown(binding.scheduleTxt, 0, 0, Gravity.CENTER)
    }


    @RequiresApi(Build.VERSION_CODES.Q)
    fun setSchedule(guestType: ScheduleModel.Data) {
        if (schedulePopupWindow != null) {
            if (schedulePopupWindow!!.isShowing) {
                schedulePopupWindow!!.dismiss()
            }
        }
        if (guestType != null) {
            binding.scheduleTxt.text = guestType.name
            sScheduleId = guestType.repeatId
            if (guestType.name.equals("Custom")) {

                binding.llDate.visibility = View.VISIBLE
            } else {
                binding.llDate.visibility = View.GONE
            }

        }


    }

    val dateSetListener__book_now_end_date = object : DatePickerDialog.OnDateSetListener {
        override fun onDateSet(
            view: DatePicker, year: Int, monthOfYear: Int,
            dayOfMonth: Int
        ) {
            // disable dates before today
            val disablePastDates = Calendar.getInstance().timeInMillis - 1000
            view.setMinDate(disablePastDates)
            cal_endDate.set(Calendar.YEAR, year)
            cal_endDate.set(Calendar.MONTH, monthOfYear)
            cal_endDate.set(Calendar.DAY_OF_MONTH, dayOfMonth)


            val myFormat = "dd/MM/YYYY" // mention the format you need
            val sdf = SimpleDateFormat(myFormat, Locale.US)
            binding.endDateTxt!!.text = sdf.format(cal_endDate.getTime())
        }
    }

    // create an OnDateSetListener
    val dateSetListener_book_now_start_date = object : DatePickerDialog.OnDateSetListener {
        override fun onDateSet(
            view: DatePicker, year: Int, monthOfYear: Int,
            dayOfMonth: Int
        ) {
            cal_startDate.set(Calendar.YEAR, year)
            cal_startDate.set(Calendar.MONTH, monthOfYear)
            cal_startDate.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            // disable dates before today
            val disablePastDates = Calendar.getInstance().timeInMillis
            view.setMinDate(disablePastDates)

            val myFormat = "dd/MM/YYYY" // mention the format you need
            val sdf = SimpleDateFormat(myFormat, Locale.US)
            binding.startDateTxt!!.text = sdf.format(cal_startDate.getTime())
        }
    }
}