package com.example.safehome.complaints

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import com.example.safehome.HomeActivity
import com.example.safehome.R
import com.example.safehome.Utils
import com.example.safehome.custom.CustomProgressDialog
import com.example.safehome.databinding.ActivityComplaintStatusBinding
import com.example.safehome.databinding.ActivityComplaintsBinding
import com.example.safehome.repository.APIClient
import com.example.safehome.repository.APIInterface

class ComplaintStatusActivity : AppCompatActivity() {

    private lateinit var binding: ActivityComplaintStatusBinding
    private lateinit var customProgressDialog: CustomProgressDialog
    private lateinit var apiInterface: APIInterface
    var User_Id: String? = ""
    var Auth_Token: String? = ""
    var status: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityComplaintStatusBinding.inflate(layoutInflater)
        setContentView(binding.root)

        status = intent.getStringExtra("status")


        // progressbar
        customProgressDialog = CustomProgressDialog()
        apiInterface = APIClient.getClient(this)
        User_Id = Utils.getStringPref(this, "User_Id", "")
        Auth_Token = Utils.getStringPref(this, "Token", "")

        binding.backBtnClick.setOnClickListener {
            onBackPressed()
        }



        val resolved = "Is Your Complaint Resolved?"
        val  delayed= "Is the Action getting delayed?"


        binding.tvReInitiate.visibility = View.GONE
        binding.radioGroupReInitiate.visibility = View.GONE
        binding.tvEscalateTo.visibility = View.GONE
        binding.escalateToCl.visibility = View.GONE
        if (status == "Pending"){
            binding.tvDelayed.text = delayed

        }else if (status == "Resolved"){
            binding.tvDelayed.text = resolved

        }else{
            binding.tvDelayed.text = delayed

        }
        // Set up a listener for the RadioGroup
        binding.radioGroup.setOnCheckedChangeListener { view, checkedId ->
            val selectedRadioButton = view.findViewById<RadioButton>(checkedId)

            // Check which radio button is selected
            when (selectedRadioButton) {
                binding.radioButtonYes -> {
                    if ( binding.tvDelayed.text == delayed){
                        binding.tvEscalateTo.visibility = View.VISIBLE
                        binding.escalateToCl.visibility = View.VISIBLE
                    }else{
                        binding.imgAttachPhoto.visibility = View.VISIBLE
                        binding.tvAttachPhoto.visibility = View.VISIBLE
                        binding.tvReInitiate.visibility = View.GONE
                        binding.radioGroupReInitiate.visibility = View.GONE
                    }


                }
                binding.radioButtonNo -> {

                    if ( binding.tvDelayed.text == delayed){
                        binding.tvEscalateTo.visibility = View.GONE
                        binding.escalateToCl.visibility = View.GONE
                    }else{
                        binding.imgAttachPhoto.visibility = View.GONE
                        binding.tvAttachPhoto.visibility = View.GONE

                        binding.tvReInitiate.visibility = View.VISIBLE
                        binding.radioGroupReInitiate.visibility = View.VISIBLE
                    }
                }
            }
        }

        binding.radioGroupReInitiate.setOnCheckedChangeListener { view, checkedId ->
            val selectedRadioButton = view.findViewById<RadioButton>(checkedId)

            // Check which radio button is selected
            when (selectedRadioButton) {
                binding.reInitiateYes -> {
                    binding.tvEscalateTo.visibility = View.VISIBLE
                    binding.escalateToCl.visibility = View.VISIBLE
                    binding.tvEscalateTo.text = "Assigned To"

                }
                binding.reInitiateNo -> {

                    binding.tvEscalateTo.visibility = View.GONE
                    binding.escalateToCl.visibility = View.GONE

                }
            }
        }

    }
}