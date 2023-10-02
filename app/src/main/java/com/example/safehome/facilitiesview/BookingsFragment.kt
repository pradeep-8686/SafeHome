package com.example.safehome.facilitiesview

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
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
import com.example.safehome.adapter.FaciBookingsAdapter
import com.example.safehome.custom.CustomProgressDialog
import com.example.safehome.databinding.FragmentBookingsBinding
import com.example.safehome.model.AllFacilitiesModel
import com.example.safehome.model.DeleteFacilityModel
import com.example.safehome.model.FaciBookings
import com.example.safehome.model.GetAllHistoryServiceTypes
import com.example.safehome.repository.APIClient
import com.example.safehome.repository.APIInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class BookingsFragment : Fragment() {

    private var paid_on_date_txt: TextView?= null
    private var paymentModeOther: Dialog? =null
    private var payUsingOther: Dialog?= null
    private lateinit var binding: FragmentBookingsBinding
    private var bookingList: ArrayList<FaciBookings.Data.Facilility> = ArrayList()
    private lateinit var faciBookingsAdapter: FaciBookingsAdapter
    private var deleteMemberDialog: Dialog? = null
    private var escalateApprovalDialog: Dialog? = null
    var cal = Calendar.getInstance()

    private lateinit var customProgressDialog: CustomProgressDialog
    private lateinit var apiInterface: APIInterface
    var User_Id: String? = ""
    var Auth_Token: String? = ""

    private lateinit var facilitiesModel : Call<FaciBookings>
    private lateinit var deleteFacilitiesModel : Call<DeleteFacilityModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentBookingsBinding.inflate(inflater, container, false)

        customProgressDialog = CustomProgressDialog()
        apiInterface = APIClient.getClient(requireContext())
        User_Id = Utils.getStringPref(requireContext(), "residentId", "")
        Auth_Token = Utils.getStringPref(requireContext(), "Token", "")


        getAllBookedFacilities()
        return binding.root
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
                        if (bookingList.isNotEmpty()) {
                            bookingList.clear()
                        }
                        val facilitiesModel = response.body() as FaciBookings
                        bookingList = facilitiesModel.data.facililities as ArrayList<FaciBookings.Data.Facilility>

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
    private fun populateData() {
        if (bookingList.size == 0){
            binding.emptyBookingFacilitiesTxt.visibility = View.VISIBLE
        }else {
            binding.emptyBookingFacilitiesTxt.visibility = View.GONE
            binding.fbookingsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
            faciBookingsAdapter = FaciBookingsAdapter(requireContext(), bookingList)
            binding.fbookingsRecyclerView.adapter = faciBookingsAdapter
            faciBookingsAdapter.setCallback(this@BookingsFragment)
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    @SuppressLint("MissingInflatedId")
    fun cancelBooking(bookFacilityId : Int) {
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
             deleteTenantServiceCall(bookFacilityId)
            if (deleteMemberDialog!!.isShowing) {
                deleteMemberDialog!!.dismiss()
            }
        }

        no.setOnClickListener {
            if (deleteMemberDialog!!.isShowing) {
                deleteMemberDialog!!.dismiss()
            }
        }
        deleteMemberDialog!!.show()
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun deleteTenantServiceCall(bookFacilityId: Int) {

        var customProgressDialog = CustomProgressDialog()

        customProgressDialog.progressDialogShow(
            requireContext(),
            this.getString(R.string.loading)
        )

        deleteFacilitiesModel = apiInterface.deleteFacility("bearer "+Auth_Token, bookFacilityId)
        deleteFacilitiesModel.enqueue(object : Callback<DeleteFacilityModel>{
            override fun onResponse(
                call: Call<DeleteFacilityModel>,
                response: Response<DeleteFacilityModel>
            ) {
                customProgressDialog.progressDialogDismiss()
                // here successfully response
                if (response.isSuccessful && response.body() != null) {

                    if (response.body()!!.statusCode!= null){
                        when(response.body()!!.statusCode){
                            1 -> {
                                if (response.body()!!.message != null && response.body()!!.message.isNotEmpty()) {
                                    Utils.showToast(requireContext(), response.body()!!.message.toString())
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

            override fun onFailure(call: Call<DeleteFacilityModel>, t: Throwable) {
                customProgressDialog.progressDialogDismiss()
                Utils.showToast(requireContext(), t.message.toString())
            }

        })

    }

    fun clickOnPaymentStatus(paymentStatus: String) {
        if (paymentStatus!= null){
            if (paymentStatus == "UnPaid"){
                showMakePaymentDialog()
            }else{
                showEscalateApprovalDialog()
            }
        }
    }

    @SuppressLint("MissingInflatedId")
    private fun showEscalateApprovalDialog() {
        val layoutInflater: LayoutInflater =
            requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = layoutInflater.inflate(R.layout.facilities_escalate_approval_dialog, null)
        escalateApprovalDialog = Dialog(requireContext(), R.style.CustomAlertDialog)
        escalateApprovalDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        escalateApprovalDialog!!.setContentView(view)
        escalateApprovalDialog!!.setCanceledOnTouchOutside(true)
        escalateApprovalDialog!!.setCancelable(true)

        val lp = WindowManager.LayoutParams()
        lp.copyFrom(escalateApprovalDialog!!.window!!.attributes)

        lp.width = (Utils.screenWidth * 0.9).toInt()
        lp.height = LinearLayout.LayoutParams.WRAP_CONTENT
        lp.gravity = Gravity.CENTER
        escalateApprovalDialog!!.window!!.attributes = lp

        val textOk = view.findViewById<TextView>(R.id.escalate_dialog_Ok_txt)
        val closeImg = view.findViewById<ImageView>(R.id.escalated_dialog_close)
        closeImg.setOnClickListener {
            if (escalateApprovalDialog!!.isShowing){
                escalateApprovalDialog!!.dismiss()
            }
        }
        textOk.setOnClickListener {

        }

        escalateApprovalDialog!!.show()
    }

    private fun showMakePaymentDialog() {
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
        val third_party_name_radio_option = view.findViewById<RadioButton>(R.id.third_party_name) as RadioButton
        val other_radio_Option = view.findViewById<RadioButton>(R.id.other_rd_option) as RadioButton
        third_party_name_radio_option.text = "Safe Home app"
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
                selectedBookingPayNow()
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
        payUsingOther!!.show()    }

    fun selectedBookingPayNow() {
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
        paid_on_date_txt = view.findViewById<TextView>(R.id.paid_on_date_txt)
        val cancel_btn = view.findViewById<TextView>(R.id.cancel_btn)
        val comments_et: EditText = view.findViewById(R.id.comments_et)
        comments_et.setBackgroundResource(android.R.color.transparent)
        val saveBtn = view.findViewById<TextView>(R.id.save_btn)

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

        saveBtn.setOnClickListener {
            if (paymentModeOther!!.isShowing) {
                paymentModeOther!!.dismiss()
            }
        }
        paymentModeOther!!.show()
    }

    fun editBookingRequest(faciBookings: FaciBookings.Data.Facilility) {
        try {
            val fIntent = Intent(requireContext(), ListBookNowActivity::class.java)
            fIntent.putExtra("bookType", faciBookings.name)
            fIntent.putExtra("facilityId", faciBookings.facilityId)

            fIntent.putExtra("bookByHour", 1.00)
            fIntent.putExtra("bookByTime", 1.00)
            fIntent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            getActivity()?.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            requireContext().startActivity(fIntent)
        } catch (e: Exception) {
        }
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
            paid_on_date_txt!!.text = sdf.format(cal.time)
        }
    }

}