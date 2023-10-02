package com.example.safehome.residentview

import android.Manifest
import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.Html
import android.text.TextWatcher
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.safehome.HomeActivity
import com.example.safehome.R
import com.example.safehome.Utils
import com.example.safehome.custom.CustomProgressDialog
import com.example.safehome.databinding.ActivityTenantBinding
import com.example.safehome.model.MobileSignUp
import com.example.safehome.repository.APIClient
import com.example.safehome.repository.APIInterface
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class TenantActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTenantBinding
    private var firstName: String? = null
    private var lastName: String? = null
    private var mobileNumber: String? = null
    private var emailId: String? = null
    private lateinit var addTenantCall: Call<MobileSignUp>
    private val PERMISSION_REQUEST_CODE = 1
    private val PICK_IMAGE_REQUEST = 2
    private var imageMultipartBody: MultipartBody.Part? = null
    private lateinit var customProgressDialog: CustomProgressDialog
    private lateinit var apiInterface: APIInterface
    var User_Id: String? = ""
    var Auth_Token: String? = ""
    var clientId: String? = ""
    val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\$"

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTenantBinding.inflate(layoutInflater)
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

        buttonClickEvents()
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun buttonClickEvents() {
        // here handle button click events
        binding.backBtnClick.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.saveBtn.setOnClickListener {
            verifyData()
        }
        binding.profilePicture.setOnClickListener {
            pickImageFromGallery()
        }

        binding.cancelBtn.setOnClickListener {
            clearData()
        }
    }

    private fun clearData() {
        binding.firstNameEt.setText("")
        binding.lastNameEt.setText("")
        binding.mobileNumberEt.setText("")
        binding.emailAddressEt.setText("")
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

        if (doUserValidation()) {
            saveTenantServiceCall()
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
                // Permission denied
                // Handle accordingly (e.g., display a message, disable functionality)
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
    private fun saveTenantServiceCall() {
        customProgressDialog.progressDialogShow(this, this.getString(R.string.loading))

        imageMultipartBody?.let {
            addTenantCall = apiInterface.addTenant(
                "bearer " + Auth_Token,
                User_Id!!.toInt(),
                firstName!!,
                lastName!!,
                mobileNumber!!.replace("\"","").trim(),
                emailId!!,
                imageMultipartBody!!
            )
        }.run {

            var mobile = mobileNumber?.replace("\"", "")
            addTenantCall = apiInterface.addTenantNoImage(
                "bearer " + Auth_Token,
                User_Id!!.toInt(),
                firstName!!.replace("\"",""),
                lastName!!.replace("\"",""),
                mobile!!.toLong(),
                emailId!!.replace("\"",""),
            )
        }
        addTenantCall.enqueue(object : Callback<MobileSignUp> {
            override fun onResponse(call: Call<MobileSignUp>, response: Response<MobileSignUp>) {

                customProgressDialog.progressDialogDismiss()
                // here successfully response
                if (response.isSuccessful && response.body() != null) {
                    // here save user signup data
                    navigationToAddTenantScreen(response.body()!!)
                }
            }

            override fun onFailure(call: Call<MobileSignUp>, t: Throwable) {
                customProgressDialog.progressDialogDismiss()
                // here failure conditions
                Utils.showToast(this@TenantActivity, t.message.toString())
            }
        })
    }

    private fun navigationToAddTenantScreen(body: MobileSignUp) {
        if (body.statusCode == 1) {
            val intent = Intent(this, AddTenantActivity::class.java)
            startActivity(intent)
            finish()
        } else if (body.statusCode == 2) {
            // toast message
            if (body.message.isNotEmpty()) {
                Utils.showToast(this, body.message)
            }
        }
    }

    private fun doUserValidation(): Boolean {
        if (firstName.isNullOrEmpty()) {
            binding.firstNameErrorCase.visibility = View.VISIBLE
        }

        if (lastName.isNullOrEmpty()) {
            binding.lastNameErrorCase.visibility = View.VISIBLE
        }

        if (mobileNumber.isNullOrEmpty()) {
            binding.mobileNumberErrorCase.visibility = View.VISIBLE
            binding.mobileNumberErrorCase.text = this.getString(R.string.please_enter_mobile_number)
        }

        var validEmail: String = binding.emailAddressEt!!.text.trim().toString()
        if(validEmail.length>0) {
            if (!validEmail.matches(emailRegex.toRegex())) {
                binding.emailErrorCase.visibility = View.VISIBLE
                binding.emailErrorCase.text = this.getString(R.string.please_enter_valid_email)
            }
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

        return true
    }


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
        }
    }

}