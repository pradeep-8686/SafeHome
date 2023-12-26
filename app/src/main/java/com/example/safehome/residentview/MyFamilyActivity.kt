package com.example.safehome.residentview

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.Manifest
import android.app.Activity
import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.Html
import android.text.TextWatcher
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.safehome.R
import com.example.safehome.Utils
import com.example.safehome.adapter.AgeGroupAdapter
import com.example.safehome.adapter.MyFamilyRelationshipAdapter
import com.example.safehome.custom.CustomProgressDialog
import com.example.safehome.databinding.ActivityMyFamilyBinding
import com.example.safehome.model.MobileSignUp
import com.example.safehome.model.RelationshipTypesModel
import com.example.safehome.repository.APIClient
import com.example.safehome.repository.APIInterface
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class MyFamilyActivity : AppCompatActivity() {
    private lateinit var relashionShipPopupWindow: PopupWindow
    private var realationshipTypesList: ArrayList<RelationshipTypesModel.Data> = ArrayList()
    private lateinit var relationshipTypeServiceCall: Call<RelationshipTypesModel>
    private lateinit var binding: ActivityMyFamilyBinding
    private var firstName: String? = null
    private var lastName: String? = null
    private var mobileNumber: String? = null
    private var emailId: String? = null
    private var ageGroupList: ArrayList<String> = ArrayList()
    private var ageGroupPopupWindow: PopupWindow? = null
    private var selectedAgeGroup: String = ""
    private lateinit var customProgressDialog: CustomProgressDialog
    private lateinit var apiInterface: APIInterface
    var User_Id: String? = ""
    var Auth_Token: String? = ""
    private lateinit var addFamilyDetailsCall: Call<MobileSignUp>
    private val PERMISSION_REQUEST_CODE = 1
    private val PICK_IMAGE_REQUEST = 2
    private var isPermissionGranted: Boolean = false
    private var imageMultipartBody: MultipartBody.Part? = null
    val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\$"
    private var Relationship_id:Int = 0

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyFamilyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // progressbar
        customProgressDialog = CustomProgressDialog()
        apiInterface = APIClient.getClient(this)
        User_Id = Utils.getStringPref(this, "residentId", "")
        Auth_Token = Utils.getStringPref(this, "Token", "")

        binding.firstNameEt.setBackgroundResource(android.R.color.transparent)
        binding.lastNameEt.setBackgroundResource(android.R.color.transparent)
        binding.mobileNumberEt.setBackgroundResource(android.R.color.transparent)
        binding.emailAddressEt.setBackgroundResource(android.R.color.transparent)

        binding.firstNameHeader.text =
            Html.fromHtml(getString(R.string.first_name_hint) + "" + "<font color='white'>*</font>")
        binding.lastNameHeader.text =
            Html.fromHtml(getString(R.string.last_name_hint) + "" + "<font color='white'>*</font>")
        binding.mobileNumberHeader.text =
            Html.fromHtml(getString(R.string.mobile_number_hint) + "" + "<font color='white'>*</font>")
        binding.selectAgeGroupHeader.text =
            Html.fromHtml(getString(R.string.age_group) + "" + "<font color='white'>*</font>")
        binding.selectRelationHeader.text =
            Html.fromHtml(getString(R.string.relationship) + "" + "<font color='white'>*</font>")

        buttonClickEvents()

        binding.backBtnClick.setOnClickListener {
            finish()
        }

        ageGroupList()
    }

    private fun ageGroupList() {
        ageGroupList.add("Adult")
        ageGroupList.add("Kid")
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun buttonClickEvents() {
        // here handle button click events
        binding.selectAgeGroupCl.setOnClickListener {
            if (ageGroupPopupWindow != null) {
                if (ageGroupPopupWindow!!.isShowing) {
                    ageGroupPopupWindow!!.dismiss()
                } else {
                    ageGroupDropDown()
                }
            } else {
                ageGroupDropDown()
            }
        }

        binding.saveBtn.setOnClickListener {
            verifyData()
        }

        binding.cancelBtn.setOnClickListener {
            clearData()
        }

        binding.selectReationCl.setOnClickListener {
            getRealtionshipeTypeServiceCall()
        }

        binding.profilePicture.setOnClickListener { pickImageFromGallery() }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun getRealtionshipeTypeServiceCall() {
        customProgressDialog.progressDialogShow(this@MyFamilyActivity, this.getString(R.string.loading))
        relationshipTypeServiceCall = apiInterface.getAllRelationShipTypes("bearer "+Auth_Token)
        relationshipTypeServiceCall.enqueue(object: Callback<RelationshipTypesModel> {
            override fun onResponse(
                call: Call<RelationshipTypesModel>,
                response: Response<RelationshipTypesModel>
            ) {
                if (response.isSuccessful && response.body()!= null){

                    customProgressDialog.progressDialogDismiss()
                    if (response.body()!!.statusCode!= null){
                        when(response.body()!!.statusCode){
                            1 -> {
                                if (realationshipTypesList.isNotEmpty()){
                                    realationshipTypesList.clear()
                                }
                                if (response.body()!!.message != null && response.body()!!.message.isNotEmpty()) {
                                    //      Utils.showToast(this@ENewsActivity, response.body()!!.message.toString())
                                    realationshipTypesList = response.body()!!.data as ArrayList<RelationshipTypesModel.Data>
                                    showRelationshipDropDown(realationshipTypesList)
                                }
                            }
                            else -> {
                                if (response.body()!!.message != null && response.body()!!.message.isNotEmpty()) {
                                    Utils.showToast(this@MyFamilyActivity, response.body()!!.message.toString())
                                }
                            }
                        }
                    }
                }else{

                    customProgressDialog.progressDialogDismiss()
                }
            }

            override fun onFailure(call: Call<RelationshipTypesModel>, t: Throwable) {
                customProgressDialog.progressDialogDismiss()
                Utils.showToast(this@MyFamilyActivity, t.message.toString())

            }

        })   
    }

    private fun showRelationshipDropDown(realationshipTypesList: ArrayList<RelationshipTypesModel.Data>) {
        val layoutInflater: LayoutInflater =
            this.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view: View = layoutInflater.inflate(R.layout.drop_down_layout, null)

        relashionShipPopupWindow = PopupWindow(
            view,
            binding.selectAgeGroupTxt.measuredWidth,
            ViewGroup.LayoutParams.WRAP_CONTENT
            //(Utils.screenHeight * 0.3).toInt()
        )
        if (realationshipTypesList.isNotEmpty()) {

            val linearLayoutManager = LinearLayoutManager(this)

            val dropDownRecyclerView = view.findViewById<RecyclerView>(R.id.drop_down_recycler_view)
            dropDownRecyclerView.layoutManager = linearLayoutManager
            val ageGroupAdapter = MyFamilyRelationshipAdapter(this, realationshipTypesList)

            dropDownRecyclerView.adapter = ageGroupAdapter
            ageGroupAdapter.setCallback(this@MyFamilyActivity)
        }
        relashionShipPopupWindow!!.elevation = 10F
        relashionShipPopupWindow!!.showAsDropDown(binding.selectRelationTxt, 0, 0, Gravity.CENTER)
    }


    private fun clearData() {
        binding.firstNameEt.setText("")
        binding.lastNameEt.setText("")
        binding.mobileNumberEt.setText("")
        binding.emailAddressEt.setText("")
        binding.selectAgeGroupTxt.setText("")
        binding.selectAgeGroupErrorCase.visibility = View.GONE
        binding.mobileNumberErrorCase.visibility = View.GONE
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun verifyData() {
        // here get sign up user data
        firstName = binding.firstNameEt.text.toString().trim()
        binding.firstNameEt.addTextChangedListener(textWatcher);

        lastName = binding.lastNameEt.text.toString().trim()
        binding.lastNameEt.addTextChangedListener(textWatcher);

        mobileNumber = binding.mobileNumberEt.text.toString().trim()
        binding.mobileNumberEt.addTextChangedListener(textWatcher);

        emailId = binding.emailAddressEt.text.toString().trim()
        binding.emailAddressEt.addTextChangedListener(textWatcher);

        emailId = binding.emailAddressEt.text.toString().trim()

        if (doUserValidation()) {
            saveFamilyMemberDetailServiceCall()
        }
    }

    private fun pickImageFromGallery() {
        if (isReadStoragePermissionGranted()) {
            launchGalleryPicker()
        } else {
            requestReadStoragePermission()
        }
    }

    private fun isReadStoragePermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestReadStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        ) {
            showPermissionRationaleDialog()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                PERMISSION_REQUEST_CODE
            )
        }
    }

    private fun showPermissionRationaleDialog() {
        AlertDialog.Builder(this)
            .setTitle("Permission Needed")
            .setMessage("This app requires access to your gallery to pick an image.")
            .setPositiveButton("OK") { dialog: DialogInterface, _: Int ->
                dialog.dismiss()
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    PERMISSION_REQUEST_CODE
                )
            }
            .setNegativeButton("Cancel") { dialog: DialogInterface, _: Int ->
                dialog.dismiss()
                // Handle the case where the user denied the permission
            }
            .show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                launchGalleryPicker()
            } else {
                Utils.showToast(this, this.getString(R.string.give_permissions))
            }
        }
    }

    private fun launchGalleryPicker() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            val selectedImageUri: Uri? = data?.data
            if (selectedImageUri != null) {
                // Image selected successfully
                try {
                    val bitmap = loadBitmapFromUri(selectedImageUri)
                    imageMultipartBody = createMultipartBody(this, selectedImageUri)
                    // Use the bitmap as needed (e.g., display, process, etc.)
                    binding.profilePicture.setImageBitmap(bitmap)
                } catch (e: IOException) {
                    // Handle bitmap loading error
                    e.printStackTrace()
                }
            }
        } else {
            // Image selection canceled or failed
            // Handle accordingly (e.g., display a message)
        }

    }

    @Throws(IOException::class)
    private fun loadBitmapFromUri(uri: Uri): Bitmap {
        val inputStream = contentResolver.openInputStream(uri)
        val bitmap = BitmapFactory.decodeStream(inputStream)
        inputStream?.close()
        return bitmap
    }

    private fun createMultipartBody(context: Context, imageUri: Uri): MultipartBody.Part {
        val contentResolver: ContentResolver = context.contentResolver
        val inputStream = contentResolver.openInputStream(imageUri)
        val fileType = contentResolver.getType(imageUri)
        val fileName = getFileName(context, imageUri)

        val requestBody = inputStream?.readBytes()?.toRequestBody(fileType?.toMediaTypeOrNull())

        return MultipartBody.Part.createFormData("image", fileName, requestBody!!)
    }

    private fun getFileName(context: Context, uri: Uri): String? {
        var fileName: String? = null
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                fileName = it.getString(it.getColumnIndexOrThrow("_display_name"))
            }
        }
        return fileName
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun saveFamilyMemberDetailServiceCall() {
        customProgressDialog.progressDialogShow(this, this.getString(R.string.loading))

        imageMultipartBody?.let {
            addFamilyDetailsCall = apiInterface.addMyFamily(
                "bearer " + Auth_Token,
                User_Id!!.toInt(),
                Relationship_id,
                firstName!!.replace("\"", ""),
                lastName!!.replace("\"", ""),
                mobileNumber!!,
                emailId!!.replace("\"", ""),
                selectedAgeGroup,
                imageMultipartBody!!
            )
        }.run {
            addFamilyDetailsCall = apiInterface.addMyFamilyNoImae(
                "bearer " + Auth_Token,
                User_Id!!.toInt(),
                Relationship_id,
                firstName!!.replace("\"", ""),
                lastName!!.replace("\"", ""),
                mobileNumber!!,
                emailId!!.replace("\"", ""),
                selectedAgeGroup
            )
        }
        addFamilyDetailsCall.enqueue(object : Callback<MobileSignUp> {
            override fun onResponse(call: Call<MobileSignUp>, response: Response<MobileSignUp>) {

                customProgressDialog.progressDialogDismiss()
                // here successfully response
                if (response.isSuccessful && response.body() != null) {
                    // here save user signup data
                    navigationToAddMemberScreen(response.body()!!)
                }
            }

            override fun onFailure(call: Call<MobileSignUp>, t: Throwable) {
                customProgressDialog.progressDialogDismiss()
                // here failure conditions
                Utils.showToast(this@MyFamilyActivity, t.message.toString())
            }
        })
    }

    private fun navigationToAddMemberScreen(body: MobileSignUp) {
        if (body.statusCode == 1) {
            val intent = Intent(this, AddMemeberActivity::class.java)
            startActivity(intent)
            finish()
        } else if (body.statusCode == 2) {
            if (body.message.isNotEmpty()) {
                Utils.showToast(this, body.message)
            }
        }
    }


    private fun doUserValidation(): Boolean {
        if (binding.selectAgeGroupTxt.text.isNullOrEmpty()) {
            binding.selectAgeGroupErrorCase.visibility = View.VISIBLE
        }

        if (firstName.isNullOrEmpty()) {
            binding.firstNameErrorCase.visibility = View.VISIBLE
        }

        if (lastName.isNullOrEmpty()) {
            binding.lastNameErrorCase.visibility = View.VISIBLE
        }

        var validEmail: String = binding.emailAddressEt!!.text.trim().toString()
        if(validEmail.length>0) {
            if (!validEmail.matches(emailRegex.toRegex())) {
                binding.emailErrorCase.visibility = View.VISIBLE
                binding.emailErrorCase.text = this.getString(R.string.please_enter_valid_email)
            }
        }

        if (!selectedAgeGroup.equals("Kid")) {
            // selected Adult then check this below condition
            if (mobileNumber.isNullOrEmpty()) {
                binding.mobileNumberErrorCase.visibility = View.VISIBLE
                binding.mobileNumberErrorCase.text =
                    this.getString(R.string.please_enter_mobile_number)
            }

            if (mobileNumber != null && mobileNumber!!.isNotEmpty()) {
                // here check valid number or not
                if (mobileNumber!!.length == 10) {
                    // true
                } else {
                    binding.mobileNumberErrorCase.visibility = View.VISIBLE
                    binding.mobileNumberErrorCase.text =
                        this.getString(R.string.please_enter_valid_mobile_number)
                }
            }

            if (firstName!!.isNotEmpty() && lastName!!.isNotEmpty() && (mobileNumber!!.isNotEmpty() && mobileNumber?.length == 10)) {
                if (validEmail.length > 0) {
                    if (validEmail.matches(emailRegex.toRegex())) {
                        return true
                    }
                    return false
                } else {
                    return true
                }
            } else {
                return false
            }
        } else {
            if (mobileNumber != null && mobileNumber!!.isNotEmpty()) {
                if (mobileNumber?.length!! >= 1) {
                    // here check valid number or not
                    if (mobileNumber!!.length == 10) {
                        return true
                    } else {
                        binding.mobileNumberErrorCase.visibility = View.VISIBLE
                        binding.mobileNumberErrorCase.text =
                            this.getString(R.string.please_enter_valid_mobile_number)
                        return false
                    }
                    return false
                } else {
                    return true
                }
            }
            // selected Kid then check this below condition
            if (firstName!!.isNotEmpty() && lastName!!.isNotEmpty() && (mobileNumber!!.isEmpty() || mobileNumber?.length == 10)) { if (validEmail.length > 0) {
                if (validEmail.matches(emailRegex.toRegex())) {
                    return true
                }
                return false
            } else {
                return true
            }
            } else {
                return false
            }
        }

        return true
    }


//    private fun doUserValidation(): Boolean {
//        if (firstName.isNullOrEmpty()) {
//            binding.firstNameErrorCase.visibility = View.VISIBLE
//          //  return false
//        }
//
//        if (lastName.isNullOrEmpty()) {
//            binding.lastNameErrorCase.visibility = View.VISIBLE
//          //  return false
//        }
//
//        if (!selectedAgeGroup.equals("Kid")) {
//            if (mobileNumber.isNullOrEmpty()) {
//                binding.mobileNumberErrorCase.visibility = View.VISIBLE
//                binding.mobileNumberErrorCase.text =
//                    this.getString(R.string.please_enter_mobile_number)
//            }
//
//            if (binding.selectAgeGroupTxt.text.isNullOrEmpty()) {
//                binding.selectAgeGroupErrorCase.visibility = View.VISIBLE
//            }
//
//            if (mobileNumber != null && mobileNumber!!.isNotEmpty()) {
//                // here check valid number or not
//                if (mobileNumber!!.length == 10) {
//                    // true
//                } else {
//                    binding.mobileNumberErrorCase.visibility = View.VISIBLE
//                    binding.mobileNumberErrorCase.text =
//                        this.getString(R.string.please_enter_valid_mobile_number)
//                }
//            }
//
//            if (firstName!!.isNotEmpty() && lastName!!.isNotEmpty() && (mobileNumber!!.isNotEmpty() && mobileNumber?.length == 10)) {
//                return true
//            } else {
//                return false
//            }
//        } else{
//            if (mobileNumber != null && mobileNumber!!.isNotEmpty()) {
//                if(mobileNumber?.length!! >= 1) {
//                    // here check valid number or not
//                    if (mobileNumber!!.length == 10) {
//                        return true
//                    } else {
//                        binding.mobileNumberErrorCase.visibility = View.VISIBLE
//                        binding.mobileNumberErrorCase.text =
//                            this.getString(R.string.please_enter_valid_mobile_number)
//                        return false
//                    }
//                    return false
//                } else {
//                    return true
//                }
//            }
//
//        }
//
//        return true
//    }


    var textWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            if (binding.emailAddressEt.text.toString().matches(emailRegex.toRegex())) {
                binding.emailErrorCase.visibility = View.GONE
            }
        }
        override fun afterTextChanged(s: Editable) {
            if (binding.firstNameEt.text.hashCode() === s.hashCode()) {
                binding.firstNameErrorCase.visibility = View.GONE
            } else if (binding.lastNameEt.text.hashCode() === s.hashCode()) {
                binding.lastNameErrorCase.visibility = View.GONE
            } else if (binding.mobileNumberEt.text.length == 10) {
                binding.mobileNumberErrorCase.visibility = View.GONE
            }

            if (selectedAgeGroup.equals("Kid")) {
                if(binding.mobileNumberEt.text.length == 0){
                    binding.mobileNumberErrorCase.visibility = View.GONE
                }
            }

        }
    }

    private fun ageGroupDropDown() {
        val layoutInflater: LayoutInflater =
            this.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view: View = layoutInflater.inflate(R.layout.drop_down_layout, null)

        ageGroupPopupWindow = PopupWindow(
            view,
            binding.selectAgeGroupTxt.measuredWidth,
            ViewGroup.LayoutParams.WRAP_CONTENT
            //(Utils.screenHeight * 0.3).toInt()
        )
        if (ageGroupList.isNotEmpty()) {

            val linearLayoutManager = LinearLayoutManager(this)

            val dropDownRecyclerView = view.findViewById<RecyclerView>(R.id.drop_down_recycler_view)
            dropDownRecyclerView.layoutManager = linearLayoutManager
            val ageGroupAdapter = AgeGroupAdapter(this, ageGroupList)

            dropDownRecyclerView.adapter = ageGroupAdapter
            ageGroupAdapter.setCallback(this@MyFamilyActivity)
        }
        ageGroupPopupWindow!!.elevation = 10F
        ageGroupPopupWindow!!.showAsDropDown(binding.selectAgeGroupTxt, 0, 0, Gravity.CENTER)
    }

    fun selectAgeGroup(s: String) {
        binding.selectAgeGroupErrorCase.visibility = View.GONE
        if (ageGroupPopupWindow != null) {
            if (ageGroupPopupWindow!!.isShowing) {
                ageGroupPopupWindow!!.dismiss()
            }
        }
        if (s.isNotEmpty()) {
            selectedAgeGroup = s
            binding.selectAgeGroupTxt.text = s
            try {
                if (selectedAgeGroup.replace("\"", "").equals("Adult")) {
                    binding.profilePicture.setImageResource(R.drawable.adult_icon)
                } else {
                    binding.profilePicture.setImageResource(R.drawable.kids_icon)
                }
            } catch (ex: Exception) {
                binding.profilePicture.setImageResource(R.drawable.profile)
            }

        }
        if (selectedAgeGroup.equals("Kid")) {
            binding.mobileNumberHeader.text =
                Html.fromHtml(getString(R.string.mobile_number_hint) + "" + "<font color='white'></font>")
            binding.mobileNumberErrorCase.visibility = View.GONE
        } else {
            binding.mobileNumberHeader.text =
                Html.fromHtml(getString(R.string.mobile_number_hint) + "" + "<font color='white'>*</font>")
        }
    }

    fun setRelationType(relationTypeName: RelationshipTypesModel.Data) {
        if (relashionShipPopupWindow != null) {
            if (relashionShipPopupWindow!!.isShowing) {
                relashionShipPopupWindow!!.dismiss()
            }
        }

        if (!relationTypeName.relationShipName.isNullOrEmpty()){
            binding.selectRelationTxt.text = relationTypeName.relationShipName
            Relationship_id = relationTypeName.relationShipId
        }
    }

}