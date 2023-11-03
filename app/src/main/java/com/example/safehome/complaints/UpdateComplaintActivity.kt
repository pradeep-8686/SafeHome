package com.example.safehome.complaints

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.safehome.R
import com.example.safehome.Utils
import com.example.safehome.adapter.AssignToAdapter
import com.example.safehome.adapter.CategoryRaiseComplaintAdapter
import com.example.safehome.adapter.ComplaintStatusAdapter
import com.example.safehome.adapter.ComplaintTypeAdapter
import com.example.safehome.adapter.PriorityAdapter
import com.example.safehome.custom.CustomProgressDialog
import com.example.safehome.databinding.ActivityRaiseComplaintBinding
import com.example.safehome.databinding.ActivityUpdateComplaintBinding
import com.example.safehome.maintenance.HistoryFragment
import com.example.safehome.model.AvailabilityTime
import com.example.safehome.repository.APIClient
import com.example.safehome.repository.APIInterface


class UpdateComplaintActivity : AppCompatActivity() {

    private val CAMERA_REQUEST: Int = 1888
    private lateinit var binding: ActivityUpdateComplaintBinding
    private lateinit var customProgressDialog: CustomProgressDialog
    private lateinit var apiInterface: APIInterface
    var User_Id: String? = ""
    var Auth_Token: String? = ""
    private var categoryPopupWindow: PopupWindow? = null
    private var statusPopupWindow: PopupWindow? = null
    private var priorityPopupWindow: PopupWindow? = null
    private var complaintTypePopupWindow: PopupWindow? = null
    private var availabilityTimePopWindow: PopupWindow? = null
    private var logoutConfirmationDialog: Dialog? = null
    private var attachPhotoWindow: PopupWindow? = null

    private var complaintTypeList: ArrayList<String> = ArrayList()
    private var categoryList: ArrayList<String> = ArrayList()
    private var priorityList: ArrayList<String> = ArrayList()
    private var availabiltyTimeList: ArrayList<AvailabilityTime> = ArrayList()
    private var selectedItems : String? = ""
    private val IMAGE_CHOOSE = 1000;
    private val PERMISSION_CODE = 1001;
    private var complaintStatusList: ArrayList<String> = ArrayList()

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateComplaintBinding.inflate(layoutInflater)
        setContentView(binding.root)

        customProgressDialog = CustomProgressDialog()
        apiInterface = APIClient.getClient(this)
        User_Id = Utils.getStringPref(this, "User_Id", "")
        Auth_Token = Utils.getStringPref(this, "Token", "")

        binding.backBtnClick.setOnClickListener {
            val intent = Intent(this, ComplaintsActivity::class.java)
            startActivity(intent)
            finish()
        }

        dropDown()
        addStatusList()

    }
    private fun addStatusList() {
        complaintStatusList.add("Pending")
        complaintStatusList.add("In Progress")
        complaintStatusList.add("Resolved")
        complaintStatusList.add("Reinitiate Pending")
        complaintStatusList.add("Reinitiate")
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
        }else if (requestCode === CAMERA_REQUEST && resultCode === RESULT_OK){

        }
    }


    @RequiresApi(Build.VERSION_CODES.Q)
    private fun dropDown() {
        addComplaintist()
        addCategoryList()
        addPriorityList()
        addAssignToList()

        binding.clTvComplaintType.setOnClickListener {
//            complaintType()
        }
        binding.tvCategoryCl.setOnClickListener {
            categorypopUp()
        }
        binding.priorityCl.setOnClickListener {
//            priorityPopUp()
        }
        binding.assignToCl.setOnClickListener{
//            assignToPopup()
        }

        binding.tvAttachPhoto.setOnClickListener {
//            attachPhotoPopup()
        }
        binding.imgAttachPhoto.setOnClickListener {
//            attachPhotoPopup()
        }

        binding.clStatus.setOnClickListener {

            if (statusPopupWindow != null) {
                if (statusPopupWindow!!.isShowing) {
                    statusPopupWindow!!.dismiss()
                } else {
                    statusDropDown()
                }
            } else {
               statusDropDown()
            }
        }


    }
    private fun statusDropDown() {
        val layoutInflater: LayoutInflater =
            getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view: View = layoutInflater.inflate(R.layout.drop_down_layout, null)

        statusPopupWindow = PopupWindow(
            view,
            binding.tvStatus.measuredWidth,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        if (complaintStatusList.isNotEmpty()) {

            val linearLayoutManager = LinearLayoutManager(this)
            val dropDownRecyclerView = view.findViewById<RecyclerView>(R.id.drop_down_recycler_view)
            dropDownRecyclerView.layoutManager = linearLayoutManager
            val categoryAdapter = ComplaintStatusAdapter(this, complaintStatusList)

            dropDownRecyclerView.adapter = categoryAdapter
            categoryAdapter.setCallbackComplaintStatus(this@UpdateComplaintActivity)
        }
        statusPopupWindow!!.elevation = 10F
        statusPopupWindow!!.showAsDropDown(binding.tvStatus, 0, 0, Gravity.CENTER)
    }
    fun setCallbackComplaintStatus(ComplaintStatus: String) {
        if (categoryPopupWindow != null) {
            if (categoryPopupWindow!!.isShowing) {
                categoryPopupWindow!!.dismiss()
            }
        }
        if (ComplaintStatus != null) {
            binding.tvStatus.text = ComplaintStatus
//            selectedCategoryId = ComplaintStatus
        }
//        Log.d(HistoryFragment.TAG, "$selectedCategoryId , $selectedYear")
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
    @SuppressLint("MissingInflatedId")
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
             if (attachPhotoWindow!!.isShowing) {
                 attachPhotoWindow!!.dismiss()
             }
        }

        val takePhotoLayout = view.findViewById<ConstraintLayout>(R.id.take_photo_cl)
        takePhotoLayout.setOnClickListener {
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(cameraIntent, CAMERA_REQUEST)
            if (attachPhotoWindow!!.isShowing) {
                attachPhotoWindow!!.dismiss()
            }
        }
        attachPhotoWindow!!.elevation = 10F
        attachPhotoWindow!!.showAsDropDown(binding.imgAttachPhoto, 0, 0, Gravity.CENTER)
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

    private fun addComplaintist() {
        complaintTypeList.add("Personal Level")
        complaintTypeList.add("Community Level")
    }

    private fun addCategoryList() {
        categoryList.add("Common Area")
    }

    private fun addPriorityList() {
        priorityList.add("High")
        priorityList.add("Medium")
        priorityList.add("Low")
    }

    private fun priorityPopUp() {
        if (priorityPopupWindow != null) {
            if (priorityPopupWindow!!.isShowing) {
                priorityPopupWindow!!.dismiss()
            } else {
                priorityDropDown()
            }
        } else {
            priorityDropDown()
        }
    }

    private fun categorypopUp() {
        if (categoryPopupWindow != null) {
            if (categoryPopupWindow!!.isShowing) {
                categoryPopupWindow!!.dismiss()
            } else {
                categoryDropDown()
            }
        } else {
            categoryDropDown()
        }

    }

    private fun complaintType() {
        if (complaintTypePopupWindow != null) {
            if (complaintTypePopupWindow!!.isShowing) {
                complaintTypePopupWindow!!.dismiss()
            } else {
                complaintTypeDropDown()
            }
        } else {
            complaintTypeDropDown()
        }
    }

    private fun categoryDropDown() {
        val layoutInflater: LayoutInflater =
            getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view: View = layoutInflater.inflate(R.layout.drop_down_layout, null)

        categoryPopupWindow = PopupWindow(
            view,
            binding.tvCategoryTxt.measuredWidth,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        if (categoryList.isNotEmpty()) {
            val linearLayoutManager = LinearLayoutManager(this@UpdateComplaintActivity)
            val dropDownRecyclerView = view.findViewById<RecyclerView>(R.id.drop_down_recycler_view)
            dropDownRecyclerView.layoutManager = linearLayoutManager
            val categoryAdapter = CategoryRaiseComplaintAdapter(this@UpdateComplaintActivity, categoryList)

            dropDownRecyclerView.adapter = categoryAdapter
            categoryAdapter.setCallbackCategory(this@UpdateComplaintActivity)
        }
        categoryPopupWindow!!.elevation = 10F
        categoryPopupWindow!!.showAsDropDown(binding.tvCategoryTxt, 0, 0, Gravity.CENTER)
    }

    private fun priorityDropDown() {
        val layoutInflater: LayoutInflater =
            getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view: View = layoutInflater.inflate(R.layout.drop_down_layout, null)

        priorityPopupWindow = PopupWindow(
            view,
            binding.priorityTxt.measuredWidth,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        if (priorityList.isNotEmpty()) {
            val linearLayoutManager = LinearLayoutManager(this@UpdateComplaintActivity)
            val dropDownRecyclerView = view.findViewById<RecyclerView>(R.id.drop_down_recycler_view)
            dropDownRecyclerView.layoutManager = linearLayoutManager
            val categoryAdapter = PriorityAdapter(this@UpdateComplaintActivity, priorityList)

            dropDownRecyclerView.adapter = categoryAdapter
            categoryAdapter.setCallbackPriority(this@UpdateComplaintActivity)
        }
        priorityPopupWindow!!.elevation = 10F
        priorityPopupWindow!!.showAsDropDown(binding.priorityTxt, 0, 0, Gravity.CENTER)
    }

    private fun complaintTypeDropDown() {
        val layoutInflater: LayoutInflater =
            getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view: View = layoutInflater.inflate(R.layout.drop_down_layout, null)

        complaintTypePopupWindow = PopupWindow(
            view,
            binding.complaintTypeTxt.measuredWidth,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        if (complaintTypeList.isNotEmpty()) {
            val linearLayoutManager = LinearLayoutManager(this@UpdateComplaintActivity)
            val dropDownRecyclerView = view.findViewById<RecyclerView>(R.id.drop_down_recycler_view)
            dropDownRecyclerView.layoutManager = linearLayoutManager
            val categoryAdapter = ComplaintTypeAdapter(this@UpdateComplaintActivity, complaintTypeList)

            dropDownRecyclerView.adapter = categoryAdapter
            categoryAdapter.setCallbackComplaintType(this@UpdateComplaintActivity)
        }
        complaintTypePopupWindow!!.elevation = 10F
        complaintTypePopupWindow!!.showAsDropDown(binding.complaintTypeTxt, 0, 0, Gravity.CENTER)
    }

    fun setCallbackComplaintType(complaintType: String) {
        if (complaintTypePopupWindow != null) {
            if (complaintTypePopupWindow!!.isShowing) {
                complaintTypePopupWindow!!.dismiss()
            }
        }
        if (complaintType != null) {
            binding.complaintTypeTxt.text = complaintType
            // selectedCategoryId = ComplaintStatus
        }
        Log.d(HistoryFragment.TAG, "$complaintType")
    }

    fun setCallbackCategory(category: String) {
        if (categoryPopupWindow != null) {
            if (categoryPopupWindow!!.isShowing) {
                categoryPopupWindow!!.dismiss()
            }
        }
        if (category != null) {
            binding.tvCategoryTxt.text = category
            // selectedCategoryId = ComplaintStatus
        }
        Log.d(HistoryFragment.TAG, "$category")
    }

    fun setCallbackPriority(priority: String) {
        if (priorityPopupWindow != null) {
            if (priorityPopupWindow!!.isShowing) {
                priorityPopupWindow!!.dismiss()
            }
        }
        if (priority != null) {
            binding.priorityTxt.text = priority
            // selectedCategoryId = ComplaintStatus
        }
        Log.d(HistoryFragment.TAG, "$priority")
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    @SuppressLint("MissingInflatedId")
    private fun assignToPopupDialog() {
        val layoutInflater: LayoutInflater =
            getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = layoutInflater.inflate(R.layout.assign_to_layout, null)
        logoutConfirmationDialog = Dialog(this@UpdateComplaintActivity, R.style.CustomAlertDialog)
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
            binding.assignToTxt.text = selectedItems
            Log.d("SelectedIds", selectedItems!!)
        }


        close.setOnClickListener {
            if (logoutConfirmationDialog!!.isShowing) {
                logoutConfirmationDialog!!.dismiss()
            }
        }

        logoutConfirmationDialog!!.show()
    }

}