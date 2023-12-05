package com.example.safehome.visitors.others

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.RadioButton
import androidx.fragment.app.Fragment
import com.example.safehome.Utils
import com.example.safehome.custom.CustomProgressDialog
import com.example.safehome.databinding.FragmentOthersAllowOnceBinding
import com.example.safehome.repository.APIClient
import com.example.safehome.repository.APIInterface
import com.example.safehome.visitors.ApprovalStatusModel
import com.example.safehome.visitors.GetAllVisitorDetailsModel
import com.example.safehome.visitors.ServiceProviderModel
import com.example.safehome.visitors.VisitorActivity
import com.example.safehome.visitors.VisitorPostResponse
import com.example.safehome.visitors.delivery.DeliveryActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class OthersAllowOnceFragment(private val visitorTypeId: Int? = null,
                              private val visitorListItem: GetAllVisitorDetailsModel.Data.Event? = null,
                              private val approvalStatus: ArrayList<ApprovalStatusModel.Data>? = null) : Fragment() {
    private lateinit var binding: FragmentOthersAllowOnceBinding
    var cal_startDate = Calendar.getInstance()

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
    private var visitorId: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOthersAllowOnceBinding.inflate(inflater, container, false)


        inIt()
        setIntentData()
        clickEvents()
        setRadioButton()

        return binding.root
    }
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
                    )
                )
            }
            if (visitorListItem.inTime != null){

                binding.startTimeText.text = Editable.Factory.getInstance().newEditable(visitorListItem.inTime)
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
        residentId = Utils.getStringPref(requireContext(), "residentId", "")
        Auth_Token = Utils.getStringPref(requireContext(), "Token", "")
    }
    private fun clickEvents() {


        binding.startDateTxt.setOnClickListener {
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

        binding.startTimeText.setOnClickListener {
            val mcurrentTime = Calendar.getInstance()
            val hour = mcurrentTime[Calendar.HOUR_OF_DAY]
            val minute = mcurrentTime[Calendar.MINUTE]
            val mTimePicker: TimePickerDialog
            mTimePicker = TimePickerDialog(
                requireContext(),
                { timePicker, selectedHour, selectedMinute ->
                    //setTime("StartTime", selectedHour, selectedMinute)
                    binding.startTimeText.text = String.format(
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
                visitorOptionsMap["InTime"] = sGuestTime!!
                if (isPublic!!) {
                    visitorOptionsMap["ApprovalStatusId"] =
                        approvalStatus?.get(0)?.statusId!!
                } else {
                    visitorOptionsMap["ApprovalStatusId"] =
                        approvalStatus?.get(1)?.statusId!!
                }
                visitorOptionsMap["AllowFor"] = "Once"


                Log.e("visitorOptionsMap", visitorOptionsMap.toString())
                if (visitorId != null){
                    visitorOptionsMap["visitorId"] = visitorId!!
                    updateInviteVisitorAPICall(visitorOptionsMap)

                }else{
                    inviteVisitorAPICall(visitorOptionsMap)

                }
            }
        }

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

    private fun validateData(): Boolean {

//        sGuestType = serviceProviderId
        sGuestName = binding.etGuestName.text.toString()
        sGuestNumber = binding.etMobileNumber.text.toString()
        sGuestDate = binding.startDateTxt.text.toString()
        sGuestTime = binding.startTimeText.text.toString()

        /*if (sGuestType == null) {
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

        if (sGuestDate!!.isEmpty()) {
            Utils.showToast(requireContext(), "Please select date")
            return false
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

    private val dateSetListener_book_now_start_date = object : DatePickerDialog.OnDateSetListener {
        override fun onDateSet(
            view: DatePicker, year: Int, monthOfYear: Int,
            dayOfMonth: Int
        ) {
            cal_startDate.set(Calendar.YEAR, year)
            cal_startDate.set(Calendar.MONTH, monthOfYear)
            cal_startDate.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            // disable dates before today
            val disablePastDates = Calendar.getInstance().timeInMillis
            view.minDate = disablePastDates

            val myFormat = "dd/MM/YYYY" // mention the format you need
            val sdf = SimpleDateFormat(myFormat, Locale.US)
            binding.startDateTxt.text = sdf.format(cal_startDate.time)
        }
    }
}