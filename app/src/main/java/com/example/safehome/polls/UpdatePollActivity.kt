package com.example.safehome.polls

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.DatePicker
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.safehome.HomeActivity
import com.example.safehome.R
import com.example.safehome.Utils
import com.example.safehome.adapter.AssignToAdapter
import com.example.safehome.adapter.ComplaintTypeAdapter
import com.example.safehome.custom.CustomProgressDialog
import com.example.safehome.databinding.ActivityUpdatePollBinding
import com.example.safehome.maintenance.HistoryFragment
import com.example.safehome.model.AvailabilityTime
import com.example.safehome.repository.APIClient
import com.example.safehome.repository.APIInterface
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class UpdatePollActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUpdatePollBinding
    private lateinit var customProgressDialog: CustomProgressDialog
    private lateinit var apiInterface: APIInterface
    var User_Id: String? = ""
    var Auth_Token: String? = ""

    private var attachPhotoWindow: PopupWindow? = null

    private val IMAGE_CHOOSE = 1000;
    private val PERMISSION_CODE = 1001;
    private var availabilityTimePopWindow: PopupWindow? = null
    private var logoutConfirmationDialog: Dialog? = null
    private var availabiltyTimeList: ArrayList<AvailabilityTime> = ArrayList()
    private var selectedItems : String? = ""
    private var keepPollPopupWindow: PopupWindow? = null
    private var keepPollList: ArrayList<String> = ArrayList()

    var cal_startDate = Calendar.getInstance()
    var cal_endDate = Calendar.getInstance()
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdatePollBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // progressbar
        customProgressDialog = CustomProgressDialog()
        apiInterface = APIClient.getClient(this)
        User_Id = Utils.getStringPref(this, "User_Id", "")
        Auth_Token = Utils.getStringPref(this, "Token", "")

        setKeepPollData()
        addAssignToList()



        binding.backBtnClick.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }
        binding.btnCancel.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }

        binding.btnCreate.setOnClickListener {

        }


        binding.tvAttachPhoto.setOnClickListener {

            attachPhotoPopup()
        }
        binding.imgAttachPhoto.setOnClickListener {

            attachPhotoPopup()

        }
        binding.etPollTo.setOnClickListener{
            assignToPopup()
        }

        binding.clTvComplaintType.setOnClickListener {
            keepPoll()
        }

        binding.startDateTxt?.setOnClickListener {
            val startDateDialog =  DatePickerDialog(this@UpdatePollActivity,
                dateSetListener_book_now_start_date,
                // set DatePickerDialog to point to today's date when it loads up
                cal_startDate.get(Calendar.YEAR),
                cal_startDate.get(Calendar.MONTH),
                cal_startDate.get(Calendar.DAY_OF_MONTH))

            startDateDialog.datePicker.minDate = System.currentTimeMillis() - 1000
            startDateDialog.show()
        }

        binding.endDateTxt?.setOnClickListener {
            val endDateDialog = DatePickerDialog(this@UpdatePollActivity,
                dateSetListener__book_now_end_date,
                // set DatePickerDialog to point to today's date when it loads up
                cal_endDate.get(Calendar.YEAR),
                cal_endDate.get(Calendar.MONTH),
                cal_endDate.get(Calendar.DAY_OF_MONTH))
            // Set a minimum date to disable previous dates
            try {
                val dateInMillis = Utils.convertStringToDateMillis(binding.startDateTxt!!.text.toString(), "dd/MM/yyyy")
                endDateDialog.datePicker.minDate = dateInMillis
            }catch (e: Exception){
                endDateDialog.datePicker.minDate = System.currentTimeMillis() - 1000 // Subtract 1 second to prevent selecting the current day
            }
            // endDateDialog.datePicker.minDate = System.currentTimeMillis() - 1000
            endDateDialog.show()
        }


    }

    private fun attachPhotoPopup() {
        if (attachPhotoWindow != null) {
            if (attachPhotoWindow!!.isShowing) {
                attachPhotoWindow!!.dismiss()
            } else {
                attachPhotoPopupDropDown()
            }
        } else {
            attachPhotoPopupDropDown()
        }
    }
    private fun attachPhotoPopupDropDown() {
        val layoutInflater: LayoutInflater =
            getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view: View = layoutInflater.inflate(R.layout.attach_photo_down_layout, null)

        attachPhotoWindow = PopupWindow(
            view,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        val photo_library_cl  = view.findViewById<ConstraintLayout>(R.id.photo_library_cl)
        photo_library_cl.setOnClickListener {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_DENIED){
                    val permissions = arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    requestPermissions(permissions, PERMISSION_CODE)
                } else{
                    chooseImageFromGallery();
                }
            }else{
                chooseImageFromGallery();
            }

        }

        attachPhotoWindow!!.elevation = 10F
        attachPhotoWindow!!.showAsDropDown(binding.imgAttachPhoto, 0, 0, Gravity.CENTER)
    }

    private fun chooseImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_CHOOSE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    chooseImageFromGallery()
                }else{
                    Toast.makeText(this,"Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == IMAGE_CHOOSE && resultCode == Activity.RESULT_OK){
            //   viewImage.setImageURI(data?.data)
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun assignToPopup() {
        if (availabilityTimePopWindow != null) {
            if (availabilityTimePopWindow!!.isShowing) {
                availabilityTimePopWindow!!.dismiss()
            } else {
//                    availabilityTimeDropDown()
                assignToPopupDialog()
            }
        } else {
//                availabilityTimeDropDown()
            assignToPopupDialog()
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    @SuppressLint("MissingInflatedId")
    private fun assignToPopupDialog() {
        val layoutInflater: LayoutInflater =
            getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = layoutInflater.inflate(R.layout.assign_to_layout, null)
        logoutConfirmationDialog = Dialog(this@UpdatePollActivity, R.style.CustomAlertDialog)
        logoutConfirmationDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        logoutConfirmationDialog!!.setContentView(view)
        logoutConfirmationDialog!!.setCanceledOnTouchOutside(true)
        logoutConfirmationDialog!!.setCancelable(true)

        val lp = WindowManager.LayoutParams()
        lp.copyFrom(logoutConfirmationDialog!!.window!!.attributes)

//        lp.width = (Utils.screenWidth * 0.9).toInt()
//        lp.height = LinearLayout.LayoutParams.WRAP_CONTENT
//        lp.gravity = Gravity.CENTER
//        logoutConfirmationDialog!!.window!!.attributes = lp

        val close = view.findViewById<ImageView>(R.id.close)
        val yes_btn: TextView = view.findViewById(R.id.yes_btn)


        val recyclerView: RecyclerView = view.findViewById(R.id.rv_available_time)
        val availabilityTimeList =  availabiltyTimeList

        val adapter = AssignToAdapter(this, availabilityTimeList, selectedItems!!)
        recyclerView.layoutManager = GridLayoutManager(this, 2)
        recyclerView.adapter = adapter

        yes_btn.setOnClickListener{
            if (logoutConfirmationDialog!!.isShowing) {
                logoutConfirmationDialog!!.dismiss()
            }

            selectedItems = ""
            selectedItems = adapter.getSelectedItems().joinToString ()
            binding.etPollTo.text = selectedItems
            Log.d("SelectedIds", selectedItems!!)
        }


        close.setOnClickListener {
            if (logoutConfirmationDialog!!.isShowing) {
                logoutConfirmationDialog!!.dismiss()
            }
        }

        logoutConfirmationDialog!!.show()
    }

    private fun addAssignToList() {

        availabiltyTimeList.add(AvailabilityTime("All Members"))
        availabiltyTimeList.add(AvailabilityTime("Owners"))
        availabiltyTimeList.add(AvailabilityTime("Tenants"))
        availabiltyTimeList.add(AvailabilityTime("Admin"))
        availabiltyTimeList.add(AvailabilityTime("President"))
        availabiltyTimeList.add(AvailabilityTime("Teasurer"))
        availabiltyTimeList.add(AvailabilityTime("Security"))
        availabiltyTimeList.add(AvailabilityTime("Association \nMembers"))

    }

    private fun keepPoll() {
        if (keepPollPopupWindow != null) {
            if (keepPollPopupWindow!!.isShowing) {
                keepPollPopupWindow!!.dismiss()
            } else {
                keepPollDropDown()
            }
        } else {
            keepPollDropDown()
        }
    }

    private fun keepPollDropDown() {
        val layoutInflater: LayoutInflater =
            getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view: View = layoutInflater.inflate(R.layout.drop_down_layout, null)

        keepPollPopupWindow = PopupWindow(
            view,
            binding.keepPollForTxt.measuredWidth,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        if (keepPollList.isNotEmpty()) {
            val linearLayoutManager = LinearLayoutManager(this@UpdatePollActivity)
            val dropDownRecyclerView = view.findViewById<RecyclerView>(R.id.drop_down_recycler_view)
            dropDownRecyclerView.layoutManager = linearLayoutManager
            val categoryAdapter = ComplaintTypeAdapter(this@UpdatePollActivity, keepPollList)

            dropDownRecyclerView.adapter = categoryAdapter
            categoryAdapter.setCallbackComplaintType(this@UpdatePollActivity)
        }
        keepPollPopupWindow!!.elevation = 10F
        keepPollPopupWindow!!.showAsDropDown(binding.keepPollForTxt, 0, 0, Gravity.CENTER)
    }

    fun setCallbackComplaintType(keepPollFor: String) {
        if (keepPollPopupWindow != null) {
            if (keepPollPopupWindow!!.isShowing) {
                keepPollPopupWindow!!.dismiss()
            }
        }
        if (keepPollFor != null) {
            binding.keepPollForTxt.text = keepPollFor
            // selectedCategoryId = ComplaintStatus

            if (keepPollFor.equals("Custom")){

                binding.tvDate.visibility = View.VISIBLE
                binding.llDate.visibility = View.VISIBLE
            }else{
                binding.tvDate.visibility = View.GONE
                binding.llDate.visibility = View.GONE
            }
        }
        Log.d(HistoryFragment.TAG, "$keepPollFor")
    }
    private fun setKeepPollData() {
        keepPollList.add("1 Day")
        keepPollList.add("3 Days")
        keepPollList.add("1 Week")
        keepPollList.add("2 Weeks")
        keepPollList.add("1 Month")
        keepPollList.add("3 Months")
        keepPollList.add("6 Months")
        keepPollList.add("Always")
        keepPollList.add("Custom")
    }


    // create an OnDateSetListener
    val dateSetListener__book_now_end_date = object : DatePickerDialog.OnDateSetListener {
        override fun onDateSet(
            view: DatePicker, year: Int, monthOfYear: Int,
            dayOfMonth: Int
        ) {
            // disable dates before today
            val disablePastDates = Calendar.getInstance().timeInMillis- 1000
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