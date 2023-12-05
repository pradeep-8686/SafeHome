package com.example.safehome.policies

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.safehome.R
import com.example.safehome.Utils

class PoliciesAdapter(
    var context: Context,
    private var policiesList: ArrayList<PoliciesModel.Data.CommunityPolicy>
) :
    RecyclerView.Adapter<PoliciesAdapter.MyViewHolder>() {

    private lateinit var policiesActivity: PoliciesActivity

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.policies_single_item_list, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        if (policiesList.isNotEmpty()) {
            return policiesList.size
        }
        return 0
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val policies = policiesList[position]


        if (policies.policyTopic != null && policies.policyTopic.isNotEmpty()) {
            holder.tvPoliciesTopic.text = policies.policyTopic
        }
        if (policies.description != null && policies.description.isNotEmpty()) {
            holder.tvDescription.text = policies.description
        }
        if (policies.postedBy != null && policies.postedBy.isNotEmpty()) {
            holder.tvPostedBy.text = "By : ${policies.postedBy}"
        }

        if (policies.validUntil != null && policies.validUntil.isNotEmpty()) {
            holder.tvValidDate.text = "Valid till : ${ Utils.formatDateAndMonth(policies.validUntil) }"
        }

        if (policies.postedDate != null && policies.postedDate.isNotEmpty()) {
            holder.tvPostedDate.text = Utils.formatDateAndMonth(policies.postedDate)
        }

        holder.itemView.setOnClickListener {
            policiesActivity.noticesItemClick(policies)
        }

    }

    fun setCallback(policiesActivity: PoliciesActivity) {
        this.policiesActivity = policiesActivity
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvPoliciesTopic: TextView = itemView.findViewById(R.id.tvPoliciesTopic)
        val tvValidDate: TextView = itemView.findViewById(R.id.tvValidDate)
        val tvPostedBy: TextView = itemView.findViewById(R.id.tvPostedBy)
        val tvPostedDate: TextView = itemView.findViewById(R.id.tvPostedDate)
        val tvDescription: TextView = itemView.findViewById(R.id.tvDescription)
    }
}