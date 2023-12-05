package com.example.safehome.alert

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.safehome.R
import com.example.safehome.complaints.RaiseComplaintActivity
import com.example.safehome.forums.RaiseForumActivity
import com.example.safehome.model.PollsKeepDropdownModel

class EmergencyTypeAdapter(var context: Context, private var statesList: ArrayList<EmergencyTypeModel.Data>) :
    RecyclerView.Adapter<EmergencyTypeAdapter.MyViewHolder>() {
    private lateinit var createAlertActivity: CreateAlertActivity


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.keep_poll_for_item_list, parent, false)
        return MyViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val stateName: String = statesList[position].emergencyTypeName
        holder.ageGroupTv.tag = stateName
        if (stateName.isNotEmpty()) {
            holder.ageGroupTv.text = stateName
        }

        holder.ageGroupTv.setOnClickListener {

                createAlertActivity.setCallbackComplaintType(statesList[position])

        }
    }

    override fun getItemCount(): Int {
        if (statesList.isNotEmpty()) {
            return statesList.size
        }
        return 0
    }

    fun setCallback(createAlertActivity: CreateAlertActivity) {
        this.createAlertActivity = createAlertActivity
    }


    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ageGroupTv: TextView = itemView.findViewById(R.id.ageGroup_tv)
    }

}