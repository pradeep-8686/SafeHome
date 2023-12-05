package com.example.safehome.policies

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.RequiresApi
import com.example.safehome.HomeActivity
import com.example.safehome.R
import com.example.safehome.Utils
import com.example.safehome.activity.BaseActivity
import com.example.safehome.databinding.ActivityPoliciesDetailsBinding

class PoliciesDetailsActivity : BaseActivity() {

    private var policisModel: PoliciesModel.Data.CommunityPolicy? = null
    private lateinit var binding: ActivityPoliciesDetailsBinding

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPoliciesDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (intent.extras?.getSerializable("policiesModel") != null) {

            policisModel =
                intent.extras?.getSerializable("policiesModel") as PoliciesModel.Data.CommunityPolicy
        }

        if (policisModel!!.policyTopic != null && policisModel!!.policyTopic.isNotEmpty()) {
            binding.tvPoliciesTopic.text = policisModel!!.policyTopic
        }
        if (policisModel!!.description != null && policisModel!!.description.isNotEmpty()) {
            binding.tvDescription.text = policisModel!!.description
        }
        if (policisModel!!.postedBy != null && policisModel!!.postedBy.isNotEmpty()) {
            binding.tvPostedBy.text = "By : ${policisModel!!.postedBy}"
        }

        if (policisModel!!.validUntil != null && policisModel!!.validUntil.isNotEmpty()) {
            binding.tvValidDate.text = "Valid till : ${Utils.formatDateAndMonth(policisModel!!.validUntil)}"
        }

        if (policisModel!!.postedDate != null && policisModel!!.postedDate.isNotEmpty()) {
            binding.tvPostedDate.text = Utils.formatDateAndMonth(policisModel!!.postedDate)
        }

        binding.backBtnClick.setOnClickListener {
            val intent = Intent(this, PoliciesActivity::class.java)
            startActivity(intent)
            finish()
        }

    }
}