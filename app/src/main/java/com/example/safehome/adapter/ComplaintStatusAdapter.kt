package com.example.safehome.adapter

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.safehome.R
import com.example.safehome.complaints.CommunityFragment
import com.example.safehome.complaints.ComplaintsActivity
import com.example.safehome.complaints.PersonalFragment
import com.example.safehome.dailyhelp.DailyHelpActivity
import com.example.safehome.dailyhelp.DailyHelpMemberListActivity
import com.example.safehome.dailyhelp.DailyHelpPaymentHistoryFragment
import com.example.safehome.eventsview.EventsActivity
import com.example.safehome.eventsview.EventsHistoryFragment
import com.example.safehome.facilitiesview.FacilitiesHistoryFragment
import com.example.safehome.maintenance.HistoryFragment
import com.example.safehome.maintenance.MaintenanceActivity
import com.example.safehome.meetings.MeetingsActivity
import com.example.safehome.meetings.MeetingsCompletedFragment
import com.example.safehome.notice.NoticeActivity
import com.example.safehome.services.ServicesActivity
import com.example.safehome.services.ServicesMemberListActivity
import com.example.safehome.services.ServicesPaymentHistoryFragment

class ComplaintStatusAdapter(var context: Context, private var statesList: List<String>) :
    RecyclerView.Adapter<ComplaintStatusAdapter.MyViewHolder>() {
    private lateinit var personalFragment: PersonalFragment
    private lateinit var communityFragment: CommunityFragment


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.age_group_item_list, parent, false)
        return MyViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val stateName: String = statesList[position]
        holder.ageGroupTv.tag = stateName
        if (stateName.isNotEmpty()) {
            holder.ageGroupTv.text = stateName
        }

        holder.ageGroupTv.setOnClickListener {
          if( context is ComplaintsActivity) {
                personalFragment.setCallbackComplaintStatus(it.tag as String)
            }
//          else {
//              communityFragment.setCallbackComplaintStatusCommunity(it.tag as String)
//            }
        }
    }

    override fun getItemCount(): Int {
        if (statesList.isNotEmpty()) {
            return statesList.size
        }
        return 0
    }

    fun setCallbackComplaintStatus(personalFragment: PersonalFragment) {
        this.personalFragment = personalFragment
    }

    fun setCallbackComplaintStatusCommunity(communityFragment: CommunityFragment) {
        this.communityFragment = communityFragment
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ageGroupTv: TextView = itemView.findViewById(R.id.ageGroup_tv)
    }

}