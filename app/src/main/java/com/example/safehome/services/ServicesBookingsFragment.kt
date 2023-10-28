package com.example.safehome.services

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.DatePicker
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.safehome.R
import com.example.safehome.Utils
import com.example.safehome.custom.CustomProgressDialog
import com.example.safehome.databinding.FragmentServicesBookingBinding
import com.example.safehome.model.AddServiceBookingList
import com.example.safehome.model.DailyHelpMemberList
import com.example.safehome.model.ServiceTypesList
import com.example.safehome.model.ServicesBookingsList
import com.example.safehome.repository.APIClient
import com.example.safehome.repository.APIInterface
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class ServicesBookingsFragment : Fragment() {
    private var time: String? = null
    private lateinit var deleteServiceMemberCall: Call<AddServiceBookingList>
    private lateinit var updatebookServiceCall: Call<AddServiceBookingList>
    private var Auth_Token: String? = null
    private var residentId: String? = null
    private lateinit var binding: FragmentServicesBookingBinding
    private var dailyHelpMembersList: ArrayList<DailyHelpMemberList> = ArrayList()
    private lateinit var servicesBookingListAdapter: ServicesBookingListAdapter
    private var paymentModeOther: Dialog? = null
    private var payUsingOther: Dialog? = null
    private var bookingOtherDialog: Dialog? = null
    private var paid_on_date_txt: TextView? = null
    var cal = Calendar.getInstance()
    private var deleteMemberDialog: Dialog? = null
    var start_date_txt: TextView? = null
    var end_date_txt: TextView? = null
    var start_time_text: TextView? = null
    var end_time_text: TextView? = null
    var cal_startDate = Calendar.getInstance()
    var cal_endDate = Calendar.getInstance()
    private lateinit var radioButton: RadioButton
    private lateinit var customProgressDialog: CustomProgressDialog
    private lateinit var apiInterface: APIInterface
    private lateinit var getBookingsServicesListCall: Call<ServicesBookingsList>
    private var servicesBookingsList: ArrayList<ServicesBookingsList.Data> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentServicesBookingBinding.inflate(inflater, container, false)


        inIt()
        binding.sampleEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if (servicesBookingsList.isNotEmpty()) {

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

        val servicesDataMemberList = ArrayList<ServicesBookingsList.Data>()
        val courseAry: ArrayList<ServicesBookingsList.Data> = servicesBookingsList

        for (eachCourse in courseAry) {

            if (!eachCourse.personName.isNullOrBlank() && eachCourse.personName.lowercase(Locale.getDefault())
                    .contains(text.lowercase(Locale.getDefault())) ||
                !eachCourse.serviceTypeName.isNullOrBlank() && eachCourse.serviceTypeName.lowercase(
                    Locale.getDefault()
                )
                    .contains(text.lowercase(Locale.getDefault()))

            ) {
                servicesDataMemberList.add(eachCourse)
            }
        }

        servicesBookingListAdapter.filterList(servicesDataMemberList);
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun inIt() {
        customProgressDialog = CustomProgressDialog()
        apiInterface = APIClient.getClient(requireContext())
        Auth_Token = Utils.getStringPref(requireContext(), "Token", "")
        residentId = Utils.getStringPref(requireContext(), "residentId", "")!!

        addDailyHelpMemberListData()
        populateData()
        getBookingsListServiceCall()
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun getBookingsListServiceCall() {
        customProgressDialog.progressDialogShow(
            requireContext(),
            this.getString(R.string.loading)
        )

        // here sign up service call
        getBookingsServicesListCall = apiInterface.getAllBookedServiceByResidentId(
            "Bearer " + Auth_Token, ResidentId = residentId!!.toInt()
        )
        getBookingsServicesListCall.enqueue(object : Callback<ServicesBookingsList> {
            override fun onResponse(
                call: Call<ServicesBookingsList>,
                response: Response<ServicesBookingsList>
            ) {
                customProgressDialog.progressDialogDismiss()
                // here successfully response
                if (response.isSuccessful && response.body() != null) {

                    if (response.body()!!.statusCode != null) {
                        when (response.body()!!.statusCode) {
                            1 -> {
                                if (response.body()!!.message != null && response.body()!!.message.isNotEmpty()) {
                                    //     Utils.showToast(requireContext(), response.body()!!.message.toString())
                                    if (servicesBookingsList.isNotEmpty()) {
                                        servicesBookingsList.clear()
                                    }
                                    servicesBookingsList =
                                        response.body()!!.data as ArrayList<ServicesBookingsList.Data>
                                }
                                populateData()

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
                    }
                } else {
                    customProgressDialog.progressDialogDismiss()
                    Utils.showToast(requireContext(), response.body()!!.message.toString())
                }
            }

            override fun onFailure(call: Call<ServicesBookingsList>, t: Throwable) {
                customProgressDialog.progressDialogDismiss()
                Utils.showToast(requireContext(), t.message.toString())
            }
        })
    }

    private fun addDailyHelpMemberListData() {
        dailyHelpMembersList.add(
            DailyHelpMemberList(
                "Raju", "5 A.M-6 A.M", "Weekdays", "B104,B102",
                "9876543210", "4.1", "", "", "", "", "AC Service & Repair"
            )
        )
        dailyHelpMembersList.add(
            DailyHelpMemberList(
                "Suresh", "5 A.M-6 A.M", "Weekdays", "B104,B102",
                "9876543210", "4.1", "", "", "", "", "Plumber"
            )
        )
        dailyHelpMembersList.add(
            DailyHelpMemberList(
                "Mohan", "5 A.M-6 A.M", "Weekend", "B104,B102",
                "9876543210", "4.1", "", "", "", "", "Electrician"
            )
        )
    }

    private fun populateData() {

        if (servicesBookingsList.size == 0) {
            binding.emptyBookingFacilitiesTxt.visibility = View.VISIBLE
        } else {
            binding.emptyBookingFacilitiesTxt.visibility = View.GONE
            binding.dailyHelpBookingRecyclerView.layoutManager =
                LinearLayoutManager(requireContext())
            servicesBookingListAdapter =
                ServicesBookingListAdapter(requireContext(), servicesBookingsList)
            binding.dailyHelpBookingRecyclerView.adapter = servicesBookingListAdapter
            servicesBookingListAdapter.setCallback(this@ServicesBookingsFragment)
        }
    }


    @SuppressLint("MissingInflatedId")
    @RequiresApi(Build.VERSION_CODES.Q)
    fun selectedBookingItem(dailyHelpMemberList: ServicesBookingsList.Data) {
        val layoutInflater: LayoutInflater =
            requireActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = layoutInflater.inflate(R.layout.booking_in_service_dialog, null)
        bookingOtherDialog = Dialog(requireContext(), R.style.CustomAlertDialog)
        bookingOtherDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        bookingOtherDialog!!.setContentView(view)
        bookingOtherDialog!!.setCanceledOnTouchOutside(true)
        bookingOtherDialog!!.setCancelable(true)

        val lp = WindowManager.LayoutParams()
        lp.copyFrom(bookingOtherDialog!!.window!!.attributes)

        lp.width = (Utils.screenWidth * 0.9).toInt()
        lp.height = LinearLayout.LayoutParams.WRAP_CONTENT
        lp.gravity = Gravity.CENTER
        bookingOtherDialog!!.window!!.attributes = lp

        // here logic ..
        val close = view.findViewById<ImageView>(R.id.close_btn_click)

        val member_Role = view.findViewById<TextView>(R.id.member_Role)
        val dailyHelpMemberListname_text = view.findViewById<TextView>(R.id.name_text)

        member_Role.setText(dailyHelpMemberList.serviceTypeName)
        dailyHelpMemberListname_text.setText(dailyHelpMemberList.personName)

        start_date_txt = view.findViewById<TextView>(R.id.start_date_txt)
//        val end_date_txt = view.findViewById<TextView>(R.id.end_date_txt)
        start_time_text = view.findViewById<TextView>(R.id.start_time_text)
//        val end_time_text = view.findViewById<TextView>(R.id.end_time_text)

        val delete_btn = view.findViewById<TextView>(R.id.delete_btn)
        val edit_btn = view.findViewById<TextView>(R.id.edit_btn)

        delete_btn.setOnClickListener {
            if (bookingOtherDialog!!.isShowing) {
                bookingOtherDialog!!.dismiss()
            }
            cancelBooking(dailyHelpMemberList.serviceBookingId)
        }

        edit_btn.setOnClickListener {
            if (bookingOtherDialog!!.isShowing) {
                bookingOtherDialog!!.dismiss()
            }
         //   updateBookingDialog(dailyHelpMemberList)
                val intent = Intent(requireContext(), ServiceSelectedMemberInfoActivity::class.java)
                intent.putExtra("servicesBookingsList", dailyHelpMemberList)
                intent.putExtra("from", "update")
                startActivity(intent)
        }

        close.setOnClickListener {
            if (bookingOtherDialog!!.isShowing) {
                bookingOtherDialog!!.dismiss()
            }
        }
        bookingOtherDialog!!.show()
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun updateBookingDialog(dailyHelpMemberList: ServicesBookingsList.Data) {
        val layoutInflater: LayoutInflater =
            requireActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = layoutInflater.inflate(R.layout.service_update_dialog, null)
        bookingOtherDialog = Dialog(requireContext(), R.style.CustomAlertDialog)
        bookingOtherDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        bookingOtherDialog!!.setContentView(view)
        bookingOtherDialog!!.setCanceledOnTouchOutside(true)
        bookingOtherDialog!!.setCancelable(true)

        val lp = WindowManager.LayoutParams()
        lp.copyFrom(bookingOtherDialog!!.window!!.attributes)

        lp.width = (Utils.screenWidth * 0.9).toInt()
        lp.height = LinearLayout.LayoutParams.WRAP_CONTENT
        lp.gravity = Gravity.CENTER
        bookingOtherDialog!!.window!!.attributes = lp

        // here logic ..
        val close = view.findViewById<ImageView>(R.id.close_btn_click)

        val member_Role = view.findViewById<TextView>(R.id.member_Role)
        val dailyHelpMemberListname_text = view.findViewById<TextView>(R.id.name_text)

        member_Role.setText(dailyHelpMemberList.serviceTypeName)
        dailyHelpMemberListname_text.setText(dailyHelpMemberList.personName)

        start_date_txt = view.findViewById<TextView>(R.id.start_date_txt)
//        end_date_txt = view.findViewById<TextView>(R.id.end_date_txt)
        start_time_text = view.findViewById<TextView>(R.id.start_time_text)
//        end_time_text = view.findViewById<TextView>(R.id.end_time_text)

        timeSet()

        val update_btn = view.findViewById<TextView>(R.id.update_btn)

        update_btn.setOnClickListener {
            updateServiceBookingListServiceCall(
                dailyHelpMemberList.serviceId,
                dailyHelpMemberList.serviceBookingId
            )
            if (bookingOtherDialog!!.isShowing) {
                bookingOtherDialog!!.dismiss()
            }
        }

        close.setOnClickListener {
            if (bookingOtherDialog!!.isShowing) {
                bookingOtherDialog!!.dismiss()
            }
        }
        bookingOtherDialog!!.show()
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun updateServiceBookingListServiceCall(serviceId: Int, serviceBookingId: Int) {
        customProgressDialog.progressDialogShow(requireContext(), this.getString(R.string.loading))
        var date = start_date_txt!!.text.toString()
        date = Utils.changeDateFormatToMMDDYYYY(date)
        var time: String = start_time_text!!.text.toString().replace(":", "-")
        val jsonObject = JsonObject()
        jsonObject.addProperty("ServiceBookingId", serviceBookingId)
        jsonObject.addProperty("ServiceId", serviceId)
        jsonObject.addProperty("Date", date)
        jsonObject.addProperty("Time", time)
        jsonObject.addProperty("BookFor", "individual resident")
        jsonObject.addProperty("ResidentId", residentId!!.toInt())
        updatebookServiceCall =
            apiInterface.updateServiceBooking("bearer " + Auth_Token, jsonObject)
        updatebookServiceCall.enqueue(object : Callback<AddServiceBookingList> {
            override fun onResponse(
                call: Call<AddServiceBookingList>,
                response: Response<AddServiceBookingList>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    customProgressDialog.progressDialogDismiss()
                    if (response.body()!!.statusCode != null) {
                        when (response.body()!!.statusCode) {
                            1 -> {
                                if (response.body()!!.message != null && response.body()!!.message.isNotEmpty()) {
                                    Utils.showToast(
                                        requireContext(),
                                        response.body()!!.message.toString()
                                    )
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
                    }
                } else {
                    customProgressDialog.progressDialogDismiss()
                }
            }

            override fun onFailure(call: Call<AddServiceBookingList>, t: Throwable) {
                customProgressDialog.progressDialogDismiss()
                Utils.showToast(requireContext(), t.message.toString())
            }

        })
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun deleteServiceMemberNetworkCall(serviceBookingId: Int) {
        customProgressDialog.progressDialogShow(requireContext(), this.getString(R.string.loading))
        deleteServiceMemberCall =
            apiInterface.deleteServiceBookingById("bearer " + Auth_Token, serviceBookingId)
        deleteServiceMemberCall.enqueue(object : Callback<AddServiceBookingList> {
            override fun onResponse(
                call: Call<AddServiceBookingList>,
                response: Response<AddServiceBookingList>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    if (deleteMemberDialog!!.isShowing) {
                        deleteMemberDialog!!.dismiss()
                    }
                    customProgressDialog.progressDialogDismiss()
                    if (response.body()!!.statusCode != null) {
                        when (response.body()!!.statusCode) {
                            1 -> {
                                if (response.body()!!.message != null && response.body()!!.message.isNotEmpty()) {
                                    Utils.showToast(
                                        requireContext(),
                                        response.body()!!.message.toString()
                                    )
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
                    }
                } else {
                    customProgressDialog.progressDialogDismiss()
                    Utils.showToast(requireContext(), response.body()!!.message.toString())
                    if (deleteMemberDialog!!.isShowing) {
                        deleteMemberDialog!!.dismiss()
                    }
                }
            }

            override fun onFailure(call: Call<AddServiceBookingList>, t: Throwable) {
                customProgressDialog.progressDialogDismiss()
                Utils.showToast(requireContext(), t.message.toString())
                if (deleteMemberDialog!!.isShowing) {
                    deleteMemberDialog!!.dismiss()
                }
            }

        })
    }

    private fun timeSet() {
        start_date_txt?.setOnClickListener {
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

        end_date_txt?.setOnClickListener {
            val endDateDialog = DatePickerDialog(
                requireContext(),
                dateSetListener__book_now_end_date,
                // set DatePickerDialog to point to today's date when it loads up
                cal_endDate.get(Calendar.YEAR),
                cal_endDate.get(Calendar.MONTH),
                cal_endDate.get(Calendar.DAY_OF_MONTH)
            )
            endDateDialog.datePicker.minDate = System.currentTimeMillis() - 1000
            endDateDialog.show()
        }

        start_time_text?.setOnClickListener {
            val mcurrentTime = Calendar.getInstance()
            val hour = mcurrentTime[Calendar.HOUR_OF_DAY]
            val minute = mcurrentTime[Calendar.MINUTE]
            val mTimePicker: TimePickerDialog
            mTimePicker = TimePickerDialog(
                requireContext(),
                { timePicker, selectedHour, selectedMinute ->
                    start_time_text!!.text = String.format(
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

        end_time_text?.setOnClickListener {
            val mcurrentTime = Calendar.getInstance()
            val hour = mcurrentTime[Calendar.HOUR_OF_DAY]
            val minute = mcurrentTime[Calendar.MINUTE]
            val mTimePicker: TimePickerDialog
            mTimePicker = TimePickerDialog(
                requireContext(),
                { timePicker, selectedHour, selectedMinute ->
                    //    setTime("EndTime", selectedHour, selectedMinute)
                    end_time_text!!.text = String.format(
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
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun selectedBookingPayUsing(dailyHelpMemberList: ServicesBookingsList.Data) {
        val layoutInflater: LayoutInflater =
            requireActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = layoutInflater.inflate(R.layout.pay_using_in_daily_help_dialog, null)
        payUsingOther = Dialog(requireContext(), R.style.CustomAlertDialog)
        payUsingOther!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        payUsingOther!!.setContentView(view)
        payUsingOther!!.setCanceledOnTouchOutside(true)
        payUsingOther!!.setCancelable(true)

        val lp = WindowManager.LayoutParams()
        lp.copyFrom(payUsingOther!!.window!!.attributes)

        lp.width = (Utils.screenWidth * 0.9).toInt()
        lp.height = LinearLayout.LayoutParams.WRAP_CONTENT
        lp.gravity = Gravity.CENTER
        payUsingOther!!.window!!.attributes = lp

        // here logic ..
        val close = view.findViewById<ImageView>(R.id.close_btn_click)
        val continue_btn = view.findViewById<TextView>(R.id.continue_btn)

        val radioGroup = view.findViewById<RadioGroup>(R.id.rdGroup) as RadioGroup
        val third_party_name_radio_option =
            view.findViewById<RadioButton>(R.id.third_party_name) as RadioButton
        val other_radio_Option = view.findViewById<RadioButton>(R.id.other_rd_option) as RadioButton

        // get selected radio button from radioGroup
        val selectedId: Int = radioGroup.getCheckedRadioButtonId()
        // find the radiobutton by returned id
//        radioButton = view.findViewById<RadioButton>(selectedId) as RadioButton

        continue_btn.setOnClickListener {
            if (radioGroup?.getCheckedRadioButtonId() == -1) {
                Toast.makeText(requireContext(), "Please select option", Toast.LENGTH_LONG).show()
            } else {
                if (payUsingOther!!.isShowing) {
                    payUsingOther!!.dismiss()
                }
//                Toast.makeText(requireContext(),radioButton?.text.toString(),Toast.LENGTH_LONG).show()
//                if (radioButton?.text.toString().equals("Other")) {
                selectedBookingPayNow(dailyHelpMemberList)
//                } else {
//                    if (payUsingOther!!.isShowing) {
//                        payUsingOther!!.dismiss()
//                    }
//                }
            }
        }

        close.setOnClickListener {
            if (payUsingOther!!.isShowing) {
                payUsingOther!!.dismiss()
            }
        }
        payUsingOther!!.show()
    }

    // create an OnDateSetListener
    val dateSetListener = object : DatePickerDialog.OnDateSetListener {
        override fun onDateSet(
            view: DatePicker, year: Int, monthOfYear: Int,
            dayOfMonth: Int
        ) {
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, monthOfYear)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            val myFormat = "dd/MM/YYYY" // mention the format you need
            val sdf = SimpleDateFormat(myFormat, Locale.US)
            paid_on_date_txt!!.text = sdf.format(cal.getTime())
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    @SuppressLint("MissingInflatedId")
    fun cancelBooking(serviceBookingId: Int) {
        val layoutInflater: LayoutInflater =
            requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = layoutInflater.inflate(R.layout.delete_tenant_dialog_popup, null)
        deleteMemberDialog = Dialog(requireContext(), R.style.CustomAlertDialog)
        deleteMemberDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        deleteMemberDialog!!.setContentView(view)
        deleteMemberDialog!!.setCanceledOnTouchOutside(true)
        deleteMemberDialog!!.setCancelable(true)

        val lp = WindowManager.LayoutParams()
        lp.copyFrom(deleteMemberDialog!!.window!!.attributes)

        lp.width = (Utils.screenWidth * 0.9).toInt()
        lp.height = LinearLayout.LayoutParams.WRAP_CONTENT
        lp.gravity = Gravity.CENTER
        deleteMemberDialog!!.window!!.attributes = lp

        val close = view.findViewById<ImageView>(R.id.close)
        val no: TextView = view.findViewById(R.id.no_btn)
        val yes: TextView = view.findViewById(R.id.yes_btn)
        val member_mob_number: TextView = view.findViewById(R.id.member_mob_number)
        member_mob_number.setText("Are you sure you want to cancel this Booking?")

        close.setOnClickListener {
            if (deleteMemberDialog!!.isShowing) {
                deleteMemberDialog!!.dismiss()
            }
        }

        yes.setOnClickListener {
            // deleteTenantServiceCall(tenant)
            deleteServiceMemberNetworkCall(serviceBookingId)
        }

        no.setOnClickListener {
            if (deleteMemberDialog!!.isShowing) {
                deleteMemberDialog!!.dismiss()
            }
        }
        deleteMemberDialog!!.show()
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
            start_date_txt!!.text = sdf.format(cal_startDate.getTime())
        }
    }

    // create an OnDateSetListener
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
            end_date_txt!!.text = sdf.format(cal_endDate.getTime())
        }
    }

    private fun setTime(Time: String, selectedHour: Int, selectedMinute: Int) {
        try {
            /*var hour_str: String? = null
            if (selectedHour < 10) {
                hour_str = "0" + selectedHour
            } else {
                hour_str = selectedHour.toString()
            }

            var minute_str: String? = null
            if (selectedMinute < 10) {
                minute_str = "0" + selectedMinute
            } else {
                minute_str = selectedMinute.toString()
            }
            if (Time.equals("StartTime")) {
                start_time_text?.setText("$hour_str:$minute_str")
            } else {
                end_time_text?.setText("$hour_str:$minute_str")
            }*/

            var hour: Int = selectedHour
            if (selectedHour in 0..11) {
                time = "$selectedHour:$selectedMinute AM"
            } else {
                if (selectedHour == 12) {
                    time = "$selectedHour:$selectedMinute PM"
                } else {
                    hour = hour!! - 12;
                    time = "$hour:$selectedMinute PM"; }
            }

            if (Time == "StartTime") {
                start_time_text!!.text = time
            } else {
                end_time_text!!.text = time
            }

        } catch (ex: Exception) {
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun selectedBookingPayNow(dailyHelpMemberList: ServicesBookingsList.Data) {
        val layoutInflater: LayoutInflater =
            requireActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = layoutInflater.inflate(R.layout.payment_mode_booking_in_daily_help_dialog, null)
        paymentModeOther = Dialog(requireContext(), R.style.CustomAlertDialog)
        paymentModeOther!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        paymentModeOther!!.setContentView(view)
        paymentModeOther!!.setCanceledOnTouchOutside(true)
        paymentModeOther!!.setCancelable(true)

        val lp = WindowManager.LayoutParams()
        lp.copyFrom(paymentModeOther!!.window!!.attributes)

        lp.width = (Utils.screenWidth * 0.9).toInt()
        lp.height = LinearLayout.LayoutParams.WRAP_CONTENT
        lp.gravity = Gravity.CENTER
        paymentModeOther!!.window!!.attributes = lp

        // here logic ..
        val close = view.findViewById<ImageView>(R.id.close_btn_click)
        val cancel_btn = view.findViewById<TextView>(R.id.cancel_btn)
        paid_on_date_txt = view.findViewById<TextView>(R.id.paid_on_date_txt)
        val comments_et: EditText = view.findViewById(R.id.comments_et)
        comments_et.setBackgroundResource(android.R.color.transparent)
        val save_btn: TextView = view.findViewById(R.id.save_btn)
        save_btn.setOnClickListener {
            updatePaymentBookingServiceCall(dailyHelpMemberList.serviceBookingId)
        }
        paid_on_date_txt?.setOnClickListener {
            val datePickerDialog = DatePickerDialog(
                requireContext(),
                dateSetListener,
                // set DatePickerDialog to point to today's date when it loads up
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            )

            datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000
            datePickerDialog.show()
        }

        close.setOnClickListener {
            if (paymentModeOther!!.isShowing) {
                paymentModeOther!!.dismiss()
            }
        }
        cancel_btn.setOnClickListener {
            if (paymentModeOther!!.isShowing) {
                paymentModeOther!!.dismiss()
            }
        }
        paymentModeOther!!.show()
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun updatePaymentBookingServiceCall(serviceBookingId: Int) {
        customProgressDialog.progressDialogShow(requireContext(), this.getString(R.string.loading))
        updatebookServiceCall = apiInterface.updateServiceBookingPayment(
            "bearer " + Auth_Token,
            serviceBookingId,
            residentId!!.toInt(),
            "Other",
            "UPI",
            "success",
            "QE2453FGFR67",
            "",
            ""
        )
        updatebookServiceCall.enqueue(object : Callback<AddServiceBookingList> {
            override fun onResponse(
                call: Call<AddServiceBookingList>,
                response: Response<AddServiceBookingList>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    customProgressDialog.progressDialogDismiss()
                    if (response.body()!!.statusCode != null) {
                        when (response.body()!!.statusCode) {
                            1 -> {
                                if (response.body()!!.message != null && response.body()!!.message.isNotEmpty()) {
                                    Utils.showToast(
                                        requireContext(),
                                        response.body()!!.message.toString()
                                    )
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
                    }
                    moveServiceActivity()
                } else {
                    customProgressDialog.progressDialogDismiss()
                    moveServiceActivity()

                }
            }

            override fun onFailure(call: Call<AddServiceBookingList>, t: Throwable) {
                moveServiceActivity()
                customProgressDialog.progressDialogDismiss()
                Utils.showToast(requireContext(), t.message.toString())
            }

        })
    }

    fun moveServiceActivity() {
        val intent: Intent = Intent(requireContext(), ServicesActivity::class.java)
        startActivity(intent)
    }

}