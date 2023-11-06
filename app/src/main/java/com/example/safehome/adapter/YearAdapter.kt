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
import com.example.safehome.forums.ForumsListActivity
import com.example.safehome.maintenance.HistoryFragment
import com.example.safehome.maintenance.MaintenanceActivity
import com.example.safehome.meetings.MeetingsActivity
import com.example.safehome.meetings.MeetingsCompletedFragment
import com.example.safehome.notice.NoticeActivity
import com.example.safehome.polls.PollsActivity
import com.example.safehome.services.ServicesActivity
import com.example.safehome.services.ServicesMemberListActivity
import com.example.safehome.services.ServicesPaymentHistoryFragment
import com.example.safehome.visitors.VisitorActivity
import com.example.safehome.visitors.VisitorsListFragment

class YearAdapter(var context: Context, private var statesList: List<String>) :
    RecyclerView.Adapter<YearAdapter.MyViewHolder>() {
    private lateinit var historyFragment: HistoryFragment
    private lateinit var facilitiesHistoryFragment: FacilitiesHistoryFragment
    private lateinit var dailyHelpMemberListActivity: DailyHelpMemberListActivity
    private lateinit var dailyHelpPaymentHistoryFragment: DailyHelpPaymentHistoryFragment
    private lateinit var servicesMemberListActivity: ServicesMemberListActivity
    private lateinit var servicesPaymentHistoryFragment: ServicesPaymentHistoryFragment
    private lateinit var eventsHistoryFragment: EventsHistoryFragment
    private lateinit var noticeActivity: NoticeActivity
    private lateinit var personalFragment: PersonalFragment
    private lateinit var personalFragment1: PersonalFragment
    private lateinit var communityFragment: CommunityFragment
    private lateinit var communityFragment1: CommunityFragment
    private lateinit var meetingsCompletedFragment: MeetingsCompletedFragment
    private lateinit var pollsActivity: PollsActivity
    private lateinit var forumsListActivity: ForumsListActivity
    private lateinit var visitorsListFragment: VisitorsListFragment
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
            if (context is MaintenanceActivity) {
                historyFragment.selectYear(it.tag as String)
            } else if (context is DailyHelpMemberListActivity) {
                dailyHelpMemberListActivity.setCallbackDailyHelpMemberListActivity(it.tag as String)
            } else if( context is DailyHelpActivity) {
                dailyHelpPaymentHistoryFragment.setCallbackPaymentHistory(it.tag as String)
            } else if( context is ServicesMemberListActivity) {
                servicesMemberListActivity.setCallbackServiceListActivity(it.tag as String)
            }else if( context is ServicesActivity) {
                servicesPaymentHistoryFragment.setCallbackServicePaymentHistoryYear(it.tag as String)
            }else if( context is EventsActivity) {
                eventsHistoryFragment.selectEventsHistoryYear(it.tag as String)
            }else if( context is NoticeActivity) {
                noticeActivity.selectNoticeHistoeyYear(it.tag as String)
            }else if( context is ComplaintsActivity) {
                personalFragment.selectComplaintYear(it.tag as String)
            }else if( context is MeetingsActivity) {
                meetingsCompletedFragment.selectComplaintYear(it.tag as String)
            }
            else if (context is PollsActivity){
                pollsActivity.selectPollsYear(it.tag as String)
            }
            else if (context is ForumsListActivity){
                forumsListActivity.selectForumsYear(it.tag as String)
            }else if (context is VisitorActivity){
                visitorsListFragment.selectVisitorYear(it.tag as String)
            }
            else {
                facilitiesHistoryFragment.selectYear(it.tag as String)
            }
        }
    }

    override fun getItemCount(): Int {
        if (statesList.isNotEmpty()) {
            return statesList.size
        }
        return 0
    }


    fun setPollsCallback(pollsActivity: PollsActivity) {
        this.pollsActivity = pollsActivity
    }
    fun setForumsCallback(forumsListActivity : ForumsListActivity) {
        this.forumsListActivity = forumsListActivity
    }

    fun setCallbackFacilitiesHistoryFrag(facilitiesHistoryFragment: FacilitiesHistoryFragment) {
        this.facilitiesHistoryFragment = facilitiesHistoryFragment
    }

    fun setCallbackDailyHelpMemberListActivity(dailyHelpMemberListActivity: DailyHelpMemberListActivity) {
        this.dailyHelpMemberListActivity = dailyHelpMemberListActivity
    }

    fun setCallbackPaymentHistory(dailyHelpPaymentHistoryFragment: DailyHelpPaymentHistoryFragment) {
        this.dailyHelpPaymentHistoryFragment = dailyHelpPaymentHistoryFragment
    }

    fun setCallbackServicesPaymentHistory(servicesPaymentHistoryFragment: ServicesPaymentHistoryFragment) {
        this.servicesPaymentHistoryFragment = servicesPaymentHistoryFragment
    }

    fun setCallbackEventsHistoryFrag(eventsHistoryFragment: EventsHistoryFragment) {
        this.eventsHistoryFragment = eventsHistoryFragment
    }

    fun setCallbackNoticeYear(noticeActivity: NoticeActivity) {
        this.noticeActivity = noticeActivity
    }

    fun setCallbackComplaintsYear(personalFragment: PersonalFragment) {
        this.personalFragment = personalFragment
    }

    fun setCallbackComplaintsYear(communityFragment: CommunityFragment) {
        this.communityFragment = communityFragment
    }

    fun setCallbackComplaintsYear(meetingsCompletedFragment: MeetingsCompletedFragment) {
        this.meetingsCompletedFragment = meetingsCompletedFragment
    }

    fun setCallback(historyFragment: HistoryFragment) {
      this.historyFragment = historyFragment
    }

    fun setVisitorsHistoryCallback(visitorsListFragment: VisitorsListFragment) {
        this.visitorsListFragment = visitorsListFragment

    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ageGroupTv: TextView = itemView.findViewById(R.id.ageGroup_tv)
    }

}