package com.example.safehome.forums

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.text.Editable
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
import com.example.safehome.HomeActivity
import com.example.safehome.R
import com.example.safehome.Utils
import com.example.safehome.custom.CustomProgressDialog
import com.example.safehome.databinding.ActivityRaiseForumBinding
import com.example.safehome.maintenance.HistoryFragment
import com.example.safehome.model.AvailabilityTime
import com.example.safehome.model.PollsKeepDropdownModel
import com.example.safehome.model.PollsPostedDropDownModel
import com.example.safehome.polls.KeepPollForAdapter
import com.example.safehome.polls.PollsPostedToAdapter
import com.example.safehome.repository.APIClient
import com.example.safehome.repository.APIInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Calendar


class RaiseForumActivity : AppCompatActivity() {
    private var pollsPostedDropdownList: ArrayList<PollsPostedDropDownModel.Data> = ArrayList()
    private lateinit var pollsPostedDropdownNetworkCall: Call<PollsPostedDropDownModel>
    private var keepsDropDownsList: ArrayList<PollsKeepDropdownModel.Data> = ArrayList()
    private lateinit var getKeepsDropdownCall: Call<PollsKeepDropdownModel>
    private lateinit var binding: ActivityRaiseForumBinding
    private lateinit var customProgressDialog: CustomProgressDialog
    private lateinit var apiInterface: APIInterface
    var User_Id: String? = ""
    var Auth_Token: String? = ""
    private lateinit var addForumServiceCall: Call<AddUpdateForumResponse>

    private var attachPhotoWindow: PopupWindow? = null

    private val IMAGE_CHOOSE = 1000;
    private val PERMISSION_CODE = 1001;
    private var availabilityTimePopWindow: PopupWindow? = null
    private var logoutConfirmationDialog: Dialog? = null
    private var availabiltyTimeList: ArrayList<AvailabilityTime> = ArrayList()
    private var selectedItems: String? = ""
    private var selectedPostToList = ArrayList<String>()
    private var topic: String = ""
    private var description: String = ""
    private var keepForumFor: String = ""
    private var forumTo: String = ""
    private var imageFile: String = ""
    private var keepPollPopupWindow: PopupWindow? = null
    private var keepPollList: ArrayList<String> = ArrayList()

    var cal_startDate = Calendar.getInstance()
    var cal_endDate = Calendar.getInstance()
    private lateinit var forumItem: GetAllForumDetailsModel.Data.Forum
    private var isUpdate: Boolean = false

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRaiseForumBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // progressbar
        customProgressDialog = CustomProgressDialog()
        apiInterface = APIClient.getClient(this)
        User_Id = Utils.getStringPref(this, "User_Id", "")
        Auth_Token = Utils.getStringPref(this, "Token", "")


        dataFromUpdateForum()
        addAssignToList()


        binding.backBtnClick.setOnClickListener {
           val intent = Intent(this, HomeActivity::class.java)
            intent.putExtra("ScreenFrom", "MenuScreenFrag")
            overridePendingTransition(
                android.R.anim.fade_in,
                android.R.anim.fade_out
            );

            startActivity(intent)

            onBackPressed()
        }
        binding.btnCancel.setOnClickListener {
         binding.tvAttachPhoto.text = "Attach Photo"
         binding.etPollTo.text = ""
         binding.etDescription.text.clear()
         binding.etTopic.text.clear()
            binding.keepPollForTxt.text = ""
        }

        binding.btnPost.setOnClickListener {


            if (validateData()) {

                if (isUpdate) {
                    updatePollNetworkCall()
                } else {

                    addPollNetworkCall()
                }

            }


        }

        binding.etTopic.setOnTouchListener { v, event ->
            if (keepPollPopupWindow != null) {
                if (keepPollPopupWindow!!.isShowing) {
                    keepPollPopupWindow!!.dismiss()
                }
            }

            v?.onTouchEvent(event) ?: true
        }

        binding.tvAttachPhoto.setOnClickListener {

            attachPhotoPopup()
        }
        binding.imgAttachPhoto.setOnClickListener {

            attachPhotoPopup()

        }
        binding.etPollTo.setOnClickListener {
            assignToPopup()
        }

        binding.clTvComplaintType.setOnClickListener {
            keepPoll()
        }


        binding.mainLayout.setOnClickListener {

            if (attachPhotoWindow?.isShowing == true) {
                attachPhotoWindow?.dismiss()
            }

            if (keepPollPopupWindow?.isShowing == true) {
                keepPollPopupWindow?.dismiss()
            }

        }

    }

    private fun dataFromUpdateForum() {
        if (intent.extras != null) {
            if (intent.extras!!.getSerializable("forumItem") != null) {

                isUpdate = true
                binding.tittleTxt.text = "Update A Forum"
                binding.btnPost.text = "Update"

                forumItem =
                    intent.extras!!.getSerializable("forumItem") as GetAllForumDetailsModel.Data.Forum

                Log.d("forumItem", forumItem.toString())

                if (forumItem.attachment != null) {
                    imageFile = forumItem.attachment//Need to display image
                }

                if (forumItem.topic != null) {

                    topic = forumItem.topic

                    binding.etTopic.text = Editable.Factory.getInstance().newEditable(topic)
                }

                if (forumItem.description != null) {

                    description = forumItem.description

                    binding.etDescription.text = Editable.Factory.getInstance().newEditable(description)
                }

                if (forumItem.keepQuestionFor != null) {
                    keepForumFor = forumItem.keepQuestionFor.toString()
                    binding.keepPollForTxt.text = forumItem.keepQuestionFor
                }



                if (forumItem.postedTo != null) {

                    var selectedPostName = ArrayList<String>()
                    for (model in forumItem.postedTo) {
                        selectedPostToList.add(model.postedToId.toString())
                        selectedPostName.add(model.name)
                    }
                    selectedItems = selectedPostName.joinToString()
                    binding.etPollTo.text = selectedPostName.joinToString()
                }

            }
        }
    }


    @SuppressLint("SuspiciousIndentation")
    @RequiresApi(Build.VERSION_CODES.Q)
    private fun addPollNetworkCall() {
        customProgressDialog.progressDialogShow(
            this@RaiseForumActivity,
            this.getString(R.string.loading)
        )

        val forumOptionsMap: MutableMap<String, Any> = HashMap()

        forumOptionsMap["Topic"] = topic
        forumOptionsMap["KeepQuestionFor"] = keepForumFor
        forumOptionsMap["Description"] = description
        forumOptionsMap["Attachment"] = imageFile


        for (i in 0 until selectedPostToList.size) {
            forumOptionsMap["PostedToIds[$i]"] = selectedPostToList[i]
        }

        Log.d(
            "pollOptionsMap", forumOptionsMap.toString()
        )

        addForumServiceCall = apiInterface.addForum(
            "bearer $Auth_Token", forumOptionsMap
        )
        addForumServiceCall.enqueue(object : Callback<AddUpdateForumResponse> {
            override fun onResponse(
                call: Call<AddUpdateForumResponse>,
                response: Response<AddUpdateForumResponse>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    customProgressDialog.progressDialogDismiss()
                    if (response.body()!!.statusCode != null) {

                        when (response.body()!!.statusCode) {
                            1 -> {
                                if (response.body()!!.message != null && response.body()!!.message.isNotEmpty()) {
                                    Utils.showToast(
                                        this@RaiseForumActivity,
                                        response.body()!!.message.toString()
                                    )
                                    moveToFacilityActivity()

                                }
                            }

                            else -> {
                                if (response.body()!!.message != null && response.body()!!.message.isNotEmpty()) {
                                    Utils.showToast(
                                        this@RaiseForumActivity,
                                        response.body()!!.message.toString()
                                    )
                                    moveToFacilityActivity()

                                }
                            }
                        }
                    }else{
                        customProgressDialog.progressDialogDismiss()
                        moveToFacilityActivity()
                    }
                } else {
                    customProgressDialog.progressDialogDismiss()
                    // Utils.showToast(this@ListBookNowActivity, "your booking is not successful")
                    moveToFacilityActivity()
                }
            }

            override fun onFailure(call: Call<AddUpdateForumResponse>, t: Throwable) {
                customProgressDialog.progressDialogDismiss()

                moveToFacilityActivity()
                Utils.showToast(this@RaiseForumActivity, t.message.toString())
            }
        })
    }

    @SuppressLint("SuspiciousIndentation")
    @RequiresApi(Build.VERSION_CODES.Q)
    private fun updatePollNetworkCall() {
        customProgressDialog.progressDialogShow(
            this@RaiseForumActivity,
            this.getString(R.string.loading)
        )

        val forumOptionsMap: MutableMap<String, Any> = HashMap()

        forumOptionsMap["forumId"] = forumItem.forumID

        forumOptionsMap["Topic "] = topic
        forumOptionsMap["KeepQuestionFor"] = keepForumFor
        forumOptionsMap["Description"] = description
        forumOptionsMap["Attachment"] = imageFile


        for (i in 0 until selectedPostToList.size) {
            forumOptionsMap["PostedToIds[$i]"] = selectedPostToList[i]
        }

        Log.d(
            "pollOptionsMap", forumOptionsMap.toString()
        )

        addForumServiceCall = apiInterface.updateForum(
            "bearer $Auth_Token", forumOptionsMap
        )
        addForumServiceCall.enqueue(object : Callback<AddUpdateForumResponse> {
            override fun onResponse(
                call: Call<AddUpdateForumResponse>,
                response: Response<AddUpdateForumResponse>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    customProgressDialog.progressDialogDismiss()
                    if (response.body()!!.statusCode != null) {

                        when (response.body()!!.statusCode) {
                            1 -> {
                                if (response.body()!!.message != null && response.body()!!.message.isNotEmpty()) {
                                    Utils.showToast(
                                        this@RaiseForumActivity,
                                        response.body()!!.message.toString()
                                    )
                                }
                            }

                            else -> {
                                if (response.body()!!.message != null && response.body()!!.message.isNotEmpty()) {
                                    Utils.showToast(
                                        this@RaiseForumActivity,
                                        response.body()!!.message.toString()
                                    )
                                }
                            }
                        }
                    }
                } else {
                    customProgressDialog.progressDialogDismiss()
                    // Utils.showToast(this@ListBookNowActivity, "your booking is not successful")
                    moveToFacilityActivity()
                }
            }

            override fun onFailure(call: Call<AddUpdateForumResponse>, t: Throwable) {
                customProgressDialog.progressDialogDismiss()

                //         moveToFacilityActivity()
                Utils.showToast(this@RaiseForumActivity, t.message.toString())
            }
        })
    }

    private fun moveToFacilityActivity() {
        val intent = Intent(this@RaiseForumActivity, ForumsListActivity::class.java)
        intent.putExtra("from", "bookingsPage")
        startActivity(intent)
        finish()
    }

    private fun validateData(): Boolean {

        topic = binding.etTopic.text.toString().trim()
        description = binding.etDescription.text.toString().trim()
        forumTo = binding.etPollTo.text.toString().trim()

        if (topic.isEmpty()) {
            binding.etTopic.error = "Question is required"
            return false
        }
        if (description.isEmpty()) {
            binding.etDescription.error = "Description is required"
            return false
        }

        if (keepForumFor.isEmpty()) {
            Toast.makeText(this, "Keep Question For field is required", Toast.LENGTH_SHORT).show()
            return false
        }


        if (forumTo.isEmpty()) {
            Toast.makeText(this, "Post To field is required", Toast.LENGTH_SHORT).show()
            return false
        }

        return true

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

        val photo_library_cl = view.findViewById<ConstraintLayout>(R.id.photo_library_cl)
        photo_library_cl.setOnClickListener {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                    val permissions = arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    requestPermissions(permissions, PERMISSION_CODE)
                } else {
                    chooseImageFromGallery();
                }
            } else {
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
        when (requestCode) {
            PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    chooseImageFromGallery()
                } else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_CHOOSE && resultCode == Activity.RESULT_OK) {
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
                //    assignToPopupDialog()
                pollsPostedDropdownCall()
            }
        } else {
//                availabilityTimeDropDown()
            //  assignToPopupDialog()
            pollsPostedDropdownCall()

        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun pollsPostedDropdownCall() {
        customProgressDialog.progressDialogShow(
            this@RaiseForumActivity,
            this.getString(R.string.loading)
        )
        pollsPostedDropdownNetworkCall = apiInterface.getAllPostedToDropDown("bearer " + Auth_Token)
        pollsPostedDropdownNetworkCall.enqueue(object : Callback<PollsPostedDropDownModel> {
            override fun onResponse(
                call: Call<PollsPostedDropDownModel>,
                response: Response<PollsPostedDropDownModel>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    customProgressDialog.progressDialogDismiss()
                    if (response.body()!!.statusCode != null) {
                        when (response.body()!!.statusCode) {
                            1 -> {
                                if (response.body()!!.message != null && response.body()!!.message.isNotEmpty()) {
                                    if (pollsPostedDropdownList.isNotEmpty()) {
                                        pollsPostedDropdownList.clear()
                                    }
                                    pollsPostedDropdownList =
                                        response.body()!!.data as ArrayList<PollsPostedDropDownModel.Data>
                                    assignToPopupDialog(pollsPostedDropdownList)
                                    //    Utils.showToast(requireContext(), response.body()!!.message.toString())
                                }
                                //  populatePollsData(totalPollsList)
                                //   viewPollResultDialog(pollResultsList)

                            }

                            else -> {
                                if (response.body()!!.message != null && response.body()!!.message.isNotEmpty()) {
                                    Utils.showToast(
                                        this@RaiseForumActivity,
                                        response.body()!!.message.toString()
                                    )
                                }
                            }
                        }
                    } else {
                        customProgressDialog.progressDialogDismiss()

                    }
                }
            }

            override fun onFailure(call: Call<PollsPostedDropDownModel>, t: Throwable) {
                customProgressDialog.progressDialogDismiss()
                Utils.showToast(this@RaiseForumActivity, t.message.toString())
            }

        })
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    @SuppressLint("MissingInflatedId")
    private fun assignToPopupDialog(pollsPostedDropdownList: ArrayList<PollsPostedDropDownModel.Data>) {
        val layoutInflater: LayoutInflater =
            getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = layoutInflater.inflate(R.layout.assign_to_layout, null)
        logoutConfirmationDialog = Dialog(this@RaiseForumActivity, R.style.CustomAlertDialog)
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
        val availabilityTimeList = availabiltyTimeList

        val adapter = PollsPostedToAdapter(this, pollsPostedDropdownList, selectedItems!!)
        recyclerView.layoutManager = GridLayoutManager(this, 2)
        recyclerView.adapter = adapter

        yes_btn.setOnClickListener {
            if (logoutConfirmationDialog!!.isShowing) {
                logoutConfirmationDialog!!.dismiss()
            }

            selectedItems = ""
            selectedPostToList.clear()
            selectedPostToList = adapter.getSelectedItemsId() as ArrayList<String>
            val selectedItemsName = adapter.getSelectedItems().joinToString()
            selectedItems = adapter.getSelectedItems().joinToString()
            binding.etPollTo.text = selectedItemsName
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

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun keepPoll() {
        if (keepPollPopupWindow != null) {
            if (keepPollPopupWindow!!.isShowing) {
                keepPollPopupWindow!!.dismiss()
            } else {
                //   keepPollDropDown()
                getKeepPollDropDownsDataCall();

            }
        } else {
            //  keepPollDropDown()
            getKeepPollDropDownsDataCall();

        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun getKeepPollDropDownsDataCall() {
        customProgressDialog.progressDialogShow(
            this@RaiseForumActivity,
            this.getString(R.string.loading)
        )
        getKeepsDropdownCall = apiInterface.getKeepForDropDown("bearer " + Auth_Token)
        getKeepsDropdownCall.enqueue(object : Callback<PollsKeepDropdownModel> {
            override fun onResponse(
                call: Call<PollsKeepDropdownModel>,
                response: Response<PollsKeepDropdownModel>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    customProgressDialog.progressDialogDismiss()
                    if (response.body()!!.statusCode != null) {
                        when (response.body()!!.statusCode) {
                            1 -> {
                                if (response.body()!!.message != null && response.body()!!.message.isNotEmpty()) {
                                    if (keepsDropDownsList.isNotEmpty()) {
                                        keepsDropDownsList.clear()
                                    }
                                    keepsDropDownsList =
                                        response.body()!!.data as ArrayList<PollsKeepDropdownModel.Data>
                                    //    Utils.showToast(requireContext(), response.body()!!.message.toString())
                                    keepPollDropDown(keepsDropDownsList)
                                }
                                //  populatePollsData(totalPollsList)
                                //   viewPollResultDialog(pollResultsList)

                            }

                            else -> {
                                if (response.body()!!.message != null && response.body()!!.message.isNotEmpty()) {
                                    Utils.showToast(
                                        this@RaiseForumActivity,
                                        response.body()!!.message.toString()
                                    )
                                }
                            }
                        }
                    } else {
                        customProgressDialog.progressDialogDismiss()

                    }
                }
            }

            override fun onFailure(call: Call<PollsKeepDropdownModel>, t: Throwable) {
                customProgressDialog.progressDialogDismiss()
                Utils.showToast(this@RaiseForumActivity, t.message.toString())
            }

        })
    }


    private fun keepPollDropDown(keepsDropDownsList: ArrayList<PollsKeepDropdownModel.Data>) {
        val layoutInflater: LayoutInflater =
            getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view: View = layoutInflater.inflate(R.layout.drop_down_layout, null)

        keepPollPopupWindow = PopupWindow(
            view,
            binding.keepPollForTxt.measuredWidth,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        if (keepsDropDownsList.isNotEmpty()) {
            val linearLayoutManager = LinearLayoutManager(this@RaiseForumActivity)
            val dropDownRecyclerView = view.findViewById<RecyclerView>(R.id.drop_down_recycler_view)
            dropDownRecyclerView.layoutManager = linearLayoutManager
            val keepPollForAdapter = KeepPollForAdapter(this@RaiseForumActivity, keepsDropDownsList)

            dropDownRecyclerView.adapter = keepPollForAdapter
            keepPollForAdapter.setCallbackForum(this@RaiseForumActivity)
        }
        keepPollPopupWindow!!.elevation = 10F
        keepPollPopupWindow!!.showAsDropDown(binding.keepPollForTxt, 0, 0, Gravity.CENTER)
    }

    fun setCallbackComplaintType(keepPollFor: PollsKeepDropdownModel.Data) {
        if (keepPollPopupWindow != null) {
            if (keepPollPopupWindow!!.isShowing) {
                keepPollPopupWindow!!.dismiss()
            }
        }
        if (keepPollFor != null) {
            binding.keepPollForTxt.text = keepPollFor.keepFor
            // selectedCategoryId = ComplaintStatus

//            keepForumFor = keepPollFor.keepFor
            keepForumFor = "Always"

        }
        Log.d(HistoryFragment.TAG, "$keepPollFor")
    }



}