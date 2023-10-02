package com.example.safehome.adapter

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.safehome.R
import com.example.safehome.dailyhelp.DailyHelpActivity
import com.example.safehome.dailyhelp.DailyHelpMemberListActivity
import com.example.safehome.facilitiesview.FacilitiesHistoryFragment
import com.example.safehome.maintenance.HistoryFragment
import com.example.safehome.maintenance.MaintenanceActivity
import com.example.safehome.model.AvailabilityTime
import com.example.safehome.services.ServicesMemberListActivity

class AvailablityTimeAdapter(
    var context: Context,
    private var availablityTimeList: List<AvailabilityTime>
) :
    RecyclerView.Adapter<AvailablityTimeAdapter.MyViewHolder>() {
    private lateinit var dailyHelpMemberListActivity: DailyHelpMemberListActivity
    private lateinit var servicesMemberListActivity: ServicesMemberListActivity

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.availablity_time_item_list, parent, false)
        return MyViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val availabilityTime: AvailabilityTime = availablityTimeList[position]
        holder.ageGroupTv.tag = availabilityTime
        if (availabilityTime.Time.isNotEmpty()) {
            holder.ageGroupTv.text = availabilityTime.Time
        }
//        if(availabilityTime.Chekbox.equals("true")){
//           holder.checkbox.isChecked= true
//        }else {
//            holder.checkbox.isChecked= false
//        }

        holder.ageGroupTv.setOnClickListener {
            if(context is DailyHelpActivity) {
                dailyHelpMemberListActivity.setCallbackDailyHelpAvilabilityTime(it.tag as AvailabilityTime)
            }else if(context is ServicesMemberListActivity){
                servicesMemberListActivity.setCallbackDailyHelpAvilabilityTime(it.tag as AvailabilityTime)
            }
        }
    }

    override fun getItemCount(): Int {
        if (availablityTimeList.isNotEmpty()) {
            return availablityTimeList.size
        }
        return 0
    }

    fun setCallbackDailyHelpMemberListActivity(dailyHelpMemberListActivity: DailyHelpMemberListActivity) {
        this.dailyHelpMemberListActivity = dailyHelpMemberListActivity
    }

    fun setCallbackServicesMemberListActivity(servicesMemberListActivity: ServicesMemberListActivity) {
        this.servicesMemberListActivity = servicesMemberListActivity
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ageGroupTv: TextView = itemView.findViewById(R.id.ageGroup_tv)
       // val checkbox:CheckBox = itemView.findViewById(R.id.checkbox)
    }

}